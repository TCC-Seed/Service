package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.Evento;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventoRepository extends JpaRepository<Evento, Long> {
  @Override
  <T extends Evento> T save(T entity);

  @Override
  Optional<Evento> findById(Long id);

  @EntityGraph(attributePaths = {"categorias", "ies"})
  @Query(
      "select distinct e from Evento e join e.ies ies "
          + "where ies.id = :iesId "
          + "and (:categoria is null or :categoria member of e.categorias)")
  List<Evento> findVisiveisPorIesECategoria(
      @Param("iesId") Long iesId, @Param("categoria") String categoria);

  @EntityGraph(attributePaths = {"categorias", "ies"})
  @Query("select distinct e from Evento e join e.ies ies where e.id = :id and ies.id = :iesId")
  Optional<Evento> findVisivelPorIdEIes(@Param("id") Long id, @Param("iesId") Long iesId);

  @EntityGraph(attributePaths = {"categorias", "ies"})
  @Query("select distinct e from Evento e where e.id = :id")
  Optional<Evento> findComCategoriasEInstituicoesPorId(@Param("id") Long id);

  @Override
  void deleteById(Long id);
}

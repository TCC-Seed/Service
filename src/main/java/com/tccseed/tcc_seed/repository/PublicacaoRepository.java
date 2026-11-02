package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.Publicacao;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PublicacaoRepository extends JpaRepository<Publicacao, Long> {
  @Override
  <T extends Publicacao> T save(T entity);

  @Override
  Optional<Publicacao> findById(Long id);

  @Override
  List<Publicacao> findAll();

  @EntityGraph(attributePaths = {"categorias", "funcionario"})
  @Query(
      "select distinct p from Publicacao p "
          + "where p.funcionario.ies.id = :iesId "
          + "and (:categoria is null or :categoria member of p.categorias)")
  List<Publicacao> findVisiveisPorIesECategoria(
      @Param("iesId") Long iesId, @Param("categoria") String categoria);

  @EntityGraph(attributePaths = {"categorias", "funcionario"})
  @Query("select p from Publicacao p where p.id = :id and p.funcionario.ies.id = :iesId")
  Optional<Publicacao> findVisivelPorIdEIes(@Param("id") Long id, @Param("iesId") Long iesId);

  @EntityGraph(attributePaths = {"categorias", "funcionario"})
  @Query("select p from Publicacao p where p.id = :id")
  Optional<Publicacao> findComFuncionarioECategoriasPorId(@Param("id") Long id);

  @Override
  void deleteById(Long id);
}

package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.FormularioPersonalizado;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FormularioPersonalizadoRepository
    extends JpaRepository<FormularioPersonalizado, Long> {
  @Override
  <T extends FormularioPersonalizado> T save(T entity);

  @Override
  Optional<FormularioPersonalizado> findById(Long id);

  @Override
  List<FormularioPersonalizado> findAll();

  @Override
  void deleteById(Long id);

  @Query("select f from FormularioPersonalizado f where f.id = :id and f.ies.id = :iesId")
  Optional<FormularioPersonalizado> findByIdAndIesId(
      @Param("id") Long id, @Param("iesId") Long iesId);

  @Query(
      "select distinct f from FormularioPersonalizado f left join fetch f.vinculos fp left join fetch fp.pergunta where f.id = :id and f.ies.id = :iesId order by fp.pergunta.id")
  Optional<FormularioPersonalizado> findDetailedByIdAndIesId(
      @Param("id") Long id, @Param("iesId") Long iesId);

  @Query("select f from FormularioPersonalizado f where f.ies.id = :iesId order by f.id")
  Page<FormularioPersonalizado> findAllByIesId(@Param("iesId") Long iesId, Pageable pageable);

  @Query(
      value =
          "select new com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.FormularioResumoResponse(f.id, f.titulo, count(fp)) from FormularioPersonalizado f left join f.vinculos fp where f.ies.id = :iesId group by f.id, f.titulo order by f.id",
      countQuery = "select count(f.id) from FormularioPersonalizado f where f.ies.id = :iesId")
  Page<com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.FormularioResumoResponse>
      findResumosByIesId(@Param("iesId") Long iesId, Pageable pageable);

  @Query(
      "select new com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.FormularioResumoResponse(f.id, f.titulo, count(fp)) from FormularioPersonalizado f left join f.vinculos fp where f.ies.id = :iesId group by f.id, f.titulo order by f.id")
  List<com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.FormularioResumoResponse>
      listarResumosByIesId(@Param("iesId") Long iesId);

  @Query("select f from FormularioPersonalizado f where f.id = :id and f.ies.id = :iesId")
  Optional<FormularioPersonalizado> findForAccess(@Param("id") Long id, @Param("iesId") Long iesId);
}

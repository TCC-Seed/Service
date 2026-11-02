package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.RespostaStatusProjection;
import com.tccseed.tcc_seed.domain.entity.RespostaFormularioPersonalizado;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RespostaFormularioPersonalizadoRepository
    extends JpaRepository<RespostaFormularioPersonalizado, Long> {
  @Override
  <T extends RespostaFormularioPersonalizado> T save(T entity);

  @Override
  Optional<RespostaFormularioPersonalizado> findById(Long id);

  @Override
  List<RespostaFormularioPersonalizado> findAll();

  @Override
  void deleteById(Long id);

  boolean existsByFormularioId(Long formularioId);

  @Query(
      value =
          "select r from RespostaFormularioPersonalizado r join fetch r.formulario join fetch r.usuario join Estudante e on e.id = r.usuario.id where r.formulario.id = :formularioId and r.formulario.ies.id = :iesId and e.ies.id = :iesId order by r.dataPreenchimento desc, r.id desc",
      countQuery =
          "select count(r.id) from RespostaFormularioPersonalizado r join Estudante e on e.id = r.usuario.id where r.formulario.id = :formularioId and r.formulario.ies.id = :iesId and e.ies.id = :iesId")
  Page<RespostaFormularioPersonalizado> findByFormularioAndIes(
      @Param("formularioId") Long formularioId, @Param("iesId") Long iesId, Pageable pageable);

  @Query(
      "select r from RespostaFormularioPersonalizado r join fetch r.formulario join fetch r.usuario where r.formulario.id = :formularioId and r.usuario.id = :usuarioId order by r.dataPreenchimento desc, r.id desc")
  List<RespostaFormularioPersonalizado> findByFormularioAndUsuario(
      @Param("formularioId") Long formularioId, @Param("usuarioId") Long usuarioId);

  @Query(
      "select r from RespostaFormularioPersonalizado r join fetch r.formulario where r.usuario.id = :usuarioId and r.formulario.id in :formularioIds order by r.dataPreenchimento desc, r.id desc")
  List<RespostaFormularioPersonalizado> findLatestCandidates(
      @Param("usuarioId") Long usuarioId, @Param("formularioIds") List<Long> formularioIds);

  @Query(
      "select r from RespostaFormularioPersonalizado r where r.id = :respostaId and r.formulario.id = :formularioId and r.usuario.id = :usuarioId")
  Optional<RespostaFormularioPersonalizado> findOwned(
      @Param("respostaId") Long respostaId,
      @Param("formularioId") Long formularioId,
      @Param("usuarioId") Long usuarioId);

  @Query(
      "select new com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.RespostaStatusProjection(r.id, count(i)) from RespostaFormularioPersonalizado r left join ItemRespostaFormularioPersonalizado i on i.respostaFormulario.id = r.id and i.resposta is not null and trim(i.resposta) <> '' left join FormularioPergunta fp on fp.formulario.id = r.formulario.id and fp.pergunta.id = i.pergunta.id where r.id in :ids and (i.id.idPergunta is null or fp.id.idPergunta is not null) group by r.id")
  List<RespostaStatusProjection> countRespondidasPorFormulario(@Param("ids") List<Long> ids);
}

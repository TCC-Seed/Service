package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.FormularioPergunta;
import com.tccseed.tcc_seed.domain.entity.id.FormularioPerguntaId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FormularioPerguntaRepository
    extends JpaRepository<FormularioPergunta, FormularioPerguntaId> {
  @Override
  <T extends FormularioPergunta> T save(T entity);

  @Override
  Optional<FormularioPergunta> findById(FormularioPerguntaId id);

  @Override
  List<FormularioPergunta> findAll();

  @Override
  void deleteById(FormularioPerguntaId id);

  @Query(
      "select fp.pergunta from FormularioPergunta fp where fp.formulario.id = :formularioId order by fp.pergunta.id asc")
  List<com.tccseed.tcc_seed.domain.entity.PerguntaFormularioPersonalizado>
      findPerguntasByFormularioId(@Param("formularioId") Long formularioId);

  @Query("select count(fp) from FormularioPergunta fp where fp.formulario.id = :formularioId")
  long countByFormularioId(@Param("formularioId") Long formularioId);

  boolean existsByFormularioIdAndPerguntaId(Long formularioId, Long perguntaId);

  void deleteByFormularioId(Long formularioId);
}

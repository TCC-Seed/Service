package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.FormularioBase;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FormularioBaseRepository extends JpaRepository<FormularioBase, Long> {

  @Query(
      value =
          """
          select resposta.id as id,
                 estudante.token as token,
                 resposta.dataPreenchimento as dataPreenchimento,
                 resposta.anotacao as anotacao,
                 resposta.secaoAutoconhecimento as secaoAutoconhecimento,
                 resposta.secaoHidratacao as secaoHidratacao,
                 resposta.secaoExercicios as secaoExercicios,
                 resposta.secaoAlimentacao as secaoAlimentacao,
                 resposta.secaoAcademia as secaoAcademia,
                 resposta.secaoMental as secaoMental,
                 resposta.secaoTrabalho as secaoTrabalho,
                 resposta.secaoSocial as secaoSocial,
                 resposta.secaoSono as secaoSono,
                 resposta.secaoLazer as secaoLazer,
                 resposta.secaoCultura as secaoCultura,
                 resposta.secaoFinanceiro as secaoFinanceiro,
                 resposta.secaoAmoroso as secaoAmoroso,
                 resposta.secaoAcess as secaoAcess
          from FormularioBase resposta
          join resposta.estudante estudante
          where estudante.ies.id = :iesId
            and resposta.dataPreenchimento >= coalesce(:dataInicio, resposta.dataPreenchimento)
            and resposta.dataPreenchimento <= coalesce(:dataFim, resposta.dataPreenchimento)
          """,
      countQuery =
          """
          select count(resposta.id)
          from FormularioBase resposta
          join resposta.estudante estudante
          where estudante.ies.id = :iesId
            and resposta.dataPreenchimento >= coalesce(:dataInicio, resposta.dataPreenchimento)
            and resposta.dataPreenchimento <= coalesce(:dataFim, resposta.dataPreenchimento)
          """)
  Page<FormularioBaseListProjection> findPageByIes(
      @Param("iesId") Long iesId,
      @Param("dataInicio") LocalDate dataInicio,
      @Param("dataFim") LocalDate dataFim,
      Pageable pageable);

  @Query(
      value =
          """
          select resposta.id as id,
                 estudante.token as token,
                 resposta.dataPreenchimento as dataPreenchimento,
                 resposta.anotacao as anotacao,
                 resposta.secaoAutoconhecimento as secaoAutoconhecimento,
                 resposta.secaoHidratacao as secaoHidratacao,
                 resposta.secaoExercicios as secaoExercicios,
                 resposta.secaoAlimentacao as secaoAlimentacao,
                 resposta.secaoAcademia as secaoAcademia,
                 resposta.secaoMental as secaoMental,
                 resposta.secaoTrabalho as secaoTrabalho,
                 resposta.secaoSocial as secaoSocial,
                 resposta.secaoSono as secaoSono,
                 resposta.secaoLazer as secaoLazer,
                 resposta.secaoCultura as secaoCultura,
                 resposta.secaoFinanceiro as secaoFinanceiro,
                 resposta.secaoAmoroso as secaoAmoroso,
                 resposta.secaoAcess as secaoAcess
          from FormularioBase resposta
          join resposta.estudante estudante
          where estudante.id = :estudanteId
            and resposta.dataPreenchimento >= coalesce(:dataInicio, resposta.dataPreenchimento)
            and resposta.dataPreenchimento <= coalesce(:dataFim, resposta.dataPreenchimento)
          """,
      countQuery =
          """
          select count(resposta.id)
          from FormularioBase resposta
          join resposta.estudante estudante
          where estudante.id = :estudanteId
            and resposta.dataPreenchimento >= coalesce(:dataInicio, resposta.dataPreenchimento)
            and resposta.dataPreenchimento <= coalesce(:dataFim, resposta.dataPreenchimento)
          """)
  Page<FormularioBaseListProjection> findPageByEstudante(
      @Param("estudanteId") Long estudanteId,
      @Param("dataInicio") LocalDate dataInicio,
      @Param("dataFim") LocalDate dataFim,
      Pageable pageable);

  @Query(
      "select resposta from FormularioBase resposta where resposta.id = :id and resposta.estudante.id = :estudanteId")
  Optional<FormularioBase> findOwnedById(
      @Param("id") Long id, @Param("estudanteId") Long estudanteId);
}

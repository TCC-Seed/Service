package com.tccseed.tcc_seed.controller.formulariobase.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
    name = "FormularioBaseBooleanos",
    description = "Somente os campos booleanos selecionados são incluídos.")
public class FormularioBaseBooleanosResponse {
  private Long id;
  private LocalDate dataPreenchimento;
  private Boolean secaoAutoconhecimento;
  private Boolean secaoHidratacao;
  private Boolean secaoExercicios;
  private Boolean secaoAlimentacao;
  private Boolean secaoAcademia;
  private Boolean secaoMental;
  private Boolean secaoTrabalho;
  private Boolean secaoSocial;
  private Boolean secaoSono;
  private Boolean secaoLazer;
  private Boolean secaoCultura;
  private Boolean secaoFinanceiro;
  private Boolean secaoAmoroso;
  private Boolean secaoAcess;

  public FormularioBaseBooleanosResponse(Long id, LocalDate dataPreenchimento) {
    this.id = id;
    this.dataPreenchimento = dataPreenchimento;
  }
}

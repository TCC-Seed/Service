package com.tccseed.tcc_seed.controller.formulariobase.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
    name = "FormularioBaseEstudante",
    description = "Resposta própria do estudante; a anotação acompanha todas as listagens.",
    example =
        "{\"id\":130,\"dataPreenchimento\":\"2026-11-01\",\"secaoSono\":false,\"secaoHidratacao\":true,\"anotacao\":\"Texto livre\"}")
public class FormularioBaseEstudanteResponse extends FormularioBaseBooleanosResponse {
  private String anotacao;

  public FormularioBaseEstudanteResponse(
      Long id, java.time.LocalDate dataPreenchimento, String anotacao) {
    super(id, dataPreenchimento);
    this.anotacao = anotacao;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public String getAnotacao() {
    return anotacao;
  }

  public void setAnotacao(String anotacao) {
    this.anotacao = anotacao;
  }
}

package com.tccseed.tcc_seed.controller.formulariobase.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
    name = "FormularioBaseFuncionario",
    description = "Resposta sem dados pessoais do estudante.",
    example =
        "{\"id\":130,\"token\":\"10000000-0000-4000-8000-000000000001\",\"dataPreenchimento\":\"2026-11-01\",\"secaoSono\":true,\"secaoHidratacao\":false}")
public class FormularioBaseFuncionarioResponse extends FormularioBaseBooleanosResponse {
  private UUID token;

  public FormularioBaseFuncionarioResponse(Long id, UUID token, LocalDate dataPreenchimento) {
    super(id, dataPreenchimento);
    this.token = token;
  }
}

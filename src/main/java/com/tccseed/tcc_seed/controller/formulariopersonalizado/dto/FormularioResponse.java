package com.tccseed.tcc_seed.controller.formulariopersonalizado.dto;

import java.util.List;

public record FormularioResponse(
    Long id, String titulo, List<PerguntaFormularioResponse> perguntas) {
  public FormularioResponse {
    perguntas = List.copyOf(perguntas);
  }
}

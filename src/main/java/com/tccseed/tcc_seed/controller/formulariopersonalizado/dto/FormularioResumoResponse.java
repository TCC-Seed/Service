package com.tccseed.tcc_seed.controller.formulariopersonalizado.dto;

public record FormularioResumoResponse(
    Long id, String titulo, long quantidadePerguntas, String statusResposta) {
  public FormularioResumoResponse(Long id, String titulo, long quantidadePerguntas) {
    this(id, titulo, quantidadePerguntas, "nao_respondido");
  }
}

package com.tccseed.tcc_seed.controller.evento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public record EventoAtualizacaoRequest(
    @NotNull LocalDate dataInicio,
    LocalDate dataFim,
    String link,
    String descricao,
    @NotNull List<@NotBlank @Size(max = 100) String> categorias,
    @NotEmpty List<@NotNull Long> iesIds) {

  public EventoAtualizacaoRequest {
    categorias = categorias == null ? null : new ArrayList<>(categorias);
    iesIds = iesIds == null ? null : new ArrayList<>(iesIds);
  }

  public List<String> categorias() {
    return categorias == null ? null : new ArrayList<>(categorias);
  }

  public List<Long> iesIds() {
    return iesIds == null ? null : new ArrayList<>(iesIds);
  }
}

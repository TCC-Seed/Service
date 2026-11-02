package com.tccseed.tcc_seed.controller.evento.dto;

import com.tccseed.tcc_seed.domain.entity.Evento;
import java.time.LocalDate;
import java.util.List;

public record EventoResponse(
    Long id,
    LocalDate dataInicio,
    LocalDate dataFim,
    String link,
    String descricao,
    List<String> categorias,
    List<Long> iesIds) {

  public EventoResponse {
    categorias = categorias == null ? List.of() : List.copyOf(categorias);
    iesIds = iesIds == null ? List.of() : List.copyOf(iesIds);
  }

  public static EventoResponse from(Evento evento) {
    return new EventoResponse(
        evento.getId(),
        evento.getDataInicio(),
        evento.getDataFim(),
        evento.getLink(),
        evento.getDescricao(),
        evento.getCategorias().stream().sorted().toList(),
        evento.getIes().stream().map(ies -> ies.getId()).sorted().toList());
  }
}

package com.tccseed.tcc_seed.controller.evento.dto;

import com.tccseed.tcc_seed.domain.entity.CategoriaEvento;
import com.tccseed.tcc_seed.domain.entity.Evento;
import com.tccseed.tcc_seed.domain.entity.id.CategoriaEventoId;

public record CategoriaEventoRequest(Long idEvento, String categoria) {
  public CategoriaEvento toEntity() {
    CategoriaEvento entidade = new CategoriaEvento();
    CategoriaEventoId id = new CategoriaEventoId();
    id.setIdEvento(idEvento);
    id.setCategoria(categoria);
    entidade.setId(id);
    Evento evento = new Evento();
    evento.setId(idEvento);
    entidade.setEvento(evento);
    return entidade;
  }
}

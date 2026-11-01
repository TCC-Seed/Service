package com.tccseed.tcc_seed.controller.evento.dto;

import com.tccseed.tcc_seed.domain.entity.Evento;
import com.tccseed.tcc_seed.domain.entity.EventoIes;
import com.tccseed.tcc_seed.domain.entity.Ies;
import com.tccseed.tcc_seed.domain.entity.id.EventoIesId;

public record EventoIesRequest(Long idIes, Long idEvento) {
    public EventoIes toEntity() {
        EventoIes entidade = new EventoIes();
        EventoIesId id = new EventoIesId();
        id.setIdIes(idIes);
        id.setIdEvento(idEvento);
        entidade.setId(id);
        Ies ies = new Ies();
        ies.setId(idIes);
        Evento evento = new Evento();
        evento.setId(idEvento);
        entidade.setIes(ies);
        entidade.setEvento(evento);
        return entidade;
    }
}

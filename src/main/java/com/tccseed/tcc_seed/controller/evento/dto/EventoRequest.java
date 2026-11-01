package com.tccseed.tcc_seed.controller.evento.dto;

import com.tccseed.tcc_seed.domain.entity.Evento;
import java.time.LocalDate;

public record EventoRequest(LocalDate dataInicio, LocalDate dataFim, String link, String descricao) {
    public Evento toEntity() {
        Evento evento = new Evento();
        evento.setDataInicio(dataInicio);
        evento.setDataFim(dataFim);
        evento.setLink(link);
        evento.setDescricao(descricao);
        return evento;
    }
}

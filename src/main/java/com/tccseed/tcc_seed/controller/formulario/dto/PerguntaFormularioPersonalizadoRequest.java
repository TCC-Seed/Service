package com.tccseed.tcc_seed.controller.formulario.dto;

import com.tccseed.tcc_seed.domain.entity.PerguntaFormularioPersonalizado;

public record PerguntaFormularioPersonalizadoRequest(String enunciado) {
    public PerguntaFormularioPersonalizado toEntity() {
        PerguntaFormularioPersonalizado entidade = new PerguntaFormularioPersonalizado();
        entidade.setEnunciado(enunciado);
        return entidade;
    }
}

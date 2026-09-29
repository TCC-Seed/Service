package com.tccseed.tcc_seed.domain.enums;

import lombok.Getter;

public enum TipoUsuario {

    ESTUDANTE("estudante"),
    FUNCIONARIO("funcionario");

    @Getter
    private final String valor;

    TipoUsuario(String valor) {
        this.valor = valor;
    }
}

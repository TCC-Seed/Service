package com.tccseed.tcc_seed.domain.enums;

public enum TipoUsuario {

    ESTUDANTE("estudante"),
    FUNCIONARIO("funcionario");

    private final String valor;

    TipoUsuario(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
package com.tccseed.tcc_seed.domain.enums;

public enum Genero {

    FEMININO("feminino"),
    MASCULINO("masculino"),
    NAO_INFORMAR("nao_informar"),
    OUTRO("outro");

    private final String valor;

    Genero(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}

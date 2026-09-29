package com.tccseed.tcc_seed.domain.enums;

import lombok.Getter;

public enum Genero {

    FEMININO("feminino"),
    MASCULINO("masculino"),
    NAO_INFORMAR("nao_informar"),
    OUTRO("outro");

    @Getter
    private final String valor;

    Genero(String valor) {
        this.valor = valor;
    }
}

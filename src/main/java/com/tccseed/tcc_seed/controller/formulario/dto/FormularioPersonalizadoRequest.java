package com.tccseed.tcc_seed.controller.formulario.dto;

import com.tccseed.tcc_seed.domain.entity.FormularioPersonalizado;

public record FormularioPersonalizadoRequest(String titulo) {
  public FormularioPersonalizado toEntity() {
    FormularioPersonalizado entidade = new FormularioPersonalizado();
    entidade.setTitulo(titulo);
    return entidade;
  }
}

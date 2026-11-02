package com.tccseed.tcc_seed.controller.formulariopersonalizado.dto;

import org.springframework.data.domain.Page;

public record RespostasFuncionarioResponse(
    FormularioResponse formulario, Page<RespostaResponse> respostas) {}

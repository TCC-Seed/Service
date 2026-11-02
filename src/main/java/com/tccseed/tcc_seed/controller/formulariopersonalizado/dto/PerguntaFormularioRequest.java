package com.tccseed.tcc_seed.controller.formulariopersonalizado.dto;

import jakarta.validation.constraints.NotBlank;

public record PerguntaFormularioRequest(@NotBlank String enunciado) {}

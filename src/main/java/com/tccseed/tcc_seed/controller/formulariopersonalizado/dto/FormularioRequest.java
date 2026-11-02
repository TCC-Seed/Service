package com.tccseed.tcc_seed.controller.formulariopersonalizado.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record FormularioRequest(
    @NotBlank @Size(max = 100) String titulo,
    @Valid @NotEmpty List<PerguntaFormularioRequest> perguntas) {}

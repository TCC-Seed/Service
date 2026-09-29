package com.tccseed.tcc_seed.controller.ies.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record IesRequest(
        @Schema(description = "Obrigatório. Nome da instituição, com até 200 caracteres.", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 200) String nome,
        @Schema(description = "Opcional. Região administrativa da instituição, com até 100 caracteres.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @Size(max = 100) String regiaoAdministrativa
) {}

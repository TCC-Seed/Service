package com.tccseed.tcc_seed.controller.funcionario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ContaExclusaoRequest(
        @NotNull @Schema(accessMode = Schema.AccessMode.WRITE_ONLY) String senhaAtual
) {
}

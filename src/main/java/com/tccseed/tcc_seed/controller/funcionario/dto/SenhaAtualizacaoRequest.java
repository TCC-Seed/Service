package com.tccseed.tcc_seed.controller.funcionario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record SenhaAtualizacaoRequest(
        @NotNull @Schema(accessMode = Schema.AccessMode.WRITE_ONLY) String senhaAtual,
        @NotNull @Schema(accessMode = Schema.AccessMode.WRITE_ONLY) String novaSenha
) {
}

package com.tccseed.tcc_seed.controller.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @Schema(description = "Obrigatório. Nome de usuário usado no login, único e sensível a maiúsculas/minúsculas. Espaços nas extremidades são removidos.",
                example = "joao", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 50) String username,
        @Schema(description = "Obrigatória. Senha do usuário, sem alteração de espaços ou letras. O campo não pode ser nulo; a regra atual não exige tamanho mínimo.",
                requiredMode = Schema.RequiredMode.REQUIRED, accessMode = Schema.AccessMode.WRITE_ONLY, example = "Exemplo@2026")
        @NotNull String senha
) {
}

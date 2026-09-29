package com.tccseed.tcc_seed.controller.auth.dto;

import com.tccseed.tcc_seed.domain.enums.TipoUsuario;
import io.swagger.v3.oas.annotations.media.Schema;

public record UsuarioResponse(
        @Schema(description = "Sempre retornado. Identificador do usuário cadastrado.",
                requiredMode = Schema.RequiredMode.REQUIRED, accessMode = Schema.AccessMode.READ_ONLY, example = "42")
        Long id,
        @Schema(description = "Sempre retornado. E-mail do cadastro; não é usado para login.",
                requiredMode = Schema.RequiredMode.REQUIRED, accessMode = Schema.AccessMode.READ_ONLY, example = "ana.silva@example.com")
        String email,
        @Schema(description = "Sempre retornado. Nome de usuário único usado para login.",
                requiredMode = Schema.RequiredMode.REQUIRED, accessMode = Schema.AccessMode.READ_ONLY, example = "ana.silva")
        String username,
        @Schema(description = "Sempre retornado. Tipo do usuário: ESTUDANTE ou FUNCIONARIO.",
                requiredMode = Schema.RequiredMode.REQUIRED, accessMode = Schema.AccessMode.READ_ONLY, example = "ESTUDANTE")
        TipoUsuario tipo
) {
}

package com.tccseed.tcc_seed.controller.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record EstudanteCadastroRequest(
        @Schema(description = "Obrigatório. E-mail único do cadastro, em formato válido, com até 254 caracteres. Não é usado para login.",
                requiredMode = Schema.RequiredMode.REQUIRED, example = "ana.silva@example.com")
        @NotBlank @Email @Size(max = 254) String email,
        @Schema(description = "Obrigatório. Nome de usuário único para login, com até 50 caracteres, sensível a maiúsculas/minúsculas. Espaços nas extremidades são removidos.",
                requiredMode = Schema.RequiredMode.REQUIRED, example = "ana.silva")
        @NotBlank @Size(max = 50) String username,
        @Schema(description = "Obrigatória. Senha de acesso, armazenada como hash. Não pode ser nula; a regra atual não exige tamanho mínimo.",
                requiredMode = Schema.RequiredMode.REQUIRED, accessMode = Schema.AccessMode.WRITE_ONLY, example = "Exemplo@2026")
        @NotNull String senha,
        @Schema(description = "Obrigatório. Nome completo da pessoa, com até 200 caracteres.",
                requiredMode = Schema.RequiredMode.REQUIRED, example = "Ana Silva")
        @NotBlank @Size(max = 200) String nome,
        @Schema(description = "Obrigatória. Matrícula acadêmica única do estudante, com até 30 caracteres.",
                requiredMode = Schema.RequiredMode.REQUIRED, example = "2026001234")
        @NotBlank @Size(max = 30) String matricula,
        @Schema(description = "Obrigatória. Data de nascimento do estudante no formato AAAA-MM-DD.",
                requiredMode = Schema.RequiredMode.REQUIRED, example = "2001-05-14")
        @NotNull LocalDate nascimento,
        @Schema(description = "Obrigatório. País de origem do estudante, com até 50 caracteres.",
                requiredMode = Schema.RequiredMode.REQUIRED, example = "Brasil")
        @NotBlank @Size(max = 50) String paisOrigem,
        @Schema(description = "Obrigatório. ID positivo da única IES à qual o estudante pertence. "
                + "Selecione uma IES existente em GET /IES, disponível sem login. "
                + "Não exige chave privada e não permite criar uma IES neste cadastro.",
                requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull @Positive Long iesId
) {
}

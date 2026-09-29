package com.tccseed.tcc_seed.controller.auth.dto;

import com.tccseed.tcc_seed.controller.ies.dto.IesRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FuncionarioCadastroRequest(
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
        @Schema(description = "Obrigatória. Formação profissional do funcionário, com até 200 caracteres.",
                requiredMode = Schema.RequiredMode.REQUIRED, example = "Psicologia")
        @NotBlank @Size(max = 200) String formacao,
        @Schema(description = "Condicional. Identificador positivo da única IES em que o funcionário trabalhará; obrigatório junto com chaveIes quando novaIes não for informada. Deve ser omitido ao criar uma nova IES.",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1")
        @Positive Long iesId,
        @Schema(description = "Condicional. Chave privada compartilhada por um funcionário da IES, com até 128 caracteres. Obrigatória junto com iesId; deve ser omitida quando novaIes for informada.",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, accessMode = Schema.AccessMode.WRITE_ONLY, example = "exemplo_de_chave_privada_compartilhada_da_ies")
        @Size(max = 128) String chaveIes,
        @Schema(description = "Condicional. Dados para criar uma IES e vincular seu primeiro funcionário no mesmo cadastro. Este é o único fluxo de criação de IES. Obrigatórios quando iesId e chaveIes não forem informados. Informe exatamente uma das opções: novaIes ou iesId com chaveIes.",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "{\"nome\": \"Universidade Exemplo\", \"regiaoAdministrativa\": \"Brasília\"}")
        @Valid IesRequest novaIes
) {
    @AssertTrue(message = "Informe iesId e chaveIes, ou apenas novaIes.")
    @Schema(hidden = true)
    public boolean isVinculoValido() {
        return novaIes != null
                ? iesId == null && chaveIes == null
                : iesId != null && iesId > 0 && chaveIes != null && !chaveIes.isBlank();
    }
}

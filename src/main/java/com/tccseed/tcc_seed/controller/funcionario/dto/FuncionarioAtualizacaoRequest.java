package com.tccseed.tcc_seed.controller.funcionario.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Edição parcial: campos ausentes ou nulos permanecem inalterados. IES, id, tipo, token e senha não são aceitos.")
public record FuncionarioAtualizacaoRequest(
        @Email @Size(max = 254) @Pattern(regexp = "(?s).*\\S.*") String email,
        @Size(max = 50) @Pattern(regexp = "(?s).*\\S.*") String username,
        @Size(max = 200) @Pattern(regexp = "(?s).*\\S.*") String nome,
        @Size(max = 200) @Pattern(regexp = "(?s).*\\S.*") String formacao
) {
    @JsonAnySetter
    public void rejeitarCampoDesconhecido(String campo, Object valor) {
        throw new IllegalArgumentException("Campo não editável: " + campo);
    }
}

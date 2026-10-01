package com.tccseed.tcc_seed.controller.estudante.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.tccseed.tcc_seed.domain.enums.Genero;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Edição parcial: campos ausentes ou nulos permanecem inalterados. IES, id, tipo, token e senha não são aceitos.")
public record EstudanteAtualizacaoRequest(
        @Email @Size(max = 254) @Pattern(regexp = "(?s).*\\S.*") String email,
        @Size(max = 50) @Pattern(regexp = "(?s).*\\S.*") String username,
        @Size(max = 200) @Pattern(regexp = "(?s).*\\S.*") String nome,
        @Size(max = 30) @Pattern(regexp = "(?s).*\\S.*") String matricula,
        LocalDate nascimento,
        Genero genero,
        @Size(max = 50) @Pattern(regexp = "(?s).*\\S.*") String paisOrigem
) {
    @JsonAnySetter
    public void rejeitarCampoDesconhecido(String campo, Object valor) {
        throw new IllegalArgumentException("Campo não editável: " + campo);
    }
}

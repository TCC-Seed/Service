package com.tccseed.tcc_seed.controller.formulariobase.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FormularioBaseRequest(
    @NotNull Boolean secaoAutoconhecimento,
    @NotNull Boolean secaoHidratacao,
    @NotNull Boolean secaoExercicios,
    @NotNull Boolean secaoAlimentacao,
    @NotNull Boolean secaoAcademia,
    @NotNull Boolean secaoMental,
    @NotNull Boolean secaoTrabalho,
    @NotNull Boolean secaoSocial,
    @NotNull Boolean secaoSono,
    @NotNull Boolean secaoLazer,
    @NotNull Boolean secaoCultura,
    @NotNull Boolean secaoFinanceiro,
    @NotNull Boolean secaoAmoroso,
    @NotNull Boolean secaoAcess,
    @Size(max = 5000, message = "A anotação deve ter no máximo 5000 caracteres.")
        @Schema(description = "Anotação opcional", maxLength = 5000)
        String anotacao) {}

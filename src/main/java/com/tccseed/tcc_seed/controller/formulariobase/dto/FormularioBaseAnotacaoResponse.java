package com.tccseed.tcc_seed.controller.formulariobase.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.ALWAYS)
@Schema(
    name = "FormularioBaseAnotacao",
    description = "Resposta sem dados pessoais do estudante.",
    example =
        "{\"id\":130,\"token\":\"10000000-0000-4000-8000-000000000001\",\"dataPreenchimento\":\"2026-11-01\",\"anotacao\":\"Dormi mal esta semana\"}")
public record FormularioBaseAnotacaoResponse(
    Long id, UUID token, LocalDate dataPreenchimento, String anotacao) {}

package com.tccseed.tcc_seed.controller.publicacao.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record PublicacaoRequest(
    String titulo,
    String descricao,
    LocalDate dataPublicacao,
    @NotNull List<String> categorias) {}

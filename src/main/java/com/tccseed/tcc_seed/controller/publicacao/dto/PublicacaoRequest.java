package com.tccseed.tcc_seed.controller.publicacao.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("PMD.ReturnEmptyCollectionRatherThanNull")
public record PublicacaoRequest(
    String titulo, String descricao, LocalDate dataPublicacao, @NotNull List<String> categorias) {
  public PublicacaoRequest {
    categorias =
        categorias == null ? null : Collections.unmodifiableList(new ArrayList<>(categorias));
  }
}

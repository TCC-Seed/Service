package com.tccseed.tcc_seed.controller.publicacao.dto;

import com.tccseed.tcc_seed.domain.entity.Publicacao;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public record PublicacaoResponse(
    Long id, String titulo, String descricao, LocalDate dataPublicacao, List<String> categorias) {

  public PublicacaoResponse {
    categorias = categorias == null ? null : new ArrayList<>(categorias);
  }

  public List<String> categorias() {
    return categorias == null ? null : new ArrayList<>(categorias);
  }

  public static PublicacaoResponse from(Publicacao publicacao) {
    return new PublicacaoResponse(
        publicacao.getId(),
        publicacao.getTitulo(),
        publicacao.getDescricao(),
        publicacao.getDataPublicacao(),
        publicacao.getCategorias().stream().sorted().toList());
  }
}

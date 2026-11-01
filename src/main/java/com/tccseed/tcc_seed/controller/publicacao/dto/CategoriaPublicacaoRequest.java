package com.tccseed.tcc_seed.controller.publicacao.dto;

import com.tccseed.tcc_seed.domain.entity.CategoriaPublicacao;
import com.tccseed.tcc_seed.domain.entity.Publicacao;
import com.tccseed.tcc_seed.domain.entity.id.CategoriaPublicacaoId;

public record CategoriaPublicacaoRequest(Long idPublicacao, String categoria) {
    public CategoriaPublicacao toEntity() {
        CategoriaPublicacao entidade = new CategoriaPublicacao();
        CategoriaPublicacaoId id = new CategoriaPublicacaoId();
        id.setIdPublicacao(idPublicacao);
        id.setCategoria(categoria);
        entidade.setId(id);
        Publicacao publicacao = new Publicacao();
        publicacao.setId(idPublicacao);
        entidade.setPublicacao(publicacao);
        return entidade;
    }
}

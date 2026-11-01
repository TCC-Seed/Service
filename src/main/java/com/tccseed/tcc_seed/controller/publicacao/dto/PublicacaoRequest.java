package com.tccseed.tcc_seed.controller.publicacao.dto;

import com.tccseed.tcc_seed.domain.entity.Funcionario;
import com.tccseed.tcc_seed.domain.entity.Publicacao;
import java.time.LocalDate;

public record PublicacaoRequest(
    Long idFuncionario, String titulo, String descricao, LocalDate dataPublicacao) {
  public Publicacao toEntity() {
    Publicacao entidade = new Publicacao();
    Funcionario funcionario = new Funcionario();
    funcionario.setId(idFuncionario);
    entidade.setFuncionario(funcionario);
    entidade.setTitulo(titulo);
    entidade.setDescricao(descricao);
    entidade.setDataPublicacao(dataPublicacao);
    return entidade;
  }
}

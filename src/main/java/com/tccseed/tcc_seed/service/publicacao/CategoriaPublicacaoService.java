package com.tccseed.tcc_seed.service.publicacao;

import com.tccseed.tcc_seed.domain.entity.CategoriaPublicacao;
import com.tccseed.tcc_seed.domain.entity.id.CategoriaPublicacaoId;
import com.tccseed.tcc_seed.repository.CategoriaPublicacaoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaPublicacaoService {
  private final CategoriaPublicacaoRepository repository;

  public CategoriaPublicacao criar(CategoriaPublicacao entidade) {
    return repository.save(entidade);
  }

  @Transactional(readOnly = true)
  public List<CategoriaPublicacao> listar() {
    return repository.findAll();
  }

  @Transactional(readOnly = true)
  public CategoriaPublicacao buscarPorId(CategoriaPublicacaoId id) {
    return repository
        .findById(id)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "CategoriaPublicacao não encontrado(a)."));
  }

  public CategoriaPublicacao atualizar(CategoriaPublicacaoId id, CategoriaPublicacao entidade) {
    if (!repository.existsById(id)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "CategoriaPublicacao não encontrado(a).");
    }
    entidade.setId(id);
    return repository.save(entidade);
  }

  public void excluir(CategoriaPublicacaoId id) {
    if (!repository.existsById(id)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "CategoriaPublicacao não encontrado(a).");
    }
    repository.deleteById(id);
  }
}

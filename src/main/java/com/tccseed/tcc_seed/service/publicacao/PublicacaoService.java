package com.tccseed.tcc_seed.service.publicacao;

import com.tccseed.tcc_seed.domain.entity.Publicacao;
import com.tccseed.tcc_seed.repository.PublicacaoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class PublicacaoService {
  private final PublicacaoRepository repository;

  public Publicacao criar(Publicacao entidade) {
    return repository.save(entidade);
  }

  @Transactional(readOnly = true)
  public List<Publicacao> listar() {
    return repository.findAll();
  }

  @Transactional(readOnly = true)
  public Publicacao buscarPorId(Long id) {
    return repository
        .findById(id)
        .orElseThrow(
            () ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicacao não encontrado(a)."));
  }

  public Publicacao atualizar(Long id, Publicacao entidade) {
    if (!repository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicacao não encontrado(a).");
    }
    entidade.setId(id);
    return repository.save(entidade);
  }

  public void excluir(Long id) {
    if (!repository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicacao não encontrado(a).");
    }
    repository.deleteById(id);
  }
}

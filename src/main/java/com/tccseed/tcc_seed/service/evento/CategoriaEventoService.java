package com.tccseed.tcc_seed.service.evento;

import com.tccseed.tcc_seed.domain.entity.CategoriaEvento;
import com.tccseed.tcc_seed.domain.entity.id.CategoriaEventoId;
import com.tccseed.tcc_seed.repository.CategoriaEventoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaEventoService {
  private final CategoriaEventoRepository repository;

  public CategoriaEvento criar(CategoriaEvento entidade) {
    return repository.save(entidade);
  }

  @Transactional(readOnly = true)
  public List<CategoriaEvento> listar() {
    return repository.findAll();
  }

  @Transactional(readOnly = true)
  public CategoriaEvento buscarPorId(CategoriaEventoId id) {
    return repository
        .findById(id)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "CategoriaEvento não encontrado(a)."));
  }

  public CategoriaEvento atualizar(CategoriaEventoId id, CategoriaEvento entidade) {
    if (!repository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CategoriaEvento não encontrado(a).");
    }
    entidade.setId(id);
    return repository.save(entidade);
  }

  public void excluir(CategoriaEventoId id) {
    if (!repository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CategoriaEvento não encontrado(a).");
    }
    repository.deleteById(id);
  }
}

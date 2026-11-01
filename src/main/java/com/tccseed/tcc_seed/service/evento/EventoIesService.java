package com.tccseed.tcc_seed.service.evento;

import com.tccseed.tcc_seed.domain.entity.EventoIes;
import com.tccseed.tcc_seed.domain.entity.id.EventoIesId;
import com.tccseed.tcc_seed.repository.EventoIesRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class EventoIesService {
  private final EventoIesRepository repository;

  public EventoIes criar(EventoIes entidade) {
    return repository.save(entidade);
  }

  @Transactional(readOnly = true)
  public List<EventoIes> listar() {
    return repository.findAll();
  }

  @Transactional(readOnly = true)
  public EventoIes buscarPorId(EventoIesId id) {
    return repository
        .findById(id)
        .orElseThrow(
            () ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "EventoIes não encontrado(a)."));
  }

  public EventoIes atualizar(EventoIesId id, EventoIes entidade) {
    if (!repository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "EventoIes não encontrado(a).");
    }
    entidade.setId(id);
    return repository.save(entidade);
  }

  public void excluir(EventoIesId id) {
    if (!repository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "EventoIes não encontrado(a).");
    }
    repository.deleteById(id);
  }
}

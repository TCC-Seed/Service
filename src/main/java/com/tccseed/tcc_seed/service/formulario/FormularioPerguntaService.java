package com.tccseed.tcc_seed.service.formulario;

import com.tccseed.tcc_seed.domain.entity.FormularioPergunta;
import com.tccseed.tcc_seed.domain.entity.id.FormularioPerguntaId;
import com.tccseed.tcc_seed.repository.FormularioPerguntaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class FormularioPerguntaService {
  private final FormularioPerguntaRepository repository;

  public FormularioPergunta criar(FormularioPergunta entidade) {
    return repository.save(entidade);
  }

  @Transactional(readOnly = true)
  public List<FormularioPergunta> listar() {
    return repository.findAll();
  }

  @Transactional(readOnly = true)
  public FormularioPergunta buscarPorId(FormularioPerguntaId id) {
    return repository
        .findById(id)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "FormularioPergunta não encontrado(a)."));
  }

  public FormularioPergunta atualizar(FormularioPerguntaId id, FormularioPergunta entidade) {
    if (!repository.existsById(id)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "FormularioPergunta não encontrado(a).");
    }
    entidade.setId(id);
    return repository.save(entidade);
  }

  public void excluir(FormularioPerguntaId id) {
    if (!repository.existsById(id)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "FormularioPergunta não encontrado(a).");
    }
    repository.deleteById(id);
  }
}

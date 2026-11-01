package com.tccseed.tcc_seed.service.formulario;

import com.tccseed.tcc_seed.domain.entity.FormularioPersonalizado;
import com.tccseed.tcc_seed.repository.FormularioPersonalizadoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class FormularioPersonalizadoService {
  private final FormularioPersonalizadoRepository repository;

  public FormularioPersonalizado criar(FormularioPersonalizado entidade) {
    return repository.save(entidade);
  }

  @Transactional(readOnly = true)
  public List<FormularioPersonalizado> listar() {
    return repository.findAll();
  }

  @Transactional(readOnly = true)
  public FormularioPersonalizado buscarPorId(Long id) {
    return repository
        .findById(id)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "FormularioPersonalizado não encontrado(a)."));
  }

  public FormularioPersonalizado atualizar(Long id, FormularioPersonalizado entidade) {
    if (!repository.existsById(id)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "FormularioPersonalizado não encontrado(a).");
    }
    entidade.setId(id);
    return repository.save(entidade);
  }

  public void excluir(Long id) {
    if (!repository.existsById(id)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "FormularioPersonalizado não encontrado(a).");
    }
    repository.deleteById(id);
  }
}

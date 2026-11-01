package com.tccseed.tcc_seed.service.formulario;

import com.tccseed.tcc_seed.domain.entity.RespostaFormularioPersonalizado;
import com.tccseed.tcc_seed.repository.RespostaFormularioPersonalizadoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class RespostaFormularioPersonalizadoService {
  private final RespostaFormularioPersonalizadoRepository repository;

  public RespostaFormularioPersonalizado criar(RespostaFormularioPersonalizado entidade) {
    return repository.save(entidade);
  }

  @Transactional(readOnly = true)
  public List<RespostaFormularioPersonalizado> listar() {
    return repository.findAll();
  }

  @Transactional(readOnly = true)
  public RespostaFormularioPersonalizado buscarPorId(Long id) {
    return repository
        .findById(id)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "RespostaFormularioPersonalizado não encontrado(a)."));
  }

  public RespostaFormularioPersonalizado atualizar(
      Long id, RespostaFormularioPersonalizado entidade) {
    if (!repository.existsById(id)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "RespostaFormularioPersonalizado não encontrado(a).");
    }
    entidade.setId(id);
    return repository.save(entidade);
  }

  public void excluir(Long id) {
    if (!repository.existsById(id)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "RespostaFormularioPersonalizado não encontrado(a).");
    }
    repository.deleteById(id);
  }
}

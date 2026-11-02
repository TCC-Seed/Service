package com.tccseed.tcc_seed.service.formulariobase;

import com.tccseed.tcc_seed.controller.formulariobase.dto.FormularioBaseEstudanteResponse;
import com.tccseed.tcc_seed.controller.formulariobase.dto.FormularioBasePageResponse;
import com.tccseed.tcc_seed.controller.formulariobase.dto.FormularioBaseRequest;
import com.tccseed.tcc_seed.domain.entity.Estudante;
import com.tccseed.tcc_seed.domain.entity.FormularioBase;
import com.tccseed.tcc_seed.repository.EstudanteRepository;
import com.tccseed.tcc_seed.repository.FormularioBaseRepository;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class FormularioBaseEstudanteService {
  private final EstudanteRepository estudanteRepository;
  private final FormularioBaseRepository formularioRepository;
  private final FormularioBaseConsultaService consultaService;
  private final FormularioBaseResponseMapper mapper;

  @Transactional(readOnly = true)
  public FormularioBasePageResponse<FormularioBaseEstudanteResponse> listar(
      UUID tokenEstudante,
      LocalDate dataInicio,
      LocalDate dataFim,
      String ordem,
      int page,
      int size,
      String campos) {
    Long estudanteId = idDoEstudante(tokenEstudante);
    return consultaService.listarEstudante(
        estudanteId, dataInicio, dataFim, ordem, page, size, campos);
  }

  @Transactional(readOnly = true)
  public FormularioBaseEstudanteResponse buscar(UUID tokenEstudante, Long id) {
    Estudante estudante = estudante(tokenEstudante);
    FormularioBase formulario = formulario(id, estudante.getId());
    return mapper.estudanteDetalhe(formulario);
  }

  public FormularioBaseEstudanteResponse criar(UUID tokenEstudante, FormularioBaseRequest request) {
    Estudante estudante = estudante(tokenEstudante);
    FormularioBase formulario = new FormularioBase();
    formulario.setEstudante(estudante);
    formulario.setDataPreenchimento(LocalDate.now());
    mapper.atualizar(formulario, request);
    return mapper.estudanteDetalhe(formularioRepository.save(formulario));
  }

  public FormularioBaseEstudanteResponse atualizar(
      UUID tokenEstudante, Long id, FormularioBaseRequest request) {
    Estudante estudante = estudante(tokenEstudante);
    FormularioBase formulario = formulario(id, estudante.getId());
    mapper.atualizar(formulario, request);
    return mapper.estudanteDetalhe(formularioRepository.save(formulario));
  }

  public void excluir(UUID tokenEstudante, Long id) {
    Estudante estudante = estudante(tokenEstudante);
    FormularioBase formulario = formulario(id, estudante.getId());
    formularioRepository.delete(formulario);
  }

  private Long idDoEstudante(UUID token) {
    return estudanteRepository
        .findIdByToken(token)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudante não encontrado."));
  }

  private Estudante estudante(UUID token) {
    return estudanteRepository
        .findByToken(token)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudante não encontrado."));
  }

  private FormularioBase formulario(Long id, Long estudanteId) {
    return formularioRepository
        .findOwnedById(id, estudanteId)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Resposta do formulário base não encontrada."));
  }
}

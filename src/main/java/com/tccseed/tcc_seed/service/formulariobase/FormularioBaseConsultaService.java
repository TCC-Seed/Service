package com.tccseed.tcc_seed.service.formulariobase;

import com.tccseed.tcc_seed.controller.formulariobase.dto.FormularioBaseAnotacaoResponse;
import com.tccseed.tcc_seed.controller.formulariobase.dto.FormularioBaseEstudanteResponse;
import com.tccseed.tcc_seed.controller.formulariobase.dto.FormularioBaseFuncionarioResponse;
import com.tccseed.tcc_seed.controller.formulariobase.dto.FormularioBasePageResponse;
import com.tccseed.tcc_seed.repository.FormularioBaseListProjection;
import com.tccseed.tcc_seed.repository.FormularioBaseRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FormularioBaseConsultaService {
  private final FormularioBaseRepository repository;
  private final FormularioBaseResponseMapper mapper;

  public FormularioBasePageResponse<FormularioBaseFuncionarioResponse> listarFuncionarioBooleanos(
      Long iesId,
      LocalDate dataInicio,
      LocalDate dataFim,
      String ordem,
      int page,
      int size,
      String campos) {
    List<FormularioBaseCampo> selecionados = parseCampos(campos);
    Page<FormularioBaseListProjection> resultado =
        repository.findPageByIes(
            iesId, dataInicio, dataFim, pageable(dataInicio, dataFim, ordem, page, size));
    return envelope(resultado, p -> mapper.funcionarioBooleanos(p, selecionados));
  }

  public FormularioBasePageResponse<FormularioBaseAnotacaoResponse> listarFuncionarioAnotacoes(
      Long iesId, LocalDate dataInicio, LocalDate dataFim, String ordem, int page, int size) {
    Page<FormularioBaseListProjection> resultado =
        repository.findPageByIes(
            iesId, dataInicio, dataFim, pageable(dataInicio, dataFim, ordem, page, size));
    return envelope(resultado, mapper::funcionarioAnotacao);
  }

  public FormularioBasePageResponse<FormularioBaseEstudanteResponse> listarEstudante(
      Long estudanteId,
      LocalDate dataInicio,
      LocalDate dataFim,
      String ordem,
      int page,
      int size,
      String campos) {
    List<FormularioBaseCampo> selecionados = parseCampos(campos);
    Page<FormularioBaseListProjection> resultado =
        repository.findPageByEstudante(
            estudanteId, dataInicio, dataFim, pageable(dataInicio, dataFim, ordem, page, size));
    return envelope(resultado, p -> mapper.estudante(p, selecionados));
  }

  private <S, T> FormularioBasePageResponse<T> envelope(
      Page<S> resultado, java.util.function.Function<S, T> conversor) {
    return new FormularioBasePageResponse<>(
        resultado.getContent().stream().map(conversor).toList(),
        resultado.getNumber(),
        resultado.getSize(),
        resultado.getTotalElements(),
        resultado.getTotalPages());
  }

  private PageRequest pageable(
      LocalDate dataInicio, LocalDate dataFim, String ordem, int page, int size) {
    if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
      throw badRequest("dataInicio não pode ser posterior a dataFim.");
    }
    if (page < 0 || size < 1 || size > 100) {
      throw badRequest("Paginação inválida: page deve ser >= 0 e size deve estar entre 1 e 100.");
    }
    String direcao =
        ordem == null || ordem.isBlank() ? "desc" : ordem.trim().toLowerCase(Locale.ROOT);
    Sort.Direction sortDirection;
    try {
      sortDirection = Sort.Direction.fromString(direcao);
    } catch (IllegalArgumentException exception) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "ordem deve ser 'asc' ou 'desc'.", exception);
    }
    return PageRequest.of(page, size, Sort.by(sortDirection, "dataPreenchimento", "id"));
  }

  private List<FormularioBaseCampo> parseCampos(String campos) {
    try {
      return FormularioBaseCampo.parse(campos);
    } catch (IllegalArgumentException exception) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
    }
  }

  private ResponseStatusException badRequest(String mensagem) {
    return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
  }
}

package com.tccseed.tcc_seed.service.formulariobase;

import com.tccseed.tcc_seed.controller.formulariobase.dto.FormularioBaseAnotacaoResponse;
import com.tccseed.tcc_seed.controller.formulariobase.dto.FormularioBaseFuncionarioResponse;
import com.tccseed.tcc_seed.controller.formulariobase.dto.FormularioBasePageResponse;
import com.tccseed.tcc_seed.repository.FuncionarioRepository;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FormularioBaseFuncionarioService {
  private final FuncionarioRepository funcionarioRepository;
  private final FormularioBaseConsultaService consultaService;

  public FormularioBasePageResponse<FormularioBaseFuncionarioResponse> listarRespostas(
      UUID tokenFuncionario,
      LocalDate dataInicio,
      LocalDate dataFim,
      String ordem,
      int page,
      int size,
      String campos) {
    Long iesId = iesDoFuncionario(tokenFuncionario);
    return consultaService.listarFuncionarioBooleanos(
        iesId, dataInicio, dataFim, ordem, page, size, campos);
  }

  public FormularioBasePageResponse<FormularioBaseAnotacaoResponse> listarAnotacoes(
      UUID tokenFuncionario,
      LocalDate dataInicio,
      LocalDate dataFim,
      String ordem,
      int page,
      int size) {
    Long iesId = iesDoFuncionario(tokenFuncionario);
    return consultaService.listarFuncionarioAnotacoes(
        iesId, dataInicio, dataFim, ordem, page, size);
  }

  private Long iesDoFuncionario(UUID token) {
    return funcionarioRepository
        .findIesIdByToken(token)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado."));
  }
}

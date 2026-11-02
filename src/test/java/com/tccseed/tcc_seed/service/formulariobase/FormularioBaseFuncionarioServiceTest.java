package com.tccseed.tcc_seed.service.formulariobase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tccseed.tcc_seed.controller.formulariobase.dto.FormularioBaseFuncionarioResponse;
import com.tccseed.tcc_seed.controller.formulariobase.dto.FormularioBasePageResponse;
import com.tccseed.tcc_seed.repository.FuncionarioRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FormularioBaseFuncionarioServiceTest {
  private static final UUID TOKEN = UUID.fromString("20000000-0000-4000-8000-000000000001");

  @Mock private FuncionarioRepository funcionarioRepository;
  @Mock private FormularioBaseConsultaService consultaService;
  private FormularioBaseFuncionarioService service;

  @BeforeEach
  void setUp() {
    service = new FormularioBaseFuncionarioService(funcionarioRepository, consultaService);
  }

  @Test
  void consultaUsaIesObtidaDoFuncionarioAutenticado() {
    var page = new FormularioBasePageResponse<FormularioBaseFuncionarioResponse>(List.of(), 0, 20, 0, 0);
    when(funcionarioRepository.findIesIdByToken(TOKEN)).thenReturn(Optional.of(73L));
    when(consultaService.listarFuncionarioBooleanos(73L, null, null, "desc", 0, 20, "secaoSono"))
        .thenReturn(page);

    assertEquals(
        page,
        service.listarRespostas(TOKEN, null, null, "desc", 0, 20, "secaoSono"));

    verify(consultaService)
        .listarFuncionarioBooleanos(73L, null, null, "desc", 0, 20, "secaoSono");
  }
}

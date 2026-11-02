package com.tccseed.tcc_seed.service.formulariobase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tccseed.tcc_seed.controller.formulariobase.dto.FormularioBaseRequest;
import com.tccseed.tcc_seed.controller.formulariobase.dto.FormularioBaseEstudanteResponse;
import com.tccseed.tcc_seed.domain.entity.Estudante;
import com.tccseed.tcc_seed.domain.entity.FormularioBase;
import com.tccseed.tcc_seed.repository.EstudanteRepository;
import com.tccseed.tcc_seed.repository.FormularioBaseRepository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class FormularioBaseEstudanteServiceTest {
  private static final UUID TOKEN = UUID.fromString("10000000-0000-4000-8000-000000000001");

  @Mock private EstudanteRepository estudanteRepository;
  @Mock private FormularioBaseRepository formularioRepository;
  @Mock private FormularioBaseConsultaService consultaService;
  @Mock private FormularioBaseResponseMapper mapper;
  private FormularioBaseEstudanteService service;

  @BeforeEach
  void setUp() {
    service =
        new FormularioBaseEstudanteService(
            estudanteRepository, formularioRepository, consultaService, mapper);
  }

  @Test
  void listaBuscaOIdDoDonoPeloUuidDaSessao() {
    when(estudanteRepository.findIdByToken(TOKEN)).thenReturn(Optional.of(42L));

    service.listar(TOKEN, null, null, "desc", 0, 20, "secaoSono");

    verify(consultaService).listarEstudante(42L, null, null, "desc", 0, 20, "secaoSono");
  }

  @Test
  void respostaDeOutroEstudanteRetorna404() {
    Estudante estudante = new Estudante();
    estudante.setId(42L);
    when(estudanteRepository.findByToken(TOKEN)).thenReturn(Optional.of(estudante));
    when(formularioRepository.findOwnedById(99L, 42L)).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> service.buscar(TOKEN, 99L));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    verify(formularioRepository).findOwnedById(99L, 42L);
  }

  @Test
  void criarAssociaEstudanteDaSessaoEDataDoServidor() {
    Estudante estudante = new Estudante();
    estudante.setId(42L);
    when(estudanteRepository.findByToken(TOKEN)).thenReturn(Optional.of(estudante));
    when(formularioRepository.save(any(FormularioBase.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(mapper.estudanteDetalhe(any(FormularioBase.class)))
        .thenReturn(new FormularioBaseEstudanteResponse(1L, LocalDate.now(), "nota"));

    service.criar(TOKEN, request());

    ArgumentCaptor<FormularioBase> captor = ArgumentCaptor.forClass(FormularioBase.class);
    verify(formularioRepository).save(captor.capture());
    assertEquals(42L, captor.getValue().getEstudante().getId());
    assertEquals(LocalDate.now(), captor.getValue().getDataPreenchimento());
    assertEquals("nota", captor.getValue().getAnotacao());
    assertTrue(captor.getValue().isSecaoAcess());
  }

  @Test
  void estudanteInexistenteRetorna404SemConsultarResposta() {
    when(estudanteRepository.findByToken(TOKEN)).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> service.buscar(TOKEN, 99L));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    verify(formularioRepository, never()).findOwnedById(any(), any());
  }

  private FormularioBaseRequest request() {
    return new FormularioBaseRequest(
        true, true, false, true, false, true, false, true, false, true, false, true, false, true, "nota");
  }
}

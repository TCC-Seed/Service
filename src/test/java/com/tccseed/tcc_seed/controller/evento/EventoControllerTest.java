package com.tccseed.tcc_seed.controller.evento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tccseed.tcc_seed.controller.evento.dto.EventoAtualizacaoRequest;
import com.tccseed.tcc_seed.controller.evento.dto.EventoCriacaoRequest;
import com.tccseed.tcc_seed.controller.evento.dto.EventoResponse;
import com.tccseed.tcc_seed.service.evento.EventoService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@ExtendWith(MockitoExtension.class)
class EventoControllerTest {
  private static final UUID TOKEN = UUID.randomUUID();

  @Mock private EventoService service;
  @Mock private Authentication authentication;
  private EventoController controller;
  private final Long id = 1L;
  private final EventoCriacaoRequest criacaoRequest =
      new EventoCriacaoRequest(
          LocalDate.now(), null, "https://example.com", "Descrição", List.of("Tecnologia"));
  private final EventoAtualizacaoRequest atualizacaoRequest =
      new EventoAtualizacaoRequest(
          LocalDate.now(), null, "https://example.com", "Descrição", List.of("Tecnologia"), List.of(2L));
  private final EventoResponse response =
      new EventoResponse(
          1L,
          LocalDate.now(),
          null,
          "https://example.com",
          "Descrição",
          List.of("Tecnologia"),
          List.of(2L));

  @BeforeEach
  void preparar() {
    controller = new EventoController(service);
    when(authentication.getName()).thenReturn(TOKEN.toString());
    doReturn(List.of(new SimpleGrantedAuthority("ROLE_FUNCIONARIO")))
        .when(authentication)
        .getAuthorities();
  }

  @Test
  void criarDelegaAoServiceERetorna201() {
    when(service.criar(criacaoRequest, TOKEN, "ROLE_FUNCIONARIO")).thenReturn(response);

    ResponseEntity<EventoResponse> resultado = controller.criar(criacaoRequest, authentication);

    assertEquals(HttpStatus.CREATED, resultado.getStatusCode());
    assertEquals(response, resultado.getBody());
    verify(service).criar(criacaoRequest, TOKEN, "ROLE_FUNCIONARIO");
  }

  @Test
  void listarDelegaAoServiceComCategoriaEUsuario() {
    when(service.listar(TOKEN, "Tecnologia")).thenReturn(List.of(response));

    assertEquals(List.of(response), controller.listar("Tecnologia", authentication));

    verify(service).listar(TOKEN, "Tecnologia");
  }

  @Test
  void buscarPorIdDelegaAoServiceComUsuario() {
    when(service.buscarPorId(id, TOKEN)).thenReturn(response);

    assertEquals(response, controller.buscarPorId(id, authentication));

    verify(service).buscarPorId(id, TOKEN);
  }

  @Test
  void atualizarDelegaAoServiceComUsuarioErole() {
    when(service.atualizar(id, atualizacaoRequest, TOKEN, "ROLE_FUNCIONARIO"))
        .thenReturn(response);

    assertEquals(response, controller.atualizar(id, atualizacaoRequest, authentication));

    verify(service).atualizar(id, atualizacaoRequest, TOKEN, "ROLE_FUNCIONARIO");
  }

  @Test
  void excluirRetorna204EDependeDoService() {
    ResponseEntity<Void> resultado = controller.excluir(id, authentication);

    assertEquals(HttpStatus.NO_CONTENT, resultado.getStatusCode());
    verify(service).excluir(id, TOKEN, "ROLE_FUNCIONARIO");
  }
}

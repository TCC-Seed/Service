package com.tccseed.tcc_seed.controller.publicacao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tccseed.tcc_seed.controller.publicacao.dto.PublicacaoAtualizacaoRequest;
import com.tccseed.tcc_seed.controller.publicacao.dto.PublicacaoRequest;
import com.tccseed.tcc_seed.controller.publicacao.dto.PublicacaoResponse;
import com.tccseed.tcc_seed.service.publicacao.PublicacaoService;
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
class PublicacaoControllerTest {
  private static final UUID TOKEN = UUID.randomUUID();

  @Mock private PublicacaoService service;
  @Mock private Authentication authentication;
  private PublicacaoController controller;
  private final Long id = 1L;
  private final PublicacaoRequest request =
      new PublicacaoRequest("Título", "Descrição", LocalDate.now(), List.of("Evento"));
  private final PublicacaoAtualizacaoRequest atualizacaoRequest =
      new PublicacaoAtualizacaoRequest("Título", "Descrição", LocalDate.now(), List.of("Evento"));
  private final PublicacaoResponse response =
      new PublicacaoResponse(1L, "Título", "Descrição", LocalDate.now(), List.of("Evento"));

  @BeforeEach
  void preparar() {
    controller = new PublicacaoController(service);
    when(authentication.getName()).thenReturn(TOKEN.toString());
    doReturn(List.of(new SimpleGrantedAuthority("ROLE_FUNCIONARIO")))
        .when(authentication)
        .getAuthorities();
  }

  @Test
  void criarDelegaAoServiceERetorna201() {
    when(service.criar(request, TOKEN, "ROLE_FUNCIONARIO")).thenReturn(response);

    ResponseEntity<PublicacaoResponse> resultado = controller.criar(request, authentication);

    assertEquals(HttpStatus.CREATED, resultado.getStatusCode());
    assertEquals(response, resultado.getBody());
    verify(service).criar(request, TOKEN, "ROLE_FUNCIONARIO");
  }

  @Test
  void listarDelegaAoServiceComCategoriaEUsuario() {
    when(service.listar(TOKEN, "Evento")).thenReturn(List.of(response));

    assertEquals(List.of(response), controller.listar("Evento", authentication));

    verify(service).listar(TOKEN, "Evento");
  }

  @Test
  void buscarPorIdDelegaAoServiceComUsuario() {
    when(service.buscarPorId(id, TOKEN)).thenReturn(response);

    assertEquals(response, controller.buscarPorId(id, authentication));

    verify(service).buscarPorId(id, TOKEN);
  }

  @Test
  void atualizarDelegaAoServiceComUsuario() {
    when(service.atualizar(id, atualizacaoRequest, TOKEN)).thenReturn(response);

    assertEquals(response, controller.atualizar(id, atualizacaoRequest, authentication));

    verify(service).atualizar(id, atualizacaoRequest, TOKEN);
  }

  @Test
  void excluirRetorna204EDependeDoService() {
    ResponseEntity<Void> resultado = controller.excluir(id, authentication);

    assertEquals(HttpStatus.NO_CONTENT, resultado.getStatusCode());
    verify(service).excluir(id, TOKEN);
  }
}

package com.tccseed.tcc_seed.service.formulariobase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tccseed.tcc_seed.controller.formulariobase.dto.FormularioBaseFuncionarioResponse;
import com.tccseed.tcc_seed.repository.FormularioBaseListProjection;
import com.tccseed.tcc_seed.repository.FormularioBaseRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class FormularioBaseConsultaServiceTest {
  @Mock private FormularioBaseRepository repository;
  @Mock private FormularioBaseResponseMapper mapper;
  private FormularioBaseConsultaService service;

  @BeforeEach
  void setUp() {
    service = new FormularioBaseConsultaService(repository, mapper);
  }

  @Test
  void consultaDoFuncionarioAplicaIesDatasOrdenacaoEPaginacao() {
    LocalDate inicio = LocalDate.of(2026, 10, 1);
    LocalDate fim = LocalDate.of(2026, 11, 1);
    Pageable pageable = PageRequest.of(2, 10, Sort.by(Sort.Direction.ASC, "dataPreenchimento", "id"));
    when(repository.findPageByIes(7L, inicio, fim, pageable))
        .thenReturn(new PageImpl<FormularioBaseListProjection>(List.of(), pageable, 0));

    var pagina = service.listarFuncionarioBooleanos(7L, inicio, fim, "asc", 2, 10, null);

    assertEquals(2, pagina.page());
    assertEquals(10, pagina.size());
    assertEquals(0, pagina.totalElements());
    verify(repository).findPageByIes(7L, inicio, fim, pageable);
  }

  @Test
  void consultaDoEstudanteUsaSomenteOIdResolvidoDaSessao() {
    Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "dataPreenchimento", "id"));
    when(repository.findPageByEstudante(42L, null, null, pageable))
        .thenReturn(new PageImpl<FormularioBaseListProjection>(List.of(), pageable, 0));

    service.listarEstudante(42L, null, null, null, 0, 20, "secaoSono");

    verify(repository).findPageByEstudante(42L, null, null, pageable);
    verify(repository, never()).findPageByIes(any(), any(), any(), any());
  }

  @Test
  void campoInvalidoRetorna400EListaWhitelist() {
    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class,
            () -> service.listarFuncionarioBooleanos(7L, null, null, null, 0, 20, "senha"));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    org.junit.jupiter.api.Assertions.assertTrue(exception.getReason().contains("secaoSono"));
    verify(repository, never()).findPageByIes(any(), any(), any(), any());
  }

  @Test
  void dataInicioPosteriorADataFimRetorna400() {
    assertThrows(
        ResponseStatusException.class,
        () ->
            service.listarFuncionarioBooleanos(
                7L,
                LocalDate.of(2026, 11, 2),
                LocalDate.of(2026, 11, 1),
                "desc",
                0,
                20,
                null));
    verify(repository, never()).findPageByIes(any(), any(), any(), any());
  }

  @Test
  void valoresDeOrdemEPageSizeInvalidosRetornam400() {
    assertThrows(
        ResponseStatusException.class,
        () -> service.listarFuncionarioBooleanos(7L, null, null, "recentes", 0, 20, null));
    assertThrows(
        ResponseStatusException.class,
        () -> service.listarFuncionarioBooleanos(7L, null, null, "desc", 0, 101, null));
    verify(repository, never()).findPageByIes(any(), any(), any(), any());
  }

  @Test
  void respostaFuncionarioNaoIncluiCamposDeIdentificacaoPessoal() throws Exception {
    FormularioBaseListProjection projection = org.mockito.Mockito.mock(FormularioBaseListProjection.class);
    when(projection.getId()).thenReturn(9L);
    when(projection.getToken()).thenReturn(java.util.UUID.fromString("10000000-0000-4000-8000-000000000001"));
    when(projection.getDataPreenchimento()).thenReturn(LocalDate.of(2026, 11, 1));
    when(projection.getSecaoSono()).thenReturn(true);
    FormularioBaseFuncionarioResponse response =
        new FormularioBaseResponseMapper().funcionarioBooleanos(
            projection, FormularioBaseCampo.parse("secaoSono"));

    String json = new com.fasterxml.jackson.databind.ObjectMapper().findAndRegisterModules().writeValueAsString(response);

    org.junit.jupiter.api.Assertions.assertTrue(json.contains("\"token\""));
    org.junit.jupiter.api.Assertions.assertTrue(json.contains("\"secaoSono\":true"));
    org.junit.jupiter.api.Assertions.assertFalse(json.contains("nome"));
    org.junit.jupiter.api.Assertions.assertFalse(json.contains("email"));
    org.junit.jupiter.api.Assertions.assertFalse(json.contains("matricula"));
    org.junit.jupiter.api.Assertions.assertFalse(json.contains("secaoHidratacao"));
  }

  @Test
  void semCamposSelecionadosIncluiTodosOsQuatorzeBooleanos() {
    assertEquals(14, FormularioBaseCampo.parse(null).size());
  }

  @Test
  void anotacaoNulaContinuaPresenteNaRespostaDoEstudante() throws Exception {
    String json =
        new com.fasterxml.jackson.databind.ObjectMapper()
            .findAndRegisterModules()
            .writeValueAsString(
                new com.tccseed.tcc_seed.controller.formulariobase.dto.FormularioBaseEstudanteResponse(
                    1L, LocalDate.of(2026, 11, 1), null));

    org.junit.jupiter.api.Assertions.assertTrue(json.contains("\"anotacao\":null"));
  }
}

package com.tccseed.tcc_seed.service.publicacao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tccseed.tcc_seed.controller.publicacao.dto.PublicacaoAtualizacaoRequest;
import com.tccseed.tcc_seed.controller.publicacao.dto.PublicacaoRequest;
import com.tccseed.tcc_seed.repository.EstudanteRepository;
import com.tccseed.tcc_seed.repository.FuncionarioRepository;
import com.tccseed.tcc_seed.repository.PublicacaoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class PublicacaoServiceTest {
  private static final UUID TOKEN = UUID.randomUUID();

  @Mock private PublicacaoRepository publicacaoRepository;
  @Mock private FuncionarioRepository funcionarioRepository;
  @Mock private EstudanteRepository estudanteRepository;
  private PublicacaoService service;
  private final Long id = 1L;
  private final PublicacaoRequest request =
      new PublicacaoRequest("Título", "Descrição", LocalDate.now(), List.of("Evento"));
  private final PublicacaoAtualizacaoRequest atualizacaoRequest =
      new PublicacaoAtualizacaoRequest("Título", "Descrição", LocalDate.now(), List.of("Evento"));

  @BeforeEach
  void preparar() {
    service =
        new PublicacaoService(publicacaoRepository, funcionarioRepository, estudanteRepository);
  }

  @Test
  void criarRejeitaRoleDeEstudante() {
    ResponseStatusException erro =
        assertThrows(
            ResponseStatusException.class,
            () -> service.criar(request, TOKEN, "ROLE_ESTUDANTE"));

    assertEquals(HttpStatus.FORBIDDEN, erro.getStatusCode());
    verify(funcionarioRepository, never()).findByToken(TOKEN);
    verify(publicacaoRepository, never()).save(org.mockito.ArgumentMatchers.any());
  }

  @Test
  void listarFiltraPelaIesEPorCategoria() {
    when(funcionarioRepository.findIesIdByToken(TOKEN)).thenReturn(Optional.of(7L));
    when(publicacaoRepository.findVisiveisPorIesECategoria(7L, "Evento"))
        .thenReturn(List.of());

    assertEquals(List.of(), service.listar(TOKEN, "Evento"));

    verify(publicacaoRepository).findVisiveisPorIesECategoria(7L, "Evento");
  }

  @Test
  void estudanteNaoPodeEditarPublicacao() {
    when(funcionarioRepository.findByToken(TOKEN)).thenReturn(Optional.empty());

    ResponseStatusException erro =
            assertThrows(
                ResponseStatusException.class,
                () -> service.atualizar(id, atualizacaoRequest, TOKEN));

    assertEquals(HttpStatus.FORBIDDEN, erro.getStatusCode());
    verify(publicacaoRepository, never()).findComFuncionarioECategoriasPorId(id);
  }
}

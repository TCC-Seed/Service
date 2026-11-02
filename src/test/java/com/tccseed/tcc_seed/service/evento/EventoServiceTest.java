package com.tccseed.tcc_seed.service.evento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tccseed.tcc_seed.controller.evento.dto.EventoAtualizacaoRequest;
import com.tccseed.tcc_seed.controller.evento.dto.EventoCriacaoRequest;
import com.tccseed.tcc_seed.domain.entity.Evento;
import com.tccseed.tcc_seed.domain.entity.Ies;
import com.tccseed.tcc_seed.repository.EstudanteRepository;
import com.tccseed.tcc_seed.repository.EventoRepository;
import com.tccseed.tcc_seed.repository.FuncionarioRepository;
import com.tccseed.tcc_seed.repository.IesRepository;
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
class EventoServiceTest {
  private static final UUID TOKEN = UUID.randomUUID();

  @Mock private EventoRepository eventoRepository;
  @Mock private FuncionarioRepository funcionarioRepository;
  @Mock private EstudanteRepository estudanteRepository;
  @Mock private IesRepository iesRepository;
  private EventoService service;

  @BeforeEach
  void preparar() {
    service =
        new EventoService(
            eventoRepository, funcionarioRepository, estudanteRepository, iesRepository);
  }

  @Test
  void estudanteNaoPodeCriarEvento() {
    EventoCriacaoRequest request =
        new EventoCriacaoRequest(LocalDate.now(), null, null, "Descrição", List.of());

    ResponseStatusException erro =
        assertThrows(
            ResponseStatusException.class,
            () -> service.criar(request, TOKEN, "ROLE_ESTUDANTE"));

    assertEquals(HttpStatus.FORBIDDEN, erro.getStatusCode());
    verify(funcionarioRepository, never()).findIesIdByToken(TOKEN);
    verify(eventoRepository, never()).save(org.mockito.ArgumentMatchers.any());
  }

  @Test
  void listarFiltraPelaIesDoUsuario() {
    when(funcionarioRepository.findIesIdByToken(TOKEN)).thenReturn(Optional.of(7L));
    when(eventoRepository.findVisiveisPorIesECategoria(7L, "Tecnologia"))
        .thenReturn(List.of());

    assertEquals(List.of(), service.listar(TOKEN, "Tecnologia"));

    verify(eventoRepository).findVisiveisPorIesECategoria(7L, "Tecnologia");
  }

  @Test
  void funcionarioDeOutraIesNaoPodeEditarEvento() {
    when(funcionarioRepository.findIesIdByToken(TOKEN)).thenReturn(Optional.of(8L));
    Evento evento = new Evento();
    evento.setIes(java.util.Set.of(ies(7L)));
    when(eventoRepository.findComCategoriasEInstituicoesPorId(1L))
        .thenReturn(Optional.of(evento));
    EventoAtualizacaoRequest request =
        new EventoAtualizacaoRequest(
            LocalDate.now(), null, null, "Descrição", List.of(), List.of(8L));

    ResponseStatusException erro =
        assertThrows(
            ResponseStatusException.class,
            () -> service.atualizar(1L, request, TOKEN, "ROLE_FUNCIONARIO"));

    assertEquals(HttpStatus.FORBIDDEN, erro.getStatusCode());
    verify(eventoRepository, never()).save(org.mockito.ArgumentMatchers.any());
  }

  private Ies ies(Long id) {
    Ies ies = new Ies();
    ies.setId(id);
    return ies;
  }
}

package com.tccseed.tcc_seed.service.formulariopersonalizado;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;

import com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.PerguntaFormularioRequest;
import com.tccseed.tcc_seed.domain.entity.PerguntaFormularioPersonalizado;
import com.tccseed.tcc_seed.repository.FuncionarioRepository;
import com.tccseed.tcc_seed.repository.PerguntaFormularioPersonalizadoRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PerguntaFormularioPersonalizadoServiceTest {
  @Mock PerguntaFormularioPersonalizadoRepository perguntaRepository;
  @Mock FuncionarioRepository funcionarioRepository;
  @Mock PerguntaStoreService perguntaStore;
  @InjectMocks PerguntaFormularioPersonalizadoService service;

  @Test
  void reutilizaEnunciadoExistenteIgnorandoEspacosECapitalizacao() {
    PerguntaFormularioPersonalizado existente = new PerguntaFormularioPersonalizado();
    existente.setId(44L);
    existente.setEnunciado("Pergunta existente");
    when(perguntaRepository.findByEnunciadoNormalizado("pergunta existente"))
        .thenReturn(Optional.of(existente));

    var resolvidas = service.resolvePerguntas(
        List.of(new PerguntaFormularioRequest("  PERGUNTA EXISTENTE  ")));

    assertEquals(44L, resolvidas.getFirst().getId());
    verify(perguntaStore, never()).criarSeAusente(anyString());
  }
}

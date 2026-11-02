package com.tccseed.tcc_seed.service.formulariopersonalizado;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.FormularioRequest;
import com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.PerguntaFormularioRequest;
import com.tccseed.tcc_seed.domain.entity.FormularioPergunta;
import com.tccseed.tcc_seed.domain.entity.FormularioPersonalizado;
import com.tccseed.tcc_seed.domain.entity.Funcionario;
import com.tccseed.tcc_seed.domain.entity.Ies;
import com.tccseed.tcc_seed.domain.entity.PerguntaFormularioPersonalizado;
import com.tccseed.tcc_seed.repository.EstudanteRepository;
import com.tccseed.tcc_seed.repository.FormularioPerguntaRepository;
import com.tccseed.tcc_seed.repository.FormularioPersonalizadoRepository;
import com.tccseed.tcc_seed.repository.FuncionarioRepository;
import com.tccseed.tcc_seed.repository.RespostaFormularioPersonalizadoRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class FormularioPersonalizadoServiceTest {
  private static final String TOKEN = "10000000-0000-4000-8000-000000000006";

  @Mock FormularioPersonalizadoRepository formularioRepository;
  @Mock FormularioPerguntaRepository vinculoRepository;
  @Mock RespostaFormularioPersonalizadoRepository respostaRepository;
  @Mock FuncionarioRepository funcionarioRepository;
  @Mock EstudanteRepository estudanteRepository;
  @Mock PerguntaFormularioPersonalizadoService perguntaService;

  @InjectMocks FormularioPersonalizadoService service;

  private Funcionario funcionario;

  @BeforeEach
  void setUp() {
    Ies ies = new Ies();
    ies.setId(9L);
    funcionario = new Funcionario();
    funcionario.setIes(ies);
    when(funcionarioRepository.findByToken(UUID.fromString(TOKEN))).thenReturn(Optional.of(funcionario));
  }

  @Test
  void funcionarioDeOutraIesRecebe404() {
    when(formularioRepository.findDetailedByIdAndIesId(22L, 9L)).thenReturn(Optional.empty());

    ResponseStatusException exception = assertThrows(
        ResponseStatusException.class, () -> service.buscarFuncionario(22L, TOKEN));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
  }

  @Test
  void alterarPerguntasDeFormularioRespondidoRetorna409() {
    FormularioPersonalizado formulario = new FormularioPersonalizado();
    formulario.setId(22L);
    PerguntaFormularioPersonalizado perguntaAtual = pergunta(1L, "Pergunta atual");
    PerguntaFormularioPersonalizado perguntaNova = pergunta(2L, "Pergunta nova");
    formulario.setIes(funcionario.getIes());
    when(formularioRepository.findDetailedByIdAndIesId(22L, 9L)).thenReturn(Optional.of(formulario));
    when(perguntaService.resolvePerguntas(any())).thenReturn(List.of(perguntaNova));
    when(vinculoRepository.findPerguntasByFormularioId(22L)).thenReturn(List.of(perguntaAtual));
    when(respostaRepository.existsByFormularioId(22L)).thenReturn(true);
    FormularioRequest request = new FormularioRequest("Novo título", List.of(new PerguntaFormularioRequest("Pergunta nova")));

    ResponseStatusException exception = assertThrows(
        ResponseStatusException.class, () -> service.atualizar(22L, request, TOKEN));

    assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
  }

  @Test
  void perguntaExistenteNormalizadaEReutilizada() {
    PerguntaFormularioPersonalizado existente = pergunta(44L, "Pergunta já cadastrada");
    when(perguntaService.resolvePerguntas(any())).thenReturn(List.of(existente));
    when(formularioRepository.save(any(FormularioPersonalizado.class)))
        .thenAnswer(invocation -> {
          FormularioPersonalizado salvo = invocation.getArgument(0);
          salvo.setId(22L);
          return salvo;
        });
    FormularioRequest request = new FormularioRequest(
        "Pesquisa", List.of(new PerguntaFormularioRequest("  PERGUNTA JÁ CADASTRADA  ")));

    var response = service.criar(request, TOKEN);

    assertEquals(44L, response.perguntas().getFirst().id());
    verify(perguntaService).resolvePerguntas(any());
    verify(vinculoRepository).save(any(FormularioPergunta.class));
  }

  private PerguntaFormularioPersonalizado pergunta(Long id, String enunciado) {
    PerguntaFormularioPersonalizado pergunta = new PerguntaFormularioPersonalizado();
    pergunta.setId(id);
    pergunta.setEnunciado(enunciado);
    return pergunta;
  }
}

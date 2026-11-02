package com.tccseed.tcc_seed.service.formulariopersonalizado;

import com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.PerguntaFormularioRequest;
import com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.PerguntaFormularioResponse;
import com.tccseed.tcc_seed.domain.entity.PerguntaFormularioPersonalizado;
import com.tccseed.tcc_seed.repository.FuncionarioRepository;
import com.tccseed.tcc_seed.repository.PerguntaFormularioPersonalizadoRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class PerguntaFormularioPersonalizadoService {
  private final PerguntaFormularioPersonalizadoRepository perguntaRepository;
  private final FuncionarioRepository funcionarioRepository;
  private final PerguntaStoreService perguntaStore;

  public List<PerguntaFormularioPersonalizado> resolvePerguntas(
      List<PerguntaFormularioRequest> requests) {
    Set<String> normalizados = new HashSet<>();
    List<PerguntaFormularioPersonalizado> perguntas = new ArrayList<>();
    for (PerguntaFormularioRequest request : requests) {
      if (request == null)
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pergunta inválida.");
      String enunciado = request.enunciado().trim();
      String chave = normalizar(enunciado);
      if (!normalizados.add(chave))
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "O formulário não pode conter perguntas duplicadas.");
      perguntas.add(findOrCreate(enunciado, chave));
    }
    return perguntas;
  }

  @Transactional(readOnly = true)
  public Page<PerguntaFormularioResponse> listar(int page, int size, String busca, String token) {
    try {
      funcionarioRepository
          .findByToken(UUID.fromString(token))
          .orElseThrow(() -> naoEncontrado("Funcionário não encontrado."));
    } catch (IllegalArgumentException e) {
      throw naoEncontrado("Funcionário não encontrado.");
    }
    if (page < 0 || size < 1 || size > 100)
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Parâmetros de paginação inválidos.");
    String filtro = busca == null ? "" : busca.trim();
    return perguntaRepository
        .buscar(filtro, PageRequest.of(page, size))
        .map(p -> new PerguntaFormularioResponse(p.getId(), p.getEnunciado()));
  }

  private PerguntaFormularioPersonalizado findOrCreate(String enunciado, String chave) {
    var existente = perguntaRepository.findByEnunciadoNormalizado(chave);
    if (existente.isPresent()) return existente.get();
    return perguntaStore
        .criarSeAusente(enunciado)
        .orElseGet(
            () ->
                perguntaRepository
                    .findByEnunciadoNormalizado(chave)
                    .orElseThrow(
                        () -> new IllegalStateException("Não foi possível resolver a pergunta.")));
  }

  private String normalizar(String enunciado) {
    return enunciado.trim().toLowerCase(Locale.ROOT);
  }

  private ResponseStatusException naoEncontrado(String mensagem) {
    return new ResponseStatusException(HttpStatus.NOT_FOUND, mensagem);
  }
}

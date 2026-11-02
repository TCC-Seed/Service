package com.tccseed.tcc_seed.service.formulariopersonalizado;

import com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.FormularioResponse;
import com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.ItemRespostaRequest;
import com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.ItemRespostaResponse;
import com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.RespostaRequest;
import com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.RespostaResponse;
import com.tccseed.tcc_seed.domain.entity.Estudante;
import com.tccseed.tcc_seed.domain.entity.FormularioPersonalizado;
import com.tccseed.tcc_seed.domain.entity.ItemRespostaFormularioPersonalizado;
import com.tccseed.tcc_seed.domain.entity.RespostaFormularioPersonalizado;
import com.tccseed.tcc_seed.domain.entity.id.ItemRespostaFormularioPersonalizadoId;
import com.tccseed.tcc_seed.repository.EstudanteRepository;
import com.tccseed.tcc_seed.repository.FormularioPerguntaRepository;
import com.tccseed.tcc_seed.repository.FormularioPersonalizadoRepository;
import com.tccseed.tcc_seed.repository.FuncionarioRepository;
import com.tccseed.tcc_seed.repository.ItemRespostaFormularioPersonalizadoRepository;
import com.tccseed.tcc_seed.repository.PerguntaFormularioPersonalizadoRepository;
import com.tccseed.tcc_seed.repository.RespostaFormularioPersonalizadoRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
public class RespostaFormularioPersonalizadoService {
  private final EstudanteRepository estudanteRepository;
  private final FuncionarioRepository funcionarioRepository;
  private final FormularioPersonalizadoRepository formularioRepository;
  private final FormularioPerguntaRepository vinculoRepository;
  private final RespostaFormularioPersonalizadoRepository respostaRepository;
  private final ItemRespostaFormularioPersonalizadoRepository itemRepository;
  private final PerguntaFormularioPersonalizadoRepository perguntaRepository;
  private final FormularioPersonalizadoService formularioService;

  public RespostaResponse criar(Long formularioId, RespostaRequest request, String token) {
    Estudante estudante = estudante(token);
    FormularioPersonalizado formulario = formulario(formularioId, estudante.getIes().getId());
    List<ItemRespostaFormularioPersonalizado> itens =
        validarItens(formularioId, request.respostas());
    RespostaFormularioPersonalizado resposta = new RespostaFormularioPersonalizado();
    resposta.setFormulario(formulario);
    resposta.setUsuario(estudante);
    resposta.setDataPreenchimento(LocalDate.now());
    resposta = respostaRepository.save(resposta);
    salvarItens(resposta, itens);
    return montar(List.of(resposta)).getFirst();
  }

  @Transactional(readOnly = true)
  public List<RespostaResponse> minhasRespostas(Long formularioId, String token) {
    Estudante estudante = estudante(token);
    formulario(formularioId, estudante.getIes().getId());
    return montar(respostaRepository.findByFormularioAndUsuario(formularioId, estudante.getId()));
  }

  public RespostaResponse atualizarMinhaResposta(
      Long formularioId, Long respostaId, RespostaRequest request, String token) {
    Estudante estudante = estudante(token);
    formulario(formularioId, estudante.getIes().getId());
    RespostaFormularioPersonalizado resposta =
        respostaRepository
            .findOwned(respostaId, formularioId, estudante.getId())
            .orElseThrow(() -> naoEncontrado("Resposta não encontrada."));
    List<ItemRespostaFormularioPersonalizado> itens =
        validarItens(formularioId, request.respostas());
    itemRepository.deleteByRespostaFormularioId(respostaId);
    resposta.setDataPreenchimento(LocalDate.now());
    salvarItens(resposta, itens);
    return montar(List.of(resposta)).getFirst();
  }

  public void excluirMinhaResposta(Long formularioId, Long respostaId, String token) {
    Estudante estudante = estudante(token);
    formulario(formularioId, estudante.getIes().getId());
    RespostaFormularioPersonalizado resposta =
        respostaRepository
            .findOwned(respostaId, formularioId, estudante.getId())
            .orElseThrow(() -> naoEncontrado("Resposta não encontrada."));
    respostaRepository.delete(resposta);
  }

  @Transactional(readOnly = true)
  public com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.RespostasFuncionarioResponse
      respostasDoFormulario(Long formularioId, String token, int page, int size) {
    UUID tokenUuid = token(token);
    Long iesId =
        funcionarioRepository
            .findByToken(tokenUuid)
            .orElseThrow(() -> naoEncontrado("Funcionário não encontrado."))
            .getIes()
            .getId();
    FormularioResponse formulario = formularioService.buscarFuncionario(formularioId, token);
    Page<RespostaFormularioPersonalizado> respostas =
        respostaRepository.findByFormularioAndIes(formularioId, iesId, pageable(page, size));
    List<RespostaResponse> corpo = montar(respostas.getContent());
    Map<Long, RespostaResponse> porId = new LinkedHashMap<>();
    corpo.forEach(r -> porId.put(r.id(), r));
    return new com.tccseed.tcc_seed.controller.formulariopersonalizado.dto
        .RespostasFuncionarioResponse(formulario, respostas.map(r -> porId.get(r.getId())));
  }

  private List<ItemRespostaFormularioPersonalizado> validarItens(
      Long formularioId, List<ItemRespostaRequest> requests) {
    Set<Long> vistos = new HashSet<>();
    List<ItemRespostaFormularioPersonalizado> itens = new ArrayList<>();
    for (ItemRespostaRequest request : requests) {
      if (request == null || request.idPergunta() == null) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Cada resposta deve informar idPergunta.");
      }
      Long perguntaId = request.idPergunta();
      if (!vistos.add(perguntaId)) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "A mesma pergunta não pode aparecer duas vezes na resposta.");
      }
      if (!vinculoRepository.existsByFormularioIdAndPerguntaId(formularioId, perguntaId)) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "A pergunta não pertence a este formulário.");
      }
      ItemRespostaFormularioPersonalizado item = new ItemRespostaFormularioPersonalizado();
      ItemRespostaFormularioPersonalizadoId id = new ItemRespostaFormularioPersonalizadoId();
      id.setIdPergunta(perguntaId);
      item.setId(id);
      item.setPergunta(perguntaRepository.getReferenceById(perguntaId));
      item.setResposta(request.resposta());
      itens.add(item);
    }
    return itens;
  }

  private void salvarItens(
      RespostaFormularioPersonalizado resposta, List<ItemRespostaFormularioPersonalizado> itens) {
    for (ItemRespostaFormularioPersonalizado item : itens) {
      ItemRespostaFormularioPersonalizadoId id = item.getId();
      id.setIdResposta(resposta.getId());
      item.setRespostaFormulario(resposta);
      itemRepository.save(item);
    }
  }

  private List<RespostaResponse> montar(List<RespostaFormularioPersonalizado> respostas) {
    if (respostas.isEmpty()) {
      return List.of();
    }
    List<Long> ids = respostas.stream().map(RespostaFormularioPersonalizado::getId).toList();
    Map<Long, List<ItemRespostaResponse>> porResposta = new LinkedHashMap<>();
    itemRepository
        .findWithPerguntasByRespostaIds(ids)
        .forEach(
            item ->
                porResposta
                    .computeIfAbsent(
                        item.getRespostaFormulario().getId(), ignored -> new ArrayList<>())
                    .add(
                        new ItemRespostaResponse(
                            item.getPergunta().getId(),
                            item.getPergunta().getEnunciado(),
                            item.getResposta())));
    Map<Long, Long> quantidades = new LinkedHashMap<>();
    for (RespostaFormularioPersonalizado resposta : respostas) {
      quantidades.computeIfAbsent(
          resposta.getFormulario().getId(), vinculoRepository::countByFormularioId);
    }
    return respostas.stream()
        .map(
            r -> {
              List<ItemRespostaResponse> itens = porResposta.getOrDefault(r.getId(), List.of());
              long preenchidas =
                  itens.stream()
                      .filter(i -> i.resposta() != null && !i.resposta().trim().isEmpty())
                      .count();
              boolean completa =
                  quantidades.getOrDefault(r.getFormulario().getId(), 0L) > 0
                      && preenchidas >= quantidades.getOrDefault(r.getFormulario().getId(), 0L);
              return new RespostaResponse(
                  r.getId(), r.getUsuario().getId(), r.getDataPreenchimento(), completa, itens);
            })
        .toList();
  }

  private Estudante estudante(String token) {
    try {
      return estudanteRepository
          .findByToken(UUID.fromString(token))
          .orElseThrow(() -> naoEncontrado("Estudante não encontrado."));
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudante não encontrado.", e);
    }
  }

  private UUID token(String token) {
    try {
      return UUID.fromString(token);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado.", e);
    }
  }

  private FormularioPersonalizado formulario(Long id, Long iesId) {
    return formularioRepository
        .findForAccess(id, iesId)
        .orElseThrow(() -> naoEncontrado("Formulário não encontrado."));
  }

  private PageRequest pageable(int page, int size) {
    if (page < 0 || size < 1 || size > 100) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Parâmetros de paginação inválidos.");
    }
    return PageRequest.of(page, size);
  }

  private ResponseStatusException naoEncontrado(String mensagem) {
    return new ResponseStatusException(HttpStatus.NOT_FOUND, mensagem);
  }
}

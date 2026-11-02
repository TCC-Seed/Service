package com.tccseed.tcc_seed.service.formulariopersonalizado;

import com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.FormularioRequest;
import com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.FormularioResponse;
import com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.FormularioResumoResponse;
import com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.PerguntaFormularioResponse;
import com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.RespostaStatusProjection;
import com.tccseed.tcc_seed.domain.entity.Estudante;
import com.tccseed.tcc_seed.domain.entity.FormularioPergunta;
import com.tccseed.tcc_seed.domain.entity.FormularioPersonalizado;
import com.tccseed.tcc_seed.domain.entity.Funcionario;
import com.tccseed.tcc_seed.domain.entity.PerguntaFormularioPersonalizado;
import com.tccseed.tcc_seed.domain.entity.id.FormularioPerguntaId;
import com.tccseed.tcc_seed.repository.EstudanteRepository;
import com.tccseed.tcc_seed.repository.FormularioPerguntaRepository;
import com.tccseed.tcc_seed.repository.FormularioPersonalizadoRepository;
import com.tccseed.tcc_seed.repository.FuncionarioRepository;
import com.tccseed.tcc_seed.repository.RespostaFormularioPersonalizadoRepository;
import java.time.LocalDateTime;
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
public class FormularioPersonalizadoService {
  private final FormularioPersonalizadoRepository formularioRepository;
  private final FormularioPerguntaRepository vinculoRepository;
  private final PerguntaFormularioPersonalizadoService perguntaService;
  private final RespostaFormularioPersonalizadoRepository respostaRepository;
  private final FuncionarioRepository funcionarioRepository;
  private final EstudanteRepository estudanteRepository;

  public FormularioResponse criar(FormularioRequest request, String token) {
    Funcionario funcionario = funcionario(token);
    List<PerguntaFormularioPersonalizado> perguntas =
        perguntaService.resolvePerguntas(request.perguntas());
    FormularioPersonalizado formulario = new FormularioPersonalizado();
    formulario.setTitulo(request.titulo().trim());
    formulario.setIes(funcionario.getIes());
    formulario.setCriadoEm(LocalDateTime.now());
    formulario = formularioRepository.save(formulario);
    salvarVinculos(formulario, perguntas);
    return resposta(formulario, perguntas);
  }

  @Transactional(readOnly = true)
  public Page<FormularioResumoResponse> listarFuncionario(String token, int page, int size) {
    Long iesId = funcionario(token).getIes().getId();
    return formularioRepository.findResumosByIesId(iesId, pageable(page, size));
  }

  @Transactional(readOnly = true)
  public FormularioResponse buscarFuncionario(Long id, String token) {
    FormularioPersonalizado formulario =
        formularioDetalhado(id, funcionario(token).getIes().getId());
    return resposta(
        formulario,
        formulario.getVinculos().stream().map(FormularioPergunta::getPergunta).toList());
  }

  public FormularioResponse atualizar(Long id, FormularioRequest request, String token) {
    Long iesId = funcionario(token).getIes().getId();
    FormularioPersonalizado formulario = formularioDetalhado(id, iesId);
    List<PerguntaFormularioPersonalizado> novas =
        perguntaService.resolvePerguntas(request.perguntas());
    Set<Long> atuaisIds = new HashSet<>();
    vinculoRepository.findPerguntasByFormularioId(id).forEach(p -> atuaisIds.add(p.getId()));
    Set<Long> novosIds = new HashSet<>();
    novas.forEach(p -> novosIds.add(p.getId()));
    boolean mudouPerguntas = !atuaisIds.equals(novosIds);
    if (mudouPerguntas && respostaRepository.existsByFormularioId(id)) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT,
          "Não é possível alterar as perguntas de um formulário que já possui respostas.");
    }
    formulario.setTitulo(request.titulo().trim());
    if (mudouPerguntas) {
      vinculoRepository.deleteByFormularioId(id);
      salvarVinculos(formulario, novas);
    }
    return resposta(
        formulario, mudouPerguntas ? novas : vinculoRepository.findPerguntasByFormularioId(id));
  }

  public void excluir(Long id, String token) {
    Long iesId = funcionario(token).getIes().getId();
    FormularioPersonalizado formulario =
        formularioRepository
            .findByIdAndIesId(id, iesId)
            .orElseThrow(() -> naoEncontrado("Formulário não encontrado."));
    formularioRepository.delete(formulario);
  }

  @Transactional(readOnly = true)
  public FormularioResponse buscarEstudante(Long id, String token) {
    Estudante estudante = estudante(token);
    FormularioPersonalizado formulario = formularioDetalhado(id, estudante.getIes().getId());
    return resposta(
        formulario,
        formulario.getVinculos().stream().map(FormularioPergunta::getPergunta).toList());
  }

  @Transactional(readOnly = true)
  public List<FormularioResumoResponse> listarEstudante(String token) {
    Estudante estudante = estudante(token);
    List<FormularioResumoResponse> resumos =
        formularioRepository.listarResumosByIesId(estudante.getIes().getId());
    if (resumos.isEmpty()) {
      return resumos;
    }
    List<Long> formularioIds = resumos.stream().map(FormularioResumoResponse::id).toList();
    var tentativas = respostaRepository.findLatestCandidates(estudante.getId(), formularioIds);
    Map<Long, Long> maisRecentes = new LinkedHashMap<>();
    for (var tentativa : tentativas) {
      maisRecentes.putIfAbsent(tentativa.getFormulario().getId(), tentativa.getId());
    }
    if (maisRecentes.isEmpty()) {
      return resumos;
    }
    List<RespostaStatusProjection> status =
        respostaRepository.countRespondidasPorFormulario(new ArrayList<>(maisRecentes.values()));
    Map<Long, Long> respondidas = new LinkedHashMap<>();
    status.forEach(s -> respondidas.put(s.respostaId(), s.itensRespondidos()));
    return resumos.stream()
        .map(
            r -> {
              Long respostaId = maisRecentes.get(r.id());
              String situacao =
                  respostaId == null
                      ? "nao_respondido"
                      : respondidas.getOrDefault(respostaId, 0L) >= r.quantidadePerguntas()
                              && r.quantidadePerguntas() > 0
                          ? "preenchido"
                          : "incompleto";
              return new FormularioResumoResponse(
                  r.id(), r.titulo(), r.quantidadePerguntas(), situacao);
            })
        .toList();
  }

  private void salvarVinculos(
      FormularioPersonalizado formulario, List<PerguntaFormularioPersonalizado> perguntas) {
    for (PerguntaFormularioPersonalizado pergunta : perguntas) {
      FormularioPergunta vinculo = new FormularioPergunta();
      FormularioPerguntaId id = new FormularioPerguntaId();
      id.setIdFormularioPersonalizado(formulario.getId());
      id.setIdPergunta(pergunta.getId());
      vinculo.setId(id);
      vinculo.setFormulario(formulario);
      vinculo.setPergunta(pergunta);
      vinculoRepository.save(vinculo);
    }
  }

  private FormularioPersonalizado formularioDetalhado(Long id, Long iesId) {
    return formularioRepository
        .findDetailedByIdAndIesId(id, iesId)
        .orElseThrow(() -> naoEncontrado("Formulário não encontrado."));
  }

  private FormularioResponse resposta(
      FormularioPersonalizado formulario, List<PerguntaFormularioPersonalizado> perguntas) {
    return new FormularioResponse(
        formulario.getId(),
        formulario.getTitulo(),
        perguntas.stream()
            .map(p -> new PerguntaFormularioResponse(p.getId(), p.getEnunciado()))
            .toList());
  }

  private Funcionario funcionario(String token) {
    try {
      return funcionarioRepository
          .findByToken(UUID.fromString(token))
          .orElseThrow(() -> naoEncontrado("Funcionário não encontrado."));
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado.", e);
    }
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

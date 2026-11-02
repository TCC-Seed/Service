package com.tccseed.tcc_seed.service.evento;

import com.tccseed.tcc_seed.controller.evento.dto.EventoAtualizacaoRequest;
import com.tccseed.tcc_seed.controller.evento.dto.EventoCriacaoRequest;
import com.tccseed.tcc_seed.controller.evento.dto.EventoResponse;
import com.tccseed.tcc_seed.domain.entity.Evento;
import com.tccseed.tcc_seed.domain.entity.Ies;
import com.tccseed.tcc_seed.repository.EstudanteRepository;
import com.tccseed.tcc_seed.repository.EventoRepository;
import com.tccseed.tcc_seed.repository.FuncionarioRepository;
import com.tccseed.tcc_seed.repository.IesRepository;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class EventoService {
  private final EventoRepository eventoRepository;
  private final FuncionarioRepository funcionarioRepository;
  private final EstudanteRepository estudanteRepository;
  private final IesRepository iesRepository;

  public EventoResponse criar(EventoCriacaoRequest request, UUID token, String role) {
    exigirFuncionario(role);
    Long iesDoFuncionario = iesDoFuncionario(token);
    Ies ies =
        iesRepository
            .findById(iesDoFuncionario)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "IES do funcionário não encontrada."));

    Evento evento = new Evento();
    preencher(evento, request);
    evento.setIes(Set.of(ies));
    return EventoResponse.from(eventoRepository.save(evento));
  }

  @Transactional(readOnly = true)
  public List<EventoResponse> listar(UUID token, String categoria) {
    Long iesId = iesDoUsuario(token);
    return eventoRepository.findVisiveisPorIesECategoria(iesId, categoria).stream()
        .map(EventoResponse::from)
        .toList();
  }

  @Transactional(readOnly = true)
  public EventoResponse buscarPorId(Long id, UUID token) {
    Long iesId = iesDoUsuario(token);
    return EventoResponse.from(
        eventoRepository
            .findVisivelPorIdEIes(id, iesId)
            .orElseThrow(EventoService::eventoNaoEncontrado));
  }

  public EventoResponse atualizar(
      Long id, EventoAtualizacaoRequest request, UUID token, String role) {
    exigirFuncionario(role);
    Long iesDoFuncionario = iesDoFuncionario(token);
    Evento evento = buscarParaAlteracao(id);
    exigirVinculoComEvento(evento, iesDoFuncionario);
    Set<Ies> iesNovas = validarIes(request.iesIds(), iesDoFuncionario);

    preencher(evento, request);
    evento.setIes(iesNovas);
    return EventoResponse.from(eventoRepository.save(evento));
  }

  public void excluir(Long id, UUID token, String role) {
    exigirFuncionario(role);
    Long iesDoFuncionario = iesDoFuncionario(token);
    Evento evento = buscarParaAlteracao(id);
    exigirVinculoComEvento(evento, iesDoFuncionario);
    eventoRepository.delete(evento);
  }

  private void preencher(Evento evento, EventoAtualizacaoRequest request) {
    preencher(
        evento,
        request.dataInicio(),
        request.dataFim(),
        request.link(),
        request.descricao(),
        request.categorias());
  }

  private void preencher(Evento evento, EventoCriacaoRequest request) {
    preencher(
        evento,
        request.dataInicio(),
        request.dataFim(),
        request.link(),
        request.descricao(),
        request.categorias());
  }

  private void preencher(
      Evento evento,
      LocalDate dataInicio,
      LocalDate dataFim,
      String link,
      String descricao,
      List<String> categorias) {
    evento.setDataInicio(dataInicio);
    evento.setDataFim(dataFim);
    evento.setLink(link);
    evento.setDescricao(descricao);
    evento.setCategorias(normalizarCategorias(categorias));
  }

  private Set<String> normalizarCategorias(List<String> categoriasRecebidas) {
    if (categoriasRecebidas == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "A lista de categorias é obrigatória.");
    }
    Set<String> categorias = new LinkedHashSet<>();
    for (String categoria : categoriasRecebidas) {
      if (categoria == null || categoria.isBlank()) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "As categorias não podem ser vazias.");
      }
      categorias.add(categoria.trim());
    }
    return categorias;
  }

  private Set<Ies> validarIes(List<Long> iesIds, Long iesDoFuncionario) {
    if (iesIds == null || iesIds.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "O evento deve estar associado a pelo menos uma IES.");
    }

    Set<Long> ids = new LinkedHashSet<>();
    for (Long iesId : iesIds) {
      if (iesId == null) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Os IDs de IES não podem ser nulos.");
      }
      ids.add(iesId);
    }
    if (!ids.contains(iesDoFuncionario)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "O evento deve incluir a IES do funcionário autenticado.");
    }

    List<Ies> instituicoes = iesRepository.findAllById(ids);
    if (instituicoes.size() != ids.size()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uma ou mais IES não existem.");
    }
    return new LinkedHashSet<>(instituicoes);
  }

  private Long iesDoUsuario(UUID token) {
    return funcionarioRepository
        .findIesIdByToken(token)
        .or(() -> estudanteRepository.findIesIdByToken(token))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
  }

  private Long iesDoFuncionario(UUID token) {
    return funcionarioRepository
        .findIesIdByToken(token)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "A operação exige um funcionário autenticado."));
  }

  private void exigirFuncionario(String role) {
    if (!"ROLE_FUNCIONARIO".equals(role)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "A operação exige a role ROLE_FUNCIONARIO.");
    }
  }

  private Evento buscarParaAlteracao(Long id) {
    return eventoRepository
        .findComCategoriasEInstituicoesPorId(id)
        .orElseThrow(EventoService::eventoNaoEncontrado);
  }

  private void exigirVinculoComEvento(Evento evento, Long iesId) {
    if (evento.getIes().stream().noneMatch(ies -> ies.getId().equals(iesId))) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "A operação exige um funcionário de uma IES associada ao evento.");
    }
  }

  private static ResponseStatusException eventoNaoEncontrado() {
    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado.");
  }
}

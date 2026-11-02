package com.tccseed.tcc_seed.service.publicacao;

import com.tccseed.tcc_seed.controller.publicacao.dto.PublicacaoAtualizacaoRequest;
import com.tccseed.tcc_seed.controller.publicacao.dto.PublicacaoRequest;
import com.tccseed.tcc_seed.controller.publicacao.dto.PublicacaoResponse;
import com.tccseed.tcc_seed.domain.entity.Funcionario;
import com.tccseed.tcc_seed.domain.entity.Publicacao;
import com.tccseed.tcc_seed.repository.EstudanteRepository;
import com.tccseed.tcc_seed.repository.FuncionarioRepository;
import com.tccseed.tcc_seed.repository.PublicacaoRepository;
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
public class PublicacaoService {
  private final PublicacaoRepository publicacaoRepository;
  private final FuncionarioRepository funcionarioRepository;
  private final EstudanteRepository estudanteRepository;

  public PublicacaoResponse criar(PublicacaoRequest request, UUID token, String role) {
    if (!"ROLE_FUNCIONARIO".equals(role)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "A criação de publicações exige a role ROLE_FUNCIONARIO.");
    }

    Funcionario funcionarioAutenticado = funcionarioAutenticado(token);
    Publicacao publicacao = new Publicacao();
    preencher(
        publicacao,
        request.titulo(),
        request.descricao(),
        request.dataPublicacao(),
        request.categorias());
    publicacao.setFuncionario(funcionarioAutenticado);
    return PublicacaoResponse.from(publicacaoRepository.save(publicacao));
  }

  @Transactional(readOnly = true)
  public List<PublicacaoResponse> listar(UUID token, String categoria) {
    Long iesId = iesDoUsuario(token);
    return publicacaoRepository.findVisiveisPorIesECategoria(iesId, categoria).stream()
        .map(PublicacaoResponse::from)
        .toList();
  }

  @Transactional(readOnly = true)
  public PublicacaoResponse buscarPorId(Long id, UUID token) {
    Long iesId = iesDoUsuario(token);
    return PublicacaoResponse.from(
        publicacaoRepository
            .findVisivelPorIdEIes(id, iesId)
            .orElseThrow(PublicacaoService::publicacaoNaoEncontrada));
  }

  public PublicacaoResponse atualizar(Long id, PublicacaoAtualizacaoRequest request, UUID token) {
    Funcionario funcionarioAutenticado = funcionarioAutenticado(token);
    Publicacao publicacao = buscarParaAlteracao(id);
    validarMesmaIes(funcionarioAutenticado, publicacao);
    preencher(
        publicacao,
        request.titulo(),
        request.descricao(),
        request.dataPublicacao(),
        request.categorias());
    return PublicacaoResponse.from(publicacaoRepository.save(publicacao));
  }

  public void excluir(Long id, UUID token) {
    Funcionario funcionarioAutenticado = funcionarioAutenticado(token);
    Publicacao publicacao = buscarParaAlteracao(id);
    validarMesmaIes(funcionarioAutenticado, publicacao);
    publicacaoRepository.delete(publicacao);
  }

  private void preencher(
      Publicacao publicacao,
      String titulo,
      String descricao,
      LocalDate dataPublicacao,
      List<String> categoriasRecebidas) {
    publicacao.setTitulo(titulo);
    publicacao.setDescricao(descricao);
    publicacao.setDataPublicacao(dataPublicacao);
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
    publicacao.setCategorias(categorias);
  }

  private Long iesDoUsuario(UUID token) {
    return funcionarioRepository
        .findIesIdByToken(token)
        .or(() -> estudanteRepository.findIesIdByToken(token))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
  }

  private Funcionario funcionarioAutenticado(UUID token) {
    return funcionarioRepository
        .findByToken(token)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "A operação exige um funcionário autenticado."));
  }

  private Publicacao buscarParaAlteracao(Long id) {
    return publicacaoRepository
        .findComFuncionarioECategoriasPorId(id)
        .orElseThrow(PublicacaoService::publicacaoNaoEncontrada);
  }

  private void validarMesmaIes(Funcionario funcionario, Publicacao publicacao) {
    if (!funcionario.getIes().getId().equals(publicacao.getFuncionario().getIes().getId())) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "A operação exige um funcionário da mesma IES da publicação.");
    }
  }

  private static ResponseStatusException publicacaoNaoEncontrada() {
    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicação não encontrada.");
  }
}

package com.tccseed.tcc_seed.controller.publicacao;

import com.tccseed.tcc_seed.controller.publicacao.dto.PublicacaoAtualizacaoRequest;
import com.tccseed.tcc_seed.controller.publicacao.dto.PublicacaoRequest;
import com.tccseed.tcc_seed.controller.publicacao.dto.PublicacaoResponse;
import com.tccseed.tcc_seed.service.publicacao.PublicacaoService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/publicacoes")
@RequiredArgsConstructor
public class PublicacaoController {
  private final PublicacaoService service;

  @PostMapping
  public ResponseEntity<PublicacaoResponse> criar(
      @Valid @RequestBody PublicacaoRequest request, Authentication auth) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.criar(request, token(auth), role(auth)));
  }

  @GetMapping
  public List<PublicacaoResponse> listar(
      @RequestParam(required = false) String categoria, Authentication auth) {
    return service.listar(token(auth), categoria);
  }

  @GetMapping("/{id}")
  public PublicacaoResponse buscarPorId(@PathVariable Long id, Authentication auth) {
    return service.buscarPorId(id, token(auth));
  }

  @PutMapping("/{id}")
  public PublicacaoResponse atualizar(
      @PathVariable Long id,
      @Valid @RequestBody PublicacaoAtualizacaoRequest request,
      Authentication auth) {
    return service.atualizar(id, request, token(auth));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> excluir(@PathVariable Long id, Authentication auth) {
    service.excluir(id, token(auth));
    return ResponseEntity.noContent().build();
  }

  private UUID token(Authentication authentication) {
    return UUID.fromString(authentication.getName());
  }

  private String role(Authentication authentication) {
    return authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .filter(authority -> authority.startsWith("ROLE_"))
        .findFirst()
        .orElse("");
  }
}

package com.tccseed.tcc_seed.controller.evento;

import com.tccseed.tcc_seed.controller.evento.dto.EventoAtualizacaoRequest;
import com.tccseed.tcc_seed.controller.evento.dto.EventoCriacaoRequest;
import com.tccseed.tcc_seed.controller.evento.dto.EventoResponse;
import com.tccseed.tcc_seed.service.evento.EventoService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "evento-controller")
@RequestMapping("/eventos")
@RequiredArgsConstructor
public class EventoController {
  private final EventoService service;

  @PostMapping
  public ResponseEntity<EventoResponse> criar(
      @Valid @RequestBody EventoCriacaoRequest request, Authentication auth) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.criar(request, token(auth), role(auth)));
  }

  @GetMapping
  public List<EventoResponse> listar(
      @RequestParam(required = false) String categoria, Authentication auth) {
    return service.listar(token(auth), categoria);
  }

  @GetMapping("/{id}")
  public EventoResponse buscarPorId(@PathVariable Long id, Authentication auth) {
    return service.buscarPorId(id, token(auth));
  }

  @PutMapping("/{id}")
  public EventoResponse atualizar(
      @PathVariable Long id,
      @Valid @RequestBody EventoAtualizacaoRequest request,
      Authentication auth) {
    return service.atualizar(id, request, token(auth), role(auth));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> excluir(@PathVariable Long id, Authentication auth) {
    service.excluir(id, token(auth), role(auth));
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

package com.tccseed.tcc_seed.controller.evento;

import com.tccseed.tcc_seed.controller.evento.dto.EventoIesRequest;
import com.tccseed.tcc_seed.domain.entity.EventoIes;
import com.tccseed.tcc_seed.domain.entity.id.EventoIesId;
import com.tccseed.tcc_seed.service.evento.EventoIesService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/eventos-ies")
@RequiredArgsConstructor
public class EventoIesController {
  private final EventoIesService service;

  @PostMapping
  public ResponseEntity<EventoIes> criar(@RequestBody EventoIesRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request.toEntity()));
  }

  @GetMapping
  public List<EventoIes> listar() {
    return service.listar();
  }

  @GetMapping("/{idIes}/{idEvento}")
  public EventoIes buscarPorId(@PathVariable Long idIes, @PathVariable Long idEvento) {
    return service.buscarPorId(id(idIes, idEvento));
  }

  @PutMapping("/{idIes}/{idEvento}")
  public EventoIes atualizar(
      @PathVariable Long idIes,
      @PathVariable Long idEvento,
      @RequestBody EventoIesRequest request) {
    return service.atualizar(id(idIes, idEvento), request.toEntity());
  }

  @DeleteMapping("/{idIes}/{idEvento}")
  public ResponseEntity<Void> excluir(@PathVariable Long idIes, @PathVariable Long idEvento) {
    service.excluir(id(idIes, idEvento));
    return ResponseEntity.noContent().build();
  }

  private EventoIesId id(Long idIes, Long idEvento) {
    EventoIesId id = new EventoIesId();
    id.setIdIes(idIes);
    id.setIdEvento(idEvento);
    return id;
  }
}

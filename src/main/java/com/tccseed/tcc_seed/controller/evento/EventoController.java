package com.tccseed.tcc_seed.controller.evento;

import com.tccseed.tcc_seed.controller.evento.dto.EventoRequest;
import com.tccseed.tcc_seed.domain.entity.Evento;
import com.tccseed.tcc_seed.service.evento.EventoService;
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
@RequestMapping("/eventos")
@RequiredArgsConstructor
public class EventoController {
  private final EventoService service;

  @PostMapping
  public ResponseEntity<Evento> criar(@RequestBody EventoRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request.toEntity()));
  }

  @GetMapping
  public List<Evento> listar() {
    return service.listar();
  }

  @GetMapping("/{id}")
  public Evento buscarPorId(@PathVariable Long id) {
    return service.buscarPorId(id);
  }

  @PutMapping("/{id}")
  public Evento atualizar(@PathVariable Long id, @RequestBody EventoRequest request) {
    return service.atualizar(id, request.toEntity());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> excluir(@PathVariable Long id) {
    service.excluir(id);
    return ResponseEntity.noContent().build();
  }
}

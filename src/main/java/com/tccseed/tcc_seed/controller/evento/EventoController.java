package com.tccseed.tcc_seed.controller.evento;

import com.tccseed.tcc_seed.domain.entity.Evento;
import com.tccseed.tcc_seed.service.evento.EventoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/eventos")
@RequiredArgsConstructor
public class EventoController {
    private final EventoService service;

    @PostMapping
    public ResponseEntity<Evento> criar(@RequestBody Evento entidade) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(entidade));
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
    public Evento atualizar(@PathVariable Long id, @RequestBody Evento entidade) {
        return service.atualizar(id, entidade);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

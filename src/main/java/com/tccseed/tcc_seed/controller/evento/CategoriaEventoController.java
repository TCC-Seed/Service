package com.tccseed.tcc_seed.controller.evento;

import com.tccseed.tcc_seed.controller.evento.dto.CategoriaEventoRequest;
import com.tccseed.tcc_seed.domain.entity.CategoriaEvento;
import com.tccseed.tcc_seed.domain.entity.id.CategoriaEventoId;
import com.tccseed.tcc_seed.service.evento.CategoriaEventoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias-evento")
@RequiredArgsConstructor
public class CategoriaEventoController {
    private final CategoriaEventoService service;

    @PostMapping
    public ResponseEntity<CategoriaEvento> criar(@RequestBody CategoriaEventoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request.toEntity()));
    }

    @GetMapping
    public List<CategoriaEvento> listar() {
        return service.listar();
    }

    @GetMapping("/{idEvento}/{categoria}")
    public CategoriaEvento buscarPorId(@PathVariable Long idEvento, @PathVariable String categoria) {
        return service.buscarPorId(id(idEvento, categoria));
    }

    @PutMapping("/{idEvento}/{categoria}")
    public CategoriaEvento atualizar(@PathVariable Long idEvento, @PathVariable String categoria, @RequestBody CategoriaEventoRequest request) {
        return service.atualizar(id(idEvento, categoria), request.toEntity());
    }

    @DeleteMapping("/{idEvento}/{categoria}")
    public ResponseEntity<Void> excluir(@PathVariable Long idEvento, @PathVariable String categoria) {
        service.excluir(id(idEvento, categoria));
        return ResponseEntity.noContent().build();
    }

    private CategoriaEventoId id(Long idEvento, String categoria) {
        CategoriaEventoId id = new CategoriaEventoId();
        id.setIdEvento(idEvento);
        id.setCategoria(categoria);
        return id;
    }

}

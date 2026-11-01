package com.tccseed.tcc_seed.controller.formulario;

import com.tccseed.tcc_seed.controller.formulario.dto.FormularioBaseRequest;
import com.tccseed.tcc_seed.domain.entity.FormularioBase;
import com.tccseed.tcc_seed.service.formulario.FormularioBaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/formularios-base")
@RequiredArgsConstructor
public class FormularioBaseController {
    private final FormularioBaseService service;

    @PostMapping
    public ResponseEntity<FormularioBase> criar(@RequestBody FormularioBaseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request.toEntity()));
    }

    @GetMapping
    public List<FormularioBase> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public FormularioBase buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public FormularioBase atualizar(@PathVariable Long id, @RequestBody FormularioBaseRequest request) {
        return service.atualizar(id, request.toEntity());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

package com.tccseed.tcc_seed.controller.formulario;

import com.tccseed.tcc_seed.domain.entity.FormularioPersonalizado;
import com.tccseed.tcc_seed.controller.formulario.dto.FormularioPersonalizadoRequest;
import com.tccseed.tcc_seed.service.formulario.FormularioPersonalizadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/formularios-personalizados")
@RequiredArgsConstructor
public class FormularioPersonalizadoController {
    private final FormularioPersonalizadoService service;

    @PostMapping
    public ResponseEntity<FormularioPersonalizado> criar(@RequestBody FormularioPersonalizadoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request.toEntity()));
    }

    @GetMapping
    public List<FormularioPersonalizado> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public FormularioPersonalizado buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public FormularioPersonalizado atualizar(@PathVariable Long id, @RequestBody FormularioPersonalizadoRequest request) {
        return service.atualizar(id, request.toEntity());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

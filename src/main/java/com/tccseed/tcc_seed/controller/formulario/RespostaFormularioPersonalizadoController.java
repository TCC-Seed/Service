package com.tccseed.tcc_seed.controller.formulario;

import com.tccseed.tcc_seed.domain.entity.RespostaFormularioPersonalizado;
import com.tccseed.tcc_seed.service.formulario.RespostaFormularioPersonalizadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/respostas-formularios-personalizados")
@RequiredArgsConstructor
public class RespostaFormularioPersonalizadoController {
    private final RespostaFormularioPersonalizadoService service;

    @PostMapping
    public ResponseEntity<RespostaFormularioPersonalizado> criar(@RequestBody RespostaFormularioPersonalizado entidade) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(entidade));
    }

    @GetMapping
    public List<RespostaFormularioPersonalizado> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public RespostaFormularioPersonalizado buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public RespostaFormularioPersonalizado atualizar(@PathVariable Long id, @RequestBody RespostaFormularioPersonalizado entidade) {
        return service.atualizar(id, entidade);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

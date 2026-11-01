package com.tccseed.tcc_seed.controller.formulario;

import com.tccseed.tcc_seed.domain.entity.PerguntaFormularioPersonalizado;
import com.tccseed.tcc_seed.service.formulario.PerguntaFormularioPersonalizadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/perguntas-formularios-personalizados")
@RequiredArgsConstructor
public class PerguntaFormularioPersonalizadoController {
    private final PerguntaFormularioPersonalizadoService service;

    @PostMapping
    public ResponseEntity<PerguntaFormularioPersonalizado> criar(@RequestBody PerguntaFormularioPersonalizado entidade) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(entidade));
    }

    @GetMapping
    public List<PerguntaFormularioPersonalizado> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public PerguntaFormularioPersonalizado buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public PerguntaFormularioPersonalizado atualizar(@PathVariable Long id, @RequestBody PerguntaFormularioPersonalizado entidade) {
        return service.atualizar(id, entidade);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

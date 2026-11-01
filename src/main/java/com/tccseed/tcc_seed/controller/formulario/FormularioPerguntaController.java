package com.tccseed.tcc_seed.controller.formulario;

import com.tccseed.tcc_seed.domain.entity.FormularioPergunta;
import com.tccseed.tcc_seed.domain.entity.id.FormularioPerguntaId;
import com.tccseed.tcc_seed.service.formulario.FormularioPerguntaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/formularios-perguntas")
@RequiredArgsConstructor
public class FormularioPerguntaController {
    private final FormularioPerguntaService service;

    @PostMapping
    public ResponseEntity<FormularioPergunta> criar(@RequestBody FormularioPergunta entidade) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(entidade));
    }

    @GetMapping
    public List<FormularioPergunta> listar() {
        return service.listar();
    }

    @GetMapping("/{idFormularioPersonalizado}/{idPergunta}")
    public FormularioPergunta buscarPorId(@PathVariable Long idFormularioPersonalizado, @PathVariable Long idPergunta) {
        return service.buscarPorId(id(idFormularioPersonalizado, idPergunta));
    }

    @PutMapping("/{idFormularioPersonalizado}/{idPergunta}")
    public FormularioPergunta atualizar(@PathVariable Long idFormularioPersonalizado, @PathVariable Long idPergunta, @RequestBody FormularioPergunta entidade) {
        return service.atualizar(id(idFormularioPersonalizado, idPergunta), entidade);
    }

    @DeleteMapping("/{idFormularioPersonalizado}/{idPergunta}")
    public ResponseEntity<Void> excluir(@PathVariable Long idFormularioPersonalizado, @PathVariable Long idPergunta) {
        service.excluir(id(idFormularioPersonalizado, idPergunta));
        return ResponseEntity.noContent().build();
    }

    private FormularioPerguntaId id(Long idFormularioPersonalizado, Long idPergunta) {
        FormularioPerguntaId id = new FormularioPerguntaId();
        id.setIdFormularioPersonalizado(idFormularioPersonalizado);
        id.setIdPergunta(idPergunta);
        return id;
    }

}

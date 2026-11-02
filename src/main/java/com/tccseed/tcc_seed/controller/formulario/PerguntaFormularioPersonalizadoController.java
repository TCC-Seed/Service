package com.tccseed.tcc_seed.controller.formulario;

import com.tccseed.tcc_seed.controller.formulario.dto.PerguntaFormularioPersonalizadoRequest;
import com.tccseed.tcc_seed.domain.entity.PerguntaFormularioPersonalizado;
import com.tccseed.tcc_seed.service.formulario.PerguntaFormularioPersonalizadoService;
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
@RequestMapping("/perguntas-formularios-personalizados")
@RequiredArgsConstructor
public class PerguntaFormularioPersonalizadoController {
  private final PerguntaFormularioPersonalizadoService service;

  @PostMapping
  public ResponseEntity<PerguntaFormularioPersonalizado> criar(
      @RequestBody PerguntaFormularioPersonalizadoRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request.toEntity()));
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
  public PerguntaFormularioPersonalizado atualizar(
      @PathVariable Long id, @RequestBody PerguntaFormularioPersonalizadoRequest request) {
    return service.atualizar(id, request.toEntity());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> excluir(@PathVariable Long id) {
    service.excluir(id);
    return ResponseEntity.noContent().build();
  }
}

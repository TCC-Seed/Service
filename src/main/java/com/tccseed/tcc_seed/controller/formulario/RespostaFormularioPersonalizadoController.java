package com.tccseed.tcc_seed.controller.formulario;

import com.tccseed.tcc_seed.controller.formulario.dto.RespostaFormularioPersonalizadoRequest;
import com.tccseed.tcc_seed.domain.entity.RespostaFormularioPersonalizado;
import com.tccseed.tcc_seed.service.formulario.RespostaFormularioPersonalizadoService;
import jakarta.persistence.EntityManager;
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
@RequestMapping("/respostas-formularios-personalizados")
@RequiredArgsConstructor
public class RespostaFormularioPersonalizadoController {
  private final RespostaFormularioPersonalizadoService service;
  private final EntityManager entityManager;

  @PostMapping
  public ResponseEntity<RespostaFormularioPersonalizado> criar(
      @RequestBody RespostaFormularioPersonalizadoRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(toEntity(request)));
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
  public RespostaFormularioPersonalizado atualizar(
      @PathVariable Long id, @RequestBody RespostaFormularioPersonalizadoRequest request) {
    return service.atualizar(id, toEntity(request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> excluir(@PathVariable Long id) {
    service.excluir(id);
    return ResponseEntity.noContent().build();
  }

  private RespostaFormularioPersonalizado toEntity(RespostaFormularioPersonalizadoRequest request) {
    return request.toEntity(
        entityManager.getReference(
            com.tccseed.tcc_seed.domain.entity.Usuario.class, request.idUsuario()));
  }
}

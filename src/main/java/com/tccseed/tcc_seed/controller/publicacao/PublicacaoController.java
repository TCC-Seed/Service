package com.tccseed.tcc_seed.controller.publicacao;

import com.tccseed.tcc_seed.controller.publicacao.dto.PublicacaoRequest;
import com.tccseed.tcc_seed.domain.entity.Publicacao;
import com.tccseed.tcc_seed.service.publicacao.PublicacaoService;
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
@RequestMapping("/publicacoes")
@RequiredArgsConstructor
public class PublicacaoController {
  private final PublicacaoService service;

  @PostMapping
  public ResponseEntity<Publicacao> criar(@RequestBody PublicacaoRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request.toEntity()));
  }

  @GetMapping
  public List<Publicacao> listar() {
    return service.listar();
  }

  @GetMapping("/{id}")
  public Publicacao buscarPorId(@PathVariable Long id) {
    return service.buscarPorId(id);
  }

  @PutMapping("/{id}")
  public Publicacao atualizar(@PathVariable Long id, @RequestBody PublicacaoRequest request) {
    return service.atualizar(id, request.toEntity());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> excluir(@PathVariable Long id) {
    service.excluir(id);
    return ResponseEntity.noContent().build();
  }
}

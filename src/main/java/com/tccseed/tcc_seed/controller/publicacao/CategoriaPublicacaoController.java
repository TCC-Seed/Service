package com.tccseed.tcc_seed.controller.publicacao;

import com.tccseed.tcc_seed.domain.entity.CategoriaPublicacao;
import com.tccseed.tcc_seed.domain.entity.id.CategoriaPublicacaoId;
import com.tccseed.tcc_seed.service.publicacao.CategoriaPublicacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/categorias-publicacao")
@RequiredArgsConstructor
public class CategoriaPublicacaoController {
    private final CategoriaPublicacaoService service;

    @PostMapping
    public ResponseEntity<CategoriaPublicacao> criar(@RequestBody CategoriaPublicacao entidade) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(entidade));
    }

    @GetMapping
    public List<CategoriaPublicacao> listar() {
        return service.listar();
    }

    @GetMapping("/{idPublicacao}/{categoria}")
    public CategoriaPublicacao buscarPorId(@PathVariable Long idPublicacao, @PathVariable String categoria) {
        return service.buscarPorId(id(idPublicacao, categoria));
    }

    @PutMapping("/{idPublicacao}/{categoria}")
    public CategoriaPublicacao atualizar(@PathVariable Long idPublicacao, @PathVariable String categoria, @RequestBody CategoriaPublicacao entidade) {
        return service.atualizar(id(idPublicacao, categoria), entidade);
    }

    @DeleteMapping("/{idPublicacao}/{categoria}")
    public ResponseEntity<Void> excluir(@PathVariable Long idPublicacao, @PathVariable String categoria) {
        service.excluir(id(idPublicacao, categoria));
        return ResponseEntity.noContent().build();
    }

    private CategoriaPublicacaoId id(Long idPublicacao, String categoria) {
        CategoriaPublicacaoId id = new CategoriaPublicacaoId();
        id.setIdPublicacao(idPublicacao);
        id.setCategoria(categoria);
        return id;
    }

}

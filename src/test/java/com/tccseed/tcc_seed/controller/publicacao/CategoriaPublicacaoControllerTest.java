package com.tccseed.tcc_seed.controller.publicacao;

import com.tccseed.tcc_seed.controller.publicacao.dto.CategoriaPublicacaoRequest;
import com.tccseed.tcc_seed.domain.entity.CategoriaPublicacao;
import com.tccseed.tcc_seed.domain.entity.id.CategoriaPublicacaoId;
import com.tccseed.tcc_seed.service.publicacao.CategoriaPublicacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaPublicacaoControllerTest {
    @Mock private CategoriaPublicacaoService service;
    private CategoriaPublicacaoController controller;
    private final CategoriaPublicacao entidade = new CategoriaPublicacao();
    private final CategoriaPublicacaoId expectedId = new CategoriaPublicacaoId();

    private CategoriaPublicacaoRequest request() {
        return new CategoriaPublicacaoRequest(null, null);
    }

    @BeforeEach
    void preparar() {
        controller = new CategoriaPublicacaoController(service);
        expectedId.setIdPublicacao(10L);
        expectedId.setCategoria("bem-estar");
    }

    @Test
    void criarDelegaAoServiceERetorna201() {
        when(service.criar(any(CategoriaPublicacao.class))).thenReturn(entidade);
        ResponseEntity<CategoriaPublicacao> response = controller.criar(request());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(entidade, response.getBody());
        verify(service).criar(any(CategoriaPublicacao.class));
    }

    @Test
    void listarDelegaAoService() {
        when(service.listar()).thenReturn(List.of(entidade));
        assertEquals(List.of(entidade), controller.listar());
        verify(service).listar();
    }

    @Test
    void buscarPorIdDelegaAoService() {
        when(service.buscarPorId(expectedId)).thenReturn(entidade);
        assertSame(entidade, controller.buscarPorId(10L, "bem-estar"));
        verify(service).buscarPorId(expectedId);
    }

    @Test
    void atualizarDelegaAoService() {
        when(service.atualizar(eq(expectedId), any(CategoriaPublicacao.class))).thenReturn(entidade);
        assertSame(entidade, controller.atualizar(10L, "bem-estar", request()));
        verify(service).atualizar(eq(expectedId), any(CategoriaPublicacao.class));
    }

    @Test
    void excluirRetorna204EDependeDoService() {
        ResponseEntity<Void> response = controller.excluir(10L, "bem-estar");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).excluir(expectedId);
    }
}

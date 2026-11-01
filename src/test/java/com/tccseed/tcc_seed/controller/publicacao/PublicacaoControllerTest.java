package com.tccseed.tcc_seed.controller.publicacao;

import com.tccseed.tcc_seed.controller.publicacao.dto.PublicacaoRequest;
import com.tccseed.tcc_seed.domain.entity.Publicacao;
import com.tccseed.tcc_seed.service.publicacao.PublicacaoService;
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
class PublicacaoControllerTest {
    @Mock private PublicacaoService service;
    private PublicacaoController controller;
    private final Publicacao entidade = new Publicacao();
    private final Long id = 1L;

    private PublicacaoRequest request() {
        return new PublicacaoRequest(null, null, null, null);
    }

    @BeforeEach
    void preparar() {
        controller = new PublicacaoController(service);
    }

    @Test
    void criarDelegaAoServiceERetorna201() {
        when(service.criar(any(Publicacao.class))).thenReturn(entidade);
        ResponseEntity<Publicacao> response = controller.criar(request());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(entidade, response.getBody());
        verify(service).criar(any(Publicacao.class));
    }

    @Test
    void listarDelegaAoService() {
        when(service.listar()).thenReturn(List.of(entidade));
        assertEquals(List.of(entidade), controller.listar());
        verify(service).listar();
    }

    @Test
    void buscarPorIdDelegaAoService() {
        when(service.buscarPorId(1L)).thenReturn(entidade);
        assertSame(entidade, controller.buscarPorId(1L));
        verify(service).buscarPorId(1L);
    }

    @Test
    void atualizarDelegaAoService() {
        when(service.atualizar(eq(1L), any(Publicacao.class))).thenReturn(entidade);
        assertSame(entidade, controller.atualizar(1L, request()));
        verify(service).atualizar(eq(1L), any(Publicacao.class));
    }

    @Test
    void excluirRetorna204EDependeDoService() {
        ResponseEntity<Void> response = controller.excluir(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).excluir(1L);
    }
}

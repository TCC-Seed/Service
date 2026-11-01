package com.tccseed.tcc_seed.controller.evento;

import com.tccseed.tcc_seed.controller.evento.dto.CategoriaEventoRequest;
import com.tccseed.tcc_seed.domain.entity.CategoriaEvento;
import com.tccseed.tcc_seed.domain.entity.id.CategoriaEventoId;
import com.tccseed.tcc_seed.service.evento.CategoriaEventoService;
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
class CategoriaEventoControllerTest {
    @Mock private CategoriaEventoService service;
    private CategoriaEventoController controller;
    private final CategoriaEvento entidade = new CategoriaEvento();
    private final CategoriaEventoId expectedId = new CategoriaEventoId();

    private CategoriaEventoRequest request() {
        return new CategoriaEventoRequest(null, null);
    }

    @BeforeEach
    void preparar() {
        controller = new CategoriaEventoController(service);
        expectedId.setIdEvento(10L);
        expectedId.setCategoria("bem-estar");
    }

    @Test
    void criarDelegaAoServiceERetorna201() {
        when(service.criar(any(CategoriaEvento.class))).thenReturn(entidade);
        ResponseEntity<CategoriaEvento> response = controller.criar(request());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(entidade, response.getBody());
        verify(service).criar(any(CategoriaEvento.class));
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
        when(service.atualizar(eq(expectedId), any(CategoriaEvento.class))).thenReturn(entidade);
        assertSame(entidade, controller.atualizar(10L, "bem-estar", request()));
        verify(service).atualizar(eq(expectedId), any(CategoriaEvento.class));
    }

    @Test
    void excluirRetorna204EDependeDoService() {
        ResponseEntity<Void> response = controller.excluir(10L, "bem-estar");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).excluir(expectedId);
    }
}

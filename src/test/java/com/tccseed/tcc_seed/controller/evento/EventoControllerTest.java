package com.tccseed.tcc_seed.controller.evento;

import com.tccseed.tcc_seed.controller.evento.dto.EventoRequest;
import com.tccseed.tcc_seed.domain.entity.Evento;
import com.tccseed.tcc_seed.service.evento.EventoService;
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
class EventoControllerTest {
    @Mock private EventoService service;
    private EventoController controller;
    private final Evento entidade = new Evento();
    private final Long id = 1L;

    private EventoRequest request() {
        return new EventoRequest(null, null, null, null);
    }

    @BeforeEach
    void preparar() {
        controller = new EventoController(service);
    }

    @Test
    void criarDelegaAoServiceERetorna201() {
        when(service.criar(any(Evento.class))).thenReturn(entidade);
        ResponseEntity<Evento> response = controller.criar(request());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(entidade, response.getBody());
        verify(service).criar(any(Evento.class));
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
        when(service.atualizar(eq(1L), any(Evento.class))).thenReturn(entidade);
        assertSame(entidade, controller.atualizar(1L, request()));
        verify(service).atualizar(eq(1L), any(Evento.class));
    }

    @Test
    void excluirRetorna204EDependeDoService() {
        ResponseEntity<Void> response = controller.excluir(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).excluir(1L);
    }
}

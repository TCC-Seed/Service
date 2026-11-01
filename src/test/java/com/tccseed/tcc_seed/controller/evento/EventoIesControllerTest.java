package com.tccseed.tcc_seed.controller.evento;

import com.tccseed.tcc_seed.domain.entity.EventoIes;
import com.tccseed.tcc_seed.domain.entity.id.EventoIesId;
import com.tccseed.tcc_seed.service.evento.EventoIesService;
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
class EventoIesControllerTest {
    @Mock private EventoIesService service;
    private EventoIesController controller;
    private final EventoIes entidade = new EventoIes();
    private final EventoIesId expectedId = new EventoIesId();

    @BeforeEach
    void preparar() {
        controller = new EventoIesController(service);
        expectedId.setIdIes(10L);
        expectedId.setIdEvento(10L);
    }

    @Test
    void criarDelegaAoServiceERetorna201() {
        when(service.criar(entidade)).thenReturn(entidade);
        ResponseEntity<EventoIes> response = controller.criar(entidade);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(entidade, response.getBody());
        verify(service).criar(entidade);
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
        assertSame(entidade, controller.buscarPorId(10L, 10L));
        verify(service).buscarPorId(expectedId);
    }

    @Test
    void atualizarDelegaAoService() {
        when(service.atualizar(expectedId, entidade)).thenReturn(entidade);
        assertSame(entidade, controller.atualizar(10L, 10L, entidade));
        verify(service).atualizar(expectedId, entidade);
    }

    @Test
    void excluirRetorna204EDependeDoService() {
        ResponseEntity<Void> response = controller.excluir(10L, 10L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).excluir(expectedId);
    }
}

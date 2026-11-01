package com.tccseed.tcc_seed.controller.formulario;

import com.tccseed.tcc_seed.domain.entity.FormularioPersonalizado;
import com.tccseed.tcc_seed.service.formulario.FormularioPersonalizadoService;
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
class FormularioPersonalizadoControllerTest {
    @Mock private FormularioPersonalizadoService service;
    private FormularioPersonalizadoController controller;
    private final FormularioPersonalizado entidade = new FormularioPersonalizado();
    private final Long id = 1L;

    @BeforeEach
    void preparar() {
        controller = new FormularioPersonalizadoController(service);
    }

    @Test
    void criarDelegaAoServiceERetorna201() {
        when(service.criar(entidade)).thenReturn(entidade);
        ResponseEntity<FormularioPersonalizado> response = controller.criar(entidade);
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
        when(service.buscarPorId(1L)).thenReturn(entidade);
        assertSame(entidade, controller.buscarPorId(1L));
        verify(service).buscarPorId(1L);
    }

    @Test
    void atualizarDelegaAoService() {
        when(service.atualizar(1L, entidade)).thenReturn(entidade);
        assertSame(entidade, controller.atualizar(1L, entidade));
        verify(service).atualizar(1L, entidade);
    }

    @Test
    void excluirRetorna204EDependeDoService() {
        ResponseEntity<Void> response = controller.excluir(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).excluir(1L);
    }
}

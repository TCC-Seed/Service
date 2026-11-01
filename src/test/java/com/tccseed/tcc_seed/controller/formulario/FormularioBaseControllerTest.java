package com.tccseed.tcc_seed.controller.formulario;

import com.tccseed.tcc_seed.domain.entity.FormularioBase;
import com.tccseed.tcc_seed.service.formulario.FormularioBaseService;
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
class FormularioBaseControllerTest {
    @Mock private FormularioBaseService service;
    private FormularioBaseController controller;
    private final FormularioBase entidade = new FormularioBase();
    private final Long id = 1L;

    @BeforeEach
    void preparar() {
        controller = new FormularioBaseController(service);
    }

    @Test
    void criarDelegaAoServiceERetorna201() {
        when(service.criar(entidade)).thenReturn(entidade);
        ResponseEntity<FormularioBase> response = controller.criar(entidade);
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

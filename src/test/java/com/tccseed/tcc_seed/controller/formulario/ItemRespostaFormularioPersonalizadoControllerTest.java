package com.tccseed.tcc_seed.controller.formulario;

import com.tccseed.tcc_seed.domain.entity.ItemRespostaFormularioPersonalizado;
import com.tccseed.tcc_seed.domain.entity.id.ItemRespostaFormularioPersonalizadoId;
import com.tccseed.tcc_seed.service.formulario.ItemRespostaFormularioPersonalizadoService;
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
class ItemRespostaFormularioPersonalizadoControllerTest {
    @Mock private ItemRespostaFormularioPersonalizadoService service;
    private ItemRespostaFormularioPersonalizadoController controller;
    private final ItemRespostaFormularioPersonalizado entidade = new ItemRespostaFormularioPersonalizado();
    private final ItemRespostaFormularioPersonalizadoId expectedId = new ItemRespostaFormularioPersonalizadoId();

    @BeforeEach
    void preparar() {
        controller = new ItemRespostaFormularioPersonalizadoController(service);
        expectedId.setIdResposta(10L);
        expectedId.setIdPergunta(10L);
    }

    @Test
    void criarDelegaAoServiceERetorna201() {
        when(service.criar(entidade)).thenReturn(entidade);
        ResponseEntity<ItemRespostaFormularioPersonalizado> response = controller.criar(entidade);
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

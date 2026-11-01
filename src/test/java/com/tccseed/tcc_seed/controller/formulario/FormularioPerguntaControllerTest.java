package com.tccseed.tcc_seed.controller.formulario;

import com.tccseed.tcc_seed.controller.formulario.dto.FormularioPerguntaRequest;
import com.tccseed.tcc_seed.domain.entity.FormularioPergunta;
import com.tccseed.tcc_seed.domain.entity.id.FormularioPerguntaId;
import com.tccseed.tcc_seed.service.formulario.FormularioPerguntaService;
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
class FormularioPerguntaControllerTest {
    @Mock private FormularioPerguntaService service;
    private FormularioPerguntaController controller;
    private final FormularioPergunta entidade = new FormularioPergunta();
    private final FormularioPerguntaId expectedId = new FormularioPerguntaId();

    private FormularioPerguntaRequest request() {
        return new FormularioPerguntaRequest(null, null);
    }

    @BeforeEach
    void preparar() {
        controller = new FormularioPerguntaController(service);
        expectedId.setIdFormularioPersonalizado(10L);
        expectedId.setIdPergunta(10L);
    }

    @Test
    void criarDelegaAoServiceERetorna201() {
        when(service.criar(any(FormularioPergunta.class))).thenReturn(entidade);
        ResponseEntity<FormularioPergunta> response = controller.criar(request());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(entidade, response.getBody());
        verify(service).criar(any(FormularioPergunta.class));
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
        when(service.atualizar(eq(expectedId), any(FormularioPergunta.class))).thenReturn(entidade);
        assertSame(entidade, controller.atualizar(10L, 10L, request()));
        verify(service).atualizar(eq(expectedId), any(FormularioPergunta.class));
    }

    @Test
    void excluirRetorna204EDependeDoService() {
        ResponseEntity<Void> response = controller.excluir(10L, 10L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).excluir(expectedId);
    }
}

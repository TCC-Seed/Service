package com.tccseed.tcc_seed.service.formulario;

import com.tccseed.tcc_seed.domain.entity.FormularioPersonalizado;
import com.tccseed.tcc_seed.repository.FormularioPersonalizadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FormularioPersonalizadoServiceTest {
    @Mock private FormularioPersonalizadoRepository repository;
    private FormularioPersonalizadoService service;
    private final FormularioPersonalizado entidade = new FormularioPersonalizado();
    private final Long id = 1L;

    @BeforeEach
    void preparar() {
        service = new FormularioPersonalizadoService(repository);
    }

    @Test
    void criarPersisteEntidade() {
        when(repository.save(entidade)).thenReturn(entidade);
        assertSame(entidade, service.criar(entidade));
        verify(repository).save(entidade);
    }

    @Test
    void listarRetornaTodosOsRegistros() {
        when(repository.findAll()).thenReturn(List.of(entidade));
        assertEquals(List.of(entidade), service.listar());
        verify(repository).findAll();
    }

    @Test
    void buscarPorIdRetornaRegistroExistente() {
        when(repository.findById(id)).thenReturn(Optional.of(entidade));
        assertSame(entidade, service.buscarPorId(id));
    }

    @Test
    void buscarPorIdInexistenteRetorna404() {
        when(repository.findById(id)).thenReturn(Optional.empty());
        ResponseStatusException erro = assertThrows(ResponseStatusException.class, () -> service.buscarPorId(id));
        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
    }

    @Test
    void atualizarRegistroExistentePersisteComIdDaRota() {
        when(repository.existsById(id)).thenReturn(true);
        when(repository.save(entidade)).thenReturn(entidade);
        assertSame(entidade, service.atualizar(id, entidade));
        assertEquals(id, entidade.getId());
        verify(repository).save(entidade);
    }

    @Test
    void atualizarRegistroInexistenteRetorna404SemSalvar() {
        when(repository.existsById(id)).thenReturn(false);
        ResponseStatusException erro = assertThrows(ResponseStatusException.class, () -> service.atualizar(id, entidade));
        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
        verify(repository, never()).save(any());
    }

    @Test
    void excluirRegistroExistente() {
        when(repository.existsById(id)).thenReturn(true);
        service.excluir(id);
        verify(repository).deleteById(id);
    }

    @Test
    void excluirRegistroInexistenteRetorna404SemExcluir() {
        when(repository.existsById(id)).thenReturn(false);
        ResponseStatusException erro = assertThrows(ResponseStatusException.class, () -> service.excluir(id));
        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
        verify(repository, never()).deleteById(any());
    }
}

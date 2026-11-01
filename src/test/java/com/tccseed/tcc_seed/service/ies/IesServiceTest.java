package com.tccseed.tcc_seed.service.ies;

import com.tccseed.tcc_seed.controller.ies.dto.IesRequest;
import com.tccseed.tcc_seed.domain.entity.*;
import com.tccseed.tcc_seed.domain.enums.TipoUsuario;
import com.tccseed.tcc_seed.repository.*;
import com.tccseed.tcc_seed.service.auditoria.AuditoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IesServiceTest {
    @Mock IesRepository iesRepository;
    @Mock UsuarioRepository usuarioRepository;
    @Mock EstudanteRepository estudanteRepository;
    @Mock FuncionarioRepository funcionarioRepository;
    @Mock AuditoriaService auditoria;
    @InjectMocks IesService service;
    Ies ies;
    Funcionario funcionario;
    String principal;

    @BeforeEach
    void setUp() {
        ies = new Ies();
        ies.setId(7L);
        ies.setNome("Universidade");
        ies.setChaveVinculo("segredo-original");
        funcionario = new Funcionario();
        funcionario.setId(10L);
        Ies outraIes = new Ies();
        outraIes.setId(8L);
        funcionario.setIes(outraIes);
        funcionario.setTipo(TipoUsuario.FUNCIONARIO);
        funcionario.setToken(UUID.randomUUID());
        principal = funcionario.getToken().toString();
    }

    @Test
    void consultaPublicaNuncaSerializaChave() {
        when(iesRepository.findById(7L)).thenReturn(Optional.of(ies));
        var response = service.consultar(7L);
        String json = new JsonMapper().writeValueAsString(response);
        assertEquals(7L, response.id());
        assertFalse(json.contains("segredo"));
        assertFalse(json.contains("chave"));
    }

    @Test
    void funcionarioDeOutraIesNaoPodeConsultarPrivado() {
        autenticar(funcionario);
        when(iesRepository.findById(7L)).thenReturn(Optional.of(ies));
        status(HttpStatus.FORBIDDEN, () -> service.consultarPrivada(7L, principal));
    }

    @Test
    void estudanteNaoAcessaChaveNemComVinculo() {
        Estudante estudante = new Estudante();
        estudante.setTipo(TipoUsuario.ESTUDANTE);
        autenticar(estudante);
        when(iesRepository.findById(7L)).thenReturn(Optional.of(ies));
        status(HttpStatus.FORBIDDEN, () -> service.consultarPrivada(7L, principal));
        verifyNoInteractions(estudanteRepository);
    }

    @Test
    void funcionarioVinculadoConsultaChave() {
        autenticar(funcionario);
        membro();
        when(iesRepository.findById(7L)).thenReturn(Optional.of(ies));
        assertEquals("segredo-original", service.consultarPrivada(7L, principal).chaveIes());
    }

    @Test
    void criacaoParaCadastroGeraSegredoAleatorio() {
        when(iesRepository.save(any())).thenAnswer(invocation -> {
            Ies nova = invocation.getArgument(0);
            nova.setId(8L);
            return nova;
        });
        Ies nova = service.criarParaCadastro(new IesRequest(" Universidade Nova ", " Região "));
        assertEquals("Universidade Nova", nova.getNome());
        assertEquals(8L, nova.getId());
        assertTrue(nova.getChaveVinculo().matches("[A-Za-z0-9_-]{43}"));
        verifyNoInteractions(estudanteRepository);
    }

    @Test
    void chaveIncorretaImpedeObterIesParaCadastro() {
        bloquear();
        status(HttpStatus.FORBIDDEN, () -> service.obterParaCadastro(7L, "errada"));
        verifyNoInteractions(estudanteRepository, funcionarioRepository);
    }

    @Test
    void chaveCorretaRetornaIesParaCadastro() {
        bloquear();
        assertSame(ies, service.obterParaCadastro(7L, "segredo-original"));
        verifyNoInteractions(estudanteRepository, funcionarioRepository);
    }

    @Test
    void outraIesNaoPodeSerAlteradaExcluidaOuTerChaveRenovada() {
        autenticar(funcionario);
        bloquear();
        status(HttpStatus.FORBIDDEN, () -> service.atualizar(7L, new IesRequest("Ataque", null), principal));
        status(HttpStatus.FORBIDDEN, () -> service.excluir(7L, principal));
        status(HttpStatus.FORBIDDEN, () -> service.renovarChave(7L, principal));
        assertEquals("Universidade", ies.getNome());
        assertEquals("segredo-original", ies.getChaveVinculo());
        verify(iesRepository, never()).delete(any());
        verify(estudanteRepository, never()).deleteAll(any());
        verifyNoInteractions(auditoria);
    }

    @Test
    void atualizacaoPreservaChaveEVinculos() {
        autenticar(funcionario);
        bloquear();
        membro();
        var response = service.atualizar(7L, new IesRequest(" Novo Nome ", null), principal);
        assertEquals("Novo Nome", response.nome());
        assertNull(response.regiaoAdministrativa());
        assertEquals("segredo-original", ies.getChaveVinculo());
        verify(estudanteRepository, never()).deleteAll(any());
        verify(auditoria).identificarUsuario(funcionario);
    }

    @Test
    void renovarChaveInvalidaAnteriorEPreservaMembros() {
        autenticar(funcionario);
        bloquear();
        membro();
        String nova = service.renovarChave(7L, principal).chaveIes();
        assertNotEquals("segredo-original", nova);
        assertEquals(43, nova.length());
        status(HttpStatus.FORBIDDEN, () -> service.obterParaCadastro(7L, "segredo-original"));
        service.obterParaCadastro(7L, nova);
        verify(estudanteRepository, never()).deleteAll(any());
    }

    @Test
    void exclusaoRemoveEstudantesEFuncionariosAntesDaIes() {
        autenticar(funcionario);
        bloquear();
        membro();
        when(funcionarioRepository.findByIesId(7L)).thenReturn(List.of(funcionario));
        Estudante estudante = new Estudante();
        estudante.setId(20L);
        estudante.setIes(ies);
        when(estudanteRepository.findByIesId(7L)).thenReturn(List.of(estudante));
        service.excluir(7L, principal);
        var ordem = inOrder(auditoria, estudanteRepository, funcionarioRepository, iesRepository);
        ordem.verify(auditoria).identificarUsuario(funcionario);
        ordem.verify(estudanteRepository).findByIesId(7L);
        ordem.verify(estudanteRepository).deleteAll(List.of(estudante));
        ordem.verify(estudanteRepository).flush();
        ordem.verify(funcionarioRepository).findByIesId(7L);
        ordem.verify(funcionarioRepository).deleteAll(List.of(funcionario));
        ordem.verify(funcionarioRepository).flush();
        ordem.verify(iesRepository).delete(ies);
        ordem.verify(iesRepository).flush();
    }

    @Test
    void dependenciasImpedemExclusaoComConflito() {
        autenticar(funcionario);
        bloquear();
        membro();
        doThrow(new DataIntegrityViolationException("foreign key")).when(iesRepository).flush();
        status(HttpStatus.CONFLICT, () -> service.excluir(7L, principal));
    }

    @Test
    void iesInexistenteEPaginacaoInvalida() {
        status(HttpStatus.NOT_FOUND, () -> service.consultar(99L));
        status(HttpStatus.BAD_REQUEST, () -> service.listar(-1, 20));
        status(HttpStatus.BAD_REQUEST, () -> service.listar(0, 101));
        status(HttpStatus.BAD_REQUEST, () -> service.listar(0, 0));
    }

    private void autenticar(Usuario usuario) {
        when(usuarioRepository.findByToken(UUID.fromString(principal))).thenReturn(Optional.of(usuario));
    }
    private void membro() {
        funcionario.setIes(ies);
    }
    private void bloquear() {
        when(iesRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(ies));
    }
    private void status(HttpStatus esperado, Runnable action) {
        assertEquals(esperado, assertThrows(ResponseStatusException.class, action::run).getStatusCode());
    }
}

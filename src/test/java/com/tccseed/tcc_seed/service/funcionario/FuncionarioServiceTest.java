package com.tccseed.tcc_seed.service.funcionario;

import com.tccseed.tcc_seed.controller.funcionario.dto.*;
import com.tccseed.tcc_seed.domain.entity.*;
import com.tccseed.tcc_seed.domain.enums.TipoUsuario;
import com.tccseed.tcc_seed.repository.*;
import com.tccseed.tcc_seed.service.auditoria.AuditoriaService;
import com.tccseed.tcc_seed.service.ies.IesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FuncionarioServiceTest {
    @Mock UsuarioRepository usuarios;
    @Mock FuncionarioRepository funcionarios;
    @Mock EstudanteRepository estudantes;
    @Mock IesRepository instituicoes;
    @Mock IesService iesService;
    @Mock AuditoriaService auditoria;
    @Mock PasswordEncoder encoder;
    @InjectMocks FuncionarioService service;
    Funcionario funcionario;
    Ies ies;
    UUID token;

    @BeforeEach
    void preparar() {
        token = UUID.randomUUID();
        ies = new Ies();
        ies.setId(5L);
        funcionario = new Funcionario();
        funcionario.setId(10L);
        funcionario.setTipo(TipoUsuario.FUNCIONARIO);
        funcionario.setToken(token);
        funcionario.setUsername("ana");
        funcionario.setEmail("ana@example.com");
        funcionario.setSenha("hash");
        funcionario.setNome("Ana");
        funcionario.setFormacao("Psicologia");
        funcionario.setIes(ies);
    }

    @Test
    void consultaSomenteTitularEEditaSemMudarIes() {
        when(usuarios.findByToken(token)).thenReturn(Optional.of(funcionario));
        assertEquals(10L, service.consultar(token.toString()).id());
        when(funcionarios.findByTokenForUpdate(token)).thenReturn(Optional.of(funcionario));
        doAnswer(invocation -> { assertEquals("ana", funcionario.getUsername()); return null; })
                .when(auditoria).identificarUsuario(funcionario);
        var resposta = service.atualizar(token.toString(),
                new FuncionarioAtualizacaoRequest(null, " nova ", null, " Medicina "));
        assertEquals("nova", resposta.username());
        assertEquals("Medicina", resposta.formacao());
        assertEquals("Ana", resposta.nome());
        assertEquals(5L, resposta.iesId());
        verify(funcionarios).flush();
    }

    @Test
    void estudanteNaoTemAcessoANenhumaOperacao() {
        Estudante estudante = new Estudante();
        estudante.setTipo(TipoUsuario.ESTUDANTE);
        when(usuarios.findByToken(token)).thenReturn(Optional.of(estudante));
        status(HttpStatus.FORBIDDEN, () -> service.consultar(token.toString()));
        status(HttpStatus.FORBIDDEN, () -> service.atualizar(token.toString(), new FuncionarioAtualizacaoRequest(null, null, "Nome", null)));
        status(HttpStatus.FORBIDDEN, () -> service.alterarSenha(token.toString(), new SenhaAtualizacaoRequest("a", "b")));
        status(HttpStatus.FORBIDDEN, () -> service.excluir(token.toString(), new ContaExclusaoRequest("a")));
        verifyNoInteractions(auditoria, instituicoes, iesService, encoder);
        verify(funcionarios, never()).delete(any());
    }

    @Test
    void comOutroFuncionarioRemoveSomenteTitularNaOrdemCorreta() {
        prepararExclusao();
        when(funcionarios.countByIesId(5L)).thenReturn(2L);
        service.excluir(token.toString(), new ContaExclusaoRequest("atual"));
        var ordem = inOrder(instituicoes, funcionarios, auditoria);
        ordem.verify(instituicoes).findByIdForUpdate(5L);
        ordem.verify(funcionarios).findByTokenForUpdate(token);
        ordem.verify(funcionarios).countByIesId(5L);
        ordem.verify(auditoria).identificarUsuario(funcionario);
        ordem.verify(funcionarios).delete(funcionario);
        ordem.verify(funcionarios).flush();
        verifyNoInteractions(iesService, estudantes);
        verify(instituicoes, never()).delete(any());
    }

    @Test
    void ultimoFuncionarioRemoveEstudantesContasEInstituicao() {
        prepararExclusao();
        when(funcionarios.countByIesId(5L)).thenReturn(1L);
        when(usuarios.findByToken(token)).thenReturn(Optional.of(funcionario));
        Estudante estudante = new Estudante();
        when(estudantes.findByIesId(5L)).thenReturn(List.of(estudante));
        when(funcionarios.findByIesId(5L)).thenReturn(List.of(funcionario));
        var completo = new FuncionarioService(usuarios, funcionarios, instituicoes,
                new IesService(instituicoes, usuarios, estudantes, funcionarios, auditoria), auditoria, encoder);
        completo.excluir(token.toString(), new ContaExclusaoRequest("atual"));
        var ordem = inOrder(auditoria, estudantes, funcionarios, instituicoes);
        ordem.verify(auditoria).identificarUsuario(funcionario);
        ordem.verify(estudantes).deleteAll(List.of(estudante));
        ordem.verify(estudantes).flush();
        ordem.verify(funcionarios).deleteAll(List.of(funcionario));
        ordem.verify(funcionarios).flush();
        ordem.verify(instituicoes).delete(ies);
        ordem.verify(instituicoes).flush();
    }

    @Test
    void senhaIncorretaNaoRemoveNemContaNemIes() {
        when(funcionarios.findIesIdByToken(token)).thenReturn(Optional.of(5L));
        when(instituicoes.findByIdForUpdate(5L)).thenReturn(Optional.of(ies));
        when(funcionarios.findByTokenForUpdate(token)).thenReturn(Optional.of(funcionario));
        status(HttpStatus.FORBIDDEN, () -> service.excluir(token.toString(), new ContaExclusaoRequest("errada")));
        verifyNoInteractions(auditoria, iesService);
        verify(funcionarios, never()).countByIesId(anyLong());
        verify(funcionarios, never()).delete(any());
    }

    @Test
    void tokenRevogadoDuranteEsperaNaoPodeExcluir() {
        when(funcionarios.findIesIdByToken(token)).thenReturn(Optional.of(5L));
        when(instituicoes.findByIdForUpdate(5L)).thenReturn(Optional.of(ies));
        status(HttpStatus.UNAUTHORIZED, () -> service.excluir(token.toString(), new ContaExclusaoRequest("atual")));
        verifyNoInteractions(encoder, auditoria, iesService);
        verify(funcionarios, never()).countByIesId(anyLong());
    }

    @Test
    void trocaSenhaExigeAtualERenovaToken() {
        when(funcionarios.findByTokenForUpdate(token)).thenReturn(Optional.of(funcionario));
        status(HttpStatus.FORBIDDEN, () -> service.alterarSenha(token.toString(), new SenhaAtualizacaoRequest("errada", "nova")));
        assertEquals(token, funcionario.getToken());
        when(encoder.matches("atual", "hash")).thenReturn(true);
        when(encoder.encode(" nova ")).thenReturn("novo-hash");
        service.alterarSenha(token.toString(), new SenhaAtualizacaoRequest("atual", " nova "));
        assertNotEquals(token, funcionario.getToken());
        assertEquals("novo-hash", funcionario.getSenha());
        verify(auditoria).identificarUsuario(funcionario);
        verify(funcionarios).flush();
    }

    @Test
    void dependenciasNaExclusaoIndividualGeramConflito() {
        prepararExclusao();
        when(funcionarios.countByIesId(5L)).thenReturn(2L);
        doThrow(new DataIntegrityViolationException("fk")).when(funcionarios).flush();
        status(HttpStatus.CONFLICT, () -> service.excluir(token.toString(), new ContaExclusaoRequest("atual")));
        verifyNoInteractions(iesService);
    }

    private void prepararExclusao() {
        when(funcionarios.findIesIdByToken(token)).thenReturn(Optional.of(5L));
        when(instituicoes.findByIdForUpdate(5L)).thenReturn(Optional.of(ies));
        when(funcionarios.findByTokenForUpdate(token)).thenReturn(Optional.of(funcionario));
        when(encoder.matches("atual", "hash")).thenReturn(true);
    }
    private void status(HttpStatus esperado, Runnable action) {
        assertEquals(esperado, assertThrows(ResponseStatusException.class, action::run).getStatusCode());
    }
}

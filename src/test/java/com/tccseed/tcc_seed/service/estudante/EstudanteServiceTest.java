package com.tccseed.tcc_seed.service.estudante;

import com.tccseed.tcc_seed.controller.estudante.dto.*;
import com.tccseed.tcc_seed.domain.entity.*;
import com.tccseed.tcc_seed.domain.enums.TipoUsuario;
import com.tccseed.tcc_seed.repository.*;
import com.tccseed.tcc_seed.service.auditoria.AuditoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstudanteServiceTest {
    @Mock UsuarioRepository usuarios;
    @Mock EstudanteRepository estudantes;
    @Mock AuditoriaService auditoria;
    @Mock PasswordEncoder encoder;
    @InjectMocks EstudanteService service;
    Estudante estudante;
    UUID token;

    @BeforeEach
    void preparar() {
        token = UUID.randomUUID();
        estudante = new Estudante();
        estudante.setId(10L);
        estudante.setTipo(TipoUsuario.ESTUDANTE);
        estudante.setToken(token);
        estudante.setUsername("ana");
        estudante.setEmail("ana@example.com");
        estudante.setSenha("hash-antigo");
        estudante.setNome("Ana");
        estudante.setMatricula("123");
        estudante.setNascimento(LocalDate.of(2000, 1, 1));
        estudante.setPaisOrigem("Brasil");
        Ies ies = new Ies();
        ies.setId(5L);
        estudante.setIes(ies);
    }

    @Test
    void consultaUsaExclusivamenteTokenDoPrincipal() {
        when(usuarios.findByToken(token)).thenReturn(Optional.of(estudante));
        var response = service.consultar(token.toString());
        assertEquals(10L, response.id());
        assertEquals(5L, response.iesId());
        verify(usuarios).findByToken(token);
        verifyNoInteractions(estudantes, auditoria, encoder);
    }

    @Test
    void funcionarioNaoPodeConsultarEditarTrocarSenhaOuExcluir() {
        Funcionario funcionario = new Funcionario();
        funcionario.setTipo(TipoUsuario.FUNCIONARIO);
        when(usuarios.findByToken(token)).thenReturn(Optional.of(funcionario));
        status(HttpStatus.FORBIDDEN, () -> service.consultar(token.toString()));
        status(HttpStatus.FORBIDDEN, () -> service.atualizar(token.toString(), patch()));
        status(HttpStatus.FORBIDDEN, () -> service.alterarSenha(token.toString(), new SenhaAtualizacaoRequest("a", "b")));
        status(HttpStatus.FORBIDDEN, () -> service.excluir(token.toString(), new ContaExclusaoRequest("a")));
        verifyNoInteractions(auditoria, encoder);
        verify(estudantes, never()).delete(any());
        verify(estudantes, never()).flush();
    }

    @Test
    void principalInvalidoOuContaExcluidaNaoAutentica() {
        status(HttpStatus.UNAUTHORIZED, () -> service.consultar("invalido"));
        status(HttpStatus.UNAUTHORIZED, () -> service.consultar(null));
        status(HttpStatus.UNAUTHORIZED, () -> service.consultar(token.toString()));
        verifyNoInteractions(auditoria);
    }

    @Test
    void edicaoParcialMantemIesESenhaEAuditaUsernameAnterior() {
        bloquear();
        doAnswer(invocation -> {
            assertEquals("ana", estudante.getUsername());
            return null;
        }).when(auditoria).identificarUsuario(estudante);
        var response = service.atualizar(token.toString(), patch());
        assertEquals("ana.nova", response.username());
        assertEquals("456", response.matricula());
        assertEquals("Ana", response.nome());
        assertEquals(5L, response.iesId());
        assertEquals("hash-antigo", estudante.getSenha());
        assertEquals(token, estudante.getToken());
        var ordem = inOrder(auditoria, estudantes);
        ordem.verify(auditoria).identificarUsuario(estudante);
        ordem.verify(estudantes).flush();
    }

    @Test
    void unicidadeRetornaConflitoSemOcultarCausa() {
        bloquear();
        doThrow(new DataIntegrityViolationException("unique")).when(estudantes).flush();
        status(HttpStatus.CONFLICT, () -> service.atualizar(token.toString(), patch()));
    }

    @Test
    void senhaIncorretaImpedeTrocaEExclusao() {
        bloquear();
        status(HttpStatus.FORBIDDEN, () -> service.alterarSenha(token.toString(), new SenhaAtualizacaoRequest("errada", "nova")));
        status(HttpStatus.FORBIDDEN, () -> service.excluir(token.toString(), new ContaExclusaoRequest("errada")));
        assertEquals(token, estudante.getToken());
        assertEquals("hash-antigo", estudante.getSenha());
        verifyNoInteractions(auditoria);
        verify(estudantes, never()).delete(any());
        verify(estudantes, never()).flush();
    }

    @Test
    void trocaSenhaRenovaIdentidadeDeTodosOsTokens() {
        bloquear();
        when(encoder.matches(" atual ", "hash-antigo")).thenReturn(true);
        when(encoder.encode(" nova ")).thenReturn("hash-novo");
        service.alterarSenha(token.toString(), new SenhaAtualizacaoRequest(" atual ", " nova "));
        assertNotEquals(token, estudante.getToken());
        assertEquals("hash-novo", estudante.getSenha());
        var ordem = inOrder(auditoria, encoder, estudantes);
        ordem.verify(auditoria).identificarUsuario(estudante);
        ordem.verify(encoder).encode(" nova ");
        ordem.verify(estudantes).flush();
    }

    @Test
    void exclusaoExigeSenhaEAuditaAntesDeRemoverEntidade() {
        bloquear();
        when(encoder.matches("atual", "hash-antigo")).thenReturn(true);
        service.excluir(token.toString(), new ContaExclusaoRequest("atual"));
        var ordem = inOrder(auditoria, estudantes);
        ordem.verify(auditoria).identificarUsuario(estudante);
        ordem.verify(estudantes).delete(estudante);
        ordem.verify(estudantes).flush();
        assertEquals(5L, estudante.getIes().getId());
    }

    private void bloquear() {
        when(estudantes.findByTokenForUpdate(token)).thenReturn(Optional.of(estudante));
    }
    private EstudanteAtualizacaoRequest patch() {
        return new EstudanteAtualizacaoRequest(null, " ana.nova ", null, " 456 ", null, null, null);
    }
    private void status(HttpStatus esperado, Runnable action) {
        assertEquals(esperado, assertThrows(ResponseStatusException.class, action::run).getStatusCode());
    }
}

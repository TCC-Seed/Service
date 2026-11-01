package com.tccseed.tcc_seed.service.auth;

import com.tccseed.tcc_seed.controller.auth.dto.FuncionarioCadastroRequest;
import com.tccseed.tcc_seed.controller.ies.dto.IesRequest;
import com.tccseed.tcc_seed.domain.entity.Funcionario;
import com.tccseed.tcc_seed.domain.entity.Ies;
import com.tccseed.tcc_seed.repository.*;
import com.tccseed.tcc_seed.security.JwtService;
import com.tccseed.tcc_seed.service.ies.IesService;
import com.tccseed.tcc_seed.service.auditoria.AuditoriaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FuncionarioCadastroTest {
    @Mock UsuarioRepository usuarios;
    @Mock FuncionarioRepository funcionarios;
    @Mock EstudanteRepository estudantes;
    @Mock PasswordEncoder encoder;
    @Mock JwtService jwt;
    @Mock IesService ies;
    @Mock AuditoriaService auditoria;
    @InjectMocks AuthService service;

    @Test
    void rejeitaCadastroSemIesEOpcoesAmbiguas() {
        IesRequest nova = new IesRequest("IES", null);
        for (var request : new FuncionarioCadastroRequest[]{
                request(null, null, null), request(1L, null, null), request(null, "chave", null),
                request(1L, " ", null), request(1L, "chave", nova), request(null, "chave", nova),
                request(0L, "chave", null)}) {
            assertFalse(request.isVinculoValido());
            assertEquals(HttpStatus.BAD_REQUEST, assertThrows(ResponseStatusException.class,
                    () -> service.cadastrarFuncionario(request)).getStatusCode());
        }
        verifyNoInteractions(funcionarios, ies);
    }

    @Test
    void cadastroCriaIesAntesDeSalvarFuncionario() {
        salvarFuncionario();
        IesRequest nova = new IesRequest("IES", null);
        Ies instituicao = new Ies();
        instituicao.setId(1L);
        when(ies.criarParaCadastro(nova)).thenReturn(instituicao);
        var response = service.cadastrarFuncionario(request(null, null, nova));
        assertEquals(10L, response.id());
        var ordem = inOrder(auditoria, ies, funcionarios);
        ordem.verify(auditoria).identificarCadastroPublico();
        ordem.verify(ies).criarParaCadastro(nova);
        ordem.verify(funcionarios).saveAndFlush(argThat(f -> f.getIes() == instituicao));
        verify(ies, never()).obterParaCadastro(any(), any());
    }

    @Test
    void cadastroExistenteExigeChaveEPropagaFalha() {
        when(encoder.encode("senha")).thenReturn("hash");
        when(ies.obterParaCadastro(1L, "errada")).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));
        assertEquals(HttpStatus.FORBIDDEN, assertThrows(ResponseStatusException.class,
                () -> service.cadastrarFuncionario(request(1L, "errada", null))).getStatusCode());
        verify(ies, never()).criarParaCadastro(any());
        verifyNoInteractions(funcionarios);
    }

    @Test
    void cadastroVinculaFuncionarioExistenteComChave() {
        salvarFuncionario();
        Ies instituicao = new Ies();
        instituicao.setId(1L);
        when(ies.obterParaCadastro(1L, "correta")).thenReturn(instituicao);
        service.cadastrarFuncionario(request(1L, "correta", null));
        var ordem = inOrder(ies, funcionarios);
        ordem.verify(ies).obterParaCadastro(1L, "correta");
        ordem.verify(funcionarios).saveAndFlush(argThat(f -> f.getIes() == instituicao));
    }

    private void salvarFuncionario() {
        when(encoder.encode("senha")).thenReturn("hash");
        when(funcionarios.saveAndFlush(any())).thenAnswer(invocation -> {
            Funcionario f = invocation.getArgument(0);
            f.setId(10L);
            return f;
        });
    }
    private FuncionarioCadastroRequest request(Long id, String chave, IesRequest nova) {
        return new FuncionarioCadastroRequest("a@example.com", "usuario", "senha", "Nome", "Formação", id, chave, nova);
    }
}

package com.tccseed.tcc_seed.service.auth;

import com.tccseed.tcc_seed.controller.auth.dto.*;
import com.tccseed.tcc_seed.controller.ies.dto.IesRequest;
import com.tccseed.tcc_seed.domain.entity.Funcionario;
import com.tccseed.tcc_seed.domain.enums.TipoUsuario;
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

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsernameLoginTest {
    @Mock UsuarioRepository usuarios;
    @Mock FuncionarioRepository funcionarios;
    @Mock EstudanteRepository estudantes;
    @Mock PasswordEncoder encoder;
    @Mock JwtService jwt;
    @Mock IesService ies;
    @Mock AuditoriaService auditoria;
    @InjectMocks AuthService service;

    @Test
    void loginUsaUsernameRemoveEspacosEPreservaMaiusculas() {
        Funcionario usuario = usuario();
        when(usuarios.findByUsername("Joao")).thenReturn(Optional.of(usuario));
        when(encoder.matches(" senha ", "hash")).thenReturn(true);
        when(jwt.createToken(usuario)).thenReturn("jwt");
        var result = service.autenticar(new LoginRequest(" Joao ", " senha "));
        assertEquals("Joao", result.usuario().username());
        assertEquals("jwt", result.jwt());
        verify(usuarios).findByUsername("Joao");
        verifyNoMoreInteractions(usuarios);
    }

    @Test
    void usernameDesconhecidoESenhaIncorretaRetornamMesmoErro() {
        var desconhecido = assertThrows(ResponseStatusException.class,
                () -> service.autenticar(new LoginRequest("desconhecido", "senha")));
        when(usuarios.findByUsername("Joao")).thenReturn(Optional.of(usuario()));
        when(encoder.matches("errada", "hash")).thenReturn(false);
        var senha = assertThrows(ResponseStatusException.class,
                () -> service.autenticar(new LoginRequest("Joao", "errada")));
        assertEquals(HttpStatus.UNAUTHORIZED, desconhecido.getStatusCode());
        assertEquals(desconhecido.getStatusCode(), senha.getStatusCode());
        assertEquals(desconhecido.getReason(), senha.getReason());
        assertEquals("Username ou senha inválidos.", senha.getReason());
        verifyNoInteractions(jwt);
    }

    @Test
    void emailNaoTemFallbackNaAutenticacao() {
        var erro = assertThrows(ResponseStatusException.class,
                () -> service.autenticar(new LoginRequest("contato@example.com", "senha")));
        assertEquals(HttpStatus.UNAUTHORIZED, erro.getStatusCode());
        verify(usuarios).findByUsername("contato@example.com");
        verifyNoMoreInteractions(usuarios);
        verifyNoInteractions(encoder, jwt);
    }

    @Test
    void usernameDuplicadoImpedeCadastroDosDoisTipos() {
        when(usuarios.existsByUsername("Joao")).thenReturn(true);
        var funcionario = new FuncionarioCadastroRequest("novo@example.com", " Joao ", "senha", "Nome", "Formação",
                null, null, new IesRequest("IES", null));
        var estudante = new EstudanteCadastroRequest("novo@example.com", " Joao ", "senha", "Nome", "123",
                LocalDate.of(2000, 1, 1), "Brasil", 1L);
        assertEquals(HttpStatus.CONFLICT, assertThrows(ResponseStatusException.class,
                () -> service.cadastrarFuncionario(funcionario)).getStatusCode());
        assertEquals(HttpStatus.CONFLICT, assertThrows(ResponseStatusException.class,
                () -> service.cadastrarEstudante(estudante)).getStatusCode());
        verifyNoInteractions(funcionarios, estudantes, ies, encoder);
    }

    private Funcionario usuario() {
        Funcionario usuario = new Funcionario();
        usuario.setId(1L);
        usuario.setUsername("Joao");
        usuario.setEmail("contato@example.com");
        usuario.setSenha("hash");
        usuario.setTipo(TipoUsuario.FUNCIONARIO);
        return usuario;
    }
}

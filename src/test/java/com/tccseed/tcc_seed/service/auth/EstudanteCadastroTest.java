package com.tccseed.tcc_seed.service.auth;

import com.tccseed.tcc_seed.controller.auth.dto.EstudanteCadastroRequest;
import com.tccseed.tcc_seed.domain.entity.Estudante;
import com.tccseed.tcc_seed.domain.entity.Ies;
import com.tccseed.tcc_seed.domain.enums.TipoUsuario;
import com.tccseed.tcc_seed.repository.EstudanteRepository;
import com.tccseed.tcc_seed.repository.FuncionarioRepository;
import com.tccseed.tcc_seed.repository.UsuarioRepository;
import com.tccseed.tcc_seed.security.JwtService;
import com.tccseed.tcc_seed.service.auditoria.AuditoriaService;
import com.tccseed.tcc_seed.service.ies.IesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstudanteCadastroTest {
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private EstudanteRepository estudanteRepository;
    @Mock private FuncionarioRepository funcionarioRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private IesService iesService;
    @Mock private AuditoriaService auditoriaService;
    private AuthService service;

    @BeforeEach
    void preparar() {
        service = new AuthService(usuarioRepository, estudanteRepository, funcionarioRepository,
                passwordEncoder, jwtService, iesService, auditoriaService);
    }

    @Test
    void cadastraEstudanteComCamposNormalizadosESenhaCodificada() {
        EstudanteCadastroRequest request = new EstudanteCadastroRequest(
                " Ana@Example.com ", " ana ", "senha", " Ana Silva ", " 20260001 ",
                LocalDate.of(2003, 2, 14), " Brasil ", 3L);
        Ies ies = new Ies();
        ies.setId(3L);
        when(usuarioRepository.existsByEmailIgnoreCase("ana@example.com")).thenReturn(false);
        when(usuarioRepository.existsByUsername("ana")).thenReturn(false);
        when(estudanteRepository.existsByMatricula("20260001")).thenReturn(false);
        when(passwordEncoder.encode("senha")).thenReturn("hash");
        when(iesService.obterParaCadastroEstudante(3L)).thenReturn(ies);
        when(estudanteRepository.saveAndFlush(any(Estudante.class))).thenAnswer(invocation -> {
            Estudante salvo = invocation.getArgument(0);
            salvo.setId(9L);
            return salvo;
        });

        var response = service.cadastrarEstudante(request);

        assertEquals(9L, response.id());
        assertEquals("ana@example.com", response.email());
        assertEquals("ana", response.username());
        assertEquals(TipoUsuario.ESTUDANTE, response.tipo());
        verify(auditoriaService).identificarCadastroPublico();
        verify(estudanteRepository).saveAndFlush(argThat(estudante ->
                estudante.getNome().equals("Ana Silva")
                        && estudante.getMatricula().equals("20260001")
                        && estudante.getPaisOrigem().equals("Brasil")
                        && estudante.getSenha().equals("hash")
                        && estudante.getIes() == ies));
    }

    @Test
    void matriculaDuplicadaImpedeCadastro() {
        EstudanteCadastroRequest request = new EstudanteCadastroRequest(
                "ana@example.com", "ana", "senha", "Ana Silva", "20260001",
                LocalDate.of(2003, 2, 14), "Brasil", 3L);
        when(usuarioRepository.existsByEmailIgnoreCase("ana@example.com")).thenReturn(false);
        when(usuarioRepository.existsByUsername("ana")).thenReturn(false);
        when(estudanteRepository.existsByMatricula("20260001")).thenReturn(true);

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.cadastrarEstudante(request));

        assertEquals(409, error.getStatusCode().value());
        verify(estudanteRepository, never()).saveAndFlush(any());
        verifyNoInteractions(iesService, passwordEncoder);
    }
}

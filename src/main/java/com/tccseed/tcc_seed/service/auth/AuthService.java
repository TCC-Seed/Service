package com.tccseed.tcc_seed.service.auth;

import com.tccseed.tcc_seed.controller.auth.dto.EstudanteCadastroRequest;
import com.tccseed.tcc_seed.controller.auth.dto.FuncionarioCadastroRequest;
import com.tccseed.tcc_seed.controller.auth.dto.LoginRequest;
import com.tccseed.tcc_seed.controller.auth.dto.UsuarioResponse;
import com.tccseed.tcc_seed.domain.entity.Estudante;
import com.tccseed.tcc_seed.domain.entity.Funcionario;
import com.tccseed.tcc_seed.domain.entity.Usuario;
import com.tccseed.tcc_seed.domain.enums.TipoUsuario;
import com.tccseed.tcc_seed.repository.EstudanteRepository;
import com.tccseed.tcc_seed.repository.FuncionarioRepository;
import com.tccseed.tcc_seed.repository.UsuarioRepository;
import com.tccseed.tcc_seed.security.JwtService;
import com.tccseed.tcc_seed.service.ies.IesService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final EstudanteRepository estudanteRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final IesService iesService;

    @Transactional
    public UsuarioResponse cadastrarEstudante(EstudanteCadastroRequest request) {
        validarEmailDisponivel(request.email());
        validarUsernameDisponivel(request.username());
        if (estudanteRepository.existsByMatricula(request.matricula().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Matrícula já cadastrada.");
        }

        Estudante estudante = new Estudante();
        preencherCamposComuns(estudante, request.email(), request.username(), request.senha(), TipoUsuario.ESTUDANTE);
        estudante.setNome(request.nome().trim());
        estudante.setMatricula(request.matricula().trim());
        estudante.setNascimento(request.nascimento());
        estudante.setPaisOrigem(request.paisOrigem().trim());
        estudante.setIes(iesService.obterParaCadastroEstudante(request.iesId()));
        return salvar(estudante);
    }

    @Transactional
    public UsuarioResponse cadastrarFuncionario(FuncionarioCadastroRequest request) {
        if (!request.isVinculoValido()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe iesId e chaveIes, ou apenas novaIes.");
        }
        validarEmailDisponivel(request.email());
        validarUsernameDisponivel(request.username());

        Funcionario funcionario = new Funcionario();
        preencherCamposComuns(funcionario, request.email(), request.username(), request.senha(), TipoUsuario.FUNCIONARIO);
        funcionario.setNome(request.nome().trim());
        funcionario.setFormacao(request.formacao().trim());
        funcionario.setIes(request.novaIes() != null
                ? iesService.criarParaCadastro(request.novaIes())
                : iesService.obterParaCadastro(request.iesId(), request.chaveIes()));
        return salvar(funcionario);
    }

    @Transactional(readOnly = true)
    public LoginResult autenticar(LoginRequest request) {
        String username = request.username().trim();
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(AuthService::credenciaisInvalidas);
        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw credenciaisInvalidas();
        }
        return new LoginResult(toResponse(usuario), jwtService.createToken(usuario));
    }

    private UsuarioResponse salvar(Usuario usuario) {
        try {
            Usuario salvo = switch (usuario) {
                case Estudante estudante -> estudanteRepository.saveAndFlush(estudante);
                case Funcionario funcionario -> funcionarioRepository.saveAndFlush(funcionario);
                default -> throw new IllegalStateException("Tipo de usuário não suportado.");
            };
            return toResponse(salvo);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Já existe um cadastro com um dos valores únicos informados.", exception);
        }
    }

    private void validarUsernameDisponivel(String username) {
        if (usuarioRepository.existsByUsername(username.trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username já cadastrado.");
        }
    }

    private void validarEmailDisponivel(String emailRecebido) {
        String email = normalizarEmail(emailRecebido);
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado.");
        }
    }

    private void preencherCamposComuns(Usuario usuario, String email, String username, String senha, TipoUsuario tipo) {
        usuario.setEmail(normalizarEmail(email));
        usuario.setUsername(username.trim());
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setTipo(tipo);
        usuario.setToken(UUID.randomUUID());
    }

    private static String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private static UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getEmail(), usuario.getUsername(), usuario.getTipo());
    }

    private static ResponseStatusException credenciaisInvalidas() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username ou senha inválidos.");
    }

    public record LoginResult(UsuarioResponse usuario, String jwt) {
    }
}

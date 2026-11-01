package com.tccseed.tcc_seed.service.estudante;

import com.tccseed.tcc_seed.controller.estudante.dto.*;
import com.tccseed.tcc_seed.domain.entity.Estudante;
import com.tccseed.tcc_seed.domain.entity.Usuario;
import com.tccseed.tcc_seed.domain.enums.TipoUsuario;
import com.tccseed.tcc_seed.repository.EstudanteRepository;
import com.tccseed.tcc_seed.repository.UsuarioRepository;
import com.tccseed.tcc_seed.service.auditoria.AuditoriaService;
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
@Transactional
@RequiredArgsConstructor
public class EstudanteService {
    private final UsuarioRepository usuarios;
    private final EstudanteRepository estudantes;
    private final AuditoriaService auditoria;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public EstudanteResponse consultar(String principal) {
        return resposta(titular(principal, false));
    }

    public EstudanteResponse atualizar(String principal, EstudanteAtualizacaoRequest request) {
        Estudante estudante = titular(principal, true);
        auditoria.identificarUsuario(estudante);
        if (request.email() != null) estudante.setEmail(request.email().trim().toLowerCase(Locale.ROOT));
        if (request.username() != null) estudante.setUsername(request.username().trim());
        if (request.nome() != null) estudante.setNome(request.nome().trim());
        if (request.matricula() != null) estudante.setMatricula(request.matricula().trim());
        if (request.nascimento() != null) estudante.setNascimento(request.nascimento());
        if (request.genero() != null) estudante.setGenero(request.genero());
        if (request.paisOrigem() != null) estudante.setPaisOrigem(request.paisOrigem().trim());
        try {
            estudantes.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "E-mail, username ou matrícula já cadastrados ou dados incompatíveis com o cadastro.", exception);
        }
        return resposta(estudante);
    }

    public void alterarSenha(String principal, SenhaAtualizacaoRequest request) {
        Estudante estudante = titular(principal, true);
        conferirSenha(estudante, request.senhaAtual());
        auditoria.identificarUsuario(estudante);
        estudante.setSenha(passwordEncoder.encode(request.novaSenha()));
        // Todos os JWTs anteriores referenciam o UUID antigo e deixam de autenticar.
        estudante.setToken(UUID.randomUUID());
        estudantes.flush();
    }

    public void excluir(String principal, ContaExclusaoRequest request) {
        Estudante estudante = titular(principal, true);
        conferirSenha(estudante, request.senhaAtual());
        auditoria.identificarUsuario(estudante);
        try {
            // A herança JOINED remove tanto estudante quanto usuario, sem remover a IES.
            estudantes.delete(estudante);
            estudantes.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A conta possui dados dependentes e não pode ser excluída.", exception);
        }
    }

    private Estudante titular(String principal, boolean bloquear) {
        UUID token;
        try {
            token = UUID.fromString(principal);
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        if (bloquear) {
            var estudante = estudantes.findByTokenForUpdate(token);
            if (estudante.isPresent()) {
                return exigirEstudante(estudante.get());
            }
        }
        var usuario = usuarios.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        Estudante estudante = exigirEstudante(usuario);
        if (bloquear) {
            // Uma credencial rotacionada enquanto aguardávamos o bloqueio não pode ser reutilizada.
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return estudante;
    }

    private Estudante exigirEstudante(Usuario usuario) {
        if (!(usuario instanceof Estudante estudante) || usuario.getTipo() != TipoUsuario.ESTUDANTE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Operação exclusiva de estudante.");
        }
        return estudante;
    }

    private void conferirSenha(Estudante estudante, String senha) {
        if (senha == null || !passwordEncoder.matches(senha, estudante.getSenha())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Senha atual incorreta.");
        }
    }

    private EstudanteResponse resposta(Estudante estudante) {
        return new EstudanteResponse(estudante.getId(), estudante.getEmail(), estudante.getUsername(),
                estudante.getNome(), estudante.getMatricula(), estudante.getNascimento(),
                estudante.getGenero(), estudante.getPaisOrigem(), estudante.getIes().getId());
    }
}

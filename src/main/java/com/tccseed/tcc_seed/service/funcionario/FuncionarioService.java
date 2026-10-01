package com.tccseed.tcc_seed.service.funcionario;

import com.tccseed.tcc_seed.controller.funcionario.dto.*;
import com.tccseed.tcc_seed.domain.entity.Funcionario;
import com.tccseed.tcc_seed.domain.entity.Usuario;
import com.tccseed.tcc_seed.domain.enums.TipoUsuario;
import com.tccseed.tcc_seed.repository.FuncionarioRepository;
import com.tccseed.tcc_seed.repository.UsuarioRepository;
import com.tccseed.tcc_seed.repository.IesRepository;
import com.tccseed.tcc_seed.service.ies.IesService;
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
public class FuncionarioService {
    private final UsuarioRepository usuarios;
    private final FuncionarioRepository funcionarios;
    private final IesRepository instituicoes;
    private final IesService iesService;
    private final AuditoriaService auditoria;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public FuncionarioResponse consultar(String principal) {
        return resposta(titular(principal, false));
    }

    public FuncionarioResponse atualizar(String principal, FuncionarioAtualizacaoRequest request) {
        Funcionario funcionario = titular(principal, true);
        auditoria.identificarUsuario(funcionario);
        if (request.email() != null) funcionario.setEmail(request.email().trim().toLowerCase(Locale.ROOT));
        if (request.username() != null) funcionario.setUsername(request.username().trim());
        if (request.nome() != null) funcionario.setNome(request.nome().trim());
        if (request.formacao() != null) funcionario.setFormacao(request.formacao().trim());
        try {
            funcionarios.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "E-mail ou username já cadastrados ou dados incompatíveis com o cadastro.", exception);
        }
        return resposta(funcionario);
    }

    public void alterarSenha(String principal, SenhaAtualizacaoRequest request) {
        Funcionario funcionario = titular(principal, true);
        conferirSenha(funcionario, request.senhaAtual());
        auditoria.identificarUsuario(funcionario);
        funcionario.setSenha(passwordEncoder.encode(request.novaSenha()));
        // Todos os JWTs anteriores referenciam o UUID antigo e deixam de autenticar.
        funcionario.setToken(UUID.randomUUID());
        funcionarios.flush();
    }

    public void excluir(String principal, ContaExclusaoRequest request) {
        UUID token = token(principal);
        // Projeção escalar: não carrega uma entidade que poderia ficar desatualizada
        // enquanto aguardamos a IES. Cadastros e DELETE /IES usam a mesma ordem.
        Long iesId = funcionarios.findIesIdByToken(token).orElseGet(() -> {
            titular(principal, false); // Diferencia perfil proibido de token inválido.
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        });
        instituicoes.findByIdForUpdate(iesId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        Funcionario funcionario = titular(principal, true);
        conferirSenha(funcionario, request.senhaAtual());
        if (!iesId.equals(funcionario.getIes().getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vínculo institucional alterado.");
        }
        if (funcionarios.countByIesId(iesId) == 1) {
            // Reutiliza a exclusão transacional de estudantes, contas, funcionários e IES.
            // Esse serviço também registra o solicitante como autor da auditoria.
            iesService.excluir(iesId, principal);
            return;
        }
        auditoria.identificarUsuario(funcionario);
        try {
            funcionarios.delete(funcionario);
            funcionarios.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A conta possui dados dependentes e não pode ser excluída.", exception);
        }
    }

    private Funcionario titular(String principal, boolean bloquear) {
        UUID token = token(principal);
        if (bloquear) {
            var funcionario = funcionarios.findByTokenForUpdate(token);
            if (funcionario.isPresent()) {
                return exigirFuncionario(funcionario.get());
            }
        }
        var usuario = usuarios.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        Funcionario funcionario = exigirFuncionario(usuario);
        if (bloquear) {
            // Uma credencial rotacionada enquanto aguardávamos o bloqueio não pode ser reutilizada.
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return funcionario;
    }

    private UUID token(String principal) {
        try {
            return UUID.fromString(principal);
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    private Funcionario exigirFuncionario(Usuario usuario) {
        if (!(usuario instanceof Funcionario funcionario) || usuario.getTipo() != TipoUsuario.FUNCIONARIO) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Operação exclusiva de funcionário.");
        }
        return funcionario;
    }

    private void conferirSenha(Funcionario funcionario, String senha) {
        if (senha == null || !passwordEncoder.matches(senha, funcionario.getSenha())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Senha atual incorreta.");
        }
    }

    private FuncionarioResponse resposta(Funcionario funcionario) {
        return new FuncionarioResponse(funcionario.getId(), funcionario.getEmail(), funcionario.getUsername(),
                funcionario.getNome(), funcionario.getFormacao(), funcionario.getIes().getId());
    }
}

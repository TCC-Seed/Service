package com.tccseed.tcc_seed.service.ies;

import com.tccseed.tcc_seed.controller.ies.dto.IesPrivadaResponse;
import com.tccseed.tcc_seed.controller.ies.dto.IesRequest;
import com.tccseed.tcc_seed.controller.ies.dto.IesResponse;
import com.tccseed.tcc_seed.domain.entity.Funcionario;
import com.tccseed.tcc_seed.domain.entity.Ies;
import com.tccseed.tcc_seed.domain.entity.Usuario;
import com.tccseed.tcc_seed.domain.enums.TipoUsuario;
import com.tccseed.tcc_seed.repository.EstudanteRepository;
import com.tccseed.tcc_seed.repository.FuncionarioRepository;
import com.tccseed.tcc_seed.repository.IesRepository;
import com.tccseed.tcc_seed.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class IesService {
    private final IesRepository iesRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstudanteRepository estudanteRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final SecureRandom random = new SecureRandom();

    @Transactional(readOnly = true)
    public Page<IesResponse> listar(int pagina, int tamanho) {
        if (pagina < 0 || tamanho < 1 || tamanho > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Paginação inválida; tamanho deve estar entre 1 e 100.");
        }
        return iesRepository.findAll(PageRequest.of(pagina, tamanho, Sort.by("id"))).map(this::publica);
    }

    @Transactional(readOnly = true)
    public IesResponse consultar(Long id) {
        return publica(buscar(id));
    }

    @Transactional(readOnly = true)
    public IesPrivadaResponse consultarPrivada(Long id, String principal) {
        Ies ies = buscar(id);
        exigirVinculo(ies, funcionario(principal));
        return privada(ies);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Ies criarParaCadastro(IesRequest request) {
        Ies ies = new Ies();
        preencher(ies, request);
        ies.setChaveVinculo(novaChave());
        return iesRepository.save(ies);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Ies obterParaCadastro(Long id, String chave) {
        Ies ies = bloquear(id);
        if (chave == null || ies.getChaveVinculo() == null || !MessageDigest.isEqual(
                ies.getChaveVinculo().getBytes(StandardCharsets.UTF_8), chave.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Chave da IES inválida.");
        }
        return ies;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Ies obterParaCadastroEstudante(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "iesId positivo é obrigatório.");
        }
        return bloquear(id);
    }

    public IesResponse atualizar(Long id, IesRequest request, String principal) {
        Ies ies = bloquear(id);
        exigirVinculo(ies, funcionario(principal));
        preencher(ies, request);
        return publica(ies);
    }

    public void excluir(Long id, String principal) {
        Ies ies = bloquear(id);
        exigirVinculo(ies, funcionario(principal));
        try {
            estudanteRepository.deleteAll(estudanteRepository.findByIesId(id));
            estudanteRepository.flush();
            funcionarioRepository.deleteAll(funcionarioRepository.findByIesId(id));
            funcionarioRepository.flush();
            iesRepository.delete(ies);
            iesRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "IES, estudantes ou funcionários possuem outros dados dependentes e não podem ser excluídos.", exception);
        }
    }

    public IesPrivadaResponse renovarChave(Long id, String principal) {
        Ies ies = bloquear(id);
        exigirVinculo(ies, funcionario(principal));
        ies.setChaveVinculo(novaChave());
        return privada(ies);
    }

    private Funcionario funcionario(String principal) {
        Usuario usuario = usuarioRepository.findByToken(UUID.fromString(principal))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (!(usuario instanceof Funcionario funcionario) || usuario.getTipo() != TipoUsuario.FUNCIONARIO) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Operação exclusiva de funcionário.");
        }
        return funcionario;
    }

    private void exigirVinculo(Ies ies, Funcionario funcionario) {
        if (funcionario.getIes() == null || !ies.getId().equals(funcionario.getIes().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário não vinculado a esta IES.");
        }
    }

    private Ies buscar(Long id) {
        return iesRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "IES não encontrada."));
    }

    private Ies bloquear(Long id) {
        return iesRepository.findByIdForUpdate(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "IES não encontrada."));
    }

    private void preencher(Ies ies, IesRequest request) {
        ies.setNome(request.nome().trim());
        ies.setRegiaoAdministrativa(request.regiaoAdministrativa() == null ? null : request.regiaoAdministrativa().trim());
    }

    private String novaChave() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private IesResponse publica(Ies ies) {
        return new IesResponse(ies.getId(), ies.getNome(), ies.getRegiaoAdministrativa());
    }

    private IesPrivadaResponse privada(Ies ies) {
        return new IesPrivadaResponse(ies.getId(), ies.getNome(), ies.getRegiaoAdministrativa(), ies.getChaveVinculo());
    }
}

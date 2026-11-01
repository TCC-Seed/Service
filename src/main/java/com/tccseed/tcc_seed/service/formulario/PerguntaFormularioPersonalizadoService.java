package com.tccseed.tcc_seed.service.formulario;

import com.tccseed.tcc_seed.domain.entity.PerguntaFormularioPersonalizado;
import com.tccseed.tcc_seed.repository.PerguntaFormularioPersonalizadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PerguntaFormularioPersonalizadoService {
    private final PerguntaFormularioPersonalizadoRepository repository;

    public PerguntaFormularioPersonalizado criar(PerguntaFormularioPersonalizado entidade) {
        return repository.save(entidade);
    }

    @Transactional(readOnly = true)
    public List<PerguntaFormularioPersonalizado> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public PerguntaFormularioPersonalizado buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PerguntaFormularioPersonalizado não encontrado(a)."));
    }

    public PerguntaFormularioPersonalizado atualizar(Long id, PerguntaFormularioPersonalizado entidade) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PerguntaFormularioPersonalizado não encontrado(a).");
        }
        entidade.setId(id);
        return repository.save(entidade);
    }

    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PerguntaFormularioPersonalizado não encontrado(a).");
        }
        repository.deleteById(id);
    }
}

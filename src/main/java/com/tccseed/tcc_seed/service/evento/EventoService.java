package com.tccseed.tcc_seed.service.evento;

import com.tccseed.tcc_seed.domain.entity.Evento;
import com.tccseed.tcc_seed.repository.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventoService {
    private final EventoRepository repository;

    public Evento criar(Evento entidade) {
        return repository.save(entidade);
    }

    @Transactional(readOnly = true)
    public List<Evento> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Evento buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado(a)."));
    }

    public Evento atualizar(Long id, Evento entidade) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado(a).");
        }
        entidade.setId(id);
        return repository.save(entidade);
    }

    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado(a).");
        }
        repository.deleteById(id);
    }
}

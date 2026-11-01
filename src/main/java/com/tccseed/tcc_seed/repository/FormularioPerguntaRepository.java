package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.FormularioPergunta;
import com.tccseed.tcc_seed.domain.entity.id.FormularioPerguntaId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FormularioPerguntaRepository extends JpaRepository<FormularioPergunta, FormularioPerguntaId> {
    @Override
    <T extends FormularioPergunta> T save(T entity);

    @Override
    Optional<FormularioPergunta> findById(FormularioPerguntaId id);

    @Override
    List<FormularioPergunta> findAll();

    @Override
    void deleteById(FormularioPerguntaId id);
}

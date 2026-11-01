package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.PerguntaFormularioPersonalizado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PerguntaFormularioPersonalizadoRepository extends JpaRepository<PerguntaFormularioPersonalizado, Long> {
    @Override
    <T extends PerguntaFormularioPersonalizado> T save(T entity);

    @Override
    Optional<PerguntaFormularioPersonalizado> findById(Long id);

    @Override
    List<PerguntaFormularioPersonalizado> findAll();

    @Override
    void deleteById(Long id);
}

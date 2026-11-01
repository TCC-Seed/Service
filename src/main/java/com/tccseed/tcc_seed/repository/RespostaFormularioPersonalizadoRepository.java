package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.RespostaFormularioPersonalizado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RespostaFormularioPersonalizadoRepository extends JpaRepository<RespostaFormularioPersonalizado, Long> {
    @Override
    <T extends RespostaFormularioPersonalizado> T save(T entity);

    @Override
    Optional<RespostaFormularioPersonalizado> findById(Long id);

    @Override
    List<RespostaFormularioPersonalizado> findAll();

    @Override
    void deleteById(Long id);
}

package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.FormularioBase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FormularioBaseRepository extends JpaRepository<FormularioBase, Long> {
    @Override
    <T extends FormularioBase> T save(T entity);

    @Override
    Optional<FormularioBase> findById(Long id);

    @Override
    List<FormularioBase> findAll();

    @Override
    void deleteById(Long id);
}

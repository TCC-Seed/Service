package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.CategoriaEvento;
import com.tccseed.tcc_seed.domain.entity.id.CategoriaEventoId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CategoriaEventoRepository extends JpaRepository<CategoriaEvento, CategoriaEventoId> {
    @Override
    <T extends CategoriaEvento> T save(T entity);

    @Override
    Optional<CategoriaEvento> findById(CategoriaEventoId id);

    @Override
    List<CategoriaEvento> findAll();

    @Override
    void deleteById(CategoriaEventoId id);
}

package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.CategoriaEvento;
import com.tccseed.tcc_seed.domain.entity.id.CategoriaEventoId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaEventoRepository
    extends JpaRepository<CategoriaEvento, CategoriaEventoId> {
  @Override
  <T extends CategoriaEvento> T save(T entity);

  @Override
  Optional<CategoriaEvento> findById(CategoriaEventoId id);

  @Override
  List<CategoriaEvento> findAll();

  @Override
  void deleteById(CategoriaEventoId id);
}

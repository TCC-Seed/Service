package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.FormularioPersonalizado;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormularioPersonalizadoRepository
    extends JpaRepository<FormularioPersonalizado, Long> {
  @Override
  <T extends FormularioPersonalizado> T save(T entity);

  @Override
  Optional<FormularioPersonalizado> findById(Long id);

  @Override
  List<FormularioPersonalizado> findAll();

  @Override
  void deleteById(Long id);
}

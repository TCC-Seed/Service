package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, Long> {
    @Override
    <T extends Evento> T save(T entity);

    @Override
    Optional<Evento> findById(Long id);

    @Override
    List<Evento> findAll();

    @Override
    void deleteById(Long id);
}

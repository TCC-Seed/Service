package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.EventoIes;
import com.tccseed.tcc_seed.domain.entity.id.EventoIesId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventoIesRepository extends JpaRepository<EventoIes, EventoIesId> {
    @Override
    <T extends EventoIes> T save(T entity);

    @Override
    Optional<EventoIes> findById(EventoIesId id);

    @Override
    List<EventoIes> findAll();

    @Override
    void deleteById(EventoIesId id);
}

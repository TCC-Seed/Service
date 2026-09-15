package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.Estudante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstudanteRepository extends JpaRepository<Estudante, Long> {

    Optional<Estudante> findByMatricula(String matricula);

    boolean existsByMatricula(String matricula);
}
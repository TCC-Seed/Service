package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.Estudante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstudanteRepository extends JpaRepository<Estudante, Long> {

    List<Estudante> findByIesId(Long iesId);

    boolean existsByMatricula(String matricula);
}

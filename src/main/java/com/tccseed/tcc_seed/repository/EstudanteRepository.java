package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.Estudante;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EstudanteRepository extends JpaRepository<Estudante, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Estudante e where e.token = :token")
    Optional<Estudante> findByTokenForUpdate(@Param("token") UUID token);

    List<Estudante> findByIesId(Long iesId);

    boolean existsByMatricula(String matricula);
}

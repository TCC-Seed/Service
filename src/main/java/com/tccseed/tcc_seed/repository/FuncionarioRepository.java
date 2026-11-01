package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select f from Funcionario f where f.token = :token")
    Optional<Funcionario> findByTokenForUpdate(@Param("token") UUID token);

    @Query("select f.ies.id from Funcionario f where f.token = :token")
    Optional<Long> findIesIdByToken(@Param("token") UUID token);

    long countByIesId(Long iesId);

    List<Funcionario> findByIesId(Long iesId);
}

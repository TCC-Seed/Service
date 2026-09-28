package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.Ies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

@Repository
public interface IesRepository extends JpaRepository<Ies, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Ies i where i.id = :id")
    Optional<Ies> findByIdForUpdate(@Param("id") Long id);

    boolean existsByNome(String nome);
}

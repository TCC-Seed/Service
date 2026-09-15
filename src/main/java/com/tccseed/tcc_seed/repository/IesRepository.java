package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.Ies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IesRepository extends JpaRepository<Ies, Long> {

    boolean existsByNome(String nome);
}

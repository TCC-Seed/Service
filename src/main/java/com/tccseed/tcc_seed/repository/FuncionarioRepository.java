package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    boolean existsByFormacao(String formacao);
}
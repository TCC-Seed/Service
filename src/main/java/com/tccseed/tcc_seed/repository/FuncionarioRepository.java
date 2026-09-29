package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    List<Funcionario> findByIesId(Long iesId);
}

package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.UsuarioIes;
import com.tccseed.tcc_seed.domain.entity.id.UsuarioIesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioIesRepository extends JpaRepository<UsuarioIes, UsuarioIesId> {

    List<UsuarioIes> findByUsuarioId(Long usuarioId);

    List<UsuarioIes> findByIesId(Long iesId);
}
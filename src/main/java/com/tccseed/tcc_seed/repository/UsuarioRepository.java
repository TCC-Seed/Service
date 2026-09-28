package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByToken(UUID token);

    Optional<Usuario> findByEmailIgnoreCase(String email);

    Optional<Usuario> findByUsername(String username);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsername(String username);
}

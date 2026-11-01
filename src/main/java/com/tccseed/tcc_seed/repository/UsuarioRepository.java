package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.Usuario;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

  Optional<Usuario> findByToken(UUID token);

  Optional<Usuario> findByUsername(String username);

  boolean existsByEmailIgnoreCase(String email);

  boolean existsByUsername(String username);
}

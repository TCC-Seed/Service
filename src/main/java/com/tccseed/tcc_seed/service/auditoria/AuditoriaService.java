package com.tccseed.tcc_seed.service.auditoria;

import com.tccseed.tcc_seed.domain.entity.Usuario;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class AuditoriaService {

    private final EntityManager entityManager;

    public void identificarCadastroPublico() {
        identificar("cadastro_publico", "", "");
    }

    public void identificarUsuario(Usuario usuario) {
        identificar("usuario", usuario.getId().toString(), usuario.getUsername());
    }

    private void identificar(String origem, String usuarioId, String username) {
        entityManager.unwrap(Session.class).doWork(connection -> {
            try (var statement = connection.prepareStatement("""
                    SELECT set_config('seed.audit_origem', ?, true),
                           set_config('seed.audit_usuario_id', ?, true),
                           set_config('seed.audit_username', ?, true)
                    """)) {
                statement.setString(1, origem);
                statement.setString(2, usuarioId);
                statement.setString(3, username);
                statement.execute();
            }
        });
    }
}

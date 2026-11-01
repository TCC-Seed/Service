package com.tccseed.tcc_seed.service.auditoria;

import com.tccseed.tcc_seed.domain.entity.Usuario;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditoriaServiceTest {
    @Mock private EntityManager entityManager;
    @Mock private Session session;
    @Mock private Connection connection;
    @Mock private PreparedStatement statement;
    private AuditoriaService service;

    @BeforeEach
    void preparar() throws Exception {
        service = new AuditoriaService(entityManager);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(connection.prepareStatement(any(String.class))).thenReturn(statement);
        doAnswer(invocation -> {
            Work work = invocation.getArgument(0);
            work.execute(connection);
            return null;
        }).when(session).doWork(any(Work.class));
    }

    @Test
    void identificaOperacaoDeCadastroPublicoNaTransacao() throws Exception {
        service.identificarCadastroPublico();
        verify(statement).setString(1, "cadastro_publico");
        verify(statement).setString(2, "");
        verify(statement).setString(3, "");
        verify(statement).execute();
        verify(statement).close();
    }

    @Test
    void identificaUsuarioPorIdEUsername() throws Exception {
        Usuario usuario = mock(Usuario.class);
        when(usuario.getId()).thenReturn(42L);
        when(usuario.getUsername()).thenReturn("ana");
        service.identificarUsuario(usuario);
        verify(statement).setString(1, "usuario");
        verify(statement).setString(2, "42");
        verify(statement).setString(3, "ana");
        verify(statement).execute();
        verify(statement).close();
    }
}

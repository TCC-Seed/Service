package com.tccseed.tcc_seed.controller.auth;

import com.tccseed.tcc_seed.controller.auth.dto.LoginRequest;
import com.tccseed.tcc_seed.controller.auth.dto.UsuarioResponse;
import com.tccseed.tcc_seed.domain.enums.TipoUsuario;
import com.tccseed.tcc_seed.exception.ApiExceptionHandler;
import com.tccseed.tcc_seed.service.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {
    AuthService service;
    MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(AuthService.class);
        mvc = MockMvcBuilders.standaloneSetup(new AuthController(service))
                .setControllerAdvice(new ApiExceptionHandler()).build();
    }

    @Test
    void aceitaUsernameSemEmailERetornaCookie() throws Exception {
        when(service.autenticar(new LoginRequest("joao", "senha")))
                .thenReturn(new AuthService.LoginResult(
                        new UsuarioResponse(1L, "joao@example.com", "joao", TipoUsuario.FUNCIONARIO), "jwt"));
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"joao\",\"senha\":\"senha\"}"))
                .andExpect(status().isOk()).andExpect(cookie().value("seed_cookie", "jwt"))
                .andExpect(cookie().httpOnly("seed_cookie", true))
                .andExpect(jsonPath("$.username").value("joao"));
    }

    @Test
    void rejeitaLoginAntigoENomeVazioOuLongoESenhaAusente() throws Exception {
        for (String json : new String[]{
                "{\"email\":\"joao@example.com\",\"senha\":\"senha\"}",
                "{\"username\":\" \",\"senha\":\"senha\"}",
                "{\"username\":\"" + "a".repeat(51) + "\",\"senha\":\"senha\"}",
                "{\"username\":\"joao\"}"}) {
            mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(json))
                    .andExpect(status().isBadRequest());
        }
        verifyNoInteractions(service);
    }
}

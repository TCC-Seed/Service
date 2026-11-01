package com.tccseed.tcc_seed.controller.estudante;

import com.tccseed.tcc_seed.controller.estudante.dto.*;
import com.tccseed.tcc_seed.exception.ApiExceptionHandler;
import com.tccseed.tcc_seed.service.estudante.EstudanteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EstudanteControllerTest {
    EstudanteService service;
    MockMvc mvc;

    @BeforeEach
    void preparar() {
        service = mock(EstudanteService.class);
        mvc = MockMvcBuilders.standaloneSetup(new EstudanteController(service))
                .setControllerAdvice(new ApiExceptionHandler()).build();
    }

    @Test
    void consultaNaoExpoeSegredosENaoAceitaIdNaRota() throws Exception {
        when(service.consultar("principal")).thenReturn(new EstudanteResponse(1L, "a@example.com", "ana",
                "Ana", "123", LocalDate.of(2000, 1, 1), null, "Brasil", 5L));
        mvc.perform(get("/estudante").principal(() -> "principal"))
                .andExpect(status().isOk()).andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.iesId").value(5))
                .andExpect(jsonPath("$.senha").doesNotExist()).andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.chaveIes").doesNotExist());
        mvc.perform(get("/estudante/2").principal(() -> "principal")).andExpect(status().isNotFound());
    }

    @Test
    void rejeitaCamposProtegidosETextoVazio() throws Exception {
        for (String json : new String[]{"{\"iesId\":2}", "{\"id\":2}", "{\"tipo\":\"FUNCIONARIO\"}",
                "{\"senha\":\"nova\"}", "{\"token\":\"outro\"}", "{\"nome\":\" \"}", "{\"matricula\":\"\"}"}) {
            mvc.perform(patch("/estudante").principal(() -> "principal")
                    .contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());
        }
        verifyNoInteractions(service);
    }

    @Test
    void senhaEExclusaoExigemSenhaAtual() throws Exception {
        mvc.perform(patch("/estudante/senha").principal(() -> "principal")
                .contentType(MediaType.APPLICATION_JSON).content("{\"novaSenha\":\"nova\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(delete("/estudante").principal(() -> "principal")
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void trocaEExclusaoLimpamCookieApenasAposSucesso() throws Exception {
        mvc.perform(patch("/estudante/senha").principal(() -> "principal")
                .contentType(MediaType.APPLICATION_JSON).content("{\"senhaAtual\":\"atual\",\"novaSenha\":\"nova\"}"))
                .andExpect(status().isNoContent()).andExpect(header().string("Set-Cookie", containsString("Max-Age=0")));
        mvc.perform(delete("/estudante").principal(() -> "principal")
                .contentType(MediaType.APPLICATION_JSON).content("{\"senhaAtual\":\"atual\"}"))
                .andExpect(status().isNoContent()).andExpect(header().string("Set-Cookie", containsString("Max-Age=0")));
        verify(service).alterarSenha("principal", new SenhaAtualizacaoRequest("atual", "nova"));
        verify(service).excluir("principal", new ContaExclusaoRequest("atual"));
    }
}

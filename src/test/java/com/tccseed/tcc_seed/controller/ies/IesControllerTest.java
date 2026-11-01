package com.tccseed.tcc_seed.controller.ies;

import com.tccseed.tcc_seed.controller.auth.AuthController;
import com.tccseed.tcc_seed.controller.ies.dto.*;
import com.tccseed.tcc_seed.exception.ApiExceptionHandler;
import com.tccseed.tcc_seed.service.auth.AuthService;
import com.tccseed.tcc_seed.service.ies.IesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class IesControllerTest {
    IesService service;
    AuthService auth;
    MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(IesService.class);
        auth = mock(AuthService.class);
        mvc = MockMvcBuilders.standaloneSetup(new IesController(service), new AuthController(auth))
                .setControllerAdvice(new ApiExceptionHandler()).build();
    }

    @Test
    void publicaNaoIncluiChaveEPrivadaImpedeCache() throws Exception {
        when(service.consultar(7L)).thenReturn(new IesResponse(7L, "IES", null));
        when(service.consultarPrivada(7L, "principal"))
                .thenReturn(new IesPrivadaResponse(7L, "IES", null, "segredo"));
        mvc.perform(get("/IES/7")).andExpect(status().isOk())
                .andExpect(jsonPath("$.chaveIes").doesNotExist());
        mvc.perform(get("/IES/7/privado").principal(() -> "principal"))
                .andExpect(status().isOk()).andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.chaveIes").value("segredo"));
    }

    @Test
    void criacaoDiretaEIngressoPosteriorNaoEstaoDisponiveis() throws Exception {
        mvc.perform(post("/IES").principal(() -> "principal").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"IES\"}")).andExpect(status().isMethodNotAllowed());
        mvc.perform(post("/IES/7/vinculos").principal(() -> "principal").contentType(MediaType.APPLICATION_JSON)
                .content("{\"chaveIes\":\"chave\"}")).andExpect(status().isNotFound());
        verifyNoInteractions(service);
    }

    @Test
    void exclusaoNaoRetornaCorpo() throws Exception {
        mvc.perform(delete("/IES/7").principal(() -> "principal"))
                .andExpect(status().isNoContent()).andExpect(content().string(""));
        verify(service).excluir(7L, "principal");
    }

    @Test
    void atualizacaoRejeitaNomeVazioERegiaoLonga() throws Exception {
        mvc.perform(put("/IES/7").principal(() -> "principal").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\" \"}")).andExpect(status().isBadRequest());
        mvc.perform(put("/IES/7").principal(() -> "principal").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"IES\",\"regiaoAdministrativa\":\"" + "x".repeat(101) + "\"}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void cadastroValidaOpcaoDeVinculoEDadosDaNovaIes() throws Exception {
        String base = "{\"email\":\"a@example.com\",\"username\":\"a\",\"senha\":\"a\",\"nome\":\"A\",\"formacao\":\"B\"";
        for (String extra : new String[]{"}", ",\"novaIes\":{\"nome\":\" \"}}",
                ",\"iesId\":1,\"chaveIes\":\"chave\",\"novaIes\":{\"nome\":\"IES\"}}"}) {
            mvc.perform(post("/auth/cadastro/funcionario").contentType(MediaType.APPLICATION_JSON)
                    .content(base + extra)).andExpect(status().isBadRequest());
        }
        verifyNoInteractions(auth);
    }
}

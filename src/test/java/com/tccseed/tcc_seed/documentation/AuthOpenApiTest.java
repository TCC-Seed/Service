package com.tccseed.tcc_seed.documentation;

import com.tccseed.tcc_seed.config.OpenApiConfig;
import com.tccseed.tcc_seed.config.SecurityConfig;
import com.tccseed.tcc_seed.controller.auth.AuthController;
import com.tccseed.tcc_seed.repository.UsuarioRepository;
import com.tccseed.tcc_seed.security.JwtService;
import com.tccseed.tcc_seed.service.auth.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AuthOpenApiTest.DocumentationApplication.class)
@AutoConfigureMockMvc
class AuthOpenApiTest {
    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class, DataJpaRepositoriesAutoConfiguration.class})
    @Import({AuthController.class, OpenApiConfig.class, SecurityConfig.class})
    static class DocumentationApplication {}

    @MockitoBean AuthService authService;
    @MockitoBean UsuarioRepository usuarioRepository;
    @MockitoBean JwtService jwtService;
    @Autowired MockMvc mvc;
    @Autowired JsonMapper mapper;

    @Test
    void csrfCookieSaiEmGetExistenteEProtegePost() throws Exception {
        var csrfResponse = mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        var csrfCookie = csrfResponse.getCookie("XSRF-TOKEN");
        assertNotNull(csrfCookie);
        String token = csrfCookie.getValue();
        assertFalse(token.isBlank());

        mvc.perform(post("/auth/cadastro/estudante").contentType("application/json").content("{}"))
                .andExpect(status().isForbidden());
        mvc.perform(post("/auth/cadastro/estudante").cookie(csrfCookie)
                        .header("X-XSRF-TOKEN", token)
                        .contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void documentaCamposPermissoesEStatusReaisNoEndpointPublico() throws Exception {
        String json = mvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
        Path output = Path.of("target/openapi/auth-after.json");
        Files.createDirectories(output.getParent());
        Files.writeString(output, json);
        JsonNode document = mapper.readTree(json);
        JsonNode schemas = document.path("components").path("schemas");
        Map<String, Set<String>> required = Map.of(
                "EstudanteCadastroRequest", Set.of("email", "username", "senha", "nome", "matricula", "nascimento", "paisOrigem", "iesId"),
                "FuncionarioCadastroRequest", Set.of("email", "username", "senha", "nome", "formacao"),
                "LoginRequest", Set.of("username", "senha"),
                "UsuarioResponse", Set.of("id", "email", "username", "tipo"));
        for (var entry : required.entrySet()) {
            JsonNode schema = schemas.path(entry.getKey());
            assertFalse(schema.isMissingNode(), entry.getKey());
            Set<String> actual = new HashSet<>();
            schema.path("required").forEach(node -> actual.add(node.asText()));
            assertEquals(entry.getValue(), actual, entry.getKey());
            JsonNode properties = schema.path("properties");
            for (String name : properties.propertyNames()) {
                JsonNode property = properties.path(name);
                assertFalse(property.path("description").asText().isBlank(), entry.getKey() + "." + name);
                assertTrue(property.has("example"), entry.getKey() + "." + name);
            }
            if (properties.has("senha")) assertTrue(properties.path("senha").path("writeOnly").asBoolean());
        }
        assertFalse(schemas.path("FuncionarioCadastroRequest").path("properties").has("vinculoValido"));
        assertEquals("2001-05-14", schemas.path("EstudanteCadastroRequest").path("properties")
                .path("nascimento").path("example").asText());
        for (String tipo : new String[]{"estudante", "funcionario"}) {
            JsonNode operation = document.path("paths").path("/auth/cadastro/" + tipo).path("post");
            assertFalse(operation.path("summary").asText().isBlank());
            assertTrue(operation.path("description").asText().contains("sem autenticação ou role"));
            assertTrue(operation.path("responses").has("201"));
            assertTrue(operation.path("responses").has("400"));
            assertTrue(operation.path("responses").has("409"));
            assertFalse(operation.path("responses").has("200"));
            assertEquals("#/components/schemas/" + (tipo.equals("estudante") ? "Estudante" : "Funcionario") + "CadastroRequest",
                    operation.path("requestBody").path("content").path("application/json").path("schema").path("$ref").asText());
        }
    }
}

package com.tccseed.tcc_seed.security;

import com.tccseed.tcc_seed.domain.entity.Usuario;
import com.tccseed.tcc_seed.domain.enums.TipoUsuario;
import com.tccseed.tcc_seed.repository.UsuarioRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import tools.jackson.databind.json.JsonMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class JwtAuthenticationTest {
    private static final String SECRET = "01234567890123456789012345678901";
    private final JsonMapper mapper = new JsonMapper();
    private final JwtService service = new JwtService(mapper, SECRET);

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createsOnlyRequiredClaimsUsingExistingUuidForBothRoles() throws Exception {
        for (TipoUsuario tipo : TipoUsuario.values()) {
            Usuario usuario = usuario(tipo);
            long before = Instant.now().getEpochSecond();
            String jwt = service.createToken(usuario);
            long after = Instant.now().getEpochSecond();
            String[] parts = jwt.split("\\.");
            var payload = mapper.readTree(Base64.getUrlDecoder().decode(parts[1]));
            Set<String> fields = new HashSet<>();
            payload.propertyNames().forEach(fields::add);
            assertEquals(Set.of("sub", "role", "iat", "exp"), fields);
            assertEquals(usuario.getToken().toString(), payload.path("sub").asText());
            assertEquals("ROLE_" + tipo.name(), payload.path("role").asText());
            long issuedAt = payload.path("iat").asLong();
            assertTrue(issuedAt >= before && issuedAt <= after);
            assertEquals(JwtService.TOKEN_LIFETIME.toSeconds(), payload.path("exp").asLong() - issuedAt);
            assertEquals(sign(parts[0] + "." + parts[1]), parts[2]);
            var claims = service.validate(jwt).orElseThrow();
            assertEquals(usuario.getToken(), claims.subject());
            assertEquals("ROLE_" + tipo.name(), claims.role());
        }
    }

    @Test
    void rejectsOldNumericSubjectsMalformedUuidsExpiredAndTamperedTokens() throws Exception {
        long now = Instant.now().getEpochSecond();
        for (String sub : new String[]{"42", "", "not-a-uuid", "1-1-1-1-1"}) {
            assertTrue(service.validate(signedToken(Map.of("sub", sub, "email", "legacy@example.com",
                    "role", "ROLE_ESTUDANTE", "iat", now, "exp", now + 60))).isEmpty());
        }
        assertTrue(service.validate(signedToken(Map.of("sub", UUID.randomUUID().toString(),
                "role", "ROLE_ESTUDANTE", "iat", now - 120, "exp", now - 60))).isEmpty());
        String valid = service.createToken(usuario(TipoUsuario.ESTUDANTE));
        String[] parts = valid.split("\\.");
        assertTrue(service.validate(parts[0] + "." + parts[1] + "." + sign("different content")).isEmpty());
    }

    @Test
    void authenticatesByUserTokenAndKeepsUuidAsPrincipal() throws Exception {
        Usuario usuario = usuario(TipoUsuario.FUNCIONARIO);
        AtomicInteger lookups = new AtomicInteger();
        var filter = new JwtAuthenticationFilter(service, repository(usuario, true, lookups));
        var response = new MockHttpServletResponse();
        AtomicInteger calls = new AtomicInteger();
        filter.doFilter(request("/private", service.createToken(usuario)), response,
                (req, res) -> calls.incrementAndGet());
        assertEquals(1, lookups.get());
        assertEquals(1, calls.get());
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertTrue(authentication.isAuthenticated());
        assertEquals(usuario.getToken().toString(), authentication.getName());
        assertEquals("ROLE_FUNCIONARIO", authentication.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void rejectsMissingInvalidAndUnknownUserTokensWithoutContinuing() throws Exception {
        Usuario usuario = usuario(TipoUsuario.ESTUDANTE);
        AtomicInteger lookups = new AtomicInteger();
        var filter = new JwtAuthenticationFilter(service, repository(usuario, false, lookups));
        for (String token : new String[]{null, "broken", service.createToken(usuario)}) {
            var response = new MockHttpServletResponse();
            filter.doFilter(request("/private", token), response, (req, res) -> fail("Chain must stop"));
            assertEquals(401, response.getStatus());
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }
        assertEquals(1, lookups.get());
    }

    @Test
    void publicRoutesNeedNeitherJwtNorUserLookup() throws Exception {
        AtomicInteger lookups = new AtomicInteger();
        var filter = new JwtAuthenticationFilter(service, repository(usuario(TipoUsuario.ESTUDANTE), false, lookups));
        for (String path : new String[]{"/IES", "/auth/login", "/swagger-ui/index.html", "/v3/api-docs", "/api-docs/example"}) {
            for (String token : new String[]{null, "broken"}) {
                AtomicInteger calls = new AtomicInteger();
                var response = new MockHttpServletResponse();
                filter.doFilter(request(path, token), response, (req, res) -> calls.incrementAndGet());
                assertEquals(1, calls.get());
                assertEquals(200, response.getStatus());
            }
        }
        assertEquals(0, lookups.get());
    }

    @Test
    void liberacaoDaListagemNaoLiberaOperacoesPrivadas() throws Exception {
        AtomicInteger lookups = new AtomicInteger();
        var filter = new JwtAuthenticationFilter(service, repository(usuario(TipoUsuario.ESTUDANTE), false, lookups));
        for (String[] route : new String[][]{{"GET", "/IES/7/privado"}, {"PUT", "/IES/7"},
                {"DELETE", "/IES/7"}, {"POST", "/IES/7/chave/renovar"}, {"POST", "/IES"},
                {"GET", "/estudante"}, {"PATCH", "/estudante"},
                {"PATCH", "/estudante/senha"}, {"DELETE", "/estudante"},
                {"GET", "/funcionario"}, {"PATCH", "/funcionario"},
                {"PATCH", "/funcionario/senha"}, {"DELETE", "/funcionario"}}) {
            var response = new MockHttpServletResponse();
            filter.doFilter(new MockHttpServletRequest(route[0], route[1]), response,
                    (req, res) -> fail("Operação privada não pode prosseguir sem autenticação"));
            assertEquals(401, response.getStatus());
        }
        assertEquals(0, lookups.get());
    }

    @Test
    void rotacaoDoUuidRejeitaJwtAntigoEAceitaNovoLogin() throws Exception {
        Usuario usuario = usuario(TipoUsuario.ESTUDANTE);
        String anterior = service.createToken(usuario);
        usuario.setToken(UUID.randomUUID());
        var repository = (UsuarioRepository) Proxy.newProxyInstance(UsuarioRepository.class.getClassLoader(),
                new Class<?>[]{UsuarioRepository.class}, (proxy, method, args) -> {
                    assertEquals("findByToken", method.getName());
                    return usuario.getToken().equals(args[0]) ? Optional.of(usuario) : Optional.empty();
                });
        var filter = new JwtAuthenticationFilter(service, repository);
        var negado = new MockHttpServletResponse();
        filter.doFilter(request("/estudante", anterior), negado, (req, res) -> fail("Token antigo aceito"));
        assertEquals(401, negado.getStatus());
        var permitido = new MockHttpServletResponse();
        AtomicInteger chamadas = new AtomicInteger();
        filter.doFilter(request("/estudante", service.createToken(usuario)), permitido,
                (req, res) -> chamadas.incrementAndGet());
        assertEquals(1, chamadas.get());
        assertEquals(usuario.getToken().toString(), SecurityContextHolder.getContext().getAuthentication().getName());
    }

    private Usuario usuario(TipoUsuario tipo) {
        Usuario usuario = new Usuario() {};
        usuario.setId(42L);
        usuario.setToken(UUID.randomUUID());
        usuario.setEmail("private@example.com");
        usuario.setTipo(tipo);
        return usuario;
    }

    private MockHttpServletRequest request(String path, String token) {
        var request = new MockHttpServletRequest("GET", path);
        if (token != null) request.setCookies(new Cookie(JwtAuthenticationFilter.COOKIE_NAME, token));
        return request;
    }

    private UsuarioRepository repository(Usuario usuario, boolean exists, AtomicInteger lookups) {
        return (UsuarioRepository) Proxy.newProxyInstance(UsuarioRepository.class.getClassLoader(),
                new Class<?>[]{UsuarioRepository.class}, (proxy, method, args) -> {
                    assertEquals("findByToken", method.getName());
                    assertEquals(usuario.getToken(), args[0]);
                    lookups.incrementAndGet();
                    return exists ? Optional.of(usuario) : Optional.empty();
                });
    }

    private String signedToken(Map<String, Object> payload) throws Exception {
        var encoder = Base64.getUrlEncoder().withoutPadding();
        String unsigned = encoder.encodeToString(mapper.writeValueAsBytes(Map.of("alg", "HS256", "typ", "JWT")))
                + "." + encoder.encodeToString(mapper.writeValueAsBytes(payload));
        return unsigned + "." + sign(unsigned);
    }

    private String sign(String unsigned) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(unsigned.getBytes(StandardCharsets.US_ASCII)));
    }
}

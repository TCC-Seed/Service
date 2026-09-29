package com.tccseed.tcc_seed.security;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import com.tccseed.tcc_seed.domain.entity.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class JwtService {

    public static final Duration TOKEN_LIFETIME = Duration.ofDays(1);

    private static final String SIGNATURE_ALGORITHM = "HmacSHA256";
    private static final Base64.Encoder BASE64_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder BASE64_DECODER = Base64.getUrlDecoder();

    private final JsonMapper objectMapper;
    private final SecretKeySpec signingKey;

    public JwtService(JsonMapper objectMapper, @Value("${jwt.secret}") String secret) {
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET precisa ter pelo menos 32 bytes.");
        }
        this.objectMapper = objectMapper;
        this.signingKey = new SecretKeySpec(secretBytes, SIGNATURE_ALGORITHM);
    }

    public String createToken(Usuario usuario) {
        Instant now = Instant.now();
        String role = switch (usuario.getTipo()) {
            case ESTUDANTE -> "ROLE_ESTUDANTE";
            case FUNCIONARIO -> "ROLE_FUNCIONARIO";
        };

        try {
            String header = encodeJson(Map.of("alg", "HS256", "typ", "JWT"));
            String payload = encodeJson(Map.of(
                    "sub", usuario.getToken().toString(),
                    "role", role,
                    "iat", now.getEpochSecond(),
                    "exp", now.plus(TOKEN_LIFETIME).getEpochSecond()
            ));
            String unsignedToken = header + "." + payload;
            return unsignedToken + "." + BASE64_ENCODER.encodeToString(sign(unsignedToken));
        } catch (Exception exception) {
            throw new IllegalStateException("Não foi possível criar o JWT.", exception);
        }
    }

    public Optional<JwtClaims> validate(String token) {
        try {
            String[] parts = token.split("\\.", -1);
            if (parts.length != 3) {
                return Optional.empty();
            }

            JsonNode header = objectMapper.readTree(BASE64_DECODER.decode(parts[0]));
            if (!"HS256".equals(header.path("alg").asText())) {
                return Optional.empty();
            }

            String unsignedToken = parts[0] + "." + parts[1];
            byte[] actualSignature = BASE64_DECODER.decode(parts[2]);
            if (!java.security.MessageDigest.isEqual(sign(unsignedToken), actualSignature)) {
                return Optional.empty();
            }

            JsonNode claims = objectMapper.readTree(BASE64_DECODER.decode(parts[1]));
            String subject = claims.path("sub").asText();
            UUID userToken = UUID.fromString(subject);
            String role = claims.path("role").asText();
            long expiration = claims.path("exp").asLong(0);
            long issuedAt = claims.path("iat").asLong(0);
            long now = Instant.now().getEpochSecond();

            if (!userToken.toString().equals(subject) || expiration <= now || issuedAt > now
                    || !("ROLE_ESTUDANTE".equals(role) || "ROLE_FUNCIONARIO".equals(role))) {
                return Optional.empty();
            }

            return Optional.of(new JwtClaims(userToken, role));
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    private String encodeJson(Object value) throws Exception {
        return BASE64_ENCODER.encodeToString(objectMapper.writeValueAsBytes(value));
    }

    private byte[] sign(String value) throws Exception {
        Mac mac = Mac.getInstance(SIGNATURE_ALGORITHM);
        mac.init(signingKey);
        return mac.doFinal(value.getBytes(StandardCharsets.US_ASCII));
    }

    public record JwtClaims(UUID subject, String role) {
    }
}

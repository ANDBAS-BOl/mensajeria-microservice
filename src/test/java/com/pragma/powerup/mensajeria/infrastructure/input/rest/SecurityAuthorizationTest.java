package com.pragma.powerup.mensajeria.infrastructure.input.rest;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAuthorizationTest {

    private static final String SECRET = "pragma_super_secret_key_change_in_production";
    private static final String URL = "/api/v1/mensajeria/sms";
    private static final String VALID_BODY =
            "{\"phoneNumber\":\"+573005698325\",\"message\":\"Tu pedido esta listo. PIN: 123456\"}";

    @Autowired
    private MockMvc mockMvc;

    private String adminToken;
    private String propietarioToken;
    private String empleadoToken;
    private String clienteToken;

    @BeforeEach
    void setUp() {
        adminToken = generateToken("1", "ADMINISTRADOR", false);
        propietarioToken = generateToken("2", "PROPIETARIO", false);
        empleadoToken = generateToken("3", "EMPLEADO", false);
        clienteToken = generateToken("4", "CLIENTE", false);
    }

    @Test
    void shouldAllowEmpleadoRole() throws Exception {
        mockMvc.perform(post(URL)
                        .header("Authorization", bearer(empleadoToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMINISTRADOR", "PROPIETARIO", "CLIENTE"})
    void shouldRejectOtherRoles(String role) throws Exception {
        mockMvc.perform(post(URL)
                        .header("Authorization", bearer(tokenForRole(role)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectWithoutToken() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectExpiredToken() throws Exception {
        String expiredToken = generateToken("5", "EMPLEADO", true);

        mockMvc.perform(post(URL)
                        .header("Authorization", bearer(expiredToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isForbidden());
    }

    private String tokenForRole(String role) {
        return switch (role) {
            case "ADMINISTRADOR" -> adminToken;
            case "PROPIETARIO" -> propietarioToken;
            case "EMPLEADO" -> empleadoToken;
            case "CLIENTE" -> clienteToken;
            default -> throw new IllegalArgumentException("Rol desconocido: " + role);
        };
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private String generateToken(String subject, String rol, boolean expired) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(expired ? now - 120_000 : now);
        Date expiration = new Date(expired ? now - 60_000 : now + 60_000);
        return Jwts.builder()
                .setSubject(subject)
                .claim("correo", "test@pragma.com")
                .claim("rol", rol)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}

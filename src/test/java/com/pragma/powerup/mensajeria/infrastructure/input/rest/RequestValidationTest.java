package com.pragma.powerup.mensajeria.infrastructure.input.rest;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RequestValidationTest {

    private static final String SECRET = "pragma_super_secret_key_change_in_production";
    private static final String URL = "/api/v1/mensajeria/sms";

    @Autowired
    private MockMvc mockMvc;

    private String empleadoToken;

    @BeforeEach
    void setUp() {
        empleadoToken = generateToken("EMPLEADO");
    }

    @Test
    void shouldReturn400WhenPhoneIsInvalid() throws Exception {
        String body = "{\"phoneNumber\":\"ABC\",\"message\":\"mensaje valido\"}";

        mockMvc.perform(post(URL)
                        .header("Authorization", bearer(empleadoToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.phoneNumber").exists());
    }

    @Test
    void shouldReturn400WhenMessageIsBlank() throws Exception {
        String body = "{\"phoneNumber\":\"+573005698325\",\"message\":\"\"}";

        mockMvc.perform(post(URL)
                        .header("Authorization", bearer(empleadoToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.message").exists());
    }

    @Test
    void shouldReturn400WhenMessageExceedsMaxLength() throws Exception {
        String body = "{\"phoneNumber\":\"+573005698325\",\"message\":\"" + "a".repeat(321) + "\"}";

        mockMvc.perform(post(URL)
                        .header("Authorization", bearer(empleadoToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.message").exists());
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private String generateToken(String rol) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject("10")
                .claim("correo", "test@pragma.com")
                .claim("rol", rol)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}

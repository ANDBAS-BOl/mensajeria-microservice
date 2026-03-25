package com.pragma.powerup.infrastructure.input.rest;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
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
class MensajeriaRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldSendSmsWithMockProviderWhenTokenHasAllowedRole() throws Exception {
        String jwt = generateToken("EMPLEADO");
        String body = "{\"phoneNumber\":\"+573005698325\",\"message\":\"Tu pedido esta listo. PIN: 123456\"}";

        mockMvc.perform(post("/api/v1/mensajeria/sms")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sent").value(true))
                .andExpect(jsonPath("$.mockProvider").value(true))
                .andExpect(jsonPath("$.provider").value("mock"));
    }

    @Test
    void shouldRejectWhenRoleIsNotAllowed() throws Exception {
        String jwt = generateToken("CLIENTE");
        String body = "{\"phoneNumber\":\"+573005698325\",\"message\":\"mensaje\"}";

        mockMvc.perform(post("/api/v1/mensajeria/sms")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectWhenRoleIsPropietario() throws Exception {
        String jwt = generateToken("PROPIETARIO");
        String body = "{\"phoneNumber\":\"+573005698325\",\"message\":\"mensaje\"}";

        mockMvc.perform(post("/api/v1/mensajeria/sms")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectWhenTokenIsMissing() throws Exception {
        String body = "{\"phoneNumber\":\"+573005698325\",\"message\":\"mensaje\"}";

        mockMvc.perform(post("/api/v1/mensajeria/sms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    private String generateToken(String rol) {
        SecretKey key = Keys.hmacShaKeyFor(
                "pragma_super_secret_key_change_in_production".getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        Date exp = new Date(now.getTime() + 60000);
        return Jwts.builder()
                .setSubject("10")
                .claim("correo", "test@pragma.com")
                .claim("rol", rol)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}

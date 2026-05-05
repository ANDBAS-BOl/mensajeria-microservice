package com.pragma.powerup.mensajeria.architecture;

import com.pragma.powerup.mensajeria.infrastructure.input.rest.SmsRestController;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ControllerContractBaselineTest {

    private static final String BASE_PATH = "/api/v1/mensajeria";

    @Test
    void shouldKeepAllEndpointContracts() {
        Map<String, EndpointContract> expected = expectedContracts();
        Map<String, EndpointContract> actual = new HashMap<>();

        collectContracts(SmsRestController.class, actual);

        assertEquals(expected, actual, "Cambio detectado en contratos HTTP de mensajeria");
    }

    private void collectContracts(Class<?> controllerClass, Map<String, EndpointContract> target) {
        for (Method method : controllerClass.getDeclaredMethods()) {
            if (!java.lang.reflect.Modifier.isPublic(method.getModifiers())) {
                continue;
            }
            if (method.isAnnotationPresent(ExceptionHandler.class) || method.isSynthetic()) {
                continue;
            }
            EndpointContract contract = toContract(method);
            if (contract != null) {
                target.put(method.getName(), contract);
            }
        }
    }

    private EndpointContract toContract(Method method) {
        PostMapping post = method.getAnnotation(PostMapping.class);
        if (post == null) {
            return null;
        }
        PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);
        String role = preAuthorize == null ? "" : preAuthorize.value();
        return new EndpointContract("POST", BASE_PATH + firstPath(post.value()), role);
    }

    private String firstPath(String[] values) {
        if (values == null || values.length == 0) {
            return "";
        }
        return values[0];
    }

    private Map<String, EndpointContract> expectedContracts() {
        return Map.of("sendSms",
                new EndpointContract("POST", "/api/v1/mensajeria/sms", "hasRole('EMPLEADO')"));
    }

    private record EndpointContract(String httpMethod, String path, String role) {
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof EndpointContract other)) {
                return false;
            }
            return Objects.equals(httpMethod, other.httpMethod)
                    && Objects.equals(path, other.path)
                    && Objects.equals(role, other.role);
        }

        @Override
        public int hashCode() {
            return Objects.hash(httpMethod, path, role);
        }
    }
}

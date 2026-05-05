package com.pragma.powerup.mensajeria.infrastructure.exceptionhandler;

import com.pragma.powerup.mensajeria.domain.exception.BusinessRuleException;
import com.pragma.powerup.mensajeria.domain.exception.DomainException;
import com.pragma.powerup.mensajeria.domain.exception.InternalProcessException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ControllerAdvisorTest {

    private final ControllerAdvisor advisor = new ControllerAdvisor();

    @Test
    void handleBusinessRule_returns400WithMessage() {
        ResponseEntity<Map<String, String>> response =
                advisor.handleBusinessRuleException(new BusinessRuleException("regla"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("regla", response.getBody().get("message"));
    }

    @Test
    void handleInternalProcess_returns500WithMessage() {
        ResponseEntity<Map<String, String>> response =
                advisor.handleInternalProcessException(new InternalProcessException("boom"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("boom", response.getBody().get("message"));
    }

    @Test
    void handleNotReadable_returns400WithGenericBody() {
        HttpMessageNotReadableException exception =
                new HttpMessageNotReadableException("bad json", new MockHttpInputMessage(new byte[0]));

        ResponseEntity<Map<String, String>> response = advisor.handleMessageNotReadableException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid request body", response.getBody().get("message"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void handleValidation_returns400WithFieldMap() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "phoneNumber", "Numero de telefono invalido"));
        Method dummy = DummyController.class.getDeclaredMethod("dummy", String.class);
        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(new org.springframework.core.MethodParameter(dummy, 0), bindingResult);

        ResponseEntity<Map<String, Object>> response = advisor.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().get("message"));
        Object errors = response.getBody().get("errors");
        assertInstanceOf(Map.class, errors);
        Map<String, String> errorMap = (Map<String, String>) errors;
        assertEquals("Numero de telefono invalido", errorMap.get("phoneNumber"));
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void handleConstraintViolation_returns400() {
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        javax.validation.Path path = mock(javax.validation.Path.class);
        when(path.toString()).thenReturn("sendSms.request.phoneNumber");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("Numero de telefono invalido");

        Set<ConstraintViolation<?>> violations = new LinkedHashSet<>();
        violations.add(violation);
        ConstraintViolationException exception = new ConstraintViolationException(violations);

        ResponseEntity<Map<String, Object>> response = advisor.handleConstraintViolationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> errorMap = (Map<String, String>) response.getBody().get("errors");
        assertTrue(errorMap.containsKey("phoneNumber"));
    }

    @Test
    void handleDomainException_returns400WithMessage() {
        ResponseEntity<Map<String, String>> response =
                advisor.handleDomainException(new TestDomainException("dom"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("dom", response.getBody().get("message"));
    }

    private static class TestDomainException extends DomainException {
        TestDomainException(String message) {
            super(message);
        }
    }

    private static class DummyController {
        @SuppressWarnings("unused")
        public void dummy(String body) {
        }
    }
}

package com.pragma.powerup.mensajeria.infrastructure.input.rest;

import com.pragma.powerup.mensajeria.application.dto.request.SendSmsRequest;
import com.pragma.powerup.mensajeria.application.dto.response.SendSmsResponse;
import com.pragma.powerup.mensajeria.application.handler.ISmsHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/v1/mensajeria")
@RequiredArgsConstructor
public class SmsRestController {

    private final ISmsHandler smsHandler;

    @PostMapping("/sms")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<SendSmsResponse> sendSms(@Valid @RequestBody SendSmsRequest request) {
        return ResponseEntity.ok(smsHandler.sendSms(request));
    }
}

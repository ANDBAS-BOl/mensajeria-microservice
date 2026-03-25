package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.SendSmsRequestDto;
import com.pragma.powerup.application.dto.response.SendSmsResponseDto;
import com.pragma.powerup.application.handler.IObjectHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/mensajeria")
@RequiredArgsConstructor
@Validated
@Tag(name = "Mensajeria", description = "Envio de notificaciones SMS para pedidos")
public class ObjectRestController {

    private final IObjectHandler objectHandler;

    @Operation(summary = "Enviar SMS", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('EMPLEADO')")
    @PostMapping("/sms")
    public ResponseEntity<SendSmsResponseDto> sendSms(@Valid @RequestBody SendSmsRequestDto requestDto) {
        SendSmsResponseDto responseDto = objectHandler.sendSms(requestDto);
        HttpStatus status = responseDto.isSent() ? HttpStatus.OK : HttpStatus.BAD_GATEWAY;
        return ResponseEntity.status(status).body(responseDto);
    }
}
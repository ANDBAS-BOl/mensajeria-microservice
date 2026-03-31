package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.SendSmsRequestDto;
import com.pragma.powerup.application.dto.response.SendSmsResponseDto;
import com.pragma.powerup.domain.api.ISmsServicePort;
import com.pragma.powerup.domain.model.SmsMessageModel;
import com.pragma.powerup.domain.model.SmsSendResultModel;
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

    private final ISmsServicePort smsServicePort;

    @PostMapping("/sms")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<SendSmsResponseDto> sendSms(@Valid @RequestBody SendSmsRequestDto request) {
        SmsSendResultModel result = smsServicePort.sendSms(
                new SmsMessageModel(request.getPhoneNumber(), request.getMessage()));

        SendSmsResponseDto response = SendSmsResponseDto.builder()
                .sent(result.isSent())
                .mockProvider(result.isMockProvider())
                .retryable(result.isRetryable())
                .provider(result.getProvider())
                .messageId(result.getMessageId())
                .errorCode(result.getErrorCode())
                .errorMessage(result.getErrorMessage())
                .build();
        return ResponseEntity.ok(response);
    }
}

package com.pragma.powerup.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SendSmsResponseDto {
    private final boolean sent;
    private final boolean mockProvider;
    private final boolean retryable;
    private final String provider;
    private final String messageId;
    private final String errorCode;
    private final String errorMessage;
}

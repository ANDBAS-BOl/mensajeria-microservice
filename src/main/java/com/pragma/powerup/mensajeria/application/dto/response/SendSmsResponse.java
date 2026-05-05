package com.pragma.powerup.mensajeria.application.dto.response;

public record SendSmsResponse(
        boolean sent,
        boolean mockProvider,
        boolean retryable,
        String provider,
        String messageId,
        String errorCode,
        String errorMessage
) {
}

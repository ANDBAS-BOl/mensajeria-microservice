package com.pragma.powerup.mensajeria.domain.model;

import com.pragma.powerup.mensajeria.domain.exception.InternalProcessException;
import com.pragma.powerup.mensajeria.domain.utils.DomainErrorMessage;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SmsSendResultModel {
    private final boolean sent;
    private final boolean mockProvider;
    private final boolean retryable;
    private final String provider;
    private final String messageId;
    private final String errorCode;
    private final String errorMessage;

    public boolean isSuccess() {
        return sent;
    }

    public void assertSent() {
        if (!sent) {
            throw new InternalProcessException(DomainErrorMessage.SMS_SEND_FAILED.getMessage());
        }
    }
}

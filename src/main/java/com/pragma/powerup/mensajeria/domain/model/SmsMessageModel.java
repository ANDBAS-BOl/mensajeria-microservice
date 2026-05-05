package com.pragma.powerup.mensajeria.domain.model;

import com.pragma.powerup.mensajeria.domain.exception.BusinessRuleException;
import com.pragma.powerup.mensajeria.domain.utils.DomainErrorMessage;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SmsMessageModel {
    public static final String VALID_PHONE_REGEX = "^\\+?\\d{7,13}$";
    public static final int MAX_MESSAGE_LENGTH = 320;

    String phoneNumber;
    String message;

    public static class SmsMessageModelBuilder {
        public SmsMessageModel build() {
            if (phoneNumber == null || phoneNumber.isBlank()) {
                throw new BusinessRuleException(DomainErrorMessage.PHONE_REQUIRED.getMessage());
            }
            if (!phoneNumber.matches(VALID_PHONE_REGEX)) {
                throw new BusinessRuleException(DomainErrorMessage.PHONE_INVALID.getMessage());
            }
            if (message == null || message.isBlank()) {
                throw new BusinessRuleException(DomainErrorMessage.MESSAGE_REQUIRED.getMessage());
            }
            if (message.length() > MAX_MESSAGE_LENGTH) {
                throw new BusinessRuleException(DomainErrorMessage.MESSAGE_TOO_LONG.getMessage());
            }
            return new SmsMessageModel(phoneNumber, message);
        }
    }
}

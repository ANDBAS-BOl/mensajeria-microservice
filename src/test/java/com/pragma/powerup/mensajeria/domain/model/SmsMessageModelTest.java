package com.pragma.powerup.mensajeria.domain.model;

import com.pragma.powerup.mensajeria.domain.exception.BusinessRuleException;
import com.pragma.powerup.mensajeria.domain.utils.DomainErrorMessage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SmsMessageModelTest {

    @Test
    void buildShouldSucceedForValidPayload() {
        SmsMessageModel model = SmsMessageModel.builder()
                .phoneNumber("+573005698325")
                .message("Tu pedido esta listo. PIN: 123456")
                .build();

        assertThat(model.getPhoneNumber()).isEqualTo("+573005698325");
        assertThat(model.getMessage()).isEqualTo("Tu pedido esta listo. PIN: 123456");
    }

    @Test
    void buildShouldFailWhenPhoneIsNull() {
        assertThatThrownBy(() -> SmsMessageModel.builder()
                .phoneNumber(null)
                .message("mensaje")
                .build())
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage(DomainErrorMessage.PHONE_REQUIRED.getMessage());
    }

    @Test
    void buildShouldFailWhenPhoneIsBlank() {
        assertThatThrownBy(() -> SmsMessageModel.builder()
                .phoneNumber("   ")
                .message("mensaje")
                .build())
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage(DomainErrorMessage.PHONE_REQUIRED.getMessage());
    }

    @Test
    void buildShouldFailWhenPhoneFormatInvalid() {
        assertThatThrownBy(() -> SmsMessageModel.builder()
                .phoneNumber("abc123")
                .message("mensaje")
                .build())
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage(DomainErrorMessage.PHONE_INVALID.getMessage());
    }

    @Test
    void buildShouldFailWhenPhoneLengthOutOfRange() {
        assertThatThrownBy(() -> SmsMessageModel.builder()
                .phoneNumber("+123456")
                .message("mensaje")
                .build())
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage(DomainErrorMessage.PHONE_INVALID.getMessage());
    }

    @Test
    void buildShouldFailWhenMessageIsNull() {
        assertThatThrownBy(() -> SmsMessageModel.builder()
                .phoneNumber("+573005698325")
                .message(null)
                .build())
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage(DomainErrorMessage.MESSAGE_REQUIRED.getMessage());
    }

    @Test
    void buildShouldFailWhenMessageIsBlank() {
        assertThatThrownBy(() -> SmsMessageModel.builder()
                .phoneNumber("+573005698325")
                .message(" ")
                .build())
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage(DomainErrorMessage.MESSAGE_REQUIRED.getMessage());
    }

    @Test
    void buildShouldFailWhenMessageExceedsMaxLength() {
        String longMessage = "a".repeat(SmsMessageModel.MAX_MESSAGE_LENGTH + 1);

        assertThatThrownBy(() -> SmsMessageModel.builder()
                .phoneNumber("+573005698325")
                .message(longMessage)
                .build())
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage(DomainErrorMessage.MESSAGE_TOO_LONG.getMessage());
    }
}

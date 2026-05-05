package com.pragma.powerup.mensajeria.domain.model;

import com.pragma.powerup.mensajeria.domain.exception.InternalProcessException;
import com.pragma.powerup.mensajeria.domain.utils.DomainErrorMessage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SmsSendResultModelTest {

    @Test
    void assertSentShouldPassWhenSent() {
        SmsSendResultModel result = SmsSendResultModel.builder()
                .sent(true)
                .mockProvider(true)
                .retryable(false)
                .provider("mock")
                .messageId("id-1")
                .build();

        assertThatCode(result::assertSent).doesNotThrowAnyException();
    }

    @Test
    void assertSentShouldFailWhenNotSent() {
        SmsSendResultModel result = SmsSendResultModel.builder()
                .sent(false)
                .mockProvider(false)
                .retryable(true)
                .provider("twilio")
                .errorCode("500")
                .errorMessage("fail")
                .build();

        assertThatThrownBy(result::assertSent)
                .isInstanceOf(InternalProcessException.class)
                .hasMessage(DomainErrorMessage.SMS_SEND_FAILED.getMessage());
    }
}

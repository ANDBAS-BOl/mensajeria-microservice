package com.pragma.powerup.mensajeria.infrastructure.out.sms;

import com.pragma.powerup.mensajeria.domain.model.SmsMessageModel;
import com.pragma.powerup.mensajeria.domain.model.SmsSendResultModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MockSmsSenderAdapterTest {

    private final MockSmsSenderAdapter adapter = new MockSmsSenderAdapter();

    @Test
    void sendShouldReturnSuccessfulMockResponse() {
        SmsMessageModel messageModel = SmsMessageModel.builder()
                .phoneNumber("+573005698325")
                .message("Tu pedido esta listo. PIN: 123456")
                .build();

        SmsSendResultModel result = adapter.send(messageModel);

        assertThat(result.isSent()).isTrue();
        assertThat(result.isMockProvider()).isTrue();
        assertThat(result.isRetryable()).isFalse();
        assertThat(result.getProvider()).isEqualTo("mock");
        assertThat(result.getMessageId()).isNotBlank();
        assertThat(result.getErrorCode()).isNull();
        assertThat(result.getErrorMessage()).isNull();
    }
}

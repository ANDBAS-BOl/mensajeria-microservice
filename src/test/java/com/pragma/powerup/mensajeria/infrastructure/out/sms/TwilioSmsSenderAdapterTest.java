package com.pragma.powerup.mensajeria.infrastructure.out.sms;

import com.pragma.powerup.mensajeria.domain.model.SmsMessageModel;
import com.pragma.powerup.mensajeria.domain.model.SmsSendResultModel;
import com.pragma.powerup.mensajeria.domain.utils.DomainErrorMessage;
import com.twilio.exception.ApiException;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.assertj.core.api.Assertions.assertThat;

class TwilioSmsSenderAdapterTest {

    @Test
    void sendShouldReturnSentWhenProviderSucceedsOnFirstAttempt() {
        TwilioProperties properties = testProperties(3);
        SmsMessageModel messageModel = validMessage();
        TestableTwilioSmsSenderAdapter adapter = new TestableTwilioSmsSenderAdapter(properties, "SM_OK_123");

        SmsSendResultModel result = adapter.send(messageModel);

        assertThat(result.isSent()).isTrue();
        assertThat(result.isRetryable()).isFalse();
        assertThat(result.isMockProvider()).isFalse();
        assertThat(result.getProvider()).isEqualTo("twilio");
        assertThat(result.getMessageId()).isEqualTo("SM_OK_123");
        assertThat(result.getErrorCode()).isNull();
        assertThat(result.getErrorMessage()).isNull();
        assertThat(adapter.getCreateMessageCalls()).isEqualTo(1);
    }

    @Test
    void sendShouldRetryOnApiException5xxAndSucceedOnSecondAttempt() {
        TwilioProperties properties = testProperties(3);
        SmsMessageModel messageModel = validMessage();
        ApiException retryableException = apiException(503, 20429);
        TestableTwilioSmsSenderAdapter adapter = new TestableTwilioSmsSenderAdapter(properties, retryableException, "SM_RETRY_OK");

        SmsSendResultModel result = adapter.send(messageModel);

        assertThat(result.isSent()).isTrue();
        assertThat(result.isRetryable()).isFalse();
        assertThat(result.getProvider()).isEqualTo("twilio");
        assertThat(result.getMessageId()).isEqualTo("SM_RETRY_OK");
        assertThat(adapter.getCreateMessageCalls()).isEqualTo(2);
    }

    @Test
    void sendShouldNotRetryOnApiException4xxAndReturnFailure() {
        TwilioProperties properties = testProperties(3);
        SmsMessageModel messageModel = validMessage();
        ApiException nonRetryableException = apiException(400, 21614);
        TestableTwilioSmsSenderAdapter adapter = new TestableTwilioSmsSenderAdapter(properties, nonRetryableException);

        SmsSendResultModel result = adapter.send(messageModel);

        assertThat(result.isSent()).isFalse();
        assertThat(result.isRetryable()).isFalse();
        assertThat(result.getProvider()).isEqualTo("twilio");
        assertThat(result.getErrorCode()).isEqualTo("21614");
        assertThat(result.getErrorMessage()).isEqualTo(DomainErrorMessage.SMS_SEND_FAILED.getMessage());
        assertThat(adapter.getCreateMessageCalls()).isEqualTo(1);
    }

    private static SmsMessageModel validMessage() {
        return SmsMessageModel.builder()
                .phoneNumber("+573005698325")
                .message("Tu pedido esta listo. PIN: 123456")
                .build();
    }

    private static TwilioProperties testProperties(int maxAttempts) {
        TwilioProperties properties = new TwilioProperties();
        properties.setAccountSid("sid");
        properties.setAuthToken("token");
        properties.setFromNumber("+15551234567");
        properties.setRetryMaxAttempts(maxAttempts);
        properties.setRetryDelayMs(0L);
        return properties;
    }

    private static ApiException apiException(int statusCode, int code) {
        return new ApiException("Twilio error") {
            @Override
            public Integer getStatusCode() {
                return statusCode;
            }

            @Override
            public Integer getCode() {
                return code;
            }
        };
    }

    private static final class TestableTwilioSmsSenderAdapter extends TwilioSmsSenderAdapter {
        private final Deque<Object> outcomes;
        private int createMessageCalls;

        private TestableTwilioSmsSenderAdapter(TwilioProperties twilioProperties, Object... outcomes) {
            super(twilioProperties);
            this.outcomes = new ArrayDeque<>();
            for (Object outcome : outcomes) {
                this.outcomes.add(outcome);
            }
        }

        @Override
        void initializeTwilio() {
        }

        @Override
        String sendMessageAndGetId(SmsMessageModel messageModel) {
            createMessageCalls++;
            Object outcome = outcomes.removeFirst();
            if (outcome instanceof ApiException apiException) {
                throw apiException;
            }
            return (String) outcome;
        }

        private int getCreateMessageCalls() {
            return createMessageCalls;
        }
    }
}

package com.pragma.powerup.infrastructure.sms;

import com.pragma.powerup.domain.model.SmsMessageModel;
import com.pragma.powerup.domain.model.SmsSendResultModel;
import com.pragma.powerup.domain.spi.ISmsSenderPort;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwilioSmsSenderAdapter implements ISmsSenderPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwilioSmsSenderAdapter.class);
    private final TwilioProperties twilioProperties;

    public TwilioSmsSenderAdapter(TwilioProperties twilioProperties) {
        this.twilioProperties = twilioProperties;
        Twilio.init(twilioProperties.getAccountSid(), twilioProperties.getAuthToken());
    }

    @Override
    public SmsSendResultModel send(SmsMessageModel messageModel) {
        int maxAttempts = Math.max(1, twilioProperties.getRetryMaxAttempts());
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                Message message = Message.creator(
                        new PhoneNumber(messageModel.getPhoneNumber()),
                        new PhoneNumber(twilioProperties.getFromNumber()),
                        messageModel.getMessage()).create();

                return SmsSendResultModel.builder()
                        .sent(true)
                        .mockProvider(false)
                        .retryable(false)
                        .provider("twilio")
                        .messageId(message.getSid())
                        .build();
            } catch (ApiException apiException) {
                boolean retryable = apiException.getStatusCode() != null && apiException.getStatusCode() >= 500;
                LOGGER.warn("Fallo envio SMS (intento {}/{}). Codigo={} Retryable={}",
                        attempt, maxAttempts, apiException.getCode(), retryable);
                if (!retryable || attempt == maxAttempts) {
                    return SmsSendResultModel.builder()
                            .sent(false)
                            .mockProvider(false)
                            .retryable(retryable)
                            .provider("twilio")
                            .errorCode(String.valueOf(apiException.getCode()))
                            .errorMessage("No fue posible enviar el SMS")
                            .build();
                }
                sleepBeforeRetry();
            } catch (Exception ex) {
                LOGGER.warn("Error tecnico enviando SMS (intento {}/{}): {}", attempt, maxAttempts, ex.getMessage());
                if (attempt == maxAttempts) {
                    return SmsSendResultModel.builder()
                            .sent(false)
                            .mockProvider(false)
                            .retryable(true)
                            .provider("twilio")
                            .errorCode("TECHNICAL_ERROR")
                            .errorMessage("No fue posible enviar el SMS")
                            .build();
                }
                sleepBeforeRetry();
            }
        }
        return SmsSendResultModel.builder()
                .sent(false)
                .mockProvider(false)
                .retryable(true)
                .provider("twilio")
                .errorCode("UNKNOWN")
                .errorMessage("No fue posible enviar el SMS")
                .build();
    }

    private void sleepBeforeRetry() {
        try {
            Thread.sleep(Math.max(0L, twilioProperties.getRetryDelayMs()));
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
    }
}

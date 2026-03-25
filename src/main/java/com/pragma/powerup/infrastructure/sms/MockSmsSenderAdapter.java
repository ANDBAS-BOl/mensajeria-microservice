package com.pragma.powerup.infrastructure.sms;

import com.pragma.powerup.domain.model.SmsMessageModel;
import com.pragma.powerup.domain.model.SmsSendResultModel;
import com.pragma.powerup.domain.spi.ISmsSenderPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class MockSmsSenderAdapter implements ISmsSenderPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockSmsSenderAdapter.class);

    @Override
    public SmsSendResultModel send(SmsMessageModel messageModel) {
        // No se loguea el contenido completo del mensaje para evitar exponer PIN.
        LOGGER.info("Mock SMS enviado a {} con longitud de mensaje {}", messageModel.getPhoneNumber(),
                messageModel.getMessage().length());
        return SmsSendResultModel.builder()
                .sent(true)
                .mockProvider(true)
                .retryable(false)
                .provider("mock")
                .messageId(UUID.randomUUID().toString())
                .build();
    }
}

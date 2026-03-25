package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.ISmsServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.SmsMessageModel;
import com.pragma.powerup.domain.model.SmsSendResultModel;
import com.pragma.powerup.domain.spi.ISmsSenderPort;

public class SmsUseCase implements ISmsServicePort {

    private final ISmsSenderPort smsSenderPort;

    public SmsUseCase(ISmsSenderPort smsSenderPort) {
        this.smsSenderPort = smsSenderPort;
    }

    @Override
    public SmsSendResultModel sendSms(SmsMessageModel smsMessage) {
        if (smsMessage.getPhoneNumber() == null || smsMessage.getPhoneNumber().isBlank()) {
            throw new DomainException("El numero de telefono es obligatorio");
        }
        if (smsMessage.getMessage() == null || smsMessage.getMessage().isBlank()) {
            throw new DomainException("El mensaje es obligatorio");
        }
        return smsSenderPort.send(smsMessage);
    }
}

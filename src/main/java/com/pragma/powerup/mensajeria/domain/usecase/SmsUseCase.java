package com.pragma.powerup.mensajeria.domain.usecase;

import com.pragma.powerup.mensajeria.domain.api.SmsUseCasePort;
import com.pragma.powerup.mensajeria.domain.model.SmsMessageModel;
import com.pragma.powerup.mensajeria.domain.model.SmsSendResultModel;
import com.pragma.powerup.mensajeria.domain.spi.SmsSenderPort;

public class SmsUseCase implements SmsUseCasePort {

    private final SmsSenderPort smsSenderPort;

    public SmsUseCase(SmsSenderPort smsSenderPort) {
        this.smsSenderPort = smsSenderPort;
    }

    @Override
    public SmsSendResultModel sendSms(SmsMessageModel smsMessage) {
        return smsSenderPort.send(smsMessage);
    }
}

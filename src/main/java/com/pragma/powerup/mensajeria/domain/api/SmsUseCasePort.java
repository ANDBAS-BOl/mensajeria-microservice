package com.pragma.powerup.mensajeria.domain.api;

import com.pragma.powerup.mensajeria.domain.model.SmsMessageModel;
import com.pragma.powerup.mensajeria.domain.model.SmsSendResultModel;

public interface SmsUseCasePort {
    SmsSendResultModel sendSms(SmsMessageModel smsMessage);
}

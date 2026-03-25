package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.SmsMessageModel;
import com.pragma.powerup.domain.model.SmsSendResultModel;

public interface ISmsServicePort {
    SmsSendResultModel sendSms(SmsMessageModel smsMessage);
}

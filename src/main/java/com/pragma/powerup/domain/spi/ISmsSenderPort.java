package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.SmsMessageModel;
import com.pragma.powerup.domain.model.SmsSendResultModel;

public interface ISmsSenderPort {
    SmsSendResultModel send(SmsMessageModel messageModel);
}

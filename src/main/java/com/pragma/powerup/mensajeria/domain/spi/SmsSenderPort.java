package com.pragma.powerup.mensajeria.domain.spi;

import com.pragma.powerup.mensajeria.domain.model.SmsMessageModel;
import com.pragma.powerup.mensajeria.domain.model.SmsSendResultModel;

public interface SmsSenderPort {
    SmsSendResultModel send(SmsMessageModel messageModel);
}

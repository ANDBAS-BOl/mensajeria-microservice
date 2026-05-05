package com.pragma.powerup.mensajeria.application.handler;

import com.pragma.powerup.mensajeria.application.dto.request.SendSmsRequest;
import com.pragma.powerup.mensajeria.application.dto.response.SendSmsResponse;

public interface ISmsHandler {

    SendSmsResponse sendSms(SendSmsRequest request);
}

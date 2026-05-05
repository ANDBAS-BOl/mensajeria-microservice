package com.pragma.powerup.mensajeria.application.handler.impl;

import com.pragma.powerup.mensajeria.application.dto.request.SendSmsRequest;
import com.pragma.powerup.mensajeria.application.dto.response.SendSmsResponse;
import com.pragma.powerup.mensajeria.application.handler.ISmsHandler;
import com.pragma.powerup.mensajeria.application.mapper.ISmsDtoMapper;
import com.pragma.powerup.mensajeria.domain.api.SmsUseCasePort;
import com.pragma.powerup.mensajeria.domain.model.SmsMessageModel;
import com.pragma.powerup.mensajeria.domain.model.SmsSendResultModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmsHandler implements ISmsHandler {

    private final SmsUseCasePort smsUseCasePort;
    private final ISmsDtoMapper smsDtoMapper;

    @Override
    public SendSmsResponse sendSms(SendSmsRequest request) {
        SmsMessageModel model = smsDtoMapper.toModel(request);
        SmsSendResultModel result = smsUseCasePort.sendSms(model);
        return smsDtoMapper.toResponseDto(result);
    }
}

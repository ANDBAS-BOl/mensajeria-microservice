package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.SendSmsRequestDto;
import com.pragma.powerup.application.dto.response.SendSmsResponseDto;
import com.pragma.powerup.application.handler.IObjectHandler;
import com.pragma.powerup.domain.api.ISmsServicePort;
import com.pragma.powerup.domain.model.SmsMessageModel;
import com.pragma.powerup.domain.model.SmsSendResultModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ObjectHandler implements IObjectHandler {

    private final ISmsServicePort smsServicePort;

    @Override
    public SendSmsResponseDto sendSms(SendSmsRequestDto sendSmsRequestDto) {
        SmsMessageModel smsMessageModel = new SmsMessageModel(
                sendSmsRequestDto.getPhoneNumber(),
                sendSmsRequestDto.getMessage());
        SmsSendResultModel resultModel = smsServicePort.sendSms(smsMessageModel);
        return SendSmsResponseDto.builder()
                .sent(resultModel.isSent())
                .provider(resultModel.getProvider())
                .messageId(resultModel.getMessageId())
                .mockProvider(resultModel.isMockProvider())
                .retryable(resultModel.isRetryable())
                .errorCode(resultModel.getErrorCode())
                .errorMessage(resultModel.getErrorMessage())
                .build();
    }
}
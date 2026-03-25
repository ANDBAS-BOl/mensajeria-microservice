package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.SendSmsRequestDto;
import com.pragma.powerup.application.dto.response.SendSmsResponseDto;

public interface IObjectHandler {

    SendSmsResponseDto sendSms(SendSmsRequestDto sendSmsRequestDto);
}
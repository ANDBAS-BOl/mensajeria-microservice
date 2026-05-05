package com.pragma.powerup.mensajeria.application.mapper;

import com.pragma.powerup.mensajeria.application.dto.request.SendSmsRequest;
import com.pragma.powerup.mensajeria.application.dto.response.SendSmsResponse;
import com.pragma.powerup.mensajeria.domain.model.SmsMessageModel;
import com.pragma.powerup.mensajeria.domain.model.SmsSendResultModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ISmsDtoMapper {

    SmsMessageModel toModel(SendSmsRequest dto);

    SendSmsResponse toResponseDto(SmsSendResultModel model);
}

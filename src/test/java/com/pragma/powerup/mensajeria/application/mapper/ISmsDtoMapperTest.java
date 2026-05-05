package com.pragma.powerup.mensajeria.application.mapper;

import com.pragma.powerup.mensajeria.application.dto.request.SendSmsRequest;
import com.pragma.powerup.mensajeria.application.dto.response.SendSmsResponse;
import com.pragma.powerup.mensajeria.domain.model.SmsMessageModel;
import com.pragma.powerup.mensajeria.domain.model.SmsSendResultModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ISmsDtoMapperTest {

    private final ISmsDtoMapper mapper = new ISmsDtoMapperImpl();

    @Test
    void shouldMapSendSmsRequestToModel() {
        SendSmsRequest request = new SendSmsRequest("+573005698325", "Tu pedido esta listo");

        SmsMessageModel model = mapper.toModel(request);

        assertThat(model.getPhoneNumber()).isEqualTo("+573005698325");
        assertThat(model.getMessage()).isEqualTo("Tu pedido esta listo");
    }

    @Test
    void shouldMapResultModelToResponse() {
        SmsSendResultModel model = SmsSendResultModel.builder()
                .sent(false)
                .mockProvider(false)
                .retryable(true)
                .provider("twilio")
                .messageId(null)
                .errorCode("30008")
                .errorMessage("No fue posible enviar el SMS")
                .build();

        SendSmsResponse response = mapper.toResponseDto(model);

        assertThat(response.sent()).isFalse();
        assertThat(response.mockProvider()).isFalse();
        assertThat(response.retryable()).isTrue();
        assertThat(response.provider()).isEqualTo("twilio");
        assertThat(response.errorCode()).isEqualTo("30008");
        assertThat(response.errorMessage()).isEqualTo("No fue posible enviar el SMS");
    }
}

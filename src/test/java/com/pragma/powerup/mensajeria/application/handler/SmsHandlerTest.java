package com.pragma.powerup.mensajeria.application.handler;

import com.pragma.powerup.mensajeria.application.dto.request.SendSmsRequest;
import com.pragma.powerup.mensajeria.application.dto.response.SendSmsResponse;
import com.pragma.powerup.mensajeria.application.handler.impl.SmsHandler;
import com.pragma.powerup.mensajeria.application.mapper.ISmsDtoMapper;
import com.pragma.powerup.mensajeria.domain.api.SmsUseCasePort;
import com.pragma.powerup.mensajeria.domain.model.SmsMessageModel;
import com.pragma.powerup.mensajeria.domain.model.SmsSendResultModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SmsHandlerTest {

    @Mock
    private SmsUseCasePort smsUseCasePort;

    @Mock
    private ISmsDtoMapper smsDtoMapper;

    private SmsHandler smsHandler;

    @BeforeEach
    void setUp() {
        smsHandler = new SmsHandler(smsUseCasePort, smsDtoMapper);
    }

    @Test
    void sendSms_mappeaRequestConverteDelegarYRetorna() {
        SendSmsRequest request = new SendSmsRequest("+573005698325", "Tu pedido esta listo. PIN: 123456");

        SmsMessageModel model = SmsMessageModel.builder()
                .phoneNumber("+573005698325")
                .message("Tu pedido esta listo. PIN: 123456")
                .build();
        SmsSendResultModel resultModel = SmsSendResultModel.builder()
                .sent(true)
                .mockProvider(true)
                .retryable(false)
                .provider("mock")
                .messageId("uuid-test-abc")
                .build();
        SendSmsResponse expectedResponse = new SendSmsResponse(
                true,
                true,
                false,
                "mock",
                "uuid-test-abc",
                null,
                null
        );

        when(smsDtoMapper.toModel(request)).thenReturn(model);
        when(smsUseCasePort.sendSms(model)).thenReturn(resultModel);
        when(smsDtoMapper.toResponseDto(resultModel)).thenReturn(expectedResponse);

        SendSmsResponse response = smsHandler.sendSms(request);

        verify(smsDtoMapper).toModel(request);
        verify(smsUseCasePort).sendSms(model);
        verify(smsDtoMapper).toResponseDto(resultModel);
        assertThat(response.sent()).isTrue();
        assertThat(response.mockProvider()).isTrue();
        assertThat(response.provider()).isEqualTo("mock");
        assertThat(response.messageId()).isEqualTo("uuid-test-abc");
    }

    @Test
    void sendSms_cuandoProveedorFalla_retornaResultadoConError() {
        SendSmsRequest request = new SendSmsRequest("+573001234567", "Confirmacion de pedido");

        SmsMessageModel model = SmsMessageModel.builder()
                .phoneNumber("+573001234567")
                .message("Confirmacion de pedido")
                .build();
        SmsSendResultModel resultModel = SmsSendResultModel.builder()
                .sent(false)
                .mockProvider(false)
                .retryable(false)
                .provider("twilio")
                .errorCode("21211")
                .errorMessage("No fue posible enviar el SMS")
                .build();
        SendSmsResponse expectedResponse = new SendSmsResponse(
                false,
                false,
                false,
                "twilio",
                null,
                "21211",
                "No fue posible enviar el SMS"
        );

        when(smsDtoMapper.toModel(request)).thenReturn(model);
        when(smsUseCasePort.sendSms(model)).thenReturn(resultModel);
        when(smsDtoMapper.toResponseDto(resultModel)).thenReturn(expectedResponse);

        SendSmsResponse response = smsHandler.sendSms(request);

        assertThat(response.sent()).isFalse();
        assertThat(response.errorCode()).isEqualTo("21211");
    }
}

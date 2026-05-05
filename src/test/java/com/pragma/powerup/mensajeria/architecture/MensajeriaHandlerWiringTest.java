package com.pragma.powerup.mensajeria.architecture;

import com.pragma.powerup.mensajeria.application.dto.request.SendSmsRequest;
import com.pragma.powerup.mensajeria.application.dto.response.SendSmsResponse;
import com.pragma.powerup.mensajeria.application.handler.impl.SmsHandler;
import com.pragma.powerup.mensajeria.application.mapper.ISmsDtoMapper;
import com.pragma.powerup.mensajeria.domain.api.SmsUseCasePort;
import com.pragma.powerup.mensajeria.domain.model.SmsMessageModel;
import com.pragma.powerup.mensajeria.domain.model.SmsSendResultModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MensajeriaHandlerWiringTest {

    @Mock
    private SmsUseCasePort smsUseCasePort;

    @Mock
    private ISmsDtoMapper smsDtoMapper;

    @InjectMocks
    private SmsHandler smsHandler;

    @Test
    void shouldMapDelegateAndReturnResponse() {
        SendSmsRequest request = new SendSmsRequest("+573005698325", "Tu pedido esta listo");
        SmsMessageModel model = SmsMessageModel.builder()
                .phoneNumber("+573005698325")
                .message("Tu pedido esta listo")
                .build();
        SmsSendResultModel resultModel = SmsSendResultModel.builder()
                .sent(true)
                .mockProvider(true)
                .retryable(false)
                .provider("mock")
                .messageId("msg-id")
                .build();
        SendSmsResponse response = new SendSmsResponse(true, true, false, "mock", "msg-id", null, null);

        when(smsDtoMapper.toModel(request)).thenReturn(model);
        when(smsUseCasePort.sendSms(model)).thenReturn(resultModel);
        when(smsDtoMapper.toResponseDto(resultModel)).thenReturn(response);

        SendSmsResponse actual = smsHandler.sendSms(request);

        verify(smsDtoMapper).toModel(request);
        verify(smsUseCasePort).sendSms(model);
        verify(smsDtoMapper).toResponseDto(resultModel);
        assertThat(actual).isEqualTo(response);
    }
}

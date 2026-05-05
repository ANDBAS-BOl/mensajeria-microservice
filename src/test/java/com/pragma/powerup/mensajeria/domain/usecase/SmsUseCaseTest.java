package com.pragma.powerup.mensajeria.domain.usecase;

import com.pragma.powerup.mensajeria.domain.exception.DomainException;
import com.pragma.powerup.mensajeria.domain.model.SmsMessageModel;
import com.pragma.powerup.mensajeria.domain.model.SmsSendResultModel;
import com.pragma.powerup.mensajeria.domain.spi.SmsSenderPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SmsUseCaseTest {

    @Mock
    private SmsSenderPort smsSenderPort;

    private SmsUseCase smsUseCase;

    @BeforeEach
    void setUp() {
        smsUseCase = new SmsUseCase(smsSenderPort);
    }

    @Test
    void sendSms_datosValidos_delegaAlSmsSenderPort() {
        SmsMessageModel model = SmsMessageModel.builder()
                .phoneNumber("+573005698325")
                .message("Tu pedido esta listo. PIN: 123456")
                .build();
        SmsSendResultModel expected = SmsSendResultModel.builder()
                .sent(true)
                .mockProvider(true)
                .retryable(false)
                .provider("mock")
                .messageId("uuid-test-123")
                .build();
        when(smsSenderPort.send(model)).thenReturn(expected);

        SmsSendResultModel result = smsUseCase.sendSms(model);

        verify(smsSenderPort).send(model);
        assertThat(result.isSent()).isTrue();
        assertThat(result.isMockProvider()).isTrue();
        assertThat(result.getProvider()).isEqualTo("mock");
        assertThat(result.getMessageId()).isEqualTo("uuid-test-123");
    }

    @Test
    void sendSms_datosValidos_retornaResultadoDelAdapter() {
        SmsMessageModel model = SmsMessageModel.builder()
                .phoneNumber("+573001234567")
                .message("Mensaje de confirmacion")
                .build();
        SmsSendResultModel expected = SmsSendResultModel.builder()
                .sent(false)
                .mockProvider(false)
                .retryable(true)
                .provider("twilio")
                .errorCode("30008")
                .errorMessage("No fue posible enviar el SMS")
                .build();
        when(smsSenderPort.send(model)).thenReturn(expected);

        SmsSendResultModel result = smsUseCase.sendSms(model);

        assertThat(result.isSent()).isFalse();
        assertThat(result.isRetryable()).isTrue();
        assertThat(result.getErrorCode()).isEqualTo("30008");
    }

    @Test
    void sendSms_cuandoProveedorFalla_propagatesDomainException() {
        SmsMessageModel model = SmsMessageModel.builder()
                .phoneNumber("+573001234567")
                .message("Mensaje de confirmacion")
                .build();
        DomainException exception = new DomainException("fallo tecnico del proveedor");
        when(smsSenderPort.send(model)).thenThrow(exception);

        assertThatThrownBy(() -> smsUseCase.sendSms(model))
                .isInstanceOf(DomainException.class)
                .hasMessage("fallo tecnico del proveedor");
    }
}

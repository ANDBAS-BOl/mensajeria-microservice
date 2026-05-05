package com.pragma.powerup.mensajeria.architecture;

import com.pragma.powerup.mensajeria.application.handler.ISmsHandler;
import com.pragma.powerup.mensajeria.domain.api.SmsUseCasePort;
import com.pragma.powerup.mensajeria.domain.spi.SmsSenderPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MensajeriaHexagonalScaffoldingTest {

    @Autowired
    private SmsUseCasePort smsUseCasePort;

    @Autowired
    private ISmsHandler smsHandler;

    @Autowired
    private SmsSenderPort smsSenderPort;

    @Test
    void shouldLoadAllHexagonalBeans() {
        assertThat(smsUseCasePort).isNotNull();
        assertThat(smsHandler).isNotNull();
        assertThat(smsSenderPort).isNotNull();
    }
}

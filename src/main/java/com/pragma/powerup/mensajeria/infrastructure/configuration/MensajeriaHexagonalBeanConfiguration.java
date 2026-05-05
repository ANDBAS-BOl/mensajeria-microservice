package com.pragma.powerup.mensajeria.infrastructure.configuration;

import com.pragma.powerup.mensajeria.domain.api.SmsUseCasePort;
import com.pragma.powerup.mensajeria.domain.spi.SmsSenderPort;
import com.pragma.powerup.mensajeria.domain.usecase.SmsUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MensajeriaHexagonalBeanConfiguration {

    @Bean
    public SmsUseCasePort smsServicePort(SmsSenderPort smsSenderPort) {
        return new SmsUseCase(smsSenderPort);
    }
}

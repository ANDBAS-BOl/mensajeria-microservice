package com.pragma.powerup.mensajeria.infrastructure.configuration;

import com.pragma.powerup.mensajeria.domain.spi.SmsSenderPort;
import com.pragma.powerup.mensajeria.infrastructure.out.sms.MockSmsSenderAdapter;
import com.pragma.powerup.mensajeria.infrastructure.out.sms.TwilioProperties;
import com.pragma.powerup.mensajeria.infrastructure.out.sms.TwilioSmsSenderAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmsProviderConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "twilio", name = "mock-enabled", havingValue = "true", matchIfMissing = true)
    public SmsSenderPort mockSmsSenderPort() {
        return new MockSmsSenderAdapter();
    }

    @Bean
    @ConditionalOnProperty(prefix = "twilio", name = "mock-enabled", havingValue = "false")
    public SmsSenderPort twilioSmsSenderPort(TwilioProperties twilioProperties) {
        return new TwilioSmsSenderAdapter(twilioProperties);
    }
}

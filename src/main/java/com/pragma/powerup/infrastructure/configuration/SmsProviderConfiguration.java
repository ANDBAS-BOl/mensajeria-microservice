package com.pragma.powerup.infrastructure.configuration;

import com.pragma.powerup.domain.spi.ISmsSenderPort;
import com.pragma.powerup.infrastructure.sms.MockSmsSenderAdapter;
import com.pragma.powerup.infrastructure.sms.TwilioProperties;
import com.pragma.powerup.infrastructure.sms.TwilioSmsSenderAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmsProviderConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "twilio", name = "mock-enabled", havingValue = "true", matchIfMissing = true)
    public ISmsSenderPort mockSmsSenderPort() {
        return new MockSmsSenderAdapter();
    }

    @Bean
    @ConditionalOnProperty(prefix = "twilio", name = "mock-enabled", havingValue = "false")
    public ISmsSenderPort twilioSmsSenderPort(TwilioProperties twilioProperties) {
        return new TwilioSmsSenderAdapter(twilioProperties);
    }
}

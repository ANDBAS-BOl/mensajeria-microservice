package com.pragma.powerup.infrastructure.sms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "twilio")
public class TwilioProperties {
    private String accountSid;
    private String authToken;
    private String fromNumber;
    private boolean mockEnabled = true;
    private int retryMaxAttempts = 3;
    private long retryDelayMs = 300L;
}

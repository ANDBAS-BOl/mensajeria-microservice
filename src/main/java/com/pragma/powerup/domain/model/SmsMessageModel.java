package com.pragma.powerup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SmsMessageModel {
    private final String phoneNumber;
    private final String message;
}

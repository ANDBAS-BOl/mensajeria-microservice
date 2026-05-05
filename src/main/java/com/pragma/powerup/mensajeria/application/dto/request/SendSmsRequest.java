package com.pragma.powerup.mensajeria.application.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public record SendSmsRequest(
        @NotBlank(message = "El numero de telefono es obligatorio")
        @Pattern(regexp = "^\\+?[0-9]{7,13}$", message = "Numero de telefono invalido")
        String phoneNumber,
        @NotBlank(message = "El mensaje es obligatorio")
        @Size(max = 320, message = "El mensaje supera el maximo permitido")
        String message
) {
}

package com.pragma.powerup.application.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
public class SendSmsRequestDto {

    @NotBlank(message = "El numero de telefono es obligatorio")
    @Pattern(regexp = "^\\+?[0-9]{7,13}$", message = "Numero de telefono invalido")
    private String phoneNumber;

    @NotBlank(message = "El mensaje es obligatorio")
    @Size(max = 320, message = "El mensaje supera el maximo permitido")
    private String message;
}

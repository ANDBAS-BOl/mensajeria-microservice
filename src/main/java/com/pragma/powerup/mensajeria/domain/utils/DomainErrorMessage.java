package com.pragma.powerup.mensajeria.domain.utils;

public enum DomainErrorMessage {

    PHONE_REQUIRED("El numero de telefono es obligatorio"),
    MESSAGE_REQUIRED("El mensaje es obligatorio"),
    PHONE_INVALID("Numero de telefono invalido"),
    MESSAGE_TOO_LONG("El mensaje supera el maximo permitido"),
    SMS_SEND_FAILED("No fue posible enviar el SMS"),
    SMS_PROVIDER_TECHNICAL_ERROR("Error tecnico del proveedor de SMS");

    private final String message;

    DomainErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

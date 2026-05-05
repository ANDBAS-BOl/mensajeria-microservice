# Mensajeria Microservice

Microservicio responsable del envio de SMS para la HU 14 (pedido en estado `LISTO`).

## Arquitectura

El microservicio aplica arquitectura hexagonal (ports and adapters), manteniendo el dominio aislado de Spring y de proveedores externos.

```text
infrastructure/input/rest (controller)
            |
            v
application/handler -> application/mapper
            |
            v
domain/usecase -> domain/spi (SmsSenderPort)
            |
            v
infrastructure/out/sms (MockSmsSenderAdapter | TwilioSmsSenderAdapter)
```

Reglas vivas de arquitectura:

- `src/test/java/com/pragma/powerup/mensajeria/architecture/ArchitectureRulesTest.java`
- `src/test/java/com/pragma/powerup/mensajeria/architecture/MensajeriaHexagonalScaffoldingTest.java`
- `src/test/java/com/pragma/powerup/mensajeria/architecture/MensajeriaHandlerWiringTest.java`
- `src/test/java/com/pragma/powerup/mensajeria/architecture/ControllerContractBaselineTest.java`

## Endpoint principal

`POST /api/v1/mensajeria/sms`

Request:

```json
{
  "phoneNumber": "+573005698325",
  "message": "Tu pedido esta listo. PIN: 123456"
}
```

Response OK:

```json
{
  "sent": true,
  "mockProvider": true,
  "retryable": false,
  "provider": "mock",
  "messageId": "uuid"
}
```

Response error proveedor:

```json
{
  "sent": false,
  "mockProvider": false,
  "retryable": true,
  "provider": "twilio",
  "errorCode": "TECHNICAL_ERROR",
  "errorMessage": "No fue posible enviar el SMS"
}
```

## Seguridad

- JWT obligatorio en endpoints funcionales.
- `POST /api/v1/mensajeria/sms`: solo rol `EMPLEADO`.
- Validacion autonoma de firma JWT con la misma `JWT_SECRET` compartida con Usuarios.

## Mensajes de error de dominio

Los mensajes de negocio estan centralizados en `domain/utils/DomainErrorMessage`:

- `PHONE_REQUIRED`
- `MESSAGE_REQUIRED`
- `PHONE_INVALID`
- `MESSAGE_TOO_LONG`
- `SMS_SEND_FAILED`
- `SMS_PROVIDER_TECHNICAL_ERROR`

Jerarquia activa:

- `DomainException` (base)
- `BusinessRuleException` (reglas de negocio, HTTP 400)
- `InternalProcessException` (errores tecnicos, HTTP 500)

## Politica de reintentos y compensacion (HU 14)

- El adaptador Twilio ejecuta reintentos configurables ante fallos transitorios.
- Configuracion: `TWILIO_RETRY_MAX_ATTEMPTS` y `TWILIO_RETRY_DELAY_MS`.
- El contrato de respuesta (`sent`, `retryable`, `errorCode`) permite a Plazoleta decidir compensacion.
- Decision alineada con el plan global: si Mensajeria falla en el envio del SMS, Plazoleta no debe confirmar el pedido en `LISTO`.

## Ejecucion local

Repositorio de infraestructura: [plazoleta-deployment](https://github.com/ANDBAS-BOl/plazoleta-deployment)

Para ejecutar el sistema completo primero levanta MySQL + MongoDB desde el repo de infraestructura.

Desde `mensajeria-microservice`:

```bash
./gradlew bootRun
```

Puerto por defecto: `8084` (definido en `application.yml`).

Para usar mock (dev/CI):

- `TWILIO_MOCK_ENABLED=true` (valor por defecto)

Para usar Twilio real:

- `TWILIO_MOCK_ENABLED=false`
- `TWILIO_ACCOUNT_SID`
- `TWILIO_AUTH_TOKEN`
- `TWILIO_FROM_NUMBER`

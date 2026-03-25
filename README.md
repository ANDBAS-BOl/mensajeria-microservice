# Mensajeria Microservice

Microservicio responsable del envio de SMS para la HU 14 (pedido LISTO + PIN).

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

- JWT obligatorio en todos los endpoints funcionales.
- Envío de SMS (`POST /api/v1/mensajeria/sms`): solo rol `EMPLEADO` (invocado desde Plazoleta al marcar pedido `LISTO`).
- Validación autónoma de firma JWT con la misma `JWT_SECRET` compartida que Usuarios.

## Politica de reintentos y compensacion

- El adaptador Twilio hace reintentos configurables para fallos transitorios.
- Configuracion: `TWILIO_RETRY_MAX_ATTEMPTS` y `TWILIO_RETRY_DELAY_MS`.
- El endpoint siempre retorna contrato explicito (`sent`, `retryable`, `errorCode`) para que Plazoleta decida si confirma `LISTO` o aplica compensacion.

## Cómo ejecutar localmente
Repositorio de infraestructura: [plazoleta-deployment](https://github.com/ANDBAS-BOl/plazoleta-deployment)

Para ejecutar el sistema completo primero necesitas levantar MySQL + MongoDB desde el repo de infraestructura.

Desde `mensajeria-microservice`:

```bash
./gradlew bootRun
```

Puerto por defecto: `8084` (definido en `application.yml`).

Para usar mock (dev/CI):

- `TWILIO_MOCK_ENABLED=true` (valor por defecto).

Para usar Twilio real:

- `TWILIO_MOCK_ENABLED=false`
- `TWILIO_ACCOUNT_SID`, `TWILIO_AUTH_TOKEN`, `TWILIO_FROM_NUMBER`

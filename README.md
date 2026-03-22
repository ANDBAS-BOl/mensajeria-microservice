# Mensajería Microservice

Este microservicio se encarga exclusivamente de las notificaciones vía SMS en el Sistema Plaza de Comidas.

## Rol en el Sistema
* **Notificaciones SMS:** Envía automáticamente un SMS al cliente con el PIN de seguridad de 6 dígitos cuando su pedido pasa al estado "LISTO".
* **Autenticación:** Valida de forma autónoma la firma del token JWT utilizando la clave secreta compartida con *Usuarios Microservice*.
* **Base de Datos:** ¡No requiere base de datos! Opera puramente vía HTTP + SDK de Twilio.

## Requisitos Previos
* JDK 17 o superior (Recomendado JDK 21 compilando a Target 17).
* Gradle 8.5.
* Cuenta y credenciales de Twilio configuradas en las propiedades de la aplicación.

## Configuración y Ejecución
1. Iniciar el microservicio:
   Desde la carpeta `mensajeria-microservice`, ejecute:
   ```bash
   ./gradlew bootRun
   ```

El servicio se iniciará por defecto en el puerto `8084`.

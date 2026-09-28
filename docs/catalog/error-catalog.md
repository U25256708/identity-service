# Catálogo de errores — Identity Service

Todos los endpoints incluyen el prefijo `/identity-service`.

## Errores de negocio

| Validación que lanza el error | Endpoint | HTTP | Nombre de la excepción | Descripción |
|---|---|---:|---|---|
| El correo ya está registrado. | `POST /identity-service/v1/users` | 409 | `USER_EMAIL_ALREADY_EXISTS` | `A user with email {email} already exists` |
| El nombre de usuario ya está registrado. | `POST /identity-service/v1/users` | 409 | `USER_USERNAME_ALREADY_EXISTS` | `A user with username {username} already exists` |
| El DNI ya está registrado. | `POST /identity-service/v1/users` | 409 | `USER_DNI_ALREADY_EXISTS` | `A user with DNI {dni} already exists` |
| El rol enviado no existe o está inactivo. | `POST /identity-service/v1/users` | 400 | `ROLE_NOT_FOUND` | `Role {role} was not found or is inactive` |
| Algún permiso enviado no existe o está inactivo. | `POST /identity-service/v1/users` | 400 | `PERMISSION_NOT_FOUND` | `Permission {permission} was not found or is inactive` |
| No existe un usuario con el UUID indicado. | `GET /identity-service/v1/users/{id}` | 404 | `USER_NOT_FOUND` | `User {id} was not found` |
| Algún valor del parámetro `filters` no corresponde a ningún campo buscable. | `GET /identity-service/v1/users` | 400 | `UNSUPPORTED_USER_SEARCH_FILTER` | `Unsupported user search filter: {filter}` |
| No existe el usuario a actualizar. | `PUT /identity-service/v1/users/{id}` | 404 | `USER_NOT_FOUND` | `User {id} was not found` |
| El usuario que se quiere actualizar no es de tipo `INTERNAL`. | `PUT /identity-service/v1/users/{id}` | 400 | `USER_NOT_INTERNAL` | `User {id} is not an internal user` |
| No existe el usuario cuyo acceso se quiere modificar. | `PATCH /identity-service/v1/users/{id}/access` | 404 | `USER_NOT_FOUND` | `User {id} was not found` |
| El usuario cuyo acceso se modifica no es de tipo `INTERNAL`. | `PATCH /identity-service/v1/users/{id}/access` | 400 | `USER_NOT_INTERNAL` | `User {id} is not an internal user` |
| El nuevo rol no existe o está inactivo. | `PATCH /identity-service/v1/users/{id}/access` | 400 | `ROLE_NOT_FOUND` | `Role {role} was not found or is inactive` |
| Algún permiso nuevo no existe o está inactivo. | `PATCH /identity-service/v1/users/{id}/access` | 400 | `PERMISSION_NOT_FOUND` | `Permission {permission} was not found or is inactive` |
| No existe el usuario que se quiere deshabilitar. | `DELETE /identity-service/v1/users/{id}/disable` | 404 | `USER_NOT_FOUND` | `User {id} was not found` |
| El usuario no existe, está inactivo o la contraseña es incorrecta. Aplica al inicio de sesión de tipo `USERNAME_PASSWORD`. | `POST /identity-service/v1/sign-in` | 401 | `INVALID_CREDENTIALS` | `Invalid username or password` |
| No existe un OTP pendiente, el código es incorrecto o se alcanzaron 3 intentos fallidos. | `POST /identity-service/v1/sign-in` con tipo `OTP` | 400 | `INVALID_OTP_CODE` | `Invalid OTP code` |
| El código OTP superó su fecha de vigencia. | `POST /identity-service/v1/sign-in` con tipo `OTP` | 400 | `EXPIRED_OTP_CODE` | `This OTP code has expired` |
| El OTP es válido, pero el usuario asociado está inactivo. | `POST /identity-service/v1/sign-in` con tipo `OTP` | 400 | `USER_IS_NOT_ACTIVE` | `The user with email {email} is not active` |
| Al registrar automáticamente al cliente no se encuentra activo el rol `BASIC`. | `POST /identity-service/v1/sign-in` con tipo `OTP` | 400 | `ROLE_NOT_FOUND` | `Role BASIC was not found or is inactive` |
| Al registrar automáticamente al cliente no se encuentra activo el permiso `BASIC_MANAGEMENT`. | `POST /identity-service/v1/sign-in` con tipo `OTP` | 400 | `PERMISSION_NOT_FOUND` | `Permission BASIC_MANAGEMENT was not found or is inactive` |

## Errores de entrada y seguridad

| Validación que lanza el error | Endpoint | HTTP | Nombre de la excepción | Descripción |
|---|---|---:|---|---|
| El JSON está mal formado, el cuerpo es incompatible o contiene un valor de enum desconocido. | Cualquier endpoint con cuerpo JSON | 400 | `INVALID_REQUEST` | `Invalid request body or enum value.` |
| Falta el token JWT en un endpoint protegido. | Todos los endpoints de `/identity-service/v1/users/**` | 401 | `UNAUTHORIZED` | `Authentication is required` |
| El JWT es inválido, expiró o no puede validarse. | Todos los endpoints de `/identity-service/v1/users/**` | 401 | `INVALID_TOKEN` | `Invalid or expired JWT` |
| El usuario autenticado no posee el rol o permiso requerido. | Todos los endpoints protegidos | 403 | `AccessDeniedException` | Respuesta predeterminada de Spring Security; no existe un manejador personalizado. |
| El parámetro `{id}` no tiene formato UUID. | Endpoints con `/users/{id}` | 400 | `MethodArgumentTypeMismatchException` | Respuesta predeterminada de Spring; no existe un manejador personalizado. |
| Uno o más campos anotados con Jakarta Validation no cumplen sus restricciones. | Endpoints que reciben un body con `@Valid` | 400 | `MethodArgumentNotValidException` | Respuesta predeterminada de Spring; no existe un manejador personalizado. |
| SendGrid rechaza el correo o falla la comunicación con el proveedor. | `POST /identity-service/v1/generate-code` | 500 | `IllegalStateException` | `SendGrid rejected the email with status {status}` o `Unable to send email through SendGrid` |

## Validaciones de los request

| Endpoint | Campo | Validación |
|---|---|---|
| `POST /identity-service/v1/users` | `username` | Obligatorio y no puede estar vacío. |
| `POST /identity-service/v1/users` | `password` | Obligatorio, no puede estar vacío y debe tener al menos 6 caracteres. |
| `POST /identity-service/v1/users` | `email` | Obligatorio, no puede estar vacío y debe tener formato de correo válido. |
| `POST /identity-service/v1/users` | `role` | Obligatorio y debe corresponder a un valor de `RoleName`. |
| `POST /identity-service/v1/users` | `firstName` | No puede ser `null`. |
| `POST /identity-service/v1/users` | `paternalLastName` | No puede ser `null`. |
| `POST /identity-service/v1/users` | `maternalLastName` | No puede ser `null`. |
| `POST /identity-service/v1/users` | `phoneNumber` | No puede ser `null`. |
| `POST /identity-service/v1/users` | `dni` | No puede ser `null`. |
| `PUT /identity-service/v1/users/me` | `firstName` | No puede ser `null`. |
| `PUT /identity-service/v1/users/me` | `paternalLastName` | No puede ser `null`. |
| `PUT /identity-service/v1/users/me` | `maternalLastName` | No puede ser `null`. |
| `PUT /identity-service/v1/users/me` | `phoneNumber` | No puede ser `null`. |
| `PUT /identity-service/v1/users/me` | `dni` | No puede ser `null`. |
| `PATCH /identity-service/v1/users/{id}/access` | `role` | No puede ser `null` y debe corresponder a un valor de `RoleName`. |
| `PATCH /identity-service/v1/users/{id}/access` | `permissions` | No puede ser `null`; cada elemento debe corresponder a un valor de `PermissionCode`. |
| `POST /identity-service/v1/generate-code` | `email` | Obligatorio, no puede estar vacío y debe tener formato de correo válido. |
| `POST /identity-service/v1/sign-in` con tipo `USERNAME_PASSWORD` | `username` | Obligatorio y no puede estar vacío. |
| `POST /identity-service/v1/sign-in` con tipo `USERNAME_PASSWORD` | `password` | Obligatorio y no puede estar vacío. |
| `POST /identity-service/v1/sign-in` con tipo `OTP` | `email` | Obligatorio, no puede estar vacío y debe tener formato de correo válido. |
| `POST /identity-service/v1/sign-in` con tipo `OTP` | `code` | Obligatorio y debe contener exactamente 6 dígitos numéricos. |

## Formato de los errores controlados

Los errores de negocio, `INVALID_REQUEST`, `UNAUTHORIZED` e `INVALID_TOKEN` utilizan esta estructura:

```json
{
  "exceptionName": "INVALID_OTP_CODE",
  "message": "Invalid OTP code",
  "timestamp": "2026-09-27 12:55",
  "path": "/identity-service/v1/sign-in"
}
```

Los errores administrados directamente por Spring, como `MethodArgumentNotValidException`, `MethodArgumentTypeMismatchException` y `AccessDeniedException`, pueden utilizar una estructura diferente porque todavía no cuentan con un manejador personalizado en `GlobalExceptionHandlerConfig`.

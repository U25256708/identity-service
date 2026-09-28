# Catálogo de errores — Identity Service

Todos los endpoints se documentan con el prefijo `/identity-service`.

Fuentes de verdad:

- Enums de error: `org.latinflavor.identity.domain.errors.UserErrors`, `OtpErrors` y `PaginationErrors`.
- Manejo global: `org.latinflavor.identity.config.GlobalExceptionHandlerConfig` y `ServerResponseAuthenticationEntryPoint`.
- Validaciones de entrada: los records en `org.latinflavor.identity.adapter.rest.request` y el record anidado `GenerateOtpController.GenerateOtpRequest`.

Las plantillas de mensaje usan `%s`, que `ApplicationException` reemplaza con `String.format`. En las tablas se muestra el marcador como `%s` y entre paréntesis el valor que lo sustituye.

---

## Catálogo de excepciones de negocio

### `UserErrors`

| Constante | HTTP | Plantilla de mensaje |
|---|---:|---|
| `USER_IS_NOT_ACTIVE` | 400 | `The user with email %s is not active` |
| `INVALID_CREDENTIALS` | 401 | `Invalid username or password` |
| `USER_NOT_FOUND` | 404 | `User %s was not found` |
| `USER_NOT_INTERNAL` | 400 | `User %s is not an internal user` |
| `USER_EMAIL_ALREADY_EXISTS` | 409 | `A user with email %s already exists` |
| `USER_USERNAME_ALREADY_EXISTS` | 409 | `A user with username %s already exists` |
| `USER_DNI_ALREADY_EXISTS` | 409 | `A user with DNI %s already exists` |
| `UNSUPPORTED_USER_SEARCH_FILTER` | 400 | `Unsupported user search filter: %s` |
| `ROLE_NOT_FOUND` | 400 | `Role %s was not found or is inactive` |
| `PERMISSION_NOT_FOUND` | 400 | `Permission %s was not found or is inactive` |

### `OtpErrors`

| Constante | HTTP | Plantilla de mensaje |
|---|---:|---|
| `INVALID_OTP_CODE` | 400 | `Invalid OTP code` |
| `EXPIRED_OTP_CODE` | 400 | `This OTP code has expired` |

### `PaginationErrors`

| Constante | HTTP | Plantilla de mensaje |
|---|---:|---|
| `INVALID_PAGE` | 400 | `Page index must be greater than or equal to zero` |
| `INVALID_PAGE_SIZE` | 400 | `Page size must be between 1 and 100` |
| `INVALID_SORT_FORMAT` | 400 | `Invalid sort format: %s. Expected 'field' or 'field,direction'` |
| `UNSUPPORTED_SORT_FIELD` | 400 | `Unsupported user sort field: %s` |

---

## Errores de negocio por endpoint

| Validación que lanza el error | Endpoint | HTTP | Excepción | Dónde se lanza |
|---|---|---:|---|---|
| El correo ya está registrado. | `POST /identity-service/v1/users` | 409 | `USER_EMAIL_ALREADY_EXISTS` | `CreateUserUseCaseImpl#checkEmail` |
| El nombre de usuario ya está registrado. | `POST /identity-service/v1/users` | 409 | `USER_USERNAME_ALREADY_EXISTS` | `CreateUserUseCaseImpl#ensureUniqueFields` |
| El DNI ya está registrado. Validación marcada para eliminar, ver observaciones. | `POST /identity-service/v1/users` | 409 | `USER_DNI_ALREADY_EXISTS` | `CreateUserUseCaseImpl#ensureUniqueFields` |
| El rol enviado no existe o está inactivo. | `POST /identity-service/v1/users` | 400 | `ROLE_NOT_FOUND` | `UserFactory#resolveRole` |
| Algún permiso enviado no existe o está inactivo. Solo se valida si `permissions` viene con valores. | `POST /identity-service/v1/users` | 400 | `PERMISSION_NOT_FOUND` | `UserFactory#resolvePermissions` |
| No existe un usuario con el UUID indicado. | `GET /identity-service/v1/users/{id}` | 404 | `USER_NOT_FOUND` | `GetUserUseCaseImpl` |
| Algún valor de `filters` no corresponde a un campo buscable. | `GET /identity-service/v1/users/search` | 400 | `UNSUPPORTED_USER_SEARCH_FILTER` | `UserSearchSpecificationFactory#resolveField` |
| `page` es negativo. | `GET /identity-service/v1/users/search` | 400 | `INVALID_PAGE` | `PaginationRequest` |
| `size` está fuera del rango 1 a 100. | `GET /identity-service/v1/users/search` | 400 | `INVALID_PAGE_SIZE` | `PaginationRequest` |
| Algún valor de `sort` no sigue el formato `campo` o `campo,dirección`. | `GET /identity-service/v1/users/search` | 400 | `INVALID_SORT_FORMAT` | `SortParser#toSortSpec` |
| Algún campo de `sort` no es ordenable. | `GET /identity-service/v1/users/search` | 400 | `UNSUPPORTED_SORT_FIELD` | `UserSearchSortFactory#resolveField` |
| No existe el usuario a actualizar. | `PUT /identity-service/v1/users/{id}` | 404 | `USER_NOT_FOUND` | `UpdateUserUseCaseImpl` |
| El usuario a actualizar no es de tipo `INTERNAL`. | `PUT /identity-service/v1/users/{id}` | 400 | `USER_NOT_INTERNAL` | `UpdateUserUseCaseImpl` |
| No existe el usuario cuyo acceso se quiere modificar. | `PATCH /identity-service/v1/users/{id}/access` | 404 | `USER_NOT_FOUND` | `UpdateInternalAccessUseCaseImpl` |
| El usuario cuyo acceso se modifica no es de tipo `INTERNAL`. | `PATCH /identity-service/v1/users/{id}/access` | 400 | `USER_NOT_INTERNAL` | `UpdateInternalAccessUseCaseImpl` |
| El nuevo rol no existe o está inactivo. | `PATCH /identity-service/v1/users/{id}/access` | 400 | `ROLE_NOT_FOUND` | `AuthorizationCatalog#requireActiveRole` |
| Algún permiso nuevo no existe o está inactivo. | `PATCH /identity-service/v1/users/{id}/access` | 400 | `PERMISSION_NOT_FOUND` | `AuthorizationCatalog#requireActivePermission` |
| No existe el usuario que se quiere deshabilitar. | `DELETE /identity-service/v1/users/{id}/disable` | 404 | `USER_NOT_FOUND` | `DeleteUserUseCaseImpl` |
| El usuario no existe, está inactivo, no tiene hash de contraseña o la contraseña no coincide. | `POST /identity-service/v1/sign-in` con tipo `USERNAME_PASSWORD` | 401 | `INVALID_CREDENTIALS` | `UsernamePasswordAuthenticationProvider#retrievalUser` |
| No hay un OTP pendiente, el código no coincide, o se alcanzaron 3 intentos fallidos. | `POST /identity-service/v1/sign-in` con tipo `OTP` | 400 | `INVALID_OTP_CODE` | `OneTimePasswordAuthenticationProvider#checkOtp` |
| El código OTP superó su fecha de vigencia. | `POST /identity-service/v1/sign-in` con tipo `OTP` | 400 | `EXPIRED_OTP_CODE` | `OneTimePasswordAuthenticationProvider#checkOtp` |
| El OTP es válido, pero el usuario asociado está inactivo. Aplica también a un usuario ya existente. | `POST /identity-service/v1/sign-in` con tipo `OTP` | 400 | `USER_IS_NOT_ACTIVE` | `OneTimePasswordAuthenticationProvider#retrieveUser` |
| Al registrar automáticamente al cliente no se encuentra activo el rol `BASIC`. | `POST /identity-service/v1/sign-in` con tipo `OTP` | 400 | `ROLE_NOT_FOUND` | `OneTimePasswordAuthenticationProvider#createUserIfNotExists` |
| Al registrar automáticamente al cliente no se encuentra activo el permiso `BASIC_MANAGEMENT`. | `POST /identity-service/v1/sign-in` con tipo `OTP` | 400 | `PERMISSION_NOT_FOUND` | `OneTimePasswordAuthenticationProvider#createUserIfNotExists` |

`GET /identity-service/v1/users` (endpoint deprecado) y `POST /identity-service/v1/generate-code` no lanzan errores de negocio; solo pueden producir errores de validación o de integración.

---

## Errores de entrada y seguridad

| Validación que lanza el error | Endpoint | HTTP | Excepción | Manejo |
|---|---|---:|---|---|
| El JSON está mal formado, falta el discriminador `type` de `sign-in`, o contiene un valor de enum desconocido como `role`, `permissions` o `type`. | Cualquier endpoint con cuerpo JSON | 400 | `INVALID_REQUEST` | `GlobalExceptionHandlerConfig#handleUnreadableRequest` |
| Falta el token JWT en un endpoint protegido. | Endpoints bajo `/identity-service/v1/users/**` | 401 | `UNAUTHORIZED` | `ServerResponseAuthenticationEntryPoint` |
| El JWT es inválido, expiró, no tiene la firma esperada o su `audience` no coincide. | Endpoints bajo `/identity-service/v1/users/**` | 401 | `INVALID_TOKEN` | `ServerResponseAuthenticationEntryPoint` |
| El usuario autenticado no tiene el rol o el permiso exigido por la regla. | Endpoints protegidos | 403 | `AccessDeniedException` | Sin manejador propio; Spring Security responde con el cuerpo vacío. |
| `page` o `size` no son numéricos. | `GET /identity-service/v1/users/search` | 400 | `MethodArgumentTypeMismatchException` | Sin manejador propio. |
| `{id}` no tiene formato UUID. | Endpoints con `/users/{id}` | 400 | `MethodArgumentTypeMismatchException` | Sin manejador propio. |
| Uno o más campos con Jakarta Validation no cumplen su restricción. | Endpoints que reciben un body con `@Valid` | 400 | `MethodArgumentNotValidException` | Sin manejador propio. |
| SendGrid responde con un estado fuera del rango 2xx. | `POST /identity-service/v1/generate-code` | 500 | `IllegalStateException` | Sin manejador propio. |
| Falla la comunicación con SendGrid. | `POST /identity-service/v1/generate-code` | 500 | `IllegalStateException` | Sin manejador propio. |

`UNAUTHORIZED` e `INVALID_TOKEN` se distinguen por el tipo de excepción que recibe el entry point: `BadCredentialsException` produce `INVALID_TOKEN` y cualquier otra produce `UNAUTHORIZED`.

---

## Validaciones de los request

### `POST /identity-service/v1/users`

| Campo | Tipo | Validación |
|---|---|---|
| `username` | `String` | `@NotBlank`. |
| `password` | `String` | `@NotBlank` y `@Size(min = 6)`. |
| `email` | `String` | `@NotBlank` y `@Email`. |
| `role` | `RoleName` | `@NotNull`. Un valor fuera del enum produce `INVALID_REQUEST`. |
| `firstName` | `String` | `@NotNull`. Admite cadena vacía. |
| `paternalLastName` | `String` | `@NotNull`. Admite cadena vacía. |
| `maternalLastName` | `String` | `@NotNull`. Admite cadena vacía. |
| `phoneNumber` | `String` | `@NotNull`. Admite cadena vacía. |
| `dni` | `String` | `@NotNull`. Admite cadena vacía. |
| `permissions` | `Set<PermissionCode>` | Opcional, sin anotaciones. Si se omite o llega vacío no se asignan permisos. Un valor fuera del enum produce `INVALID_REQUEST`. |

### `PUT /identity-service/v1/users/{id}`

| Campo | Tipo | Validación |
|---|---|---|
| `firstName` | `String` | `@NotNull`. Admite cadena vacía. |
| `paternalLastName` | `String` | `@NotNull`. Admite cadena vacía. |
| `maternalLastName` | `String` | `@NotNull`. Admite cadena vacía. |
| `phoneNumber` | `String` | `@NotNull`. Admite cadena vacía. |
| `dni` | `String` | `@NotNull`. Se valida pero no se persiste, ver observaciones. |
| `active` | `Boolean` | Opcional, sin anotaciones. `null` no cambia el estado; `true` habilita y `false` deshabilita. |

### `PATCH /identity-service/v1/users/{id}/access`

| Campo | Tipo | Validación |
|---|---|---|
| `role` | `RoleName` | `@NotNull`. Un valor fuera del enum produce `INVALID_REQUEST`. |
| `permissions` | `Set<PermissionCode>` | `@NotNull`; puede ser un conjunto vacío. Un valor fuera del enum produce `INVALID_REQUEST`. |

### `POST /identity-service/v1/sign-in`

El cuerpo es polimórfico y exige el discriminador `type`.

| `type` | Campo | Tipo | Validación |
|---|---|---|---|
| `USERNAME_PASSWORD` | `username` | `String` | `@NotNull` y `@NotEmpty`. |
| `USERNAME_PASSWORD` | `password` | `String` | `@NotNull` y `@NotEmpty`. |
| `OTP` | `email` | `String` | `@NotBlank` y `@Email`. |
| `OTP` | `code` | `String` | `@NotBlank` y `@Pattern(regexp = "\\d{6}")`, es decir exactamente seis dígitos. |

### `POST /identity-service/v1/generate-code`

| Campo | Tipo | Validación |
|---|---|---|
| `email` | `String` | `@NotBlank` y `@Email`. |

---

## Parámetros de consulta de `GET /identity-service/v1/users/search`

| Parámetro | Valor por defecto | Validación | Error |
|---|---|---|---|
| `q` | ausente | Si viene vacío o en blanco se ignoran `q` y `filters`. | — |
| `filters` | todos los campos buscables | Cada valor se normaliza quitando `_` y `-`, y comparando sin distinguir mayúsculas. | `UNSUPPORTED_USER_SEARCH_FILTER` |
| `page` | `0` | Entero mayor o igual que cero. | `INVALID_PAGE`, o `MethodArgumentTypeMismatchException` si no es numérico. |
| `size` | `20` | Entero entre 1 y 100. | `INVALID_PAGE_SIZE`, o `MethodArgumentTypeMismatchException` si no es numérico. |
| `sort` | `username,asc` | Repetible, con formato `campo` o `campo,dirección`, donde dirección es `asc` o `desc`. Los valores en blanco se descartan. | `INVALID_SORT_FORMAT`, `UNSUPPORTED_SORT_FIELD` |

Campos aceptados en `filters`: `username`, `firstName`, `paternalLastName`, `maternalLastName`, `email`, `phoneNumber`, `dni`, `employeeCode`.

Campos aceptados en `sort`: los mismos ocho, más `active`. A diferencia de `filters`, `sort` no normaliza `_` ni `-`, por lo que `first_name` produce `UNSUPPORTED_SORT_FIELD`.

`INVALID_SORT_FORMAT` se produce en tres casos: más de dos partes separadas por coma, campo vacío, o dirección distinta de `asc` o `desc`.

---

## Formato de las respuestas de error

### Errores con formato propio

Los errores de negocio, `INVALID_REQUEST`, `UNAUTHORIZED` e `INVALID_TOKEN` se serializan con el record `ServerResponse`, que se construye en `ServerResponse#error`:

```json
{
  "exceptionName": "INVALID_OTP_CODE",
  "message": "Invalid OTP code",
  "timestamp": "2026-09-27 12:55",
  "path": "/identity-service/v1/sign-in"
}
```

`userId` y `data` se omiten porque el record usa `@JsonInclude(NON_NULL)`. `path` contiene el `requestURI` completo, incluido el prefijo `/identity-service`.

### Errores sin formato propio

`GlobalExceptionHandlerConfig` solo declara manejadores para `ApplicationException` y `HttpMessageNotReadableException`. Las siguientes excepciones resuelven por la página de error por defecto de Spring Boot:

| Excepción | HTTP | Cuerpo de la respuesta |
|---|---:|---|
| `MethodArgumentNotValidException` | 400 | Estructura estándar de Spring Boot con `timestamp`, `status`, `error` y `path`, más los errores de campo. |
| `MethodArgumentTypeMismatchException` | 400 | Estructura estándar de Spring Boot. |
| `IllegalStateException` | 500 | Estructura estándar de Spring Boot. |
| `AccessDeniedException` | 403 | Cuerpo vacío, lo produce `AccessDeniedHandlerImpl` de Spring Security. |

`MessageConstants.DEFAULT_ERROR_MESSAGE` existe pero no se usa en ningún manejador.

---

## Observaciones

| # | Observación |
|---|---|
| 1 | `CreateUserUseCaseImpl#ensureUniqueFields` contiene el comentario `//TODO: Remover esto en documentacion` junto a la validación de DNI. `USER_DNI_ALREADY_EXISTS` figura en el catálogo por acuerdo explícito, pero la validación es candidata a eliminarse. |
| 2 | `UserUpdateEnricher#enrichWithOptionalField` no procesa `dni`. El campo es obligatorio en `UpdateUserRequest` y viaja en `UpdateUserCommand`, pero se descarta sin error, por lo que un `PUT` con un DNI nuevo responde `204` sin aplicarlo. |
| 3 | `PUT /identity-service/v1/users/{id}` no vuelve a validar la unicidad de correo, nombre de usuario ni DNI, así que un usuario nunca puede recibir un `409` en la actualización. |
| 4 | `UserUpdateEnricher` trata los campos como opcionales aunque `UpdateUserRequest` los marca con `@NotNull`; los `null` nunca llegan al enricher. |
| 5 | `PUT` y `PATCH` se ejecutan dentro de una transacción, por lo que un error de `AuthorizationCatalog` revierte los cambios previos de la petición. En cambio los errores de paginación y `UNSUPPORTED_USER_SEARCH_FILTER` se lanzan antes de abrir transacción. |
| 6 | `INVALID_CREDENTIALS` responde `401` con la estructura de `ServerResponse`, igual que `UNAUTHORIZED`. Un cliente no puede distinguirlos por el código HTTP, solo por `exceptionName`. |
| 7 | `RoleName` y `PermissionCode` se validan contra el catálogo en memoria que arma `AuthorizationCatalogInitializer` al iniciar la aplicación. Si el inicializador no corre, `ROLE_NOT_FOUND` y `PERMISSION_NOT_FOUND` se devuelven para cualquier valor, incluso válido. |

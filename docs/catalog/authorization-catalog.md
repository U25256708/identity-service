# Catálogo de autorización — Identity Service

Este documento lista, por endpoint, los roles y permisos exigidos. La fuente de verdad son las reglas de `web.security.authorization-rules` en `src/main/resources/application.yaml`, combinadas con los endpoints declarados en `org.latinflavor.identity.adapter.rest.controller`.

Todos los endpoints se documentan con el prefijo `/identity-service`.

---

## Cómo se evalúa el acceso

| Aspecto | Comportamiento |
|---|---|
| Origen de las reglas | `web.security.authorization-rules` en `application.yaml`, procesadas por `SecurityConfig#setupRequestAuthorization`. |
| Selección de la regla | Por combinación de método HTTP y patrón de ruta. Se aplica la **primera** regla que coincide; el orden del YAML importa. |
| Rutas públicas | `web.security.unauthenticated-paths` más `OPTIONS /**`. |
| Resto de las peticiones | `anyRequest().denyAll()`. |
| Combinación de rol y permiso | Un rol de la lista **y** un permiso de la lista. Dentro de cada lista la condición es **OR**: basta con uno de los roles y uno de los permisos. |
| Origen de las autoridades | Claims `roles` y `permissions` del JWT, sin prefijo, unidos en un mismo conjunto de `GrantedAuthority`. |
| Listas vacías | Si `roles` o `permissions` están vacías, esa dimensión no se exige. Ninguna regla del proyecto las deja vacías. |
| Token ausente o inválido | `401` con `UNAUTHORIZED` o `INVALID_TOKEN`. |
| Rol o permiso insuficiente | `403` por `AccessDeniedException`, sin manejador de excepciones propio. |

---

## Roles

Definidos en `org.latinflavor.identity.domain.model.RoleName`.

| Rol | Descripción | Creación |
|---|---|---|
| `BASIC` | Cliente de la plataforma. | Automática por `AuthorizationCatalogInitializer` al iniciar la aplicación. |
| `INTERNAL` | Usuario interno con permisos básicos de gestión. | Automática por `AuthorizationCatalogInitializer` al iniciar la aplicación. |
| `ADMIN` | Admin de la plataforma. | Automática por `AuthorizationCatalogInitializer` al iniciar la aplicación. |

Los roles se asignan a un usuario con `POST /identity-service/v1/users` o con `PATCH /identity-service/v1/users/{id}/access`. El cliente autogenerado por el flujo OTP recibe el rol `BASIC`.

---

## Permisos

Definidos en `org.latinflavor.identity.domain.model.PermissionCode`. Todos se crean y registran al iniciar la aplicación por `AuthorizationCatalogInitializer`.

| Permiso | ¿Se exige en alguna regla? | Endpoint que lo exige |
|---|---|---|
| `CREATE_USER` | Sí | `POST /identity-service/v1/users` |
| `READ_USER` | Sí | `GET /identity-service/v1/users`, `GET /identity-service/v1/users/{id}`, `GET /identity-service/v1/users/search` |
| `UPDATE_USER` | Sí | `PUT /identity-service/v1/users/{id}` |
| `DISABLE_USER` | Sí | `DELETE /identity-service/v1/users/{id}/disable` |
| `MANAGE_USER_ACCESS` | Sí | `PATCH /identity-service/v1/users/{id}/access` |
| `USER_MANAGEMENT` | Sí | Alternativa genérica en las cinco reglas de `/v1/users` |
| `ACCESS_MANAGEMENT` | Sí | Alternativa genérica en las cinco reglas de `/v1/users` |
| `UPDATE_OWN_USER` | No | Reservado para actualización del propio usuario. |
| `BASIC_MANAGEMENT` | No | Asignado al cliente creado por el flujo OTP. |
| `CUSTOMER_MANAGEMENT` | No | Reservado para otros microservicios. |
| `BOOKING_MANAGEMENT` | No | Reservado para otros microservicios. |
| `TABLE_MANAGEMENT` | No | Reservado para otros microservicios. |
| `SCHEDULE_MANAGEMENT` | No | Reservado para otros microservicios. |
| `CATALOG_MANAGEMENT` | No | Reservado para otros microservicios. |
| `REPORT_MANAGEMENT` | No | Reservado para otros microservicios. |

---

## Accesos por endpoint

| Método | Ruta | Autenticación | Roles admitidos | Permisos admitidos | Regla aplicada |
|---|---|---|---|---|---|
| `POST` | `/identity-service/v1/sign-in` | No | — | — | `unauthenticated-paths` |
| `POST` | `/identity-service/v1/generate-code` | No | — | — | `unauthenticated-paths` |
| `GET` | `/identity-service/v1/users` (deprecado) | Sí | `ADMIN` o `INTERNAL` | `READ_USER` o `USER_MANAGEMENT` o `ACCESS_MANAGEMENT` | `GET /identity-service/v1/users/**` |
| `GET` | `/identity-service/v1/users/{id}` | Sí | `ADMIN` o `INTERNAL` | `READ_USER` o `USER_MANAGEMENT` o `ACCESS_MANAGEMENT` | `GET /identity-service/v1/users/**` |
| `GET` | `/identity-service/v1/users/search` | Sí | `ADMIN` o `INTERNAL` | `READ_USER` o `USER_MANAGEMENT` o `ACCESS_MANAGEMENT` | `GET /identity-service/v1/users/**` |
| `POST` | `/identity-service/v1/users` | Sí | `ADMIN` | `CREATE_USER` o `USER_MANAGEMENT` o `ACCESS_MANAGEMENT` | `POST /identity-service/v1/users` |
| `PUT` | `/identity-service/v1/users/{id}` | Sí | `ADMIN` | `UPDATE_USER` o `USER_MANAGEMENT` o `ACCESS_MANAGEMENT` | `PUT /identity-service/v1/users/*` |
| `PATCH` | `/identity-service/v1/users/{id}/access` | Sí | `ADMIN` | `MANAGE_USER_ACCESS` o `USER_MANAGEMENT` o `ACCESS_MANAGEMENT` | `PATCH /identity-service/v1/users/*/access` |
| `DELETE` | `/identity-service/v1/users/{id}/disable` | Sí | `ADMIN` | `DISABLE_USER` o `USER_MANAGEMENT` o `ACCESS_MANAGEMENT` | `DELETE /identity-service/v1/users/*/disable` |
| `GET` | `/v3/api-docs/**`, `/swagger-ui/**` | No | — | — | `unauthenticated-paths` |
| `OPTIONS` | Cualquier ruta | No | — | — | `HttpMethod.OPTIONS, /**` |

La columna **Regla aplicada** indica el patrón del YAML que resuelve la petición, no la ruta literal del controller. El patrón `GET /identity-service/v1/users/**` también cubre `GET /identity-service/v1/users` porque `/**` admite cero segmentos.

---

## Matriz de acceso por rol

Resultado de cruzar cada rol con las reglas, suponiendo que el usuario no tiene permisos asignados.

| Endpoint | `BASIC` | `INTERNAL` | `ADMIN` |
|---|---|---|---|
| `POST /v1/sign-in` | Permitido | Permitido | Permitido |
| `POST /v1/generate-code` | Permitido | Permitido | Permitido |
| `GET /v1/users` | Denegado | Permitido con `READ_USER`, `USER_MANAGEMENT` o `ACCESS_MANAGEMENT` | Permitido con `READ_USER`, `USER_MANAGEMENT` o `ACCESS_MANAGEMENT` |
| `GET /v1/users/{id}` | Denegado | Permitido con `READ_USER`, `USER_MANAGEMENT` o `ACCESS_MANAGEMENT` | Permitido con `READ_USER`, `USER_MANAGEMENT` o `ACCESS_MANAGEMENT` |
| `GET /v1/users/search` | Denegado | Permitido con `READ_USER`, `USER_MANAGEMENT` o `ACCESS_MANAGEMENT` | Permitido con `READ_USER`, `USER_MANAGEMENT` o `ACCESS_MANAGEMENT` |
| `POST /v1/users` | Denegado | Denegado | Permitido con `CREATE_USER`, `USER_MANAGEMENT` o `ACCESS_MANAGEMENT` |
| `PUT /v1/users/{id}` | Denegado | Denegado | Permitido con `UPDATE_USER`, `USER_MANAGEMENT` o `ACCESS_MANAGEMENT` |
| `PATCH /v1/users/{id}/access` | Denegado | Denegado | Permitido con `MANAGE_USER_ACCESS`, `USER_MANAGEMENT` o `ACCESS_MANAGEMENT` |
| `DELETE /v1/users/{id}/disable` | Denegado | Denegado | Permitido con `DISABLE_USER`, `USER_MANAGEMENT` o `ACCESS_MANAGEMENT` |

Ningún rol tiene permisos asociados por defecto en la base de datos. Los permisos se asignan individualmente por usuario mediante `POST /v1/users` o `PATCH /v1/users/{id}/access`, por lo que esta matriz describe el máximo alcanzable según el rol, no el estado real de un usuario.

---

## Observaciones

| # | Observación |
|---|---|
| 1 | El prefijo `/identity-service` lo agrega `WebConfig#configurePathMatch` mediante `HandlerTypePredicate.forBasePackage("org.latinflavor.identity")` a todo controller del paquete raíz, y se declara en `APIConstants.API_PATH_PREFIX`. No se usa `server.servlet.context-path`. Los controllers declaran `@RequestMapping("/v1")` y el prefijo se antepone en tiempo de ejecución, por lo que las rutas de las reglas de seguridad y las de los controllers coinciden. |
| 2 | `SecurityConfig#authorityManager` combina rol y permiso con AND, pero dentro de cada lista usa `anyMatch`. Un usuario con rol `ADMIN` y permiso `BASIC_MANAGEMENT` no pasa ninguna regla de `/v1/users` salvo que además posea uno de los tres permisos `USER_MANAGEMENT`, `ACCESS_MANAGEMENT` o el permiso específico de la operación. |
| 3 | `USER_MANAGEMENT` y `ACCESS_MANAGEMENT` actúan como permisos comodín en las cinco reglas de escritura y lectura, por lo que un permiso específico como `READ_USER` no aporta restricción adicional frente a ellos. |
| 4 | No existe una tabla de relación entre roles y permisos, solo `user_roles` y `user_permissions`. Un permiso no se hereda de forma automática por tener un rol. |
| 5 | `error-catalog.md` documenta `PUT /identity-service/v1/users/me`, pero ese endpoint no existe en `UserManagementController` y ninguna regla de seguridad lo cubre. |
| 6 | Las reglas de lectura permiten el rol `INTERNAL`, pero `UserType` solo admite `INTERNAL` y `CUSTOMER`; el rol `BASIC` corresponde a clientes de tipo `CUSTOMER` y no puede leer usuarios. |

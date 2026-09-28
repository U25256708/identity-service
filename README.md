# LatinFlavor Identity Service

Microservicio de identidad del proyecto **LatinFlavor**. Centraliza el registro de usuarios, la autenticación por JWT y el control de acceso basado en roles y permisos. Es la fuente de verdad de las credenciales que el `customer-service` y el `booking-service` consumen.

---

## Contenido

- [Stack tecnológico](#stack-tecnológico)
- [Arquitectura](#arquitectura)
- [Requisitos previos](#requisitos-previos)
- [Configuración](#configuración)
- [Puesta en marcha](#puesta-en-marcha)
- [Endpoints](#endpoints)
- [Seguridad](#seguridad)
- [Base de datos y migraciones](#base-de-datos-y-migraciones)
- [Documentación](#documentación)
- [Comandos habituales](#comandos-habituales)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Estado del proyecto](#estado-del-proyecto)

---

## Stack tecnológico

| Capa | Tecnología | Versión |
|---|---|---|
| Lenguaje | Java | 21 |
| Framework | Spring Boot | 3.5.7 |
| Web | Spring Web MVC | 3.5.7 |
| Persistencia | Spring Data JPA / Hibernate | 3.5.7 |
| Seguridad | Spring Security + JJWT (RS256) | 3.5.7 / 0.12.6 |
| Base de datos | PostgreSQL | 42.7.7 (driver) |
| Migraciones | Liquibase | 4.33.0 |
| Validación | Jakarta Validation (Bean Validation) | 3.5.7 |
| Mapeo de objetos | MapStruct + Lombok | 1.6.3 / 1.18.38 |
| Correo | SendGrid Java SDK | 4.10.3 |
| Build | Maven Wrapper | 3.9+ |

---

## Arquitectura

El proyecto aplica arquitectura hexagonal con **paquetes por capa técnica**:

```
org.latinflavor.identity
├── adapter          # Mundo externo: implementaciones y límites técnicos
│   ├── rest         #   Controllers, requests, responses y mappers MapStruct
│   ├── persistence   #   Repositories de Spring Data (JPA)
│   └── external      #   JWT, SendGrid y cliente de customer-service
├── application      # Casos de uso
│   ├── port
│   │   ├── in       #   Puertos de entrada (contratos de los casos de uso)
│   │   └── out      #   Puertos de salida (contratos con la persistencia y externos)
│   ├── command      #   Commands y criteria de entrada
│   ├── factory      #   Factorías de dominio (specifications, ordenamiento, enriquecimiento)
│   └── service      #   Implementaciones de los casos de uso y de autenticación
├── domain           # Núcleo: modelo, enums de error
├── config           # Configuración de Spring y propiedades tipadas
└── shared           # Utilidades transversales: paginación, errores, respuestas
```

Reglas de dependencia:

- `adapter` depende de `application.port`, nunca al revés.
- `domain` no depende de nada externo, ni siquiera de Spring.
- Los casos de uso solo conocen los puertos; la implementación concreta se inyecta en tiempo de ejecución.
- La regla de autorización vive en `application.yaml` y la aplica `SecurityConfig`, de modo que cambiar permisos no obliga a recompilar.

---

## Requisitos previos

| Herramienta | Versión mínima | Nota |
|---|---|---|
| JDK | 21 | `java -version` |
| Maven | 3.9 | Se provee con el wrapper, no hace falta instalarlo |
| PostgreSQL | 14 | Solo debe existir la base de datos vacía |
| OpenSSL | 3.x | Necesario para generar el par de claves RSA |

---

## Configuración

Todas las variables se declaran en `src/main/resources/application.yaml` y leen su valor de una variable de entorno con un valor por defecto entre paréntesis.

### Base de datos

| Variable | Por defecto | Descripción |
|---|---|---|
| `DB_HOST` | `localhost` | Host de PostgreSQL. |
| `DB_PORT` | `5432` | Puerto de PostgreSQL. |
| `DB_NAME` | `latinflavordb` | Nombre de la base de datos. |
| `DB_USER` | `root` | Usuario de conexión. |
| `DB_PASSWORD` | `root` | Contraseña de conexión. |

### Servidor

| Variable | Por defecto | Descripción |
|---|---|---|
| `SERVER_PORT` | `8080` | Puerto HTTP del servicio. |
| `CUSTOMER_SERVICE_URL` | `http://localhost:8081` | Base URL del `customer-service`. |

### JWT

Las claves **no tienen valor por defecto**: si faltan, la aplicación no arranca.

| Variable | Descripción |
|---|---|
| `JWT_ISSUER` | Emisor exigido al validar el token. |
| `JWT_AUDIENCE` | Audiencia exigida al validar el token. |
| `JWT_KEY_ID` | Identificador de la clave en la cabecera del token. |
| `JWT_ACCESS_TOKEN_TTL` | Vigencia del token de acceso, por ejemplo `15m`. |
| `JWT_PRIVATE_KEY_BASE64` | Clave privada RSA en Base64, codificada en **PKCS#8**. |
| `JWT_PUBLIC_KEY_BASE64` | Clave pública RSA en Base64, codificada en **X.509 SubjectPublicKeyInfo**. |

Generación del par de claves:

```bash
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out private-pkcs8.pem
openssl rsa -in private-pkcs8.pem -pubout -out public.pem

export JWT_PRIVATE_KEY_BASE64=$(base64 -w0 < private-pkcs8.pem)
export JWT_PUBLIC_KEY_BASE64=$(base64 -w0 < public.pem)
```

En PowerShell:

```powershell
$env:JWT_PRIVATE_KEY_BASE64 = [Convert]::ToBase64String([IO.File]::ReadAllBytes("private-pkcs8.pem"))
$env:JWT_PUBLIC_KEY_BASE64 = [Convert]::ToBase64String([IO.File]::ReadAllBytes("public.pem"))
```

### SendGrid

| Variable | Por defecto | Descripción |
|---|---|---|
| `SENDGRID_ENABLED` | requerido | Si es `false`, el envío de correos se omite silenciosamente. |
| `SENDGRID_API_KEY` | requerido | Clave de la API de SendGrid. |
| `SENDGRID_FROM_EMAIL` | requerido | Remitente de los correos. |
| `SENDGRID_TEMPLATE_ID` | requerido | Plantilla dinámica usada para los códigos OTP. |

> **Nunca suba credenciales al repositorio.** El archivo `.env` está en `.gitignore`. Si una clave se expone en un commit, revócala y genera una nueva antes de seguir.

---

## Puesta en marcha

### 1. Crear la base de datos

```sql
CREATE DATABASE latinflavordb;
```

Las tablas las crea Liquibase en el primer arranque dentro del esquema `identity_schema`.

### 2. Exportar las variables de entorno

Linux o macOS:

```bash
export $(grep -v '^#' .env | xargs) DB_HOST=localhost DB_PASSWORD=root
```

Windows PowerShell:

```powershell
Get-Content .env | Where-Object { $_ -and -not $_.StartsWith('#') } | ForEach-Object {
    $name, $value = $_ -split '=', 2
    [Environment]::SetEnvironmentVariable($name, $value, 'User')
}
```

### 3. Compilar y arrancar

```bash
./mvnw clean compile
./mvnw spring-boot:run
```

En Windows:

```powershell
.\mvnw.cmd clean compile
.\mvnw.cmd spring-boot:run
```

Al iniciar, `AuthorizationCatalogInitializer` inserta y registra en memoria los tres roles (`BASIC`, `INTERNAL`, `ADMIN`) y los quince permisos definidos en los enums del dominio. No hace falta sembrar datos de forma manual.

---

## Endpoints

Todos los endpoints se exponen con el prefijo `/identity-service`.

### Públicos

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/identity-service/v1/sign-in` | Inicia sesión con `USERNAME_PASSWORD` u `OTP` y devuelve el JWT. |
| `POST` | `/identity-service/v1/generate-code` | Solicita el envío de un código OTP por correo. |

### Gestión de usuarios

| Método | Ruta | Rol | Permiso |
|---|---|---|---|
| `POST` | `/identity-service/v1/users` | `ADMIN` | `CREATE_USER` |
| `GET` | `/identity-service/v1/users/{id}` | `ADMIN` o `INTERNAL` | `READ_USER` |
| `GET` | `/identity-service/v1/users` | `ADMIN` o `INTERNAL` | `READ_USER` |
| `GET` | `/identity-service/v1/users/search` | `ADMIN` o `INTERNAL` | `READ_USER` |
| `PUT` | `/identity-service/v1/users/{id}` | `ADMIN` | `UPDATE_USER` |
| `PATCH` | `/identity-service/v1/users/{id}/access` | `ADMIN` | `MANAGE_USER_ACCESS` |
| `DELETE` | `/identity-service/v1/users/{id}/disable` | `ADMIN` | `DISABLE_USER` |

`GET /identity-service/v1/users` está deprecado: devuelve la lista completa sin paginar. Use `GET /identity-service/v1/users/search`.

### Búsqueda paginada

`GET /identity-service/v1/users/search` acepta:

| Parámetro | Tipo | Por defecto | Descripción |
|---|---|---|---|
| `q` | `String` | — | Texto libre que se busca en los campos filtrados. |
| `filters` | `List<String>` | todos | Campos donde se aplica `q`. Acepta `first_name`, `first-name` o `firstName`. |
| `page` | `int` | `0` | Índice de página, base cero. |
| `size` | `int` | `20` | Elementos por página, entre 1 y 100. |
| `sort` | `List<String>` | `username,asc` | Repetible, con formato `campo` o `campo,dirección`. |

Ejemplo:

```bash
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/identity-service/v1/users/search?q=admin&filters=username&filters=email&page=0&size=20&sort=firstName,desc"
```

La respuesta usa el envelope `PageResponse`:

```json
{
  "content": [],
  "page": 0,
  "size": 20,
  "totalElements": 0,
  "totalPages": 0
}
```

---

## Seguridad

- **Autenticación:** stateless con JWT firmado en RS256. `JwtAuthenticationFilter` valida el token y reconstruye las autoridades a partir de los claims `roles` y `permissions`.
- **Autorización:** declarativa en `application.yaml`. Cada regla combina un patrón de ruta, un método HTTP, una lista de roles y una lista de permisos.
- **Semántica:** se exige **un rol de la lista Y un permiso de la lista**. Dentro de cada lista la condición es **OR**, por lo que `USER_MANAGEMENT` y `ACCESS_MANAGEMENT` funcionan como permisos comodín.
- **Cierre por defecto:** `anyRequest().denyAll()`. Una ruta sin regla explícita queda bloqueada.
- **Hash de contraseñas:** BCrypt.
- **Revocación:** deshabilitar un usuario incrementa su `tokenVersion`, lo que invalida los JWT emitidos antes del cambio.
- **CORS:** configurable en `web.cors`, con por defecto los puertos `3000`, `4200` y `8080`.
- **Cookies y CSRF:** CSRF está deshabilitado porque la autenticación no usa cookies de sesión.

---

## Base de datos y migraciones

- Esquema: `identity_schema`.
- Validación del esquema: `spring.jpa.hibernate.ddl-auto=validate`, el modelo Java y la base de datos deben coincidir.
- Migraciones: Liquibase, en `src/main/resources/db/changelog`.

| ChangeSet | Contenido |
|---|---|
| `001-create-users` | Tabla `users`. |
| `002-create-roles` | Tabla `roles` con índice único sin distinción de mayúsculas sobre `name`. |
| `003-create-permissions` | Tabla `permissions` con check de formato `^[A-Z][A-Z0-9_]*$` sobre `code`. |
| `004-create-user-roles` | Tabla `user_roles` y sus claves foráneas. |
| `005-create-user-permissions` | Tabla `user_permissions` y sus claves foráneas. |
| `006-create-otp-credentials` | Tabla `otp_credentials`. |

No existe una tabla que relacione roles con permisos: los permisos se asignan a cada usuario de forma individual, no se heredan del rol.

---

## Documentación

| Documento | Ruta | Contenido |
|---|---|---|
| Catálogo de errores | `docs/catalog/error-catalog.md` | Excepciones, códigos HTTP, validaciones de entrada y formato de las respuestas. |
| Catálogo de autorización | `docs/catalog/authorization-catalog.md` | Roles, permisos y accesos por endpoint. |
| Colección de Postman | `docs/collection/identity-service.json` | Peticiones listas para importar. |
| Diagramas | `docs/diagrams/users` | CUS, BPM y SD en PlantUML. |

Los diagramas siguen una numeración por caso de uso y se renderizan con PlantUML:

```bash
plantuml -tpng docs/diagrams/users/**/*.puml
```

| Caso | Diagramas |
|---|---|
| 01 Crear usuario | SD |
| 02 Listar usuarios | SD |
| 03 Obtener usuario por ID | SD |
| 04 Actualizar usuario | CUS, BPM, SD |
| 05 Deshabilitar usuario | SD |
| 06 Actualizar acceso interno | SD |
| 07 Solicitar OTP de registro | SD |
| 08 Validar OTP y registrar cliente | SD |
| 09 Iniciar sesión con usuario y contraseña | CUS, BPM, SD |
| 10 Buscar usuarios internos | CUS, BPM, SD |

---

## Comandos habituales

| Objetivo | Comando |
|---|---|
| Compilar | `./mvnw clean compile` |
| Arrancar en desarrollo | `./mvnw spring-boot:run` |
| Ejecutar las pruebas | `./mvnw test` |
| Empaquetar el JAR | `./mvnw clean package` |
| Limpiar artefactos | `./mvnw clean` |

---

## Estructura del proyecto

```
identity-service/
├── src/main/java/org/latinflavor/identity/
│   ├── IdentityServiceApplication.java
│   ├── adapter/
│   ├── application/
│   ├── config/
│   ├── domain/
│   └── shared/
├── src/main/resources/
│   ├── application.yaml
│   └── db/changelog/
├── docs/
│   ├── catalog/
│   ├── collection/
│   └── diagrams/
├── .env                        # Variables locales, no versionado
├── .mvn/wrapper/               # Maven Wrapper
├── mvnw / mvnw.cmd
└── pom.xml
```

---

## Estado del proyecto

Funcionalidades terminadas:

- Registro, consulta, actualización y deshabilitación de usuarios internos.
- Búsqueda paginada con filtros, ordenamiento validado y envelope `PageResponse`.
- Autenticación por usuario y contraseña, y por OTP con registro automático del cliente.
- Envío de códigos OTP mediante SendGrid.
- Control de acceso por rol y permiso configurable desde `application.yaml`.
- Migraciones Liquibase y catálogo de errores documentado.

Pendiente:

- Suite de pruebas automatizadas: el proyecto declara las dependencias de test, pero aún no tiene casos.
- El prefijo `/identity-service` aparece en las reglas de seguridad y en la documentación, pero los controllers declaran `@RequestMapping("/v1")`. Falta definir si lo agrega un gateway o si debe configurarse con `server.servlet.context-path`.
- `springdoc-openapi` está permitido en las reglas de CORS y seguridad, pero no figura como dependencia, por lo que no hay interfaz de Swagger activa.
---

Proyecto académico del curso Integrador de Software I. Los microservicios de la suite LatinFlavor son `identity-service`, `customer-service` y `booking-service`.

# API de seguimiento de envíos locales

Backend REST para registrar envíos, consultar su avance y conservar el historial de cambios y auditoría de novedades.

## Requisitos

- Java 21
- PostgreSQL
- Maven Wrapper incluido (`mvnw.cmd`)

## Configuración y ejecución

La aplicación se conecta por defecto a `tracking_envios_db` en `localhost:5432` con el usuario `postgres`. Configura la contraseña mediante `DB_PASSWORD` antes de iniciar la aplicación.

En PowerShell:

```powershell
$env:DB_PASSWORD = "TU_CONTRASENA"
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

La API queda disponible en `http://localhost:8080`. Hibernate actualiza el esquema según `spring.jpa.hibernate.ddl-auto` de `application.properties`; para entornos con datos importantes, configura migraciones y copias de seguridad antes de modificar el esquema.

## Contratos principales

Las rutas no requieren autenticación en la configuración actual. Para solicitudes JSON usa `Content-Type: application/json`.

| Método | Ruta | Comportamiento |
|---|---|---|
| `GET` | `/api/estado` | Verifica disponibilidad |
| `GET` | `/api/usuarios` | Lista usuarios; acepta `?rol=OPERADOR` o `?rol=REPARTIDOR` |
| `GET` | `/api/envios` | Lista envíos; acepta `?estado=CREADO`, `EN_RUTA` o `ENTREGADO` |
| `POST` | `/api/envios` | Crea envío, paquete, destinatario e historial inicial |
| `GET` | `/api/envios/{id}` | Consulta un envío |
| `GET` | `/api/envios/seguimiento/{codigo}` | Consulta el envío por código |
| `GET` | `/api/envios/{id}/paquete` | Consulta el paquete |
| `PUT` | `/api/envios/{id}/paquete` | Reemplaza el paquete solo mientras el envío esté `CREADO` |
| `GET` | `/api/envios/{id}/destinatario` | Consulta el destinatario |
| `PATCH` | `/api/envios/{id}/estado` | Avanza el estado y registra el historial |
| `GET` | `/api/envios/{id}/historial` | Consulta el historial |
| `GET` | `/api/envios/{id}/novedades` | Consulta novedades |
| `POST` | `/api/envios/{id}/novedades` | Registra una novedad si el envío está `EN_RUTA` |
| `GET` | `/api/envios/{id}/auditoria-novedades` | Consulta auditoría de novedades eliminadas |
| `DELETE` | `/api/novedades/{id}` | Elimina una novedad antes de cerrar el envío, guardando auditoría |

### Crear un envío

`POST /api/envios`:

```json
{
  "idOperador": 1,
  "paquete": {
    "descripcion": "Paquete de prueba",
    "pesoKg": 2.5,
    "altoCm": 10,
    "anchoCm": 20,
    "largoCm": 30,
    "valorDeclarado": 50000,
    "fragil": false
  },
  "destinatario": {
    "nombre": "Persona de prueba",
    "telefono": "3001234567",
    "direccionCompleta": "Calle 10 # 20-30",
    "barrio": "Centro",
    "ciudad": "Bogotá"
  }
}
```

El usuario indicado debe existir, estar activo y tener rol `OPERADOR`. La respuesta exitosa es `201 Created`; guarda el `id` y `codigoSeguimiento`.

### Actualizar el paquete

`PUT /api/envios/{id}/paquete` reemplaza los datos completos del paquete. Solo está permitido antes del despacho:

```json
{
  "descripcion": "Paquete actualizado",
  "pesoKg": 3.2,
  "altoCm": 12,
  "anchoCm": 22,
  "largoCm": 32,
  "valorDeclarado": 65000,
  "fragil": true
}
```

### Avanzar el estado

`PATCH /api/envios/{id}/estado` acepta el siguiente estado válido y exige el usuario activo que registra el cambio. Las únicas transiciones son `CREADO -> EN_RUTA` y `EN_RUTA -> ENTREGADO`; cada cambio se guarda en el historial.

```json
{
  "nuevoEstado": "EN_RUTA",
  "idUsuario": 2,
  "observacion": "Paquete recogido para despacho",
  "idRepartidor": 2
}
```

`idRepartidor` y `observacion` son opcionales. Si se indica un repartidor, debe existir, estar activo y tener rol `REPARTIDOR`; un envío ya asignado no se puede reasignar.

### Registrar una novedad

`POST /api/envios/{id}/novedades` requiere que el envío esté `EN_RUTA` y que el usuario sea un repartidor activo (si el envío ya tiene repartidor asignado, debe ser el mismo):

```json
{
  "idUsuario": 2,
  "descripcion": "Demora por cierre vial",
  "fechaNovedad": "2026-11-12T15:30:00"
}
```

La respuesta exitosa es `201 Created`.

### Eliminar una novedad

`DELETE /api/novedades/{id}` no lleva body. Devuelve `204 No Content` si se elimina y registra auditoría. Devuelve `409 Conflict` si el envío ya está `ENTREGADO` o una restricción de integridad impide el borrado.

## Reglas del dominio

- El código de seguimiento es aleatorio y único.
- El estado de un envío solo avanza `CREADO -> EN_RUTA -> ENTREGADO`; no se permiten saltos, retrocesos ni repetir el mismo estado.
- Toda transición válida genera una entrada en el historial.
- El paquete solo puede reemplazarse antes del despacho.
- Las novedades solo pueden registrarse mientras el envío está en ruta.
- Las novedades pueden eliminarse físicamente antes de la entrega; la auditoría se conserva dentro de la misma transacción.

La colección Postman del proyecto se encuentra en [`docs/envios.postman_collection.json`](docs/envios.postman_collection.json).

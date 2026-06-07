# Changelog - global-catalog-service

## 2026-05-09 - Migracion hexagonal y despliegue dev en Azure App Service

### Resumen

Se reorganizo `global-catalog-service` hacia arquitectura hexagonal y se dejo preparado para desplegar en Azure App Service como ambiente de desarrollo usando H2, sin aprovisionar una base de datos externa.

El servicio quedo funcionando en App Service despues de corregir el startup command para que Azure ejecute el `quarkus-run.jar` de Quarkus, en vez de intentar arrancar un JAR interno no ejecutable.

### Cambios principales

- Se separo el codigo en capas:
  - `domain`: modelos de negocio `Product`, `ProductChange`, `ProductStatus`.
  - `application`: caso de uso `ProductCatalogService`, puertos y excepciones propias.
  - `infrastructure`: adaptadores REST, persistencia Panache/JPA y configuracion.
- Se mantuvo el contrato HTTP existente:
  - `POST /api/products`
  - `GET /api/products/{country}/{productId}`
  - `PUT /api/products/{country}/{productId}`
  - `PATCH /api/products/{country}/{productId}/price`
  - `DELETE /api/products/{country}/{productId}`
  - `GET /api/products/search`
  - `GET /api/products/{country}/{productId}/changes`
- Se agrego soporte H2 para despliegues de desarrollo:
  - dependencia `quarkus-jdbc-h2`.
  - perfil `%appservice-dev`.
  - migraciones especificas en `src/main/resources/db/migration-h2`.
- Se preservaron las migraciones SQL Server existentes en `src/main/resources/db/migration`.
- Se agregaron pruebas unitarias de la capa application.

### Validaciones realizadas

- Build y tests:

```powershell
.\mvnw.cmd test
```

Resultado:

```text
Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

- Build para App Service dev con H2:

```powershell
.\mvnw.cmd package -DskipTests '-Dquarkus.profile=appservice-dev'
```

- Validacion local del artefacto compilado con H2:
  - `GET /q/health` respondio `UP`.
  - `GET /api/products/search?country=PE` devolvio productos seed.

### Despliegue recomendado en App Service Code

Compilar antes de desplegar:

```powershell
cd global-catalog-service
.\mvnw.cmd package -DskipTests '-Dquarkus.profile=appservice-dev'
```

Desplegar con VS Code Azure Extension la carpeta:

```text
target/quarkus-app
```

No desplegar la carpeta raiz del proyecto si se quiere evitar que App Service compile o elija un JAR incorrecto.

### App Settings requeridos

```text
QUARKUS_PROFILE=appservice-dev
H2_JDBC_URL=jdbc:h2:file:/home/site/data/global-catalog-service;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
WEBSITES_ENABLE_APP_SERVICE_STORAGE=true
```

Para desarrollo con H2, mantener el App Service en una sola instancia. H2 file-based no es apto para scale-out.

### Startup command correcto

Si `quarkus-run.jar` queda directamente en `/home/site/wwwroot`:

```bash
java -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080 -jar /home/site/wwwroot/quarkus-run.jar
```

Si queda dentro de `/home/site/wwwroot/quarkus-app`:

```bash
java -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080 -jar /home/site/wwwroot/quarkus-app/quarkus-run.jar
```

Health check recomendado:

```text
/q/health
```

### Problemas encontrados y solucion

#### App Service detenido

Sintoma:

```text
HTTP/1.1 403 Site Disabled
Error 403 - This web app is stopped.
```

Causa:

El App Service estaba apagado, por lo que la request no llegaba a Quarkus.

Accion:

Iniciar el App Service desde Azure Portal y volver a probar `/q/health`.

#### Azure arrancaba parking page

Sintoma en logs:

```text
Could not find an executable jar in /home/site/wwwroot/ or any subdirectory.
Using default parking page at /usr/local/appservice/parkingpage.jar
```

Causa:

No habia startup command y App Service no encontraba el artefacto correcto.

Accion:

Configurar el startup command apuntando explicitamente a `quarkus-run.jar`.

#### Azure elegia el JAR equivocado

Sintoma en logs:

```text
Found other jar files in /home/site/wwwroot, choosing the first one alphabetically
no main manifest attribute, in /home/site/wwwroot/app/global-catalog-service-1.0.0-SNAPSHOT.jar
```

Causa:

App Service intento ejecutar el JAR interno de la aplicacion Quarkus (`app/global-catalog-service-1.0.0-SNAPSHOT.jar`), que no es ejecutable directamente.

Accion:

Usar startup command explicito con `quarkus-run.jar`.

#### Quarkus compilado para SQL Server no puede cambiarse a H2 solo con variables

Sintoma local:

```text
Build time property cannot be changed at runtime:
quarkus.datasource.db-kind is set to 'h2' but it is build time fixed to 'mssql'
quarkus.flyway.locations is set to 'db/migration-h2' but it is build time fixed to 'db/migration'
```

Causa:

`quarkus.datasource.db-kind` y `quarkus.flyway.locations` son propiedades de build-time.

Accion:

Compilar el artefacto con el perfil H2 antes de desplegar:

```powershell
.\mvnw.cmd package -DskipTests '-Dquarkus.profile=appservice-dev'
```

### Recomendaciones

- Para desarrollo/demo en App Service, usar H2 solo temporalmente.
- Para produccion, volver a SQL Server o una base administrada, configurando:
  - `DB_JDBC_URL`
  - `DB_USERNAME`
  - `DB_PASSWORD`
- No guardar secretos en el repositorio.
- Mantener `WEBSITES_ENABLE_APP_SERVICE_STORAGE=true` si se usa H2 file-based.
- Evitar scale-out con H2; usar una sola instancia.
- Revisar logs con Log Stream despues de cada despliegue.
- Validar siempre:

```bash
curl https://<app-service-url>/q/health
curl "https://<app-service-url>/api/products/search?country=PE"
```

### Pendientes sugeridos

- Crear script de empaquetado para App Service dev.
- Crear ZIP reproducible con el contenido de `target/quarkus-app`.
- Documentar el paso a SQL Server cuando se aprovisione base de datos.
- Agregar pipeline CI/CD cuando el proyecto deje de depender del despliegue manual por VS Code.

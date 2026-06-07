# MercadoAndes Global Catalog Service

Servicio backend REST para la gestión del catálogo de productos global de MercadoAndes.

## Tecnologías Utilizadas

- **Java 21**: Lenguaje de programación.
- **Quarkus 3.35.1**: Framework Java optimizado para Kubernetes y despliegues rápidos en la nube.
- **Azure Cosmos DB Java SDK v4**: SDK oficial de Azure para persistencia NoSQL de alta disponibilidad y baja latencia.
- **OpenAPI**: Contrato de API documentado y generado con herramientas REST-JAXRS (`catalog-api.yaml`).
- **Maven**: Gestor de dependencias y compilación.

## Funcionalidad Mínima

Este repositorio implementa la persistencia de productos en **Azure Cosmos DB**. Su funcionalidad principal y activa es:

- **Creación de Productos (`POST /api/products`)**: Recibe un payload JSON del producto, realiza la validación de consistencia de identidad, normaliza los campos y los persiste directamente en un contenedor de Cosmos DB de manera eficiente en un único intento.
- **Soporte de Mock Local (`use-mock-storage`)**: Cuenta con un almacenamiento simulado en memoria (`mockStorage` usando `ConcurrentHashMap`) que se activa dinámicamente mediante la propiedad `mercadoandes.catalog.use-mock-storage = true`, ideal para desarrollo ágil y ejecución de pruebas de manera local y desconectada.
- **Otros Endpoints de la API (`GET`, `PUT`, `DELETE`)**: Se encuentran definidos como stubs mínimos conformes al contrato OpenAPI para asegurar la compatibilidad sin dependencias adicionales.

## Comandos Principales

- **Ejecutar en modo desarrollo local:**
  ```bash
  ./mvnw quarkus:dev
  ```

- **Ejecutar pruebas unitarias:**
  ```bash
  ./mvnw clean test
  ```

- **Compilar y empaquetar para despliegue (Fast-JAR):**
  ```bash
  ./mvnw clean package -DskipTests
  ```
  El empaquetado resultante se generará en el directorio `target/quarkus-app/` listo para ser ejecutado con `java -jar target/quarkus-app/quarkus-run.jar`.

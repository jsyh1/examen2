docs/database/schema.sql
docs/uml/
# Sistema de Gestión de Seguros

Aplicación de escritorio en JavaFX para administrar seguros de vida y de vehículo mediante CRUD, MySQL/JDBC y arquitectura hexagonal.

## Modelo funcional

- `Seguro` es abstracto y contiene `numeroPoliza` (`String`), `fechaExpedicion` (`LocalDate`), `valor` (`BigDecimal`) y `estado` (`EstadoSeguro`). Conserva `id` como clave técnica y la relación con `Asegurado` para el CRUD.
- `SeguroVida` agrega `nombreBeneficiario` y `parentesco`, ambos obligatorios.
- `SeguroVehiculo` agrega `marca`, obligatoria.
- `Asegurado` contiene `identificacion` única, `nombre` y `correoElectronico` validado; mantiene un `id` técnico.
- Un asegurado puede tener varias pólizas. La interfaz solo permite seleccionar asegurados existentes; no ofrece CRUD de asegurados.

No se mantienen `nombre`/`descripcion` de póliza, `fechaInicio`/`fechaFin`, `valorPrima`, `valorCobertura`, `placa`, `modelo` ni `valorAsegurado`. `fechaExpedicion` no representa inicio de vigencia. Los estados disponibles son `ACTIVO`, `VENCIDO`, `CANCELADO` y `PENDIENTE`; el CRUD no bloquea cambios de estado mediante transiciones adicionales.

## Tecnologías

- JDK 26, Maven 3.9 o posterior.
- JavaFX Controls/FXML 26.
- MySQL 8 y MySQL Connector/J 9.4.0.
- JUnit 5.11.

## Arquitectura y paquetes

El dominio no depende de JDBC ni JavaFX. Los casos de uso de `co.edu.poli.aplication.port.in` son implementados por servicios que dependen del contrato `SeguroRepository` en `port.out`. `MysqlSeguroRepository` adapta ese contrato a JDBC; `Controller` adapta la vista a los casos de uso y `App` los compone.

```text
co.edu.poli.aplication.domain.model   Entidades y estado
co.edu.poli.aplication.port.in        Crear, consultar, listar, actualizar y eliminar
co.edu.poli.aplication.port.out       SeguroRepository
co.edu.poli.aplication.service        Servicios de aplicación
co.edu.poli.infrastructure.persistence ConexionDB y MysqlSeguroRepository
co.edu.poli.infrastructure.ui         Controlador JavaFX
co.edu.poli.vista                      App
src/main/resources/co/edu/poli/vista/vista.fxml
```

## MySQL y configuración

Para una instalación nueva, crea las tablas y un asegurado ficticio de catálogo:

```bash
mysql -u usuario -p < docs/database/schema.sql
```

Copia `.env.example` a `.env` y configura los datos de tu instancia:

```dotenv
DB_HOST=localhost
DB_PORT=3307
DB_NAME=seguros
DB_USER=usuario_local
DB_PASSWORD=contrasena_local
```

También se aceptan variables de entorno del proceso, que tienen prioridad sobre `.env`. `DB_USER` y `DB_PASSWORD` son obligatorias; el código no contiene credenciales predeterminadas. `.env` está excluido por `.gitignore`.

### Migrar una instalación existente

Realiza un respaldo y ejecuta una sola vez:

```bash
mysql -u usuario -p < docs/database/migration_modelo_academico.sql
```

La migración conserva las columnas antiguas y añade el nuevo modelo. Mapea `tipo_documento` + `numero_documento` a una identificación compuesta (`tipo:número`), copia el correo existente y el nombre del beneficiario. No convierte `fecha_inicio` en fecha de expedición ni `valor_prima` en valor del seguro; tampoco inventa el parentesco. Revisa las filas que el script muestra y completa manualmente `fecha_expedicion`, `valor` y `parentesco`; corrige también cualquier marca vacía. Después ejecuta:

```bash
mysql -u usuario -p < docs/database/migration_finalizar_modelo_academico.sql
```

Ese segundo paso exige valores completos y añade restricciones. Ninguno de los scripts elimina tablas o columnas. Los campos antiguos permanecen para permitir revisión y respaldo; la aplicación nueva no los consulta.

## Compilar y ejecutar

Con JDK 26 activo:

```bash
mvn clean test
mvn javafx:run
mvn clean package javadoc:javadoc
```

La clase de entrada es `co.edu.poli.vista.App`.

## Pruebas

Las pruebas unitarias de dominio y servicios no requieren MySQL:

```bash
mvn clean test
```

Para ejecutar la prueba CRUD real, instala el esquema y configura las variables `DB_*` o `.env`, luego activa la integración:

```bash
mvn -Dmysql.integration=true -Dtest=MysqlSeguroRepositoryIT test
```

La prueba cubre ambos subtipos, los campos específicos, múltiples pólizas para un asegurado, actualización entre subtipos, titular y borrado.

## CRUD y UML

La vista permite crear, buscar por ID/número de póliza, listar, actualizar y eliminar. Los campos comunes son número, fecha de expedición, valor, estado y asegurado; los campos de subtipo cambian según la selección.

- [Diagrama de clases](docs/uml/diagrama-clases-seguros.puml)
- [Arquitectura hexagonal](docs/uml/arquitectura-hexagonal.puml)
- [Persistencia y servicios](docs/uml/persistencia-servicios.puml)

Los archivos PlantUML son editables. Las escrituras a varias tablas usan transacciones y los datos de usuario se envían con `PreparedStatement`.

## Limitaciones

No se incluye CRUD de asegurados, autenticación ni creación automática del esquema al iniciar. Los datos de los seguros existentes que no tienen equivalencia semántica requieren revisión manual durante la migración.
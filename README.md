# Sistema de Gestión de Seguros

Aplicación de escritorio para administrar pólizas de vida y vehiculares con JavaFX, arquitectura hexagonal y persistencia relacional en MySQL.

## Funcionalidades

- Crear, consultar por ID o número de póliza, listar, actualizar y eliminar seguros.
- Administrar pólizas de vida y vehículo, conservando sus atributos específicos.
- Seleccionar asegurados precargados y consultar su información asociada.
- Validar datos obligatorios, importes, vigencia, unicidad de póliza y cambios de estado.

El sistema no incluye un CRUD de asegurados: el catálogo se carga desde `asegurados`.

## Tecnologías y requisitos

- JDK 26 y Maven 3.9 o posterior.
- JavaFX Controls/FXML 26.
- MySQL 8.0 o posterior y MySQL Connector/J 9.4.0.
- JUnit 5.11.0.

La compilación configura `maven.compiler.release=26`. Un JDK anterior no puede compilar el proyecto tal cual.

## Estructura

```text
src/main/java/
	module-info.java
	co/edu/poli/aplication/
		domain/model/       Seguro, SeguroVida, SeguroVehiculo, Asegurado, EstadoSeguro
		port/in/            Contratos de los casos de uso
		port/out/           SeguroRepository
		service/            Implementaciones de los casos de uso
	co/edu/poli/infrastructure/
		persistence/        MysqlSeguroRepository y ConexionDB
		ui/                 Controlador JavaFX
	co/edu/poli/vista/    App
src/main/resources/co/edu/poli/vista/vista.fxml
docs/database/schema.sql
docs/uml/
```

## Arquitectura

El dominio contiene las invariantes y no importa JavaFX ni JDBC. Los servicios de aplicación implementan los puertos de entrada y dependen del contrato `SeguroRepository`. `MysqlSeguroRepository` adapta ese puerto a JDBC/MySQL y `Controller` traduce interacciones JavaFX a casos de uso. `App` compone los adaptadores y entrega los casos de uso al controlador.

`Seguro` es abstracto; `SeguroVida` y `SeguroVehiculo` aportan los datos particulares. Un asegurado puede asociarse a varios seguros. `EstadoSeguro` restringe transiciones: `PENDIENTE` puede pasar a `ACTIVO` o `CANCELADO`; `ACTIVO` puede pasar a `VENCIDO` o `CANCELADO`; `VENCIDO` y `CANCELADO` son terminales.

## Base de datos

Ejecuta el esquema con un usuario que pueda crear bases y tablas:

```bash
mysql -u usuario -p < docs/database/schema.sql
```

El script crea `seguros`, `asegurados`, `seguros_vida` y `seguros_vehiculo`, con claves foráneas, unicidad documental y de póliza, controles e índices. También inserta un asegurado ficticio para probar la selección inicial. Las operaciones de alta y actualización escriben la fila común y el detalle de subtipo en una transacción.

## Configuración

Copia `.env.example` a `.env` y ajusta la configuración local:

```dotenv
DB_HOST=localhost
DB_PORT=3306
DB_NAME=seguros
DB_USER=usuario_local
DB_PASSWORD=contrasena_local
```

La aplicación lee primero variables de entorno del proceso y después `.env`. `.env` está excluido de Git. No guardes credenciales reales en archivos versionados.

## Compilar y ejecutar

Con JDK 26 activo:

```bash
mvn clean test
mvn javafx:run
```

La clase de entrada modular es `co.edu.poli.vista.App`.

Para empaquetar y generar Javadoc:

```bash
mvn clean package javadoc:javadoc
```

## Pruebas

Las pruebas unitarias de dominio y servicios se ejecutan sin MySQL:

```bash
mvn test
```

La prueba JDBC CRUD usa una base MySQL preparada con `docs/database/schema.sql`, el mismo `.env` y una propiedad de activación para evitar conectarse accidentalmente durante las pruebas unitarias:

```bash
mvn -Dmysql.integration=true -Dtest=MysqlSeguroRepositoryIT test
```

## Diagramas UML

- [Diagrama de clases de seguros](docs/uml/diagrama-clases-seguros.puml)
- [Arquitectura hexagonal](docs/uml/arquitectura-hexagonal.puml)
- [Persistencia y servicios](docs/uml/persistencia-servicios.puml)

Los archivos `.puml` son la fuente editable. Se pueden renderizar con PlantUML instalado localmente.

## Operaciones y limitaciones

El CRUD de seguros está implementado mediante JDBC parametrizado; las relaciones y subtipos se guardan en MySQL. El catálogo de asegurados debe existir antes de registrar pólizas. No se incluye creación automática del esquema en el arranque, ni gestión de usuarios/autenticación. La conexión MySQL y la ejecución real de la prueba de integración dependen de una instancia configurada.
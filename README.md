# Sistema de Gestión de Seguros

Aplicación de escritorio desarrollada en **Java 26 y JavaFX** para gestionar pólizas de seguros de vida y de vehículo. Utiliza **MySQL con JDBC** para la persistencia y aplica **arquitectura hexagonal** para separar el dominio, los casos de uso y los adaptadores.

## Tabla de contenido

- [Funcionalidades](#funcionalidades)
- [Modelo de dominio](#modelo-de-dominio)
- [Tecnologías](#tecnologías)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Diagramas UML](#diagramas-uml)
- [Requisitos previos](#requisitos-previos)
- [Configuración de MySQL](#configuración-de-mysql)
- [Compilar, probar y ejecutar](#compilar-probar-y-ejecutar)
- [Publicación académica](#publicación-académica)
- [Limitaciones conocidas](#limitaciones-conocidas)

## Funcionalidades

La interfaz JavaFX permite realizar operaciones CRUD sobre los seguros:

- **Crear** una póliza de vida o de vehículo.
- **Consultar** seguros por identificador o número de póliza y listar los registros.
- **Actualizar** los datos de una póliza.
- **Eliminar** una póliza.
- Asociar varias pólizas a un mismo asegurado existente.
- Mostrar los campos específicos según el tipo de seguro seleccionado.

Los datos se almacenan en MySQL. Las operaciones que modifican varias tablas utilizan transacciones y los valores de entrada se envían mediante `PreparedStatement`.

## Modelo de dominio

- **`Seguro`**: clase abstracta con identificador técnico, `numeroPoliza` (`String`), `fechaExpedicion` (`LocalDate`), `valor` (`BigDecimal`), `estado` (`EstadoSeguro`) y la relación con un asegurado.
- **`SeguroVida`**: agrega `nombreBeneficiario` y `parentesco`; ambos son obligatorios.
- **`SeguroVehiculo`**: agrega `marca`, obligatoria.
- **`Asegurado`**: contiene identificador técnico, `identificacion` única, `nombre` y `correoElectronico`.

Un asegurado puede tener más de una póliza. En el alcance actual, los asegurados se seleccionan de un catálogo existente: **la aplicación no incluye CRUD de asegurados**.

Los estados disponibles son `ACTIVO`, `VENCIDO`, `CANCELADO` y `PENDIENTE`. La fecha almacenada es la fecha de expedición, no la fecha de inicio de vigencia.

## Tecnologías

| Tecnología | Uso |
|---|---|
| Java JDK 26 | Lenguaje y plataforma |
| Maven 3.9 o posterior | Compilación y gestión del proyecto |
| JavaFX Controls / FXML 26 | Interfaz de escritorio |
| MySQL 8 | Base de datos relacional |
| MySQL Connector/J 9.4.0 | Conexión JDBC |
| JUnit 5.11 | Pruebas automatizadas |
| PlantUML | Diagramas de arquitectura y clases |

## Estructura del proyecto

Resumen de los componentes principales:

```text
.
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── module-info.java
│   │   │   └── co/edu/poli/
│   │   │       ├── aplication/
│   │   │       │   ├── domain/model/   # Entidades y estado
│   │   │       │   ├── port/in/        # Casos de uso de entrada
│   │   │       │   ├── port/out/       # Contratos de salida
│   │   │       │   └── service/        # Servicios de aplicación
│   │   │       ├── infrastructure/
│   │   │       │   ├── persistence/    # JDBC y repositorio MySQL
│   │   │       │   └── ui/             # Controlador JavaFX
│   │   │       └── vista/              # Aplicación JavaFX
│   │   └── resources/
│   │       └── co/edu/poli/vista/vista.fxml
│   └── test/                           # Pruebas automatizadas
├── docs/
│   ├── database/
│   │   ├── schema.sql
│   │   ├── migration_modelo_academico.sql
│   │   └── migration_finalizar_modelo_academico.sql
│   └── uml/
│       ├── diagrama-clases-seguros.puml
│       ├── arquitectura-hexagonal.puml
│       └── persistencia-servicios.puml
├── .env.example
├── .gitignore
└── pom.xml
```

### Responsabilidad de las capas

| Componente | Responsabilidad |
|---|---|
| `domain.model` | Modelo de dominio y reglas propias de las entidades |
| `port.in` | Contratos de los casos de uso disponibles para la aplicación |
| `port.out` | Contrato de persistencia `SeguroRepository` |
| `service` | Implementación de los casos de uso |
| `MysqlSeguroRepository` | Adaptador de persistencia mediante JDBC |
| `ConexionDB` | Configuración y apertura de conexiones |
| `Controller` | Adaptador entre los eventos de la interfaz y los casos de uso |
| `App` | Arranque y composición de los componentes |

El dominio no necesita depender directamente de JavaFX ni de JDBC. El adaptador de persistencia implementa el puerto de salida y la interfaz invoca los casos de uso de la aplicación.

## Diagramas UML

Las fuentes PlantUML se pueden consultar y editar desde estos enlaces:

- [Diagrama de clases del modelo de seguros](docs/uml/diagrama-clases-seguros.puml)
- [Diagrama de arquitectura hexagonal](docs/uml/arquitectura-hexagonal.puml)
- [Diagrama de persistencia y servicios](docs/uml/persistencia-servicios.puml)

## Requisitos previos

Antes de ejecutar el proyecto, instala y configura:

1. JDK 26.
2. Maven 3.9 o una versión posterior.
3. MySQL 8 en funcionamiento.
4. Las variables de conexión a la base de datos.

Comprueba las versiones desde una terminal:

```bash
java -version
mvn -version
```

## Configuración de MySQL

### Instalación nueva

Desde la raíz del proyecto, crea el esquema y los datos de catálogo incluidos en el script:

```bash
mysql -u usuario -p < docs/database/schema.sql
```

Reemplaza `usuario` por un usuario de MySQL con permisos para crear y configurar la base de datos.

### Configurar la conexión

Copia el archivo de ejemplo y edita el archivo local `.env`:

```bash
cp .env.example .env
```

Usa los valores correspondientes a tu instalación:

```dotenv
DB_HOST=localhost
DB_PORT=3307
DB_NAME=seguros
DB_USER=usuario_local
DB_PASSWORD=contrasena_local
```

Ajusta `DB_PORT` al puerto en el que escucha tu instancia de MySQL. La aplicación también acepta estas variables desde el entorno del proceso; las variables de entorno tienen prioridad sobre los valores de `.env`. `DB_USER` y `DB_PASSWORD` deben configurarse. No incluyas contraseñas reales en Git: el archivo `.env` está excluido por `.gitignore`.

### Migrar una base de datos existente

**Haz primero una copia de seguridad.** La migración está dividida en dos scripts y requiere revisar los datos entre ambos pasos.

1. Ejecuta una sola vez la migración del modelo:

   ```bash
   mysql -u usuario -p < docs/database/migration_modelo_academico.sql
   ```

2. Revisa las filas que el script reporte y completa manualmente los datos que no puedan inferirse con seguridad, especialmente `fecha_expedicion`, `valor` y `parentesco`. Revisa también las marcas vacías.

3. Cuando los datos estén completos, ejecuta el script que finaliza el modelo:

   ```bash
   mysql -u usuario -p < docs/database/migration_finalizar_modelo_academico.sql
   ```

La migración transforma la identificación anterior en una identificación compuesta (`tipo:número`) y copia los datos que tienen equivalencia directa. No interpreta `fecha_inicio` como fecha de expedición, no convierte `valor_prima` en valor del seguro y no inventa el parentesco. Los scripts conservan las columnas antiguas; aun así, realiza una copia de seguridad antes de ejecutarlos.

## Compilar, probar y ejecutar

Ejecuta cada comando por separado desde la raíz del proyecto.

**Ejecutar las pruebas unitarias:**

```bash
mvn clean test
```

**Iniciar la aplicación JavaFX:**

```bash
mvn javafx:run
```

La clase de entrada es `co.edu.poli.vista.App`.

**Generar el paquete y la documentación Javadoc:**

```bash
mvn clean package javadoc:javadoc
```

### Prueba de integración con MySQL

Esta prueba requiere que MySQL esté disponible, que el esquema esté instalado y que la configuración `DB_*` o el archivo `.env` sean correctos:

```bash
mvn -Dmysql.integration=true -Dtest=MysqlSeguroRepositoryIT test
```

La prueba de integración está preparada para comprobar los dos subtipos de seguro, sus campos particulares, varias pólizas asociadas al mismo asegurado, la actualización entre subtipos, la conservación del titular y la eliminación.

## Publicación académica

La Wiki del repositorio debe reunir las imágenes de los diagramas y el video de desarrollo y demostración, con narración en la voz del estudiante.

- [Abrir la Wiki del proyecto](https://github.com/jsyh1/examen2/wiki)
- **Video:** añadir el enlace a la página de la Wiki cuando se publique.

Se recomienda que el video muestre las herramientas empleadas —incluida la IA, si se utilizó—, la arquitectura y el modelo de clases, la documentación Javadoc, las operaciones CRUD en JavaFX, el caso de un asegurado con varias pólizas y la publicación o actualización del código en GitHub desde el IDE.

## Limitaciones conocidas

- No se incluye CRUD de asegurados.
- No hay autenticación de usuarios.
- El esquema de base de datos debe instalarse manualmente.
- Las bases de datos antiguas requieren revisión manual de los campos que no tienen equivalencia semántica directa.

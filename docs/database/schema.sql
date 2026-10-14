CREATE DATABASE IF NOT EXISTS seguros
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE seguros;

CREATE TABLE IF NOT EXISTS asegurados (
    id INT NOT NULL AUTO_INCREMENT,
    identificacion VARCHAR(80) NOT NULL,
    nombre VARCHAR(160) NOT NULL,
    correo_electronico VARCHAR(254) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_asegurado_identificacion UNIQUE (identificacion),
    CONSTRAINT chk_asegurado_nombre CHECK (CHAR_LENGTH(TRIM(nombre)) > 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS seguros (
    id INT NOT NULL AUTO_INCREMENT,
    numero_poliza VARCHAR(60) NOT NULL,
    fecha_expedicion DATE NOT NULL,
    valor DECIMAL(14, 2) NOT NULL,
    estado VARCHAR(15) NOT NULL,
    tipo VARCHAR(15) NOT NULL,
    asegurado_id INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_seguro_numero_poliza UNIQUE (numero_poliza),
    CONSTRAINT chk_seguro_valor CHECK (valor >= 0),
    CONSTRAINT chk_seguro_estado CHECK (estado IN ('ACTIVO', 'VENCIDO', 'CANCELADO', 'PENDIENTE')),
    CONSTRAINT chk_seguro_tipo CHECK (tipo IN ('VIDA', 'VEHICULO')),
    CONSTRAINT fk_seguro_asegurado FOREIGN KEY (asegurado_id)
        REFERENCES asegurados (id) ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_seguro_asegurado (asegurado_id),
    INDEX idx_seguro_estado (estado),
    INDEX idx_seguro_tipo (tipo)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS seguros_vida (
    seguro_id INT NOT NULL,
    nombre_beneficiario VARCHAR(160) NOT NULL,
    parentesco VARCHAR(80) NOT NULL,
    PRIMARY KEY (seguro_id),
    CONSTRAINT chk_vida_beneficiario CHECK (CHAR_LENGTH(TRIM(nombre_beneficiario)) > 0),
    CONSTRAINT chk_vida_parentesco CHECK (CHAR_LENGTH(TRIM(parentesco)) > 0),
    CONSTRAINT fk_vida_seguro FOREIGN KEY (seguro_id)
        REFERENCES seguros (id) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS seguros_vehiculo (
    seguro_id INT NOT NULL,
    marca VARCHAR(80) NOT NULL,
    PRIMARY KEY (seguro_id),
    CONSTRAINT chk_vehiculo_marca CHECK (CHAR_LENGTH(TRIM(marca)) > 0),
    CONSTRAINT fk_vehiculo_seguro FOREIGN KEY (seguro_id)
        REFERENCES seguros (id) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

-- Datos de ejemplo ficticios para habilitar la selección de asegurados.
INSERT INTO asegurados (identificacion, nombre, correo_electronico)
VALUES ('TEST-0001', 'Asegurado de prueba', 'prueba@example.invalid')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), correo_electronico = VALUES(correo_electronico);

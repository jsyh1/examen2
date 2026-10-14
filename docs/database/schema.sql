CREATE DATABASE IF NOT EXISTS seguros
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE seguros;

CREATE TABLE IF NOT EXISTS asegurados (
    id INT NOT NULL AUTO_INCREMENT,
    tipo_documento VARCHAR(30) NOT NULL,
    numero_documento VARCHAR(40) NOT NULL,
    nombre VARCHAR(160) NOT NULL,
    telefono VARCHAR(40) NOT NULL,
    correo VARCHAR(254) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_asegurado_documento UNIQUE (tipo_documento, numero_documento),
    CONSTRAINT chk_asegurado_nombre CHECK (CHAR_LENGTH(TRIM(nombre)) > 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS seguros (
    id INT NOT NULL AUTO_INCREMENT,
    numero_poliza VARCHAR(60) NOT NULL,
    nombre VARCHAR(140) NOT NULL,
    descripcion VARCHAR(1000) NOT NULL DEFAULT '',
    valor_prima DECIMAL(14, 2) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado VARCHAR(15) NOT NULL,
    tipo VARCHAR(15) NOT NULL,
    asegurado_id INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_seguro_numero_poliza UNIQUE (numero_poliza),
    CONSTRAINT chk_seguro_prima CHECK (valor_prima >= 0),
    CONSTRAINT chk_seguro_fechas CHECK (fecha_fin >= fecha_inicio),
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
    beneficiario VARCHAR(160) NOT NULL,
    valor_cobertura DECIMAL(16, 2) NOT NULL,
    PRIMARY KEY (seguro_id),
    CONSTRAINT chk_vida_cobertura CHECK (valor_cobertura > 0),
    CONSTRAINT fk_vida_seguro FOREIGN KEY (seguro_id)
        REFERENCES seguros (id) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS seguros_vehiculo (
    seguro_id INT NOT NULL,
    placa VARCHAR(20) NOT NULL,
    marca VARCHAR(80) NOT NULL,
    modelo VARCHAR(80) NOT NULL,
    valor_asegurado DECIMAL(16, 2) NOT NULL,
    PRIMARY KEY (seguro_id),
    CONSTRAINT chk_vehiculo_valor CHECK (valor_asegurado > 0),
    CONSTRAINT fk_vehiculo_seguro FOREIGN KEY (seguro_id)
        REFERENCES seguros (id) ON UPDATE CASCADE ON DELETE CASCADE,
    INDEX idx_vehiculo_placa (placa)
) ENGINE=InnoDB;

-- Datos de ejemplo ficticios para habilitar la selección de asegurados.
INSERT INTO asegurados (tipo_documento, numero_documento, nombre, telefono, correo)
VALUES ('CC', 'TEST-0001', 'Asegurado de prueba', '0000000000', 'prueba@example.invalid')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

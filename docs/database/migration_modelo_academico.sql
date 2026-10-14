-- Migración aditiva desde el esquema anterior de gestión de seguros.
-- Ejecutar una sola vez después de un respaldo de la base existente.
-- No elimina ni renombra columnas antiguas.
USE seguros;

ALTER TABLE asegurados
    ADD COLUMN identificacion VARCHAR(80) NULL,
    ADD COLUMN correo_electronico VARCHAR(254) NULL;

-- Conserva ambos componentes del documento anterior y copia el correo existente.
UPDATE asegurados
SET identificacion = CONCAT(TRIM(tipo_documento), ':', TRIM(numero_documento)),
    correo_electronico = correo
WHERE identificacion IS NULL OR correo_electronico IS NULL;

ALTER TABLE asegurados
    MODIFY identificacion VARCHAR(80) NOT NULL,
    MODIFY correo_electronico VARCHAR(254) NOT NULL,
    ADD CONSTRAINT uq_asegurado_identificacion UNIQUE (identificacion);

-- No se asigna fecha_inicio como fecha_expedicion ni valor_prima como valor.
-- Esos datos necesitan revisión y captura manual antes de finalizar la migración.
ALTER TABLE seguros
    ADD COLUMN fecha_expedicion DATE NULL,
    ADD COLUMN valor DECIMAL(14, 2) NULL;

-- El nombre de beneficiario sí puede copiarse del campo anterior.
-- No existe una fuente segura para parentesco, debe capturarse manualmente.
ALTER TABLE seguros_vida
    ADD COLUMN nombre_beneficiario VARCHAR(160) NULL,
    ADD COLUMN parentesco VARCHAR(80) NULL;

UPDATE seguros_vida
SET nombre_beneficiario = beneficiario
WHERE nombre_beneficiario IS NULL;

-- seguros_vehiculo ya contiene marca en el esquema anterior.
-- placa, modelo y valor_asegurado se conservan, pero la aplicación nueva los ignora.

-- Revisión previa a migration_finalizar_modelo_academico.sql:
SELECT id, numero_poliza, fecha_inicio, fecha_expedicion, valor_prima, valor
FROM seguros
WHERE fecha_expedicion IS NULL OR valor IS NULL;

SELECT seguro_id, beneficiario, nombre_beneficiario, parentesco
FROM seguros_vida
WHERE nombre_beneficiario IS NULL OR parentesco IS NULL;

SELECT seguro_id, marca
FROM seguros_vehiculo
WHERE marca IS NULL OR CHAR_LENGTH(TRIM(marca)) = 0;

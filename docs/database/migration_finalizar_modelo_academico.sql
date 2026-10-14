-- Ejecutar solo después de completar manualmente los campos pendientes
-- y confirmar que las consultas de revisión en migration_modelo_academico.sql
-- no devuelven filas.
USE seguros;

ALTER TABLE seguros
    MODIFY fecha_expedicion DATE NOT NULL,
    MODIFY valor DECIMAL(14, 2) NOT NULL,
    ADD CONSTRAINT chk_seguro_valor CHECK (valor >= 0);

ALTER TABLE seguros_vida
    MODIFY nombre_beneficiario VARCHAR(160) NOT NULL,
    MODIFY parentesco VARCHAR(80) NOT NULL,
    ADD CONSTRAINT chk_vida_beneficiario CHECK (CHAR_LENGTH(TRIM(nombre_beneficiario)) > 0),
    ADD CONSTRAINT chk_vida_parentesco CHECK (CHAR_LENGTH(TRIM(parentesco)) > 0);

ALTER TABLE seguros_vehiculo
    ADD CONSTRAINT chk_vehiculo_marca CHECK (CHAR_LENGTH(TRIM(marca)) > 0);

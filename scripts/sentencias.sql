-- Correccion para bases que ya tenian zonas antes de agregar la capacidad.
UPDATE zonas
SET capacidad = 1
WHERE capacidad IS NULL;

ALTER TABLE zonas
ALTER COLUMN capacidad SET DEFAULT 1;

ALTER TABLE zonas
ALTER COLUMN capacidad SET NOT NULL;

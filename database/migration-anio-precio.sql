-- Para una tabla existente creada con la versión anterior del proyecto.
-- No se inventan años o precios para los libros ya registrados.
BEGIN;
ALTER TABLE catalogos.libros
    ADD COLUMN IF NOT EXISTS anio_publicacion INTEGER
        CHECK (anio_publicacion BETWEEN 1 AND 9999),
    ADD COLUMN IF NOT EXISTS precio NUMERIC(10, 2) CHECK (precio >= 0);
COMMIT;

-- Completar los datos reales de cada libro antiguo, por ejemplo:
-- UPDATE catalogos.libros SET anio_publicacion = 1943, precio = 250.50 WHERE id = 1;
-- Cuando TODOS los registros tengan sus datos, ejecutar:
-- ALTER TABLE catalogos.libros ALTER COLUMN anio_publicacion SET NOT NULL;
-- ALTER TABLE catalogos.libros ALTER COLUMN precio SET NOT NULL;

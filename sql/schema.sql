-- =========================================================================
-- 1. ESTRUCTURA DE TABLAS (POSTGRESQL / NEON)
-- =========================================================================
CREATE TABLE IF NOT EXISTS usuario (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telefono VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS tipo_espacio (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT
);

CREATE TABLE IF NOT EXISTS espacio (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    capacidad INT NOT NULL,
    precio_hora NUMERIC(10,2) NOT NULL,
    tipo_espacio_id INT REFERENCES tipo_espacio(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reserva (
    id SERIAL PRIMARY KEY,
    usuario_id INT REFERENCES usuario(id) ON DELETE CASCADE,
    espacio_id INT REFERENCES espacio(id) ON DELETE CASCADE,
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP NOT NULL,
    total_pagar NUMERIC(10,2) NOT NULL
);

-- =========================================================================
-- 2. LIMPIEZA OPCIONAL DEL ESPACIO DE PRUEBA INICIAL (SOLO SI NO TIENE RESERVAS)
-- =========================================================================
DELETE FROM espacio
WHERE LOWER(nombre) = 'sala de juntas'
  AND id NOT IN (SELECT DISTINCT espacio_id FROM reserva WHERE espacio_id IS NOT NULL);

-- =========================================================================
-- 3. CATEGORÍAS DE ESPACIOS (tipo_espacio)
-- =========================================================================
INSERT INTO tipo_espacio (nombre, descripcion)
SELECT 'Sala de Juntas', 'Sala cerrada para reuniones ejecutivas. Incluye pantalla 4K HDMI, pizarra acrílica, Wi-Fi dedicado y estación de café.'
WHERE NOT EXISTS (SELECT 1 FROM tipo_espacio WHERE nombre = 'Sala de Juntas');

INSERT INTO tipo_espacio (nombre, descripcion)
SELECT 'Oficina Privada', 'Espacio independiente y silencioso con cerradura privada, escritorios ergonómicos y conexión de alta velocidad.'
WHERE NOT EXISTS (SELECT 1 FROM tipo_espacio WHERE nombre = 'Oficina Privada');

INSERT INTO tipo_espacio (nombre, descripcion)
SELECT 'Escritorio Compartido', 'Puesto de trabajo flexible en zona abierta colaborativa con acceso a áreas comunes, lockers y networking.'
WHERE NOT EXISTS (SELECT 1 FROM tipo_espacio WHERE nombre = 'Escritorio Compartido');

INSERT INTO tipo_espacio (nombre, descripcion)
SELECT 'Auditorio / Eventos', 'Salón amplio equipado con sistema de sonido profesional, micrófonos inalámbricos y proyector para charlas.'
WHERE NOT EXISTS (SELECT 1 FROM tipo_espacio WHERE nombre = 'Auditorio / Eventos');

INSERT INTO tipo_espacio (nombre, descripcion)
SELECT 'Laboratorio de Cómputo', 'Sala equipada con estaciones de trabajo de alto rendimiento, doble monitor y red cableada de baja latencia.'
WHERE NOT EXISTS (SELECT 1 FROM tipo_espacio WHERE nombre = 'Laboratorio de Cómputo');

-- =========================================================================
-- 4. ESPACIOS FÍSICOS REALES (espacio) CON CAPACIDAD Y TARIFAS OFICIALES
-- =========================================================================
INSERT INTO espacio (nombre, capacidad, precio_hora, tipo_espacio_id)
SELECT 'Sala Ejecutiva Norte (Piso 2)', 12, 65000.00, id
FROM tipo_espacio WHERE nombre = 'Sala de Juntas'
  AND NOT EXISTS (SELECT 1 FROM espacio WHERE nombre = 'Sala Ejecutiva Norte (Piso 2)');

INSERT INTO espacio (nombre, capacidad, precio_hora, tipo_espacio_id)
SELECT 'Sala Creativa Glass', 6, 40000.00, id
FROM tipo_espacio WHERE nombre = 'Sala de Juntas'
  AND NOT EXISTS (SELECT 1 FROM espacio WHERE nombre = 'Sala Creativa Glass');

INSERT INTO espacio (nombre, capacidad, precio_hora, tipo_espacio_id)
SELECT 'Oficina Privada Suite A1', 4, 35000.00, id
FROM tipo_espacio WHERE nombre = 'Oficina Privada'
  AND NOT EXISTS (SELECT 1 FROM espacio WHERE nombre = 'Oficina Privada Suite A1');

INSERT INTO espacio (nombre, capacidad, precio_hora, tipo_espacio_id)
SELECT 'Oficina Privada Startup B3', 8, 55000.00, id
FROM tipo_espacio WHERE nombre = 'Oficina Privada'
  AND NOT EXISTS (SELECT 1 FROM espacio WHERE nombre = 'Oficina Privada Startup B3');

INSERT INTO espacio (nombre, capacidad, precio_hora, tipo_espacio_id)
SELECT 'Zona Coworking Open Space', 20, 15000.00, id
FROM tipo_espacio WHERE nombre = 'Escritorio Compartido'
  AND NOT EXISTS (SELECT 1 FROM espacio WHERE nombre = 'Zona Coworking Open Space');

INSERT INTO espacio (nombre, capacidad, precio_hora, tipo_espacio_id)
SELECT 'Auditorio Principal Innovación', 50, 140000.00, id
FROM tipo_espacio WHERE nombre = 'Auditorio / Eventos'
  AND NOT EXISTS (SELECT 1 FROM espacio WHERE nombre = 'Auditorio Principal Innovación');

INSERT INTO espacio (nombre, capacidad, precio_hora, tipo_espacio_id)
SELECT 'Lab Tech & Desarrollo DevSpace', 15, 75000.00, id
FROM tipo_espacio WHERE nombre = 'Laboratorio de Cómputo'
  AND NOT EXISTS (SELECT 1 FROM espacio WHERE nombre = 'Lab Tech & Desarrollo DevSpace');


-- =====================================================================
-- SgOVI - Datos de prueba
-- =====================================================================
-- Aplicar despues de schema.sql:
--   psql -h db-aules.uji.es -U <usuario> <bd> -f data.sql
--
-- IMPORTANTE: las contrasenas estan como '__HASH_X__'. Para generarlas:
--   1. Ejecutar es.uji.ei1027.sgovi.util.GenerarHashes
--   2. Copiar la salida y reemplazar los placeholders de abajo
--   3. NUNCA commitear los hashes reales en este fichero
-- =====================================================================

-- ---------------------------------------------------------------------
-- Tecnicos
-- ---------------------------------------------------------------------
INSERT INTO tecnico (id_tecnico, dni, nombre, apellidos, email, telefono, activo) VALUES
    ('tec_admin', '11111111A', 'Ana', 'Garcia Lopez', 'ana.garcia@ovi.test', '964111111', TRUE);

-- ---------------------------------------------------------------------
-- Usuarios OVI (uno aceptado para hacer login, otro pendiente)
-- ---------------------------------------------------------------------
INSERT INTO usuarioovi (id_usuario, dni, nombre, apellidos, email, telefono, consentimiento_lopd, estado) VALUES
('usr_carlos01', '22222222B', 'Carlos', 'Martinez Ruiz',  'carlos.martinez@test', '600222222', TRUE, 'aceptado'),
('usr_lucia02',  '33333333C', 'Lucia',  'Fernandez Gil',  'lucia.fernandez@test', '600333333', TRUE, 'pendiente');
-- ---------------------------------------------------------------------
-- Candidatos PAP/PATI (uno aceptado, otro pendiente)
-- Coordenadas: Castellon de la Plana (39.9864, -0.0513)
-- ---------------------------------------------------------------------
INSERT INTO asistentepersonal (id_ap, dni, nombre, apellidos, email, telefono, tipo_ap, formacion, experiencia, disponibilidad, latitud, longitud, consentimiento_lopd, estado, activo) VALUES
('ap_marta01', '44444444D', 'Marta', 'Lopez Sanchez', 'marta.lopez@test',  '600444444', 'PAP',  'Grado en Trabajo Social',  '3 anyos en residencias',          'Mananas L-V', 39.9864, -0.0513, TRUE, 'aceptado',  TRUE),
('ap_jorge02', '55555555E', 'Jorge', 'Ruiz Vidal',    'jorge.ruiz@test',   '600555555', 'PATI', 'Tecnico en Atencion Infantil', '1 anyo en centro educativo', 'Tardes L-V', 39.9700, -0.0400, TRUE, 'pendiente', TRUE);-- Formadores
-- ---------------------------------------------------------------------
INSERT INTO formador (id_formador, dni, nombre, apellidos, email, telefono) VALUES
    ('frm_pablo01', '66666666F', 'Pablo', 'Navarro Gomez', 'pablo.navarro@test', '600666666');
-- ---------------------------------------------------------------------
-- Solicitudes (una en revision, una aprobada)
-- ---------------------------------------------------------------------
INSERT INTO solicitud_servicio_ap (id_usuario, tipo_asistencia, estado, preferencias) VALUES
('usr_carlos01', 'PAP',  'en revision', 'Preferiblemente con experiencia en personas mayores.'),
('usr_carlos01', 'PATI', 'aprobada',    'Apoyo en tareas escolares vespertinas.');

-- ---------------------------------------------------------------------
-- Una seleccion sobre la solicitud aprobada
-- ---------------------------------------------------------------------
INSERT INTO seleccion (id_solicitud, id_ap, id_tecnico, estado) VALUES
(2, 'ap_marta01', 'tec_admin', 'propuesta');


INSERT INTO usuario (username, password, rol, activo) VALUES
    ('tec_admin',    'EIaH2EW8ahPgasBvEkjGFZhvHE/NGJIy', 'TECNICO',     TRUE),
    ('usr_carlos01', 'Rj4v6490+H09GTCAUO5ErCmRYufSwih3', 'USUARIO_OVI', TRUE),
    ('usr_lucia02',  'dXeixdvomvLSk/2BKgEWODJEunR5iHIo', 'USUARIO_OVI', FALSE),
    ('ap_marta01',   'PYzKxla7faXF5nVi4mCoQRoqpePhwxWy', 'CANDIDATO',   TRUE),
    ('ap_jorge02',   'HNmHw8/H8narWF8t1PS7qfN1tR7DOY/D', 'CANDIDATO',   FALSE),
    ('frm_pablo01',  'tFZKBlIKxhB832hhkxodb5YWpnIaGNFu', 'FORMADOR',    TRUE);
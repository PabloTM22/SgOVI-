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
INSERT INTO asistentepersonal (id_ap, dni, nombre, apellidos, email, telefono, tipo_ap, formacion, experiencia, disponibilidad, latitud, longitud, consentimiento_lopd, estado) VALUES
    ('ap_marta01', '44444444D', 'Marta', 'Lopez Sanchez', 'marta.lopez@test',  '600444444', 'PAP',  'Grado en Trabajo Social',  '3 anyos en residencias',
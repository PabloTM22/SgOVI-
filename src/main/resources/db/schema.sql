-- =====================================================================
-- SgOVI - Esquema de base de datos
-- =====================================================================
-- PostgreSQL. Aplicar con:
--   psql -h db-aules.uji.es -U <usuario> <bd> -f schema.sql
-- =====================================================================

DROP TABLE IF EXISTS asistenciaformacion CASCADE;
DROP TABLE IF EXISTS actividadformacion CASCADE;
DROP TABLE IF EXISTS registrocontrato CASCADE;
DROP TABLE IF EXISTS comunicacionusuario CASCADE;
DROP TABLE IF EXISTS seleccion CASCADE;
DROP TABLE IF EXISTS solicitud_servicio_ap CASCADE;
DROP TABLE IF EXISTS formador CASCADE;
DROP TABLE IF EXISTS asistentepersonal CASCADE;
DROP TABLE IF EXISTS usuarioovi CASCADE;
DROP TABLE IF EXISTS tecnico CASCADE;
DROP TABLE IF EXISTS usuario CASCADE;

-- ---------------------------------------------------------------------
-- Personas con cuenta de acceso
-- ---------------------------------------------------------------------

CREATE TABLE tecnico (
                         id_tecnico   VARCHAR(50)  PRIMARY KEY,
                         dni          VARCHAR(15)  UNIQUE NOT NULL,
                         nombre       VARCHAR(100) NOT NULL,
                         apellidos    VARCHAR(150) NOT NULL,
                         email        VARCHAR(150) UNIQUE NOT NULL,
                         telefono     VARCHAR(20),
                         activo       BOOLEAN      DEFAULT TRUE
);

CREATE TABLE usuarioovi (
                            id_usuario           VARCHAR(50)  PRIMARY KEY,
                            dni                  VARCHAR(15)  UNIQUE NOT NULL,
                            nombre               VARCHAR(100) NOT NULL,
                            apellidos            VARCHAR(150) NOT NULL,
                            email                VARCHAR(150) UNIQUE NOT NULL,
                            telefono             VARCHAR(20),
                            consentimiento_lopd  BOOLEAN      NOT NULL DEFAULT FALSE,
                            estado               VARCHAR(20)  NOT NULL DEFAULT 'pendiente',
                            CHECK (estado IN ('pendiente', 'aceptado', 'rechazado'))
);

CREATE TABLE asistentepersonal (
                                   id_ap                VARCHAR(50)    PRIMARY KEY,
                                   dni                  VARCHAR(15)    UNIQUE NOT NULL,
                                   nombre               VARCHAR(100)   NOT NULL,
                                   apellidos            VARCHAR(150)   NOT NULL,
                                   email                VARCHAR(150)   UNIQUE NOT NULL,
                                   telefono             VARCHAR(20),
                                   tipo_ap              VARCHAR(10)    NOT NULL,
                                   formacion            TEXT,
                                   experiencia          TEXT,
                                   disponibilidad       TEXT,
                                   latitud              DECIMAL(9,6),
                                   longitud             DECIMAL(9,6),
                                   consentimiento_lopd  BOOLEAN        NOT NULL DEFAULT FALSE,
                                   estado               VARCHAR(20)    NOT NULL DEFAULT 'pendiente',
                                   CHECK (tipo_ap IN ('PAP', 'PATI')),
                                   CHECK (estado IN ('pendiente', 'aceptado', 'rechazado'))
);
CREATE TABLE formador (
                          id_formador  VARCHAR(50)  PRIMARY KEY,
                          dni          VARCHAR(15)  UNIQUE NOT NULL,
                          nombre       VARCHAR(100) NOT NULL,
                          apellidos    VARCHAR(150) NOT NULL,
                          email        VARCHAR(150) UNIQUE NOT NULL,
                          telefono     VARCHAR(20)
);

-- ---------------------------------------------------------------------
-- Sol·licituds de servei d'AP y seleccion de candidatos
-- ---------------------------------------------------------------------

CREATE TABLE solicitud_servicio_ap (
                                       id_solicitud     SERIAL        PRIMARY KEY,
                                       id_usuario       VARCHAR(50)   NOT NULL,
                                       tipo_asistencia  VARCHAR(10)   NOT NULL,
                                       estado           VARCHAR(30)   NOT NULL,
                                       fecha_solicitud  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
                                       preferencias     TEXT,
                                       CHECK (tipo_asistencia IN ('PAP', 'PATI')),
                                       CHECK (estado IN ('en revision', 'aprobada',
                                                         'cerrada con contrato',
                                                         'cerrada con contrato finalizado', 'rechazada'))
);

ALTER TABLE solicitud_servicio_ap
    ADD CONSTRAINT solicitud_servicio_ap_id_usuario_fkey
        FOREIGN KEY (id_usuario) REFERENCES usuarioovi(id_usuario)
            ON UPDATE CASCADE ON DELETE RESTRICT;

CREATE TABLE seleccion (
                           id_seleccion     SERIAL        PRIMARY KEY,
                           id_solicitud     INT           NOT NULL,
                           id_ap            VARCHAR(50)   NOT NULL,
                           id_tecnico       VARCHAR(50),
                           estado           VARCHAR(20)   NOT NULL,
                           fecha_propuesta  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
                           CHECK (estado IN ('propuesta', 'contactada', 'aceptada', 'descartada'))
);

ALTER TABLE seleccion
    ADD CONSTRAINT seleccion_id_solicitud_fkey
        FOREIGN KEY (id_solicitud) REFERENCES solicitud_servicio_ap(id_solicitud)
            ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE seleccion
    ADD CONSTRAINT seleccion_id_ap_fkey
        FOREIGN KEY (id_ap) REFERENCES asistentepersonal(id_ap)
            ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE seleccion
    ADD CONSTRAINT seleccion_id_tecnico_fkey
        FOREIGN KEY (id_tecnico) REFERENCES tecnico(id_tecnico)
            ON UPDATE CASCADE ON DELETE SET NULL;

-- ---------------------------------------------------------------------
-- Comunicaciones del proceso de seleccion
-- ---------------------------------------------------------------------

CREATE TABLE comunicacionusuario (
                                     id_comunicacion  INT           PRIMARY KEY,
                                     id_seleccion     INT           NOT NULL,
                                     id_usuario       VARCHAR(50),
                                     id_ap            VARCHAR(50),
                                     id_tecnico       VARCHAR(50),
                                     mensaje          TEXT          NOT NULL,
                                     fe
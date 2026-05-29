

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
    contrasena   VARCHAR(255) NOT NULL,
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
    contrasena           VARCHAR(255) NOT NULL,
    aceptado_tecnico     BOOLEAN      DEFAULT FALSE
);

CREATE TABLE asistentepersonal (
    id_ap            VARCHAR(50)    PRIMARY KEY,
    dni              VARCHAR(15)    UNIQUE NOT NULL,
    nombre           VARCHAR(100)   NOT NULL,
    apellidos        VARCHAR(150)   NOT NULL,
    email            VARCHAR(150)   UNIQUE NOT NULL,
    telefono         VARCHAR(20),
    tipo_ap          VARCHAR(10)    NOT NULL,
    formacion        TEXT,
    experiencia      TEXT,
    disponibilidad   TEXT,
    latitud          DECIMAL(9,6),
    longitud         DECIMAL(9,6),
    estado_aceptado  BOOLEAN        DEFAULT FALSE,
    contrasena       VARCHAR(255)   NOT NULL,
    activo           BOOLEAN        DEFAULT TRUE,
    CHECK (tipo_ap IN ('PAP', 'PATI'))
);

CREATE TABLE formador (
    id_formador  SERIAL       PRIMARY KEY,
    dni          VARCHAR(15)  UNIQUE NOT NULL,
    nombre       VARCHAR(100) NOT NULL,
    apellidos    VARCHAR(150) NOT NULL,
    email        VARCHAR(150) UNIQUE NOT NULL,
    telefono     VARCHAR(20),
    contrasena   VARCHAR(255) NOT NULL
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
                      'cerrada con contrato', 'rechazada'))
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
    fecha_envio      TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    CHECK (
        (CASE WHEN id_usuario IS NULL THEN 0 ELSE 1 END) +
        (CASE WHEN id_ap      IS NULL THEN 0 ELSE 1 END) +
        (CASE WHEN id_tecnico IS NULL THEN 0 ELSE 1 END) = 1
    )
);

ALTER TABLE comunicacionusuario
    ADD CONSTRAINT comunicacionusuario_id_seleccion_fkey
    FOREIGN KEY (id_seleccion) REFERENCES seleccion(id_seleccion)
    ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE comunicacionusuario
    ADD CONSTRAINT comunicacionusuario_id_usuario_fkey
    FOREIGN KEY (id_usuario) REFERENCES usuarioovi(id_usuario)
    ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE comunicacionusuario
    ADD CONSTRAINT comunicacionusuario_id_ap_fkey
    FOREIGN KEY (id_ap) REFERENCES asistentepersonal(id_ap)
    ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE comunicacionusuario
    ADD CONSTRAINT comunicacionusuario_id_tecnico_fkey
    FOREIGN KEY (id_tecnico) REFERENCES tecnico(id_tecnico)
    ON UPDATE CASCADE ON DELETE RESTRICT;

-- ---------------------------------------------------------------------
-- Contratos resultado de una seleccion aceptada
-- ---------------------------------------------------------------------

CREATE TABLE registrocontrato (
    id_contrato    SERIAL        PRIMARY KEY,
    id_seleccion   INT           NOT NULL,
    id_tecnico     VARCHAR(50),
    fecha_inicio   DATE          NOT NULL,
    fecha_fin      DATE,
    pdf_ruta       VARCHAR(255)
);

ALTER TABLE registrocontrato
    ADD CONSTRAINT registrocontrato_id_seleccion_fkey
    FOREIGN KEY (id_seleccion) REFERENCES seleccion(id_seleccion)
    ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE registrocontrato
    ADD CONSTRAINT registrocontrato_id_tecnico_fkey
    FOREIGN KEY (id_tecnico) REFERENCES tecnico(id_tecnico)
    ON UPDATE CASCADE ON DELETE SET NULL;

-- ---------------------------------------------------------------------
-- Activitats de formacio i divulgacio
-- ---------------------------------------------------------------------

CREATE TABLE actividadformacion (
    id_actividad       SERIAL        PRIMARY KEY,
    id_formador        INT           NOT NULL,
    tipo_actividad     VARCHAR(20)   NOT NULL,
    titulo             VARCHAR(150)  NOT NULL,
    descripcion        TEXT,
    fecha_actividad    TIMESTAMP     NOT NULL,
    lugar              VARCHAR(150),
    plazas             INT,
    num_participantes  INT
);

ALTER TABLE actividadformacion
    ADD CONSTRAINT actividadformacion_id_formador_fkey
    FOREIGN KEY (id_formador) REFERENCES formador(id_formador)
    ON UPDATE CASCADE ON DELETE RESTRICT;

CREATE TABLE asistenciaformacion (
    id_asistencia      INT           PRIMARY KEY,
    id_actividad       INT           NOT NULL,
    id_usuario         VARCHAR(50),
    id_ap              VARCHAR(50),
    fecha_inscripcion  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    asistio            BOOLEAN       DEFAULT FALSE,
    certificado_ruta   VARCHAR(255),
    CHECK (
        (id_usuario IS NOT NULL AND id_ap IS NULL) OR
        (id_usuario IS NULL AND id_ap IS NOT NULL)
    )
);

ALTER TABLE asistenciaformacion
    ADD CONSTRAINT asistenciaformacion_id_actividad_fkey
    FOREIGN KEY (id_actividad) REFERENCES actividadformacion(id_actividad)
    ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE asistenciaformacion
    ADD CONSTRAINT asistenciaformacion_id_usuario_fkey
    FOREIGN KEY (id_usuario) REFERENCES usuarioovi(id_usuario)
    ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE asistenciaformacion
    ADD CONSTRAINT asistenciaformacion_id_ap_fkey
    FOREIGN KEY (id_ap) REFERENCES asistentepersonal(id_ap)
    ON UPDATE CASCADE ON DELETE RESTRICT;

-- ---------------------------------------------------------------------
-- Tabla centralizada de credenciales
-- (coexiste de momento con contrasena en las tablas de dominio;
-- los pasos siguientes del refactor eliminaran la duplicacion)
-- ---------------------------------------------------------------------

CREATE TABLE usuario (
    username   VARCHAR(50)  PRIMARY KEY,
    password   VARCHAR(255) NOT NULL,
    rol        VARCHAR(20)  NOT NULL,
    activo     BOOLEAN      DEFAULT TRUE,
    CHECK (rol IN ('TECNICO', 'USUARIO_OVI', 'CANDIDATO', 'FORMADOR'))
);


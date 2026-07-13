--liquibase formatted sql

--changeset team:1
CREATE TABLE usuario (
    id_usuario  BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    correo      VARCHAR(100) NOT NULL UNIQUE,
    contraseña  VARCHAR(255) NOT NULL,
    rol         VARCHAR(20)  NOT NULL,
    activo      BOOLEAN      DEFAULT TRUE
);

--changeset team:2
INSERT INTO usuario (nombre, correo, contraseña, rol, activo) VALUES
('Juan Pérez',      'juan.perez@foodtruckduocuc.cl',      'eF7!uB2qK',    'Cliente',        true),
('María García',    'maria.garcia@foodtruckduocuc.cl',    'P@ssCoffee24', 'Cliente',        true),
('Carlos López',    'carlos.lopez@foodtruckduocuc.cl',    'Adm1n$2026',   'Administrador',  true),
('Ana Martínez',    'ana.martinez@foodtruckduocuc.cl',    'OpEr4t0r#89',  'Operador',       true),
('Luis Fernández',  'luis.fernandez@foodtruckduocuc.cl',  'Lunch*Box91',  'Cliente',        true),
('Rosa Sánchez',    'rosa.sanchez@foodtruckduocuc.cl',    'R0saS3gura',   'Cliente',        true),
('Pedro Díaz',      'pedro.diaz@foodtruckduocuc.cl',      'Deliv3ry!7',   'Operador',       true),
('Isabel Moreno',   'isabel.moreno@foodtruckduocuc.cl',   'C00kTaste#1',  'Cliente',        true),
('Francisco Ruiz',  'francisco.ruiz@foodtruckduocuc.cl',  'Fr@nco2026',   'Administrador',  true),
('Gabriela Torres', 'gabriela.torres@foodtruckduocuc.cl', 'GabiT0rres$55','Cliente',        true);
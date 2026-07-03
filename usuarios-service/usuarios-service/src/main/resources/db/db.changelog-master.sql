--liquibase formatted sql

--changeset team:1
CREATE TABLE usuario (
    id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    contraseña VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

--changeset team:2
-- Ejemplo: esta fila representa un usuario real. 'Juan Pérez' es un cliente con correo empresarial y contraseña única.

INSERT INTO usuario (nombre, correo, contraseña, rol) VALUES
('Juan Pérez', 'juan.perez@foodtruckduocuc.cl', 'eF7!uB2qK', 'Cliente'),
('María García', 'maria.garcia@foodtruckduocuc.cl', 'P@ssCoffee24', 'Cliente'),
('Carlos López', 'carlos.lopez@foodtruckduocuc.cl', 'Adm1n$2026', 'Administrador'),
('Ana Martínez', 'ana.martinez@foodtruckduocuc.cl', 'OpEr4t0r#89', 'Operador'),
('Luis Fernández', 'luis.fernandez@foodtruckduocuc.cl', 'Lunch*Box91', 'Cliente'),
('Rosa Sánchez', 'rosa.sanchez@foodtruckduocuc.cl', 'R0saS3gura', 'Cliente'),
('Pedro Díaz', 'pedro.diaz@foodtruckduocuc.cl', 'Deliv3ry!7', 'Operador'),
('Isabel Moreno', 'isabel.moreno@foodtruckduocuc.cl', 'C00kTaste#1', 'Cliente'),
('Francisco Ruiz', 'francisco.ruiz@foodtruckduocuc.cl', 'Fr@nco2026', 'Administrador'),
('Gabriela Torres', 'gabriela.torres@foodtruckduocuc.cl', 'GabiT0rres$55', 'Cliente');

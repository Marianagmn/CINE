-- Script para crear la base de datos del sistema de cine
-- Para ejecutar en phpMyAdmin (XAMPP) o desde la línea de comandos MySQL

-- Crear la base de datos si no existe
CREATE DATABASE IF NOT EXISTS cine;

-- Usar la base de datos
USE cine;

-- Crear la tabla de tickets
CREATE TABLE IF NOT EXISTS tickets (
    id INT PRIMARY KEY AUTO_INCREMENT,
    movie VARCHAR(100),
    time VARCHAR(20),
    theater VARCHAR(100),
    city VARCHAR(50),
    seat VARCHAR(10),
    combo VARCHAR(100),
    total INT,
    status VARCHAR(20),  -- 'reserved' o 'purchased'
    reservation_time DATETIME,
    INDEX idx_ticket_unique (movie, time, seat, status)
);
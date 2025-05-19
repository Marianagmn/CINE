--  Script to create the database for the cinema system
-- Designed to run in phpMyAdmin (XAMPP) or from the MySQL command line


-- Create the database only if it doesn't already exist
CREATE DATABASE IF NOT EXISTS cine;

-- Select the 'cine' database to work with
USE cine;

-- Create the 'tickets' table to store ticket booking and purchase info
CREATE TABLE IF NOT EXISTS tickets (
    id INT PRIMARY KEY AUTO_INCREMENT,    -- Unique identifier for each ticket
    movie VARCHAR(100),   -- Movie name
    time VARCHAR(20),     -- Time of the showing
    theater VARCHAR(100),   -- Theater name
    city VARCHAR(50),   -- City where the movie is shown
    seat VARCHAR(10),   -- Seat identifier
    combo VARCHAR(100),   -- Snacks or combos selected
    total INT,   -- Total cost
    status VARCHAR(20),  -- 'reserved' o 'purchased'
    reservation_time DATETIME,   -- When the ticket was reserved
    INDEX idx_ticket_unique (movie, time, seat, status)
);
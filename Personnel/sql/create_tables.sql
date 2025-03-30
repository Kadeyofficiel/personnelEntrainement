-- Script de création de la base de données personnel_db

-- Créer la base de données si elle n'existe pas
CREATE DATABASE IF NOT EXISTS personnel_db;
USE personnel_db;

-- Table des ligues
CREATE TABLE IF NOT EXISTS ligue (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL
);

-- Table des employés
CREATE TABLE IF NOT EXISTS employe (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    mail VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    dateArrive DATE NULL,
    dateDepart DATE NULL,
    admin BOOLEAN DEFAULT FALSE,
    ligue_id INT NULL,
    FOREIGN KEY (ligue_id) REFERENCES ligue(id) ON DELETE CASCADE
); 
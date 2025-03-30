-- Script pour ajouter les colonnes manquantes à la table employe
USE personnel_db;

-- Ajouter les colonnes de date si elles n'existent pas
ALTER TABLE employe ADD COLUMN IF NOT EXISTS dateArrive DATE NULL;
ALTER TABLE employe ADD COLUMN IF NOT EXISTS dateDepart DATE NULL; 
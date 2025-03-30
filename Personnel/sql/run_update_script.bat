@echo off
echo Mise à jour de la structure de la base de données...
mysql -u root < update_tables.sql
echo Terminé.
pause 
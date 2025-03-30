package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * INSTRUCTIONS: 
 * 1. Ouvrez ce fichier dans Eclipse ou IntelliJ
 * 2. Cliquez avec le bouton droit sur ce fichier dans l'IDE
 * 3. Sélectionnez "Run As > Java Application"
 * 
 * Ce programme va ajouter les colonnes manquantes à votre base de données.
 */
public class RunMeToFixDatabase {
    
    public static void main(String[] args) {
        try {
            System.out.println("Début de la mise à jour de la base de données...");
            
            // Charger le pilote JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Établir la connexion
            Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/personnel_db", 
                "root", 
                ""
            );
            
            System.out.println("Connexion à la base de données établie.");
            
            // Créer un objet Statement
            Statement statement = connection.createStatement();
            
            // Ajouter les colonnes de date si elles n'existent pas
            try {
                statement.execute("ALTER TABLE employe ADD COLUMN dateArrive DATE NULL");
                System.out.println("Colonne dateArrive ajoutée avec succès.");
            } catch (Exception e) {
                System.out.println("La colonne dateArrive existe déjà ou erreur: " + e.getMessage());
            }
            
            try {
                statement.execute("ALTER TABLE employe ADD COLUMN dateDepart DATE NULL");
                System.out.println("Colonne dateDepart ajoutée avec succès.");
            } catch (Exception e) {
                System.out.println("La colonne dateDepart existe déjà ou erreur: " + e.getMessage());
            }
            
            try {
                statement.execute("ALTER TABLE employe ADD COLUMN admin BOOLEAN DEFAULT FALSE");
                System.out.println("Colonne admin ajoutée avec succès.");
            } catch (Exception e) {
                System.out.println("La colonne admin existe déjà ou erreur: " + e.getMessage());
            }
            
            System.out.println("Mise à jour de la base de données terminée.");
            
            // Fermer la connexion
            connection.close();
            
            System.out.println("Vous pouvez maintenant fermer cette fenêtre et relancer votre application.");
            
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 
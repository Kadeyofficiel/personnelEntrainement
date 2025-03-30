package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Classe utilitaire pour mettre à jour la structure de la base de données
 */
public class UpdateDatabase {
    
    public static void main(String[] args) {
        try {
            // Charger le pilote JDBC
            Class.forName(Credentials.getDriverClassName());
            
            // Établir la connexion
            Connection connection = DriverManager.getConnection(
                Credentials.getUrl(), 
                Credentials.getUser(), 
                Credentials.getPassword()
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
            
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 
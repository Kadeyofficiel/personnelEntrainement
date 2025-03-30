package jdbc;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.SortedSet;

import personnel.*;

public class JDBC implements Passerelle 
{
	Connection connection;

	public JDBC()
	{
		try
		{
			Class.forName(Credentials.getDriverClassName());
			connection = DriverManager.getConnection(Credentials.getUrl(), Credentials.getUser(), Credentials.getPassword());
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("Pilote JDBC non installé.");
		}
		catch (SQLException e)
		{
			System.out.println(e);
		}
	}
	
	@Override
	public GestionPersonnel getGestionPersonnel() 
	{
		GestionPersonnel gestionPersonnel = new GestionPersonnel();
		try 
		{
			// Récupération du root
			PreparedStatement instructionRoot;
			instructionRoot = connection.prepareStatement("SELECT id, nom, prenom, mail, password FROM employe WHERE ligue_id IS NULL LIMIT 1");
			ResultSet resultRoot = instructionRoot.executeQuery();
			
			if (resultRoot.next()) 
			{
				int idRoot = resultRoot.getInt("id");
				String nomRoot = resultRoot.getString("nom");
				String prenomRoot = resultRoot.getString("prenom");
				String mailRoot = resultRoot.getString("mail");
				String passwordRoot = resultRoot.getString("password");
				
				gestionPersonnel.addRoot(idRoot, nomRoot, prenomRoot, mailRoot, passwordRoot, null, null);
			}
			
			// Récupération des ligues
			String requeteLigue = "SELECT * FROM ligue";
			Statement instructionLigue = connection.createStatement();
			ResultSet ligues = instructionLigue.executeQuery(requeteLigue);
			
			while (ligues.next())
			{
				int idLigue = ligues.getInt("id");
				String nomLigue = ligues.getString("nom");
				Ligue ligue = gestionPersonnel.addLigue(idLigue, nomLigue);
			}
			
			// Récupération de toutes les ligues créées
			SortedSet<Ligue> toutesLigues = gestionPersonnel.getLigues();
			
			// Récupération des employés
			String requeteEmployes = "SELECT * FROM employe WHERE ligue_id IS NOT NULL";
			Statement instructionEmployes = connection.createStatement();
			ResultSet employes = instructionEmployes.executeQuery(requeteEmployes);
			
			while (employes.next())
			{
				int idEmploye = employes.getInt("id");
				String nomEmploye = employes.getString("nom");
				String prenomEmploye = employes.getString("prenom");
				String mailEmploye = employes.getString("mail");
				String passwordEmploye = employes.getString("password");
				LocalDate dateArrive = null;
				LocalDate dateDepart = null;
				
				try {
					if (employes.getDate("dateArrive") != null)
						dateArrive = employes.getDate("dateArrive").toLocalDate();
					if (employes.getDate("dateDepart") != null)
						dateDepart = employes.getDate("dateDepart").toLocalDate();
				} catch (SQLException e) {
					// Ignorer si les colonnes n'existent pas
				}
				
				int ligueId = employes.getInt("ligue_id");
				boolean estAdmin = false;
				
				try {
					estAdmin = employes.getBoolean("admin");
				} catch (SQLException e) {
					// Ignorer si la colonne n'existe pas
				}
				
				// Trouver la ligue correspondante
				for (Ligue ligue : toutesLigues)
				{
					if (ligue.getId() == ligueId)
					{
						try {
							Employe employe = ligue.addEmploye(idEmploye, nomEmploye, prenomEmploye, mailEmploye, passwordEmploye, dateArrive, dateDepart);
							
							// Si c'est un admin, définir comme administrateur
							if (estAdmin) {
								ligue.setAdministrateur(employe);
							}
						} catch (ExceptionDate e) {
							System.err.println("Date invalide pour l'employé " + nomEmploye + " " + prenomEmploye);
						}
						break;
					}
				}
			}
		}
		catch (SQLException e)
		{
			System.out.println(e);
		}
		catch (SauvegardeImpossible e)
		{
			System.out.println(e);
		}
		return gestionPersonnel;
	}

	@Override
	public void sauvegarderGestionPersonnel(GestionPersonnel gestionPersonnel) throws SauvegardeImpossible 
	{
		// La sauvegarde est déjà gérée par les méthodes insert, update et delete
		// Pas besoin de fermer la connexion ici car elle est réutilisée
	}
	
	public void close() throws SauvegardeImpossible
	{
		try
		{
			if (connection != null)
				connection.close();
		}
		catch (SQLException e)
		{
			throw new SauvegardeImpossible(e);
		}
	}
	
	@Override
	public int insert(Employe employe) throws SauvegardeImpossible
	{
		try 
		{
			// Vérifier si c'est le root
			boolean estRoot = employe.estRoot();
			
			String sql;
			PreparedStatement instruction;
			
			if (estRoot) {
				// Insertion du root (cas spécial)
				sql = "INSERT INTO employe (nom, prenom, mail, password) VALUES (?, ?, ?, ?)";
				instruction = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				instruction.setString(1, employe.getNom());
				instruction.setString(2, employe.getPrenom());
				instruction.setString(3, employe.getMail());
				instruction.setString(4, employe.getPassword());
			} else {
				// Insertion d'un employé normal
				sql = "INSERT INTO employe (nom, prenom, mail, password, dateArrive, dateDepart, admin, ligue_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
				instruction = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				instruction.setString(1, employe.getNom());
				instruction.setString(2, employe.getPrenom());
				instruction.setString(3, employe.getMail());
				instruction.setString(4, employe.getPassword());
				
				// Gestion des dates
				if (employe.getDateArrive() != null)
					instruction.setDate(5, java.sql.Date.valueOf(employe.getDateArrive()));
				else
					instruction.setNull(5, java.sql.Types.DATE);
					
				if (employe.getDateDepart() != null)
					instruction.setDate(6, java.sql.Date.valueOf(employe.getDateDepart()));
				else
					instruction.setNull(6, java.sql.Types.DATE);
				
				// Admin et ligue_id
				instruction.setBoolean(7, employe.getLigue() != null && employe.getLigue().getAdministrateur() == employe);
				
				if (employe.getLigue() != null)
					instruction.setInt(8, employe.getLigue().getId());
				else
					instruction.setNull(8, java.sql.Types.INTEGER);
			}
			
			instruction.executeUpdate();
			ResultSet id = instruction.getGeneratedKeys();
			id.next();
			return id.getInt(1);
		} 
		catch (SQLException exception) 
		{
			exception.printStackTrace();
			throw new SauvegardeImpossible(exception);
		}
	}
	
	@Override
	public void update(Employe employe) throws SauvegardeImpossible
	{
		try 
		{
			PreparedStatement instruction;
			instruction = connection.prepareStatement(
				"UPDATE employe SET nom = ?, prenom = ?, mail = ?, password = ?, dateArrive = ?, dateDepart = ?, admin = ? WHERE id = ?");
				
			instruction.setString(1, employe.getNom());
			instruction.setString(2, employe.getPrenom());
			instruction.setString(3, employe.getMail());
			instruction.setString(4, employe.getPassword());
			
			// Gestion des dates
			if (employe.getDateArrive() != null)
				instruction.setDate(5, java.sql.Date.valueOf(employe.getDateArrive()));
			else
				instruction.setNull(5, java.sql.Types.DATE);
				
			if (employe.getDateDepart() != null)
				instruction.setDate(6, java.sql.Date.valueOf(employe.getDateDepart()));
			else
				instruction.setNull(6, java.sql.Types.DATE);
			
			// Admin status
			boolean estAdmin = employe.getLigue() != null && employe.getLigue().getAdministrateur() == employe;
			instruction.setBoolean(7, estAdmin);
			
			// ID de l'employé
			instruction.setInt(8, employe.getId());
			
			instruction.executeUpdate();
		} 
		catch (SQLException exception) 
		{
			exception.printStackTrace();
			throw new SauvegardeImpossible(exception);
		}
	}
	
	@Override
	public void delete(Employe employe) throws SauvegardeImpossible
	{
		try 
		{
			PreparedStatement instruction;
			instruction = connection.prepareStatement("DELETE FROM employe WHERE id = ?");
			instruction.setInt(1, employe.getId());
			instruction.executeUpdate();
		} 
		catch (SQLException exception) 
		{
			exception.printStackTrace();
			throw new SauvegardeImpossible(exception);
		}
	}
	
	@Override
	public void delete(Ligue ligue) throws SauvegardeImpossible
	{
		try 
		{
			// D'abord supprimer les employés de la ligue
			PreparedStatement instructionEmployes;
			instructionEmployes = connection.prepareStatement("DELETE FROM employe WHERE ligue_id = ?");
			instructionEmployes.setInt(1, ligue.getId());
			instructionEmployes.executeUpdate();
			
			// Puis supprimer la ligue
			PreparedStatement instructionLigue;
			instructionLigue = connection.prepareStatement("DELETE FROM ligue WHERE id = ?");
			instructionLigue.setInt(1, ligue.getId());
			instructionLigue.executeUpdate();
		} 
		catch (SQLException exception) 
		{
			exception.printStackTrace();
			throw new SauvegardeImpossible(exception);
		}
	}
	
	@Override
	public void update(Ligue ligue) throws SauvegardeImpossible
	{
		try 
		{
			PreparedStatement instruction;
			instruction = connection.prepareStatement("UPDATE ligue SET nom = ? WHERE id = ?");
			instruction.setString(1, ligue.getNom());
			instruction.setInt(2, ligue.getId());
			instruction.executeUpdate();
		} 
		catch (SQLException exception) 
		{
			exception.printStackTrace();
			throw new SauvegardeImpossible(exception);
		}
	}
	
	@Override
	public int insert(Ligue ligue) throws SauvegardeImpossible
	{
		try 
		{
			PreparedStatement instruction;
			instruction = connection.prepareStatement("INSERT INTO ligue (nom) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
			instruction.setString(1, ligue.getNom());		
			instruction.executeUpdate();
			ResultSet id = instruction.getGeneratedKeys();
			id.next();
			return id.getInt(1);
		} 
		catch (SQLException exception) 
		{
			exception.printStackTrace();
			throw new SauvegardeImpossible(exception);
		}
	}
}

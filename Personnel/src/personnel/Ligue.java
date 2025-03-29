package personnel;

import java.io.Serializable;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.time.LocalDate;

/**
 * Représente une ligue. Chaque ligue est reliée à une liste
 * d'employés dont un administrateur. Comme il n'est pas possible
 * de créer un employé sans l'affecter à une ligue, le root est 
 * l'administrateur de la ligue jusqu'à ce qu'un administrateur 
 * lui ait été affecté avec la fonction {@link #setAdministrateur}.
 */

public class Ligue implements Serializable, Comparable<Ligue>
{
	// Pour la sérialisation
	private static final long serialVersionUID = 1L;
	
	// Identifiant unique dans la base de données
	private int id = -1;
	
	// Nom de la ligue
	private String nom;
	
	// Collection triée des employés de cette ligue
	private SortedSet<Employe> employes;
	
	// L'administrateur de cette ligue (un employé avec des droits spéciaux)
	private Employe administrateur;
	
	// Référence à l'objet principal de gestion
	private GestionPersonnel gestionPersonnel;
	
	/**
	 * Crée une ligue et l'insère dans la base de données.
	 * @param nom le nom de la ligue.
	 */
	
	Ligue(GestionPersonnel gestionPersonnel, String nom) throws SauvegardeImpossible
	{
		this(gestionPersonnel, -1, nom);
		this.id = gestionPersonnel.insert(this); 
	}

	/**
	 * Constructeur pour créer une ligue avec un ID déjà existant 
	 * (utilisé lors du chargement depuis la BD)
	 */
	Ligue(GestionPersonnel gestionPersonnel, int id, String nom)
	{
		this.nom = nom;
		employes = new TreeSet<>();
		this.gestionPersonnel = gestionPersonnel;
		administrateur = gestionPersonnel.getRoot();
		this.id = id;
	}

	/**
	 * Retourne le nom de la ligue.
	 * @return le nom de la ligue.
	 */

	public String getNom()
	{
		return nom;
	}

	/**
	 * Retourne l'id de la ligue.
	 * @return l'id de la ligue.
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * Change le nom.
	 * @param nom le nouveau nom de la ligue.
	 */

	public void setNom(String nom) throws SauvegardeImpossible
	{
		this.nom = nom;
		gestionPersonnel.update(this);
	}

	/**
	 * Retourne l'administrateur de la ligue.
	 * @return l'administrateur de la ligue.
	 */
	
	public Employe getAdministrateur()
	{
		return administrateur;
	}

	/**
	 * Fait de administrateur l'administrateur de la ligue.
	 * Lève DroitsInsuffisants si l'administrateur n'est pas 
	 * un employé de la ligue ou le root. Révoque les droits de l'ancien 
	 * administrateur.
	 * @param administrateur le nouvel administrateur de la ligue.
	 */
	
	public void setAdministrateur(Employe administrateur) throws SauvegardeImpossible
	{
		Employe root = gestionPersonnel.getRoot();
		if (administrateur != root && administrateur.getLigue() != this)
			throw new DroitsInsuffisants();
		this.administrateur = administrateur;
		gestionPersonnel.update(this);
	}

	/**
	 * Retourne les employés de la ligue.
	 * @return les employés de la ligue dans l'ordre alphabétique.
	 */
	
	public SortedSet<Employe> getEmployes()
	{
		return Collections.unmodifiableSortedSet(employes);
	}

	/**
	 * Ajoute un employé dans la ligue. Cette méthode 
	 * est le seul moyen de créer un employé.
	 * @param nom le nom de l'employé.
	 * @param prenom le prénom de l'employé.
	 * @param mail l'adresse mail de l'employé.
	 * @param password le password de l'employé.
	 * @param dateArrive la date d'arrivée de l'employé.
	 * @param dateDepart la date de départ de l'employé.
	 * @return l'employé créé. 
	 */

	public Employe addEmploye(String nom, String prenom, String mail, String password, LocalDate dateArrive, LocalDate dateDepart) throws SauvegardeImpossible, ExceptionDate
	{
		if (dateDepart != null && dateDepart.isBefore(dateArrive)) {
			throw new ExceptionDate();
		}
		Employe employe = new Employe(this.gestionPersonnel, this, nom, prenom, mail, password, dateArrive, dateDepart);
		employes.add(employe);
		gestionPersonnel.update(this);
		return employe;
	}
	
	/**
	 * Version qui permet de créer un employé avec un ID existant
	 * (utilisé lors du chargement depuis la BD)
	 */
	public Employe addEmploye(int id, String nom, String prenom, String mail, String password, LocalDate dateArrive, LocalDate dateDepart) throws ExceptionDate
	{
		if (dateDepart != null && dateDepart.isBefore(dateArrive)) {
			throw new ExceptionDate();
		}
		Employe employe = new Employe(this.gestionPersonnel, this, id, nom, prenom, mail, password, dateArrive, dateDepart);
		employes.add(employe);
		return employe;
	}
	
	/**
	 * Version supplémentaire qui permet de spécifier si l'employé est administrateur
	 */
	public Employe addEmploye(String nom, String prenom, String mail, String password, LocalDate dateArrive, LocalDate dateDepart, boolean estAdmin, int id) throws ExceptionDate
	{
		if (dateDepart != null && dateDepart.isBefore(dateArrive)) {
			throw new ExceptionDate("La date de départ ne peut pas être antérieure à la date d'arrivée");
		}
		Employe employe = new Employe(this.gestionPersonnel, this, id, nom, prenom, mail, password, dateArrive, dateDepart);
		employes.add(employe);
		
		if (estAdmin) {
			try {
				setAdministrateur(employe);
			} catch (SauvegardeImpossible e) {
				System.err.println("Impossible de définir l'administrateur: " + e.getMessage());
			}
		}
		
		return employe;
	}
	
	/**
	 * Supprime un employé de la ligue et de la base de données
	 */
	void remove(Employe employe) throws SauvegardeImpossible
	{
		employes.remove(employe);
		gestionPersonnel.delete(employe);
		gestionPersonnel.update(this);
	}
	
	/**
	 * Supprime la ligue, entraîne la suppression de tous les employés
	 * de la ligue.
	 */
	
	public void remove() throws SauvegardeImpossible
	{
		SortedSet<Employe> employesACopie = new TreeSet<>(employes);
		
		for (Employe employe : employesACopie)
		{
			if (!employe.estRoot()) {
				try {
					employe.remove();
				} catch (Exception e) {
					System.err.println("Erreur lors de la suppression de l'employé: " + e.getMessage());
				}
			}
		}
		
		gestionPersonnel.remove(this);
	}
	
	/**
	 * Permet de comparer des ligues entre elles (tri alphabétique)
	 */
	@Override
	public int compareTo(Ligue autre)
	{
		return getNom().compareTo(autre.getNom());
	}
	
	/**
	 * Représentation textuelle d'une ligue (son nom)
	 */
	@Override
	public String toString()
	{
		return nom;
	}
}

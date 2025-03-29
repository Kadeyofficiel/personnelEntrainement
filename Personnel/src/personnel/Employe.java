package personnel;


import java.time.LocalDate;
import java.io.Serializable;

public class Employe implements Serializable, Comparable<Employe>
{
	private static final long serialVersionUID = 4795721718037994734L;
	
	private String nom, prenom, password, mail;
	
	private Ligue ligue;
	
	private GestionPersonnel gestionPersonnel;
	
	private LocalDate dateArrive;
	private LocalDate dateDepart;
	
	private int id = -1;
	
	Employe(GestionPersonnel gestionPersonnel, Ligue ligue, String nom, String prenom, String mail, String password, LocalDate dateArrive, LocalDate dateDepart)
	{
		this.gestionPersonnel = gestionPersonnel;
		this.nom = nom;
		this.prenom = prenom;
		this.password = password;
		this.mail = mail;
		this.ligue = ligue;
		this.dateArrive = dateArrive;
		this.dateDepart = dateDepart;
		
		if (this.estRoot() || ligue == null)
			return;
		
		try {
			this.id = gestionPersonnel.insert(this);
		}
		catch (SauvegardeImpossible e) {
			System.err.println("Erreur lors de l'insertion de l'employé: " + e.getMessage());
		}
	}
	
	Employe(GestionPersonnel gestionPersonnel, Ligue ligue, int id, String nom, String prenom, String mail, String password, LocalDate dateArrive, LocalDate dateDepart)
	{
		this.gestionPersonnel = gestionPersonnel;
		this.ligue = ligue;
		this.id = id;
		this.nom = nom;
		this.prenom = prenom;
		this.mail = mail;
		this.password = password;
		this.dateArrive = dateArrive;
		this.dateDepart = dateDepart;
	}
	
	public boolean estAdmin(Ligue ligue)
	{
		return ligue.getAdministrateur() == this;
	}
	
	public boolean estRoot()
	{
		return gestionPersonnel.getRoot() == this;
	}
	
	public String getNom()
	{
		return nom;
	}

	public void setNom(String nom) throws SauvegardeImpossible
	{
		this.nom = nom;
		gestionPersonnel.update(this);
	}

	public String getPrenom()
	{
		return prenom;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public void setPrenom(String prenom) throws SauvegardeImpossible
	{
		this.prenom = prenom;
		gestionPersonnel.update(this);
	}
	
	public String getMail()
	{
		return mail;
	}
	
	public void setMail(String mail) throws SauvegardeImpossible
	{
		this.mail = mail;
		gestionPersonnel.update(this);
	}

	public boolean checkPassword(String password)
	{
		return this.password.equals(password);
	}

	public void setPassword(String password) throws SauvegardeImpossible
	{
		this.password = password;
		gestionPersonnel.update(this);
	}

	public Ligue getLigue()
	{
		return ligue;
	}

	public LocalDate getDateArrive()
	{
		return dateArrive;
	}
	
	public LocalDate getDateArrivee()
	{
		return dateArrive;
	}
	
	public LocalDate getDateDepart()
	{
		return dateDepart;
	}
	
	public void setDateArrive(LocalDate dateArrive) throws SauvegardeImpossible
	{
		this.dateArrive = dateArrive;
		gestionPersonnel.update(this);
	}
	
	public void setDateDepart(LocalDate dateDepart) throws SauvegardeImpossible {
	    if (dateDepart != null && dateDepart.isBefore(this.dateArrive)) {
	        throw new IllegalArgumentException("La date de départ doit être après ou égale à la date d'arrivée.");
	    }
	    this.dateDepart = dateDepart;
	    gestionPersonnel.update(this);
	}

	public int getId()
	{
		return id;
	}
	
	public int getID()
	{
		return id;
	}
	
	public boolean getAdmin()
	{
		return ligue != null && ligue.getAdministrateur() == this;
	}
	
	void setId(int id)
	{
		this.id = id;
	}

	public void remove() throws SauvegardeImpossible
	{
		Employe root = gestionPersonnel.getRoot();
		if (this != root)
		{
			if (estAdmin(getLigue()))
				getLigue().setAdministrateur(root);
			getLigue().remove(this);
		}
		else
			throw new ImpossibleDeSupprimerRoot();
	}

	@Override
	public int compareTo(Employe autre)
	{
		int cmp = getNom().compareTo(autre.getNom());
		if (cmp != 0)
			return cmp;
		return getPrenom().compareTo(autre.getPrenom());
	}
	
	@Override
	public String toString()
	{
		String res = nom + " " + prenom + " " + mail + " (";
		if (estRoot())
			res += "super-utilisateur";
		else
			res += ligue.toString();
		return res + ")";
	}
}

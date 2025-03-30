package personnel;

import java.io.Serializable;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;											
import java.time.LocalDate;

public class GestionPersonnel implements Serializable
{
	private static final long serialVersionUID = -105283113987886425L;
	
	private static GestionPersonnel gestionPersonnel = null;
	
	private SortedSet<Ligue> ligues;
	
	private Employe root;
	
	public final static int SERIALIZATION = 1, JDBC = 2, 
			TYPE_PASSERELLE = JDBC;  
	
	private static Passerelle passerelle = TYPE_PASSERELLE == JDBC ? new jdbc.JDBC() : new serialisation.Serialization();	
	
	public static GestionPersonnel getGestionPersonnel()
	{
		if (gestionPersonnel == null)
		{
			gestionPersonnel = passerelle.getGestionPersonnel();
			if (gestionPersonnel == null)
				gestionPersonnel = new GestionPersonnel();
		}
		return gestionPersonnel;
	}

	public GestionPersonnel()
	{
		if (gestionPersonnel != null)
			throw new RuntimeException("Vous ne pouvez créer qu'une seuls instance de cet objet.");
		ligues = new TreeSet<>();
		gestionPersonnel = this;
	}
	
	public void sauvegarder() throws SauvegardeImpossible
	{
		passerelle.sauvegarderGestionPersonnel(this);
	}
	
	public Ligue getLigue(Employe administrateur)
	{
		if (administrateur.estAdmin(administrateur.getLigue()))
			return administrateur.getLigue();
		else
			return null;
	}

	public SortedSet<Ligue> getLigues()
	{
		return Collections.unmodifiableSortedSet(ligues);
	}

	public Ligue addLigue(String nom) throws SauvegardeImpossible
	{
		Ligue ligue = new Ligue(this, nom); 
		ligues.add(ligue);
		return ligue;
	}
	
	public Ligue addLigue(int id, String nom)
	{
		Ligue ligue = new Ligue(this, id, nom);
		ligues.add(ligue);
		return ligue;
	}

	void remove(Ligue ligue)
	{
		ligues.remove(ligue);
		try {
			delete(ligue);
		}
		catch (SauvegardeImpossible e) {
			System.err.println(e.getMessage());
		}
	}
	
	int insert(Ligue ligue) throws SauvegardeImpossible
	{
		return passerelle.insert(ligue);
	}
	
	int insert(Employe employe) throws SauvegardeImpossible
	{
		return passerelle.insert(employe);
	}

	public Employe getRoot()
	{
		return root;
	}
	
	public Employe addRoot(int id, String nom, String prenom, String mail, String password, LocalDate dateArrive, LocalDate dateDepart) throws SauvegardeImpossible
	{
		root = new Employe(this, null, id, nom, prenom, mail, password, dateArrive, dateDepart);
		return root;
	}
	
	public Employe addSimpleRoot(int id, String nom, String password) throws SauvegardeImpossible
	{
		return addRoot(id, nom, "", "", password, null, null);
	}

	public Employe addRoot(GestionPersonnel gestionPersonnel, String nom, String password, int id) throws SauvegardeImpossible
	{
		return addRoot(id, nom, "", "", password, null, null);
	}

	public void update(Ligue ligue) throws SauvegardeImpossible
	{
		if (passerelle != null)
			passerelle.update(ligue);
	}
	
	public void update(Employe employe) throws SauvegardeImpossible
	{
		if (passerelle != null)
			passerelle.update(employe);
	}
	
	public void delete(Employe employe) throws SauvegardeImpossible
	{
		if (passerelle != null)
			passerelle.delete(employe);
	}
	
	public void delete(Ligue ligue) throws SauvegardeImpossible
	{
		if (passerelle != null)
			passerelle.delete(ligue);
	}
}

package serialisation;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import personnel.GestionPersonnel;
import personnel.Ligue;
import personnel.SauvegardeImpossible;
import personnel.Employe;

public class Serialization implements personnel.Passerelle
{
	private static final String FILE_NAME = "GestionPersonnel.srz";

	@Override
	public GestionPersonnel getGestionPersonnel()
	{
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME)))
		{
			return (GestionPersonnel) ois.readObject();
		}
		catch (IOException | ClassNotFoundException e)
		{
			// Si le fichier n'existe pas ou ne peut pas être lu, on crée un GestionPersonnel avec un root par défaut
			GestionPersonnel gestionPersonnel = new GestionPersonnel();
			try {
				// Créer un root avec l'ID -1 (valeur temporaire)
				gestionPersonnel.addRoot(-1, "root", "", "", "toor", null, null);
			} catch (SauvegardeImpossible ex) {
				System.err.println(ex.getMessage());
			}
			return gestionPersonnel;
		}
	}
	
	/**
	 * Sauvegarde le gestionnaire pour qu'il soit ouvert automatiquement 
	 * lors d'une exécution ultérieure du programme.
	 * @throws SauvegardeImpossible Si le support de sauvegarde est inaccessible.
	 */
	@Override
	public void sauvegarderGestionPersonnel(GestionPersonnel gestionPersonnel) throws SauvegardeImpossible
	{
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME)))
		{
			oos.writeObject(gestionPersonnel);
		}
		catch (IOException e)
		{
			throw new SauvegardeImpossible(e);
		}
	}
	
	@Override
	public int insert(Ligue ligue) throws SauvegardeImpossible
	{
		return -1;
	}
	
	@Override
	public int insert(Employe employe) throws SauvegardeImpossible
	{
		return -1;
	}
	
	@Override
	public void update(Employe employe) throws SauvegardeImpossible
	{
		
	}
	
	@Override
	public void delete(Employe employe) throws SauvegardeImpossible
	{
		
	}
	
	@Override
	public void delete(Ligue ligue) throws SauvegardeImpossible
	{
		
	}
	
	@Override
	public void update(Ligue ligue) throws SauvegardeImpossible
	{
		
	}
}
																													
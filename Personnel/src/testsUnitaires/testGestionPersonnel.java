package testsUnitaires;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import personnel.*;

class testGestionPersonnel 
{
	GestionPersonnel gestionPersonnel = GestionPersonnel.getGestionPersonnel();
	//TEST UNITAIRE DE GESTION PERSONNEL
	@Test
	void addLigue() throws SauvegardeImpossible
	{
		Ligue ligue = gestionPersonnel.addLigue("Flechettes");
		assertEquals("Flechettes", ligue.getNom());
	}

	@Test
	void addEmploye() throws SauvegardeImpossible, ExceptionDate
	{
		Ligue ligue = gestionPersonnel.addLigue("Fléchettes");
		Employe employe = ligue.addEmploye("Bouchard", "Gérard", "g.bouchard@gmail.com", "azerty", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 30)); 
		assertEquals(employe, ligue.getEmployes().first());
	}
}
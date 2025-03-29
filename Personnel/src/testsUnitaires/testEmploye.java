package testsUnitaires;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import personnel.*;

class TestEmploye {
	
	GestionPersonnel gestionPersonnel;
	
	public TestEmploye() throws SauvegardeImpossible, ExceptionDate {
		gestionPersonnel = GestionPersonnel.getGestionPersonnel();
	}
	
	@Test
	void AddEmploye() throws SauvegardeImpossible, ExceptionDate
	{
		Ligue ligue = gestionPersonnel.addLigue("Fléchettes");
		Employe employe = ligue.addEmploye("Bouchard", "Gérard", "g.bouchard@gmail.com", "azerty", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 30));
		assertEquals("Bouchard", employe.getNom());
		assertEquals("Gérard", employe.getPrenom());
		assertEquals("g.bouchard@gmail.com", employe.getMail());
		assertEquals(true, employe.checkPassword("azerty"));
	}
	
	@Test
	void NomEmploye() throws SauvegardeImpossible, ExceptionDate
	{
		Ligue ligue = gestionPersonnel.addLigue("Fléchettes");
		Employe employe = ligue.addEmploye("Bouchard", "Gérard", "g.bouchard@gmail.com", "azerty", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 30));
		assertEquals("Bouchard", employe.getNom());
	}
	
	@Test
	void PrenomEmploye() throws SauvegardeImpossible, ExceptionDate
	{
		Ligue ligue = gestionPersonnel.addLigue("Fléchettes");
		Employe employe = ligue.addEmploye("Bouchard", "Gérard", "g.bouchard@gmail.com", "azerty", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 30));
		assertEquals("Gérard", employe.getPrenom());
	}
	
	@Test
	void MailEmploye() throws SauvegardeImpossible, ExceptionDate
	{
		Ligue ligue = gestionPersonnel.addLigue("Fléchettes");
		Employe employe = ligue.addEmploye("Bouchard", "Gérard", "g.bouchard@gmail.com", "azerty", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 30));
		assertEquals("g.bouchard@gmail.com", employe.getMail());
	}
	
	@Test
	void PasswordEmploye() throws SauvegardeImpossible, ExceptionDate
	{
		Ligue ligue = gestionPersonnel.addLigue("Fléchettes");
		Employe employe = ligue.addEmploye("Bouchard", "Gérard", "g.bouchard@gmail.com", "azerty", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 30));
		assertEquals("azerty", employe.getPassword());
	}
	
	@Test
	void deleteEmploye() throws SauvegardeImpossible, ExceptionDate
	{
		Employe employe;
		Ligue ligue = gestionPersonnel.addLigue("Fléchettes");
		employe = ligue.addEmploye("Bouchard", "Gérard", "g.bouchard@gmail.com", "azerty", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 30));
		employe.remove();
		assertFalse(ligue.getEmployes().contains(employe));
	}
	
	@Test
    void testValidDates() throws SauvegardeImpossible , ExceptionDate
	{
		Ligue ligue = gestionPersonnel.addLigue("Fléchettes");
		assertDoesNotThrow(() -> ligue.addEmploye("Bouchard", "Gérard", "g.bouchard@gmail.com", "azerty" , LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 30)));  
   }    

	  
	@Test
	void testGetDate() throws SauvegardeImpossible ,  ExceptionDate 
	{
		  Ligue ligue = gestionPersonnel.addLigue("Fléchettes");
		  Employe employe = ligue.addEmploye("Bouchard", "Gérard", "g.bouchard@gmail.com", "azerty", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31));
		  assertEquals( employe.getDateArrive(), LocalDate.of(2023, 1, 1));
		  assertEquals( employe.getDateDepart(), LocalDate.of(2023, 12, 31));
	 }	 
	   
}
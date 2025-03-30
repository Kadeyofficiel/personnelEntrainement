package commandLine;

import personnel.*;
import commandLineMenus.*;
import static commandLineMenus.rendering.examples.util.InOut.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class PersonnelConsole
{
	private GestionPersonnel gestionPersonnel;
	LigueConsole ligueConsole;
	EmployeConsole employeConsole;
	
	public PersonnelConsole(GestionPersonnel gestionPersonnel)
	{
		this.gestionPersonnel = gestionPersonnel;
		this.employeConsole = new EmployeConsole();
		this.ligueConsole = new LigueConsole(gestionPersonnel, employeConsole);
	}
	
	public void start()
	{
		menuPrincipal().start();
	}
	
	private Menu menuPrincipal()
	{
		Menu menu = new Menu("Gestion du personnel des ligues");
		menu.add(employeConsole.editerEmploye(gestionPersonnel.getRoot()));
		menu.add(ligueConsole.menuLigues());
		menu.add(raccourciAjouterEmploye());
		menu.add(menuQuitter());
		return menu;
	}

	private Menu raccourciAjouterEmploye()
	{
		Menu menu = new Menu("Raccourci: Ajouter un employé", "a");
		menu.add(selectionnerLiguePourAjout());
		menu.addBack("q");
		return menu;
	}
	
	private List<Ligue> selectionnerLiguePourAjout()
	{
		return new List<Ligue>("Sélectionner une ligue pour ajouter un employé", "l", 
				() -> new ArrayList<>(gestionPersonnel.getLigues()),
				(ligue) -> ajouterEmployeMenu(ligue)
				);
	}
	
	private Menu ajouterEmployeMenu(Ligue ligue)
	{
		Menu menu = new Menu("Ajouter un employé à " + ligue.getNom());
		menu.add(ajouterEmploye(ligue));
		menu.addBack("q");
		return menu;
	}
	
	private Option ajouterEmploye(final Ligue ligue)
	{
		return new Option("Ajouter un employé", "a",
				() -> 
				{
					try {
						String nom = getString("Nom : ");
						String prenom = getString("Prénom : ");
						String mail = getString("Mail : ");
						String password = getString("Password : ");
						
						boolean datesValides = false;
						LocalDate dateArrive = null;
						LocalDate dateDepart = null;
						
						while (!datesValides) {
							try {
								String dateArriveStr = getString("Date d'arrivée (AAAA-MM-JJ ou laissez vide) : ");
								String dateDepartStr = getString("Date de départ (AAAA-MM-JJ ou laissez vide) : ");
								
								dateArrive = dateArriveStr.isEmpty() ? null : LocalDate.parse(dateArriveStr);
								dateDepart = dateDepartStr.isEmpty() ? null : LocalDate.parse(dateDepartStr);
								
								datesValides = true;
							} catch(DateTimeParseException e) {
								System.out.println("Format de date invalide. Veuillez saisir un bon format de date ex: AAAA-MM-JJ");
							}
						}
						
						ligue.addEmploye(nom, prenom, mail, password, dateArrive, dateDepart);
						System.out.println("Employé ajouté avec succès.");
					} catch(ExceptionDate e) {
						System.out.println("La date d'arrivée est après la date de départ");
					} catch (SauvegardeImpossible e) {
						System.err.println("Impossible de sauvegarder l'employé : " + e.getMessage());
					}
				}
		);
	}

	private Menu menuQuitter()
	{
		Menu menu = new Menu("Quitter", "q");
		menu.add(quitterEtEnregistrer());
		menu.add(quitterSansEnregistrer());
		menu.addBack("r");
		return menu;
	}
	
	private Option quitterEtEnregistrer()
	{
		return new Option("Quitter et enregistrer", "q", 
				() -> 
				{
					try
					{
						gestionPersonnel.sauvegarder();
						Action.QUIT.optionSelected();
					} 
					catch (SauvegardeImpossible e)
					{
						System.out.println("Impossible d'effectuer la sauvegarde");
					}
				}
			);
	}
	
	private Option quitterSansEnregistrer()
	{
		return new Option("Quitter sans enregistrer", "a", Action.QUIT);
	}
	
	private boolean verifiePassword()
	{
		boolean ok = gestionPersonnel.getRoot().checkPassword(getString("password : "));
		if (!ok)
			System.out.println("Password incorrect.");
		return ok;
	}
	
	public static void main(String[] args)
	{
		PersonnelConsole personnelConsole = 
				new PersonnelConsole(GestionPersonnel.getGestionPersonnel());
		if (personnelConsole.verifiePassword())
			personnelConsole.start();
	}
}

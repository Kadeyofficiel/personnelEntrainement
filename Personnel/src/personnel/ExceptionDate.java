package personnel;

public class ExceptionDate extends Exception {
	public String getMessage(){
		return "La date de départ ne peut pas être avant la date d'arrivée.";	
	}
}
package personnel;

public class Erreurdate extends ExceptionDate
{
	private static final long serialVersionUID = 1L;

	public Erreurdate()
	{
		super("Erreur dans les dates");
	}
	
	public Erreurdate(String message)
	{
		super(message);
	}
} 
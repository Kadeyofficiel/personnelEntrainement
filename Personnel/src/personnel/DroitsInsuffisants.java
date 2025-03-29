package personnel;

public class DroitsInsuffisants extends RuntimeException
{
	private static final long serialVersionUID = -7047171662944223002L;	
	
	public DroitsInsuffisants()
	{
		super("Droits insuffisants : vous ne pouvez effectuer des opérations que sur votre propre ligue");
	}
	
	public DroitsInsuffisants(String message)
	{
		super(message);
	}
}

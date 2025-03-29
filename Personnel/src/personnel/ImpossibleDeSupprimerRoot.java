package personnel;

public class ImpossibleDeSupprimerRoot extends RuntimeException
{
	private static final long serialVersionUID = 6850643427556906205L;
	
	public ImpossibleDeSupprimerRoot()
	{
		super("Impossible de supprimer le super-utilisateur (root)");
	}
}

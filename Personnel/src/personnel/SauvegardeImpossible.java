package personnel;

public class SauvegardeImpossible extends Exception
{
	private static final long serialVersionUID = 6651919630441855001L;	
	
	private Exception exception;
	
	public SauvegardeImpossible(Exception exception)
	{
		super("Impossible de sauvegarder ou manipuler les données: " + exception.getMessage());
		this.exception = exception;
	}
	
	@Override
	public void printStackTrace() 
	{
		super.printStackTrace();
		System.err.println("Causé par : ");
		exception.printStackTrace();			
	}
	
	public Exception getException() 
	{
		return exception;
	}
}

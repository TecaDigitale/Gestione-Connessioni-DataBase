/*
 * Created on 10-dic-2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package mx.database;

/**
 * Questa classe viene utilizzata gestire tutti gli errori significativi genereti
 * all'interno della classe MsSql.
 * 
 * @author Randazzo Massimiliano 10/12/2003
 *
 */
public class MsSqlException extends Exception
{
	/**
   * 
   */
  private static final long serialVersionUID = 5011245095113503493L;
  
  /**
	 * Codice ID dell'errore
	 */
	int idError = -1;
	
	/**
	 * 
	 */
	public MsSqlException()
	{
		super();
	}

	public MsSqlException(int i, String arg0)
	{
		super(arg0);
		idError = i;
	}
	/**
	 * @param arg0
	 */
	public MsSqlException(String arg0)
	{
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public MsSqlException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public MsSqlException(Throwable arg0)
	{
		super(arg0);
	}

	/**
	 * Questo metodo restituisce il codice identificativo dell'errore
	 * @return codice identificativo dell'errore
	 */
	public int getIdError()
	{
		return idError;
	}

}

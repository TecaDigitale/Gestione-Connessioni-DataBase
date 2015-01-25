/**
 * 
 */
package mx.database.struttura.exception;

/**
 * Questa classe viene utilizzata per gestire le eccezione legate alla creazione del database
 * 
 * @author Massimiliano Randazzo
 *
 */
public class DatabaseException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1204125652033592161L;

	/**
	 * Costruttore
	 * @param msg
	 */
	public DatabaseException(String msg)
	{
		super(msg);
	}
}

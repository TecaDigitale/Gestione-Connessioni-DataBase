/**
 * 
 */
package mx.database.struttura.createTables;

import java.sql.SQLException;

import mx.database.ConnectionPool;
import mx.database.MsSqlPool;
import mx.database.struttura.xsd.View;
import mx.log4j.Logger;

/**
 * @author massi
 *
 */
public class CreateView
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private static Logger log = new Logger(CreateView.class, "mx.databaseStruttura");

	/**
	 * 
	 */
	public CreateView()
	{
	}

	public static void createView(ConnectionPool pool, View view)
	{
		String scriptSQL = null;
		MsSqlPool msp = null;
		
		try
		{
			scriptSQL = "create view "+view.getName()+" as ("+view.getValue()+")";
			msp = pool.getConn();
			msp.esegui(scriptSQL);
		}
		catch (SQLException e)
		{
			log.error(e);
		}
		catch (Exception e)
		{
			log.error(e);
		}
		finally
		{
			if (msp != null)
				pool.releaseConn(msp);
		}
	}
}

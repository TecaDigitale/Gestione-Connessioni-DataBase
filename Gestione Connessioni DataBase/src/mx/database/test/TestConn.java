/**
 * 
 */
package mx.database.test;

import java.sql.SQLException;

import mx.database.ConnectionPool;
import mx.database.MsSql;
import mx.database.MsSqlPool;

/**
 * @author massi
 *
 */
public class TestConn
{

	/**
	 * 
	 */
	public TestConn()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		ConnectionPool pool = null;
		MsSqlPool msp = null;
		
		try
		{
			pool = new ConnectionPool("192.168.4.20", "Servizi", "Servizi", MsSql.POSTGRES, "Servizi", 2, "5433");
			if (pool != null)
			{
				msp = pool.getConn();
				if (msp != null)
					System.out.println("Assegnata la connessione "+msp.getNCon());
				else
					System.err.println("Problemi nel'assegnazione della connnesione");
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (msp != null)
				pool.releaseConn(msp);
			if (pool != null)
				pool.closeAll();
		}

	}

}

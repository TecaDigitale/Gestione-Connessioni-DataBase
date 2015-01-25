/*
 * Created on 13-apr-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package mx.database.test;

import java.sql.SQLException;

import mx.database.ConnectionPool;
import mx.database.MsSqlException;
import mx.database.MsSqlPool;


import junit.framework.TestCase;

/**
 * @author Randazzo
 *
 */
public class ConnectionPoolTest extends TestCase
{

	/**
	 * Constructor for ConnectionPoolTest.
	 * @param arg0
	 */
	public ConnectionPoolTest(String arg0)
	{
		super(arg0);
	}

	public static void main(String[] args)
	{
//		junit.swingui.TestRunner.run(ConnectionPoolTest.class);
	}

  final public void testStored()
  {
		MsSqlPool con1 = null;
		String sql = "";
		ConnectionPool conn = new ConnectionPool();
		conn.setSName("Localhost");
		conn.setPName("1433");
		conn.setUName("SvincoloUser");
		conn.setPwd("SvincoloUser");
		conn.setDBase("Svincolo_Svil");
		conn.setNConn(1);
		try
		{
			conn.loadConn();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		// prendo la prima connessione
		con1 = conn.getConn();
		System.out.println("Conn 1 = "+con1.getNCon());
		assertNotNull("La connessione numero 1 non � stata assegnata", con1);

		try
		{
			sql = "{call writePolizza (1,23456788,24,?,?)}";
			con1.eseguiSP(sql);
		}
		catch (MsSqlException e1)
		{
			e1.printStackTrace();
			assertFalse(e1.getMessage(),false);
		}
		catch (SQLException e1)
		{
			e1.printStackTrace();
			assertFalse(e1.getMessage(),false);
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
			assertFalse(e1.getMessage(),false);
		}
		finally
		{
			conn.releaseConn(con1);
		}

		// prendo la prima connessione
		con1 = conn.getConn();
		System.out.println("Conn 1 = "+con1.getNCon());
		assertNotNull("La connessione numero 1 non � stata assegnata", con1);

		try
		{
			sql = "{call writePolizza (1,23456786,24,?,?)}";
			con1.eseguiSP(sql);
		}
		catch (MsSqlException e1)
		{
			e1.printStackTrace();
			assertFalse(e1.getMessage(),false);
		}
		catch (SQLException e1)
		{
			e1.printStackTrace();
			assertFalse(e1.getMessage(),false);
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
			assertFalse(e1.getMessage(),false);
		}
		finally
		{
			conn.releaseConn(con1);
		}

		// prendo la prima connessione
		con1 = conn.getConn();
		System.out.println("Conn 1 = "+con1.getNCon());
		assertNotNull("La connessione numero 1 non � stata assegnata", con1);

		try
		{
			sql = "{call writePolizza (1,23456787,24,?,?)}";
			con1.eseguiSP(sql);
		}
		catch (MsSqlException e1)
		{
			e1.printStackTrace();
			assertFalse(e1.getMessage(),false);
		}
		catch (SQLException e1)
		{
			e1.printStackTrace();
			assertFalse(e1.getMessage(),false);
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
			assertFalse(e1.getMessage(),false);
		}
		finally
		{
			conn.releaseConn(con1);
		}
  }
  /*
	final public void testLoadConn()
	{
		MsSqlPool con1 = null;
		MsSqlPool con2 = null;
		MsSqlPool con3 = null;

		ConnectionPool conn = new ConnectionPool();
		conn.setSName("Localhost");
		conn.setPName("7210");
		conn.setUName("GESTIMGUSER");
		conn.setPwd("GESTIMGUSER");
		conn.setDBase("GESTIMG");
		conn.setTipoDb("MaxDB");
		conn.setNConn(1);
		try
		{
			conn.loadConn();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		// prendo la prima connessione
		con1 = conn.getConn();
		System.out.println("Conn 1 = "+con1.getNCon());
		assertNotNull("La connessione numero 1 non � stata assegnata", con1);

		try
		{
			con1.esegui("Insert Stato (id_Stato, Desc_Stato) values ('CRE','Creazione file XML')");
		}
		catch (SQLException e1)
		{
			e1.printStackTrace();
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
		// prendo la seconda connessione
		con2 = conn.getConn();
		ResultSet rs = null;
		
		try
		{
			rs = con2.StartSelect("Select * from Stato");
			while (rs.next())
			{
				System.out.print("id_Stato = "+rs.getString("id_Stato"));
				System.out.println(" Desc_Stato = "+rs.getString("desc_Stato"));
			}
		}
		catch (SQLException e2)
		{
			e2.printStackTrace();
		}
		catch (Exception e2)
		{
			e2.printStackTrace();
		}
		finally
		{
			try
			{
				if (rs != null)rs.close();
				if (rs != null)con2.StopSelect();
			}
			catch (SQLException e3)
			{
				e3.printStackTrace();
			}
		}
		System.out.println("Conn 2 = "+con2.getNCon());
		assertNotNull("La connessione numero 2 non � stata assegnata", con2);

		conn.releaseConn(con1);
		System.out.println("Rilasciata Conn 1 = "+con1.getNCon());
		
		con3 = conn.getConn();
		System.out.println("Conn 3 = "+con3.getNCon());
		try
		{
			con3.esegui("Insert Stato (id_Stato, Desc_Stato) values ('PRO','Pronto alla Pubblicazione')");
		}
		catch (SQLException e1)
		{
			e1.printStackTrace();
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
		assertNotNull("La connessione numero 3 non � stata assegnata", con3);

		con1 = conn.getConn();
		System.out.println("Conn 1 = "+con1.getNCon());
		assertNotNull("La connessione numero 1 non � stata assegnata", con1);

		conn.releaseConn(con2);
		System.out.println("Rilasciata Conn 2 = "+con2.getNCon());

		con2 = conn.getConn();
		try
		{
			rs = con2.StartSelect("Select * from Stato");
			while (rs.next())
			{
				System.out.print("id_Stato = "+rs.getString("id_Stato"));
				System.out.println(" Desc_Stato = "+rs.getString("desc_Stato"));
			}
		}
		catch (SQLException e2)
		{
			e2.printStackTrace();
		}
		catch (Exception e2)
		{
			e2.printStackTrace();
		}
		finally
		{
			try
			{
				if (rs != null)rs.close();
				if (rs != null)con2.StopSelect();
			}
			catch (SQLException e3)
			{
				e3.printStackTrace();
			}
		}
		System.out.println("Conn 2 = "+con2.getNCon());
		assertNotNull("La connessione numero 2 non � stata assegnata", con2);

		conn.releaseConn(con1);
		System.out.println("Rilasciata Conn 1 = "+con1.getNCon());
		conn.releaseConn(con2);
		System.out.println("Rilasciata Conn 2 = "+con2.getNCon());
		conn.releaseConn(con3);
		System.out.println("Rilasciata Conn 3 = "+con3.getNCon());

		conn.closeAll();
	}
*/
}

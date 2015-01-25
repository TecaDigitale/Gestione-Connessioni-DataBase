/*
 * Created on 14-nov-2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package mx.database.test;

import java.sql.ResultSet;
import java.sql.SQLException;

import mx.database.MsSql;


import junit.framework.TestCase;

/**
 * Classe utilizzata per il testo della classe MsSql
 * @author Randazzo
 *
 */
public class MsSqlTest extends TestCase
{

	/**
	 * Constructor for MsSqlTest.
	 * @param arg0
	 */
	public MsSqlTest(String arg0)
	{
		super(arg0);
	}

	public static void main(String[] args)
	{
//		junit.swingui.TestRunner.run(MsSqlTest.class);
	}

	/**
	 * Metodo utilizzato per il test della funzione Select
	 *
	 */
	final public void testSelectMSSQL() throws Exception
	{
		ResultSet res= null;
		MsSql msSql = null;
		try
		{
			msSql = new MsSql();
			msSql.setServerName("localhost");
			msSql.setPortName("1433");
			msSql.setUserName("sa");
			msSql.setPassword("Andromeda");
			msSql.setDatabase("Northwind");
			msSql.setTipoDb("MS-SQL");
			msSql.openDb();
			res = msSql.StartSelect("Select * from CATEGORIES");
			System.out.println("Record Trovati "+msSql.getRecTot());
			assertTrue("Ci Sono Record ",msSql.getRecTot()>0);
			assertTrue("Non ci sono record Assegnato",res.next());
			assertTrue("Il contatatore � 0",res.getInt("Conta")>0);
		}
		catch (SQLException e)
		{
			throw new Exception(e);
		}
		catch (Exception e)
		{
			throw new Exception(e);
		}
		finally
		{
			if (res != null)
			{ 
				res.close();
				msSql.StopSelect();
			}
			msSql.closeDb();
		}
	}

	/**
	 * Metodo utilizzato per il test della funzione Select
	 *
	 */
	final public void testSelectMaxDB() throws Exception
	{
		ResultSet res= null;
		MsSql msSql = null;
		try
		{
			msSql = new MsSql();
			msSql.setServerName("localhost");
			msSql.setPortName("7210");
			msSql.setUserName("AIACUSER");
			msSql.setPassword("AIACUSER");
			msSql.setDatabase("AIAC");
			msSql.setTipoDb("MaxDB");
			msSql.openDb();
			res = msSql.StartSelect("Select * from SERVIZIO");
			System.out.println("Record Trovati "+msSql.getRecTot());
			assertTrue("Ci Sono Record ",msSql.getRecTot()>0);
			while(res.next())
			{
			  System.out.println(res.getRow()+" - "+res.getString("Desc_Servizio"));
			}
		}
		catch (SQLException e)
		{
			throw new Exception(e);
		}
		catch (Exception e)
		{
			throw new Exception(e);
		}
		finally
		{
			if (res != null)
			{ 
				res.close();
				msSql.StopSelect();
			}
			msSql.closeDb();
		}
	}

	/**
	 * Metodo utilizzato per il test della funzione Select
	 *
	 */
	final public void testSelectMySQL() throws Exception
	{
		ResultSet res= null;
		MsSql msSql = null;
		try
		{
			msSql = new MsSql();
			msSql.setServerName("localhost");
			msSql.setPortName("3306");
			msSql.setUserName("root");
			msSql.setPassword("");
			msSql.setDatabase("Intronati");
			msSql.setTipoDb("MySql");
			msSql.openDb();
			res = msSql.StartSelect("Select * from T_MENU_HEADER");
			System.out.println("Record Trovati "+msSql.getRecTot());
			assertTrue("Ci Sono Record ",msSql.getRecTot()>0);
		  System.out.println(res.getRow());
			while(res.next())
			{
			  System.out.println(res.getRow()+" - "+res.getString("headerDesc")+" - "+res.getString("htmlLink"));
			}
		}
		catch (SQLException e)
		{
			throw new Exception(e);
		}
		catch (Exception e)
		{
			throw new Exception(e);
		}
		finally
		{
			if (res != null)
			{ 
				res.close();
				msSql.StopSelect();
			}
			msSql.closeDb();
		}
	}

	/**
	 * Metodo utilizzato per il test della funzione Select
	 *
	final public void testSelect2() throws Exception
	{
		ResultSet res= null;
		MsSql msSql = null;
		try
		{
			msSql = new MsSql();

			msSql.setServerName("LigSql01");
			assertEquals("Il nome del server non coincide","LigSql01",msSql.getServerName());

			msSql.setPortName("1433");
			assertEquals("La porta del server non coincide","1433",msSql.getPortName());

			msSql.setUserName("FruizionePolizze");
			assertEquals("L'utente non coincide","FruizionePolizze",msSql.getUserName());

			msSql.setPassword("FruizionePolizze");
			assertEquals("La password non coincide","FruizionePolizze",msSql.getPassword());

			msSql.setDatabase("FruizionePolizze_Svil");
			assertEquals("Il database non coincide","FruizionePolizze_Svil",msSql.getDatabase());

			res = msSql.StartSelect("Select count(*) as Conta from Polizze");
			assertTrue("Non ci sono record Assegnato",res.next());
			assertTrue("Il contatatore � 0",res.getInt("Conta")>0);
		}
		catch (SQLException e)
		{
			throw new Exception(e);
		}
		catch (Exception e)
		{
			throw new Exception(e);
		}
		finally
		{
			if (res != null)
			{ 
				res.close();
				msSql.StopSelect();
			}
		}
	}
	 */
}

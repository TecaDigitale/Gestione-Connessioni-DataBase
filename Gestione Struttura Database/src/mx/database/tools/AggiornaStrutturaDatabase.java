/**
 * 
 */
package mx.database.tools;

import java.sql.SQLException;

import javax.xml.bind.JAXBException;

import mx.database.ConnectionPool;
import mx.database.MsSql;
import mx.database.struttura.CreateTables;
import mx.database.struttura.GestioneXsd;
import mx.database.struttura.exception.DatabaseException;
import mx.database.struttura.xsd.Database;
import mx.log4j.Logger;

/**
 * @author massi
 *
 */
public class AggiornaStrutturaDatabase
{

	private static Logger log = new Logger(AggiornaStrutturaDatabase.class, "mx.database.tools");

	/**
	 * 
	 */
	public AggiornaStrutturaDatabase()
	{
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		AggiornaStrutturaDatabase aggiorna = null;
		ConnectionPool pool = null;
		String fileXml = null;
		String serverName = "localhost";
		String userName = "root";
		String password = "";
		String tipoDatabase = MsSql.MYSQL;
		String nomeDatabase = null;
		int numConnessioni = 2;
		String serverPort=null;

		try
		{
			for (int x=0; x<args.length; x++)
			{
				if (args[x].equals("-ds"))
				{
					x++;
					serverName =args[x];
				}
				else if (args[x].equals("-du"))
				{
					x++;
					userName =args[x];
				}
				else if (args[x].equals("-dp"))
				{
					x++;
					password =args[x];
				}
				else if (args[x].equals("-dt"))
				{
					x++;
					tipoDatabase =args[x];
				}
				else if (args[x].equals("-dn"))
				{
					x++;
					nomeDatabase =args[x];
				}
				else if (args[x].equals("-dc"))
				{
					x++;
					numConnessioni = Integer.parseInt(args[x]);
				}
				else if (args[x].equals("-dsp"))
				{
					x++;
					serverPort =args[x];
				}
				else
					fileXml = args[x];
			}
			if (nomeDatabase != null && fileXml != null)
			{
				pool = new ConnectionPool(serverName, userName, password, tipoDatabase, nomeDatabase, numConnessioni, serverPort);
				aggiorna = new AggiornaStrutturaDatabase();
				aggiorna.esegui(pool, fileXml);
			}
			else
			{
				System.out.println("E' necessario indicare i seguenti parametri");
				System.out.println("java mx.database.tools.AggiornaStrutturaDatabase <Parametri> <File Struttura Database>");
				System.out.println("-ds <Server Name> Nome del database Server Opzionale (default: localhost)");
				System.out.println("-du <User Name> Nome del login per l'accesso al database Server Opzionale (default: root)");
				System.out.println("-dp <Password Name> Password del login per l'accesso al database Server Opzionale (default: Senza password)");
				System.out.println("-dt <Tipo Database> Tipo di database Server Opzionale (default: MySql)");
				System.out.println("    Valori accettati: MySql = Database MySql");
				System.out.println("                      Postgres = Database Postgres");
				System.out.println("-dn <Name Database> Nome del Database");
				System.out.println("-dc <Numero connessioni> Numero minimo di connessioni Opzionale (default: 2)");
				System.out.println("-dsp <Server Port> Numero della porta del server Opzionale");
			}
		}
		catch (NumberFormatException e)
		{
			log.error(e);
		}
		catch (SQLException e)
		{
			log.error(e);
		}
		catch (JAXBException e)
		{
			log.error(e);
		}
		catch (DatabaseException e)
		{
			log.error(e);
		}
		finally
		{
			if (pool != null)
				pool.closeAll();
		}
	}

	public void esegui(ConnectionPool pool, String fileXml) throws JAXBException, DatabaseException
	{
		CreateTables createTables = null;
		Database database = null;

		try
		{
			createTables = new CreateTables(pool);
			database = (Database)GestioneXsd.readXml(fileXml, Database.class.getPackage().getName());
			createTables.createTables(database);
		}
		catch (JAXBException e)
		{
			throw e;
		}
		catch (DatabaseException e)
		{
			throw e;
		}
		
	}
}

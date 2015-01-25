/**
 * 
 */
package mx.database.struttura;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import mx.database.ConnectionPool;
import mx.database.MsSqlPool;
import mx.database.struttura.createTables.CreateConstraint;
import mx.database.struttura.createTables.CreateTable;
import mx.database.struttura.createTables.CreateView;
import mx.database.struttura.createTables.UpdateTable;
import mx.database.struttura.exception.DatabaseException;
import mx.database.struttura.xsd.Database;
import mx.database.struttura.xsd.Index;
import mx.database.struttura.xsd.Table;
import mx.log4j.Logger;

/**
 * Questa classe viene utilizzata per la creazione delle Tabelle del Database
 * 
 * @author Massimiliano Randazzo
 *
 */
public class CreateTables
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private Logger log = new Logger(CreateTables.class, "mx.databaseStruttura");

	/**
	 * Questo metodo viene utilizzato per la gestione del pool di Connessione
	 */
	private ConnectionPool pool = null;

	/**
	 * Questa variabile viene utilizzata per indicare il prefisso da indicare nel nome della Tabella
	 */
	private static String prefix = null;

	/**
	 * Questa variabile viene utilizzata per indicare il suffisso da indicare nel nome della Tabella
	 */
	private static String suffix = null;

	/**
	 * Costruttore
	 */
	public CreateTables(ConnectionPool pool)
	{
		this.pool = pool;
	}

	/**
	 * Questo metodo viene utilizzato per verificare le lista delle tabelle da creare
	 * 
	 * @param database Struttura del database da creare
	 * @throws DatabaseException 
	 */
	public void createTables(Database database) throws DatabaseException
	{
		Vector<String> listaTables = null;
		Vector<String> listaViews = null;
		Vector<String> listaConstraint = null;
		
		try
		{
			listaTables = readListTable();
			if (database.getTable() != null)
			{
  			for (int x=0; x<database.getTable().size(); x++)
  			{
  				log.info("["+(x+1)+"/"+database.getTable().size()+"] Tabella "+database.getTable().get(x).getName());
  				if (!listaTables.contains(genNameTable(database.getTable().get(x))))
  				{
  					log.info("Crea");
  					CreateTable.createTable(pool, database.getTable().get(x));
  				}
  				else
  				{
  					log.info("Aggiorna");
  					UpdateTable.updateTable(pool, database.getTable().get(x));
  				}
  			}
  			for (int x=0; x<database.getTable().size(); x++)
  			{
  				if (database.getTable().get(x).getConstraint()!= null)
  				{
    				log.info("["+(x+1)+"/"+database.getTable().size()+"] Constraint "+database.getTable().get(x).getName());
    				listaConstraint = readlistConstraint(database.getTable().get(x).getName());
    				for (int y=0;y<database.getTable().get(x).getConstraint().size(); y++)
    				{
    					log.info(database.getTable().get(x).getConstraint().get(y).getName());
      				if (!listaConstraint.contains(database.getTable().get(x).getConstraint().get(y).getName().toUpperCase()))
      				{
      					log.info("Crea");
      					CreateConstraint.createConstraint(pool, database.getTable().get(x).getName(), database.getTable().get(x).getConstraint().get(y));
      				}
    				}
  				}
  			}
			}

			listaViews = readlistView();
			if (database.getView() != null)
			{
  			for (int x=0; x<database.getView().size(); x++)
  			{
  				log.info("["+(x+1)+"/"+database.getTable().size()+"] Vista "+database.getView().get(x).getName());
  				if (!listaViews.contains(database.getView().get(x).getName()))
  				{
  					log.info("Crea");
  					CreateView.createView(pool, database.getView().get(x));
  				}
  			}
			}
		}
		catch (DatabaseException e)
		{
			throw e;
		}
  }

	/**
	 * Questo metodo viene utilizzato per generare il nome della tabella
	 * 
	 * @param table
	 * @return
	 */
	public static String genNameTable(Table table)
	{
		String ris = "";
		
		if (prefix != null && 
				!prefix.trim().equals(""))
		{
			ris = prefix;
			if (!ris.endsWith("_"))
				ris += "_";
		}
		ris += table.getName();
		if (suffix != null &&
				!suffix.trim().equals(""))
		{
			if (!suffix.startsWith("_"))
				ris += "_";
			ris += suffix;
		}
		return ris.toUpperCase();
	}

	/**
	 * Questo metodo viene utilizzato per generare il nome della tabella
	 * 
	 * @param index
	 * @return
	 */
	public static String genNameIndex(Index index)
	{
		String ris = "";
		
		if (prefix != null && 
				!prefix.trim().equals(""))
		{
			ris = prefix;
			if (!ris.endsWith("_"))
				ris += "_";
		}
		ris += index.getName();
		if (suffix != null &&
				!suffix.trim().equals(""))
		{
			if (!suffix.startsWith("_"))
				ris += "_";
			ris += suffix;
		}
		return ris.toUpperCase();
	}

	private Vector<String> readlistConstraint(String tableName)
	{
		MsSqlPool msp = null;
		Vector<String> views = new Vector<String>();
		ResultSet rs = null;
		String scripotSql = null;
		
		msp = pool.getConn();
		
		try
		{
			if (msp.getTipoDb().endsWith(MsSqlPool.POSTGRES))
			{
				scripotSql = "SELECT constraint_name, constraint_type FROM information_schema.table_constraints WHERE table_name = '"+tableName.toLowerCase()+"' AND constraint_type='FOREIGN KEY';";

				rs = msp.StartSelect(scripotSql);
				while(rs.next())
					views.add(rs.getString(1).toUpperCase());
				
			}
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
			try
			{
				if (rs != null)
					rs.close();
				if (msp != null)
				{
					msp.StopSelect();
					pool.releaseConn(msp);
				}
			}
			catch (SQLException e)
			{
				log.error(e);
			}
		}

		return views;
	}

	/**
	 * Questo metodo viene utilizzato per leggere la lista delle viste che compongono il Database
	 * @return
	 */
	private Vector<String> readlistView()
	{
		MsSqlPool msp = null;
		Vector<String> views = new Vector<String>();
		ResultSet rs = null;
		String scripotSql = null;
		
		msp = pool.getConn();
		
		try
		{
			if (msp.getTipoDb().endsWith(MsSqlPool.POSTGRES))
			{
				scripotSql = "SELECT viewname FROM pg_views WHERE schemaname NOT IN ('pg_catalog', 'information_schema')";

				rs = msp.StartSelect(scripotSql);
				while(rs.next())
					views.add(rs.getString(1).toUpperCase());
				
			}
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
			try
			{
				if (rs != null)
					rs.close();
				if (msp != null)
				{
					msp.StopSelect();
					pool.releaseConn(msp);
				}
			}
			catch (SQLException e)
			{
				log.error(e);
			}
		}

		return views;
	}

	/**
	 * Questo metodo viene utilizzato per leggere la lista delle tabelle che compongono il Database
	 * 
	 * @return
	 * @throws DatabaseException
	 */
	private Vector<String> readListTable() throws DatabaseException
	{
		Vector<String> listTable = null;
		String querySql = null;
		MsSqlPool msp = null;
		ResultSet rsTables = null;

		try
		{
			listTable = new Vector<String>();

			msp = pool.getConn();

			if (msp.getTipoDb().equals(MsSqlPool.MAXDB))
				querySql = "select TABLENAME from tables where TYPE='TABLE'";
			else if (msp.getTipoDb().equals(MsSqlPool.POSTGRES))
				querySql = "SELECT tablename FROM pg_tables";

			if (querySql != null &&
					!querySql.equals(""))
			{
				rsTables = msp.StartSelect(querySql);
				while(rsTables.next())
					listTable.add(rsTables.getString(1).toUpperCase());
			}
			else
				throw new DatabaseException("Questa tipologia di Database ["+msp.getTipoDb()+"] non \u00E8 supportata");
		}
		catch (SQLException e)
		{
			log.error(e);
		}
		catch (DatabaseException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			log.error(e);
		}
		finally
		{
			try
			{
				if (rsTables != null)
					rsTables.close();
				if (msp != null)
				{
					msp.StopSelect();
					pool.releaseConn(msp);
				}
			}
			catch (SQLException e)
			{
				log.error(e);
			}
		}

		return listTable;
	}

	/**
	 * Questo metodo viene utilizzato per indicare il prefisso della tabella
	 * 
	 * @param prefix
	 */
	public void setPrefix(String prefix)
	{
		CreateTables.prefix = prefix;
	}


	/**
	 * Questo metodo viene utilizzato per indicere il suffisso della tabella
	 * @param suffix
	 */
	public void setSuffix(String suffix)
	{
		CreateTables.suffix = suffix;
	}
}

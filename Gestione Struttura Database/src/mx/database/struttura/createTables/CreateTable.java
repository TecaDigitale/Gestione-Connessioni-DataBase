/**
 * 
 */
package mx.database.struttura.createTables;

import java.sql.SQLException;

import mx.database.ConnectionPool;
import mx.database.MsSqlPool;
import mx.database.struttura.CreateTables;
import mx.database.struttura.xsd.Column;
import mx.database.struttura.xsd.Index;
import mx.database.struttura.xsd.Table;
import mx.log4j.Logger;

/**
 * @author massi
 *
 */
public class CreateTable
{

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private Logger log = new Logger(UpdateTable.class, "mx.databaseStruttura");

	/**
	 * Questo metodo viene utilizzato per la gestione del pool di Connessione
	 */
	private ConnectionPool pool = null;

	/**
	 * Costruttore
	 * 
	 * @param pool Pool di connessioni verso il database
	 */
	public CreateTable(ConnectionPool pool)
	{
		this.pool=pool;
	}

	/**
	 * Questo metodo viene utilizzato per creare la singola Tabella
	 * 
	 * @param table
	 */
	public static void createTable(ConnectionPool pool,Table table)
	{
		CreateTable createTable = null;
		
		createTable = new CreateTable(pool);
		createTable.createTable(table);
	}

	/**
	 * Questo metodo viene utilizzato per creare la singola Tabella
	 * 
	 * @param table
	 */
	private void createTable(Table table)
	{
		String scriptSQL = "";
		Column column = null;
		MsSqlPool msp = null;
		String tableName = "";

		try
		{
			msp = pool.getConn();
			
			tableName = CreateTables.genNameTable(table);
			scriptSQL = "create table "+tableName+" (";
			for (int x=0; x<table.getColumn().size(); x++)
			{
				if (x>0)
					scriptSQL+= ", ";

				column = table.getColumn().get(x);
				scriptSQL += column.getValue().toUpperCase()+" ";
				if (column.getType().equals("INT11"))
					scriptSQL += "INT4";
				else
					scriptSQL += column.getType();
				if (column.getLength()!=null &&
						column.getLength().intValue()>0)
					scriptSQL +="("+column.getLength().intValue()+")";
				scriptSQL += " ";
				scriptSQL += (column.isNull()?"":"not ")+"null";
			}
			scriptSQL += ")";
			msp.esegui(scriptSQL);

			scriptSQL = "";
			for (int x=0; x<table.getColumn().size(); x++)
			{
				column = table.getColumn().get(x);
				if (column.isPrimaryKey())
				{
					if (scriptSQL.equals(""))
					{
					  scriptSQL = "alter table "+tableName+
					  						" add constraint PK_"+tableName+
					  						" primary key (";
						
					}
					else
						scriptSQL += ", ";
					scriptSQL += column.getValue().toUpperCase();
				}
			}

			if (!scriptSQL.equals(""))
			{
				scriptSQL += ")";
				msp.esegui(scriptSQL);
			}
			
			if (table.getIndex()!=null &&
					table.getIndex().size()>0)
			{
				for (int x=0; x<table.getIndex().size(); x++)
				{
					msp.esegui(genScriptIndex(table.getIndex().get(x), tableName));
				}
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
			if (msp != null)
				pool.releaseConn(msp);
		}
	}

	public static String genScriptIndex(Index index, String tableName)
	{
		String scriptSQL = "";

		scriptSQL = "create ";
		if (index.isUnique())
			scriptSQL += "unique ";
		scriptSQL += "index "+CreateTables.genNameIndex(index)+" ON ";
		scriptSQL += tableName+" (";
		for(int y=0; y<index.getColumn().size(); y++)
		{
			scriptSQL += (y==0?"":", ")+index.getColumn().get(y).getValue()+" ";
			scriptSQL += (index.getColumn().get(y).isCrescent()?"ASC":"DESC");
		}
		scriptSQL += ")";
		return scriptSQL;
	}
}

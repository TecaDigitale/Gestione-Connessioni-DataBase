/**
 * 
 */
package mx.database.struttura.createTables;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import mx.database.ConnectionPool;
import mx.database.MsSqlPool;
import mx.database.struttura.CreateTables;
import mx.database.struttura.exception.DatabaseException;
import mx.database.struttura.xsd.Column;
import mx.database.struttura.xsd.Index;
import mx.database.struttura.xsd.Table;
import mx.log4j.Logger;

/**
 * @author massi
 *
 */
public class UpdateTable
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
	public UpdateTable(ConnectionPool pool)
	{
		this.pool=pool;
	}

	/**
	 * Questo metodo viene utilizzato per gestire l'aggiornamento delle tabelle
	 * 
	 * @param pool Pool di connessioni verso il database
	 * @param table Tabella da aggiornare
	 * @throws DatabaseException
	 */
	public static void updateTable(ConnectionPool pool, Table table) throws DatabaseException
	{
		UpdateTable update = null;
		
		update = new UpdateTable(pool);
		update.updateTable(table);
	}

	/**
	 * Questo metodo viene utilizzato per gestire l'aggiornamento delle tabelle
	 * 
	 * @param table Tabella da aggiornare
	 * @throws DatabaseException
	 */
	private void updateTable(Table table) throws DatabaseException
	{
		Vector<Column> columns = null;
		Vector<Index> indexs = null;
		boolean trovato = false;
		String scriptSQL = "";
		Column column = null;
		MsSqlPool msp = null;

		try
		{
			columns = readListColumn(CreateTables.genNameTable(table));
			msp = pool.getConn();
			for (int x=0; x<table.getColumn().size(); x++)
			{
				trovato = false;
				for (int y=0; y<columns.size(); y++)
				{
					if (columns.get(y).getValue().toLowerCase().trim().equals(table.getColumn().get(x).getValue().toLowerCase().trim()))
					{
						trovato = true;
						break;
					}
				}
				if (!trovato)
				{
					scriptSQL = "ALTER TABLE "+CreateTables.genNameTable(table)+" ADD ";
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
					msp.esegui(scriptSQL);
				}
			}

			indexs = readListIndex(CreateTables.genNameTable(table));
			for (int x=0; x<table.getIndex().size(); x++)
			{
				trovato=false;
				for (int y=0; y<indexs.size(); y++)
				{
					
					if (indexs.get(y).getName().toLowerCase().trim().equals(CreateTables.genNameIndex(table.getIndex().get(x)).toLowerCase().trim()))
					{
						trovato=true;
						break;
					}
				}
				if (!trovato)
					msp.esegui(CreateTable.genScriptIndex(table.getIndex().get(x), CreateTables.genNameTable(table)));
			}
		}
		catch (DatabaseException e)
		{
			throw e;
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

	/**
	 * Questo metodo viene utilizzato per leggere la lista delle colonne che compongono la tabella
	 * 
	 * @param tableName Nome della tabella da analizzare
	 * @return
	 * @throws DatabaseException 
	 */
	private Vector<Column> readListColumn(String tableName) throws DatabaseException
	{
		Vector<Column> listColumn = null;
		Column column = null;
		String querySql = null;
		MsSqlPool msp = null;
		ResultSet rsTables = null;

		try
		{
			listColumn = new Vector<Column>();

			msp = pool.getConn();

			if (msp.getTipoDb().equals(MsSqlPool.POSTGRES))
				querySql = "select column_name as name," +
						              "is_nullable as isNull, " +
						              "udt_name as Type, " +
						              "character_maximum_length as Length " +
						        "from information_schema.columns " +
						       "where table_name='"+tableName.toLowerCase().trim()+"'";

			if (querySql != null &&
					!querySql.equals(""))
			{
				rsTables = msp.StartSelect(querySql);
				while(rsTables.next())
				{
					column = new Column();
					column.setValue(rsTables.getString("name"));
					column.setNull(rsTables.getString("isNull").equals("YES"));
					column.setType(rsTables.getString("type").toUpperCase());
					if (rsTables.getString("Length")!= null)
						column.setLength(new BigInteger(rsTables.getString("Length")));
					listColumn.add(column);
				}
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

		return listColumn;
	}

	private Vector<Index> readListIndex(String tableName)
	{
		Vector<Index> indexs = null;
		Index index = null;
		Column column = null;
		String scriptSQL1 = "";
		MsSqlPool msp1 = null;
		ResultSet rs1 = null;
		String scriptSQL2 = "";
		MsSqlPool msp2 = null;
		ResultSet rs2 = null;
		
		try
		{
			scriptSQL1 = "SELECT relname, indkey, indisunique " +
					          "FROM pg_class, pg_index  " +
					         "WHERE pg_class.oid = pg_index.indexrelid AND " +
					               "pg_class.oid IN (SELECT indexrelid " +
					                                  "FROM pg_index, pg_class " +
					                                 "WHERE pg_class.relname='"+tableName.toLowerCase()+"' AND " +
					                                       "pg_class.oid=pg_index.indrelid AND " +
					                                       "indisprimary != 't')";

			indexs = new Vector<Index>();
			msp1 = pool.getConn();
			rs1 = msp1.StartSelect(scriptSQL1);
			while(rs1.next())
			{
				index = new Index();
				index.setName(rs1.getString("relname"));
				index.setUnique(rs1.getString("indisunique").equals("t"));

				try
				{
					scriptSQL2 = "SELECT t.relname, a.attname, a.attnum " +
							           "FROM pg_index c LEFT JOIN pg_class t ON c.indrelid  = t.oid " +
							                           "LEFT JOIN pg_attribute a ON a.attrelid = t.oid AND " +
							                                     "a.attnum = ANY(indkey) " +
							           "WHERE t.relname = '"+tableName.toLowerCase()+"' AND a.attnum in ("+rs1.getString("indkey").trim().replace(" ", ", ")+") " +
							           		"ORDER BY a.attnum";
					msp2 = pool.getConn();
					rs2 = msp2.StartSelect(scriptSQL2);
					while(rs2.next())
					{
						column = new Column();
						column.setValue(rs2.getString("relname"));
						index.getColumn().add(column);
					}
					indexs.add(index);
				}
				catch (SQLException e)
				{
					log.error(e);
					throw e;
				}
				catch (Exception e)
				{
					log.error(e);
					throw e;
				}
				finally
				{
					try
					{
						if (rs2 != null)
							rs2.close();
						if (msp2 != null)
						{
							msp2.StopSelect();
							pool.releaseConn(msp2);
						}
					}
					catch (SQLException e)
					{
						log.error(e);
						throw e;
					}
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
			try
			{
				if (rs1 != null)
					rs1.close();
				if (msp1 != null)
				{
					msp1.StopSelect();
					pool.releaseConn(msp1);
				}
			}
			catch (SQLException e)
			{
				log.error(e);
			}
		}
		return indexs;
	}
}

/*
 * Created on 14-nov-2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package mx.database.connessioni;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import mx.database.MsSqlException;
import mx.database.interfacce.IMsSql;
import mx.log4j.Logger;

import com.microsoft.jdbc.base.BaseResultSet;

/**
 * Questa classe viene utilizzata per gli accessi al database utilizzando le
 * librerie per i database Microsoft SQL.
 * 
 *  @author Randazzo Massimiliano
 * @version 1.0
 *
 */
public class MicrosoftSql implements IMsSql
{

  /**
   * Questa variabile viene utilizzata per eseguire lo log delle applicazioni
   */
  private static Logger log = new Logger(MicrosoftSql.class, "mx.database");

	/**
	 * Nome del Server SQL
	 */
	private String serverName;

	/**
	 * Porta relativa al Server SQL
	 */
	private String portName;
	
	/**
	 * Nome dell'utente da utilizzare per l'accesso al database
	 */
	private String userName;
	
	/**
	 * Password da utilizzare per l'accesso al database
	 */
	private String password;
	
	/**
	 * Nome del database da utilizzare per la ricerca
	 */
	private String database;
	
	/**
	 * Questa variabile viene utilizzata dei metodi StartSelect e StopSelect
	 */
	private PreparedStatement pStat;
	
	private BaseResultSet res = null;
	
	/**
	 * Questa variabile viene utilizzata per indicare il numero di tentativi di connessione prima di rinunciare
	 */
	private int tentativi=0;

	/**
	 * Connessione verso il Database
	 */
	protected Connection conn=null;

	/**
	 * Costruttore della classe semplice 
	 */
	public MicrosoftSql()
	{
		super();
	}

	/**
	 * Metodo utilizzarto per l'apertura della connessione con il database
	 *
	 */
	public void openDb() throws SQLException, Exception 
	{
		String strCon = "";

		try
		{
			if (conn == null || conn.isClosed())
			{			
				strCon = "jdbc:microsoft:sqlserver://" +
										  serverName+":"+
										  portName+";User="+
										  userName+";Password="+
										  password+";DatabaseName="+
										  database;
				Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver"); 
				conn = DriverManager.getConnection(strCon,userName,password);
				tentativi = 0;
			}
		}
		catch (ClassNotFoundException e)
		{
			log.error(e);
			throw new Exception(e);
		}
		catch (SQLException e)
		{
			if ((e.getMessage().indexOf("Error establishing socket")>-1 || e.getSQLState().equals("08001")) && tentativi<20)
			{
				tentativi++;
				log.warn("["+tentativi+"/20] Problemi di connessione con il server ["+strCon+"] aspetto 10 sec. e riprovo");
				Thread.sleep(10000);
				this.openDb();
			}
			else
			{
				log.error(e);
				throw e;
			}
		}
		catch (Exception e)
		{
			log.error(e);
			throw e;
		}
	}

	/**
	 * Questo metodo viene utilizzato per eseguire l'operazione di Select verso il Database
	 * @param SQL
	 * @return Restituisce il recordSet della ricerca
	 * @throws SQLException
	 * @throws Exception
	 */
	public ResultSet StartSelect(String SQL) throws SQLException, Exception
	{
		
		try
		{
			pStat = conn.prepareStatement(SQL);
			res = (BaseResultSet)pStat.executeQuery();
		}
		catch (SQLException e)
		{
			if (e.getSQLState().equals("08S01"))
			{
				conn.close();
				this.openDb();
				this.StartSelect(SQL);
			}
			else
			{
				log.error(e);
				throw new SQLException(e.getSQLState(),e.getMessage());
			}
		}
		catch (Exception e)
		{
			log.error(e);
			throw new Exception(e);
		}
		return (ResultSet) res;
	}
	
	/**
	 * Questo metodo viene utilizzato per l'esecuzione delle Stored Procedure nelle
	 * quali viene anche indicato il codice di errore, nel caso in cui la Stored 
	 * Procedure generasse un errore tale errore viene riportato come eccezione
	 * di tipo MsSqlException
	 * @param NomeSp Nome della Stored Procedure da eseguire ({call CheckStampe ('' , '', ?, ?)})
	 * @throws MsSqlException 
	 * @throws SQLException
	 * @throws Exception
	 */
	public void eseguiSP(String NomeSp) throws MsSqlException, SQLException, Exception
	{
		CallableStatement cs = null;
		try
		{
			cs = conn.prepareCall(NomeSp);
			
			cs.registerOutParameter(1,Types.INTEGER);
			cs.registerOutParameter(2,Types.VARCHAR,100);
			
			cs.execute();
			
			if (cs.getInt(1) != 0)
			{
				throw new MsSqlException(cs.getInt(1),cs.getString(2));
			}
		}
		catch (NumberFormatException e)
		{
			log.error(e);
			throw new Exception(e.getMessage());
		}
		catch (SQLException e)
		{
			if (e.getSQLState().equals("42000") && e.getErrorCode() ==0)
			{
				this.closeDb();
				this.openDb();
			}
			if (e.getSQLState().equals("08S01"))
			{
				this.closeDb();
				this.openDb();
				this.eseguiSP(NomeSp);
			}
			else
			{
				log.error(e);
				throw e;
			}
		}
		catch (MsSqlException e)
		{
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
				if (cs != null) cs.close();
			}
			catch (SQLException e1)
			{
				log.error(e1);
				throw e1;
			}
		}
	}

	/**
	 * Questo metodo viene utilizzato per l'esecuzione delle Stored Procedure nelle
	 * quali viene anche indicato il codice di errore, nel caso in cui la Stored 
	 * Procedure generasse un errore tale errore viene riportato come eccezione
	 * di tipo MsSqlException
	 * @param NomeSp Nome della Stored Procedure da eseguire ({call CheckStampe ('' , '', ? ,? , ?)})
	 * @throws MsSqlException 
	 * @throws SQLException
	 * @throws Exception
	 */	
	public int eseguiSPRis(String NomeSp) throws MsSqlException, SQLException, Exception
	{
		int ris = -1;
		CallableStatement cs = null;
		try
		{
			cs = conn.prepareCall(NomeSp);
			
			cs.registerOutParameter(1,Types.INTEGER);
			cs.registerOutParameter(2,Types.INTEGER);
			cs.registerOutParameter(3,Types.VARCHAR,100);
			
			cs.execute();
			
			if (cs.getInt(2) != 0)
			{
				throw new MsSqlException(cs.getInt(2), cs.getString(3));
			}
			else
			{
				ris = cs.getInt(1);
			}
		}
		catch (NumberFormatException e)
		{
			log.error(e);
			throw new Exception(e.getMessage());
		}
		catch (SQLException e)
		{
			if (e.getSQLState().equals("42000") && e.getErrorCode() ==0)
			{
				this.closeDb();
				this.openDb();
			}
			if (e.getSQLState().equals("08S01"))
			{
				this.closeDb();
				this.openDb();
				ris = this.eseguiSPRis(NomeSp);
			}
			else
			{
				log.error(e);
				throw e;
			}
		}
		catch (MsSqlException e)
		{
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
				if (cs != null) cs.close();
			}
			catch (SQLException e1)
			{
				log.error(e1);
				throw e1;
			}
		}
		return ris;
	}
	
	/**
	 * Questo metodo server per chiudere le connessioni con il database
	 *
	 */
	public void StopSelect() throws SQLException
	{
		try
		{

			if (res != null) res.close();
			if (pStat != null) pStat.close();
		}
		catch (SQLException e1)
		{
			log.error(e1);
			throw new SQLException(e1.getSQLState(),e1.getMessage());
		}
	}

	/**
	 * Questo metodo viene utilizzato per chiudere la connessione con il Server
	 * @throws SQLException
	 */
	public void closeDb() throws SQLException
	{
		try
		{
			if (conn !=null)conn.close();
		}
		catch (SQLException e)
		{
			log.error(e);
			throw new SQLException(e.getSQLState(),e.getMessage());
		}
	}

	/**
	 * Questo metodo viene utilizzato per leggere la password utilizzata 
	 * per l'accesso al database
	 * @return Password
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * Questo metodo viene utilizzato per leggere la porta utilizzata per 
	 * accedere al server SQL
	 * @return porta
	 */
	public String getPortName()
	{
		return portName;
	}

	/**
	 * Questo metodo viene utilizzato per leggere il nome del server SQL 
	 * utilizzato per l'accesso
	 * @return Nome Server
	 */
	public String getServerName()
	{
		return serverName;
	}

	/**
	 * Questo metodo viene utilizzato per leggere il nome dell'utente 
	 * utilizzato per l'accesso al database
	 * @return Nome Utente
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * Questo metodo viene utilizzato per indicare la password da 
	 * utilizzare per l'accesso al database
	 * @param string Password dell'utente 
	 */
	public void setPassword(String string)
	{
		password = string;
	}

	/**
	 * Questo metodo viene utilizzato per indicare la porta del server SQL
	 * 
	 * @param string Porta del Server SQL
	 */
	public void setPortName(String string)
	{
		portName = string;
	}

	/**
	 * Questo metodo viene utilizzato per indicare il nome del Server SQL
	 * 
	 * @param string Nome del Server SQL
	 */
	public void setServerName(String string)
	{
		serverName = string;
	}

	/**
	 * Questo metodo viene utilizzato per indicare l'utente da utilizzare per
	 * l'accesso al database 
	 * @param string nome utente
	 */
	public void setUserName(String string)
	{
		userName = string;
	}

	/**
	 * Questo metodo viene utilizzato per leggere il nome del Datbase
	 * @return Nome del Database
	 */
	public String getDatabase()
	{
		return database;
	}

	/**
	 * Questo metodo viene utilizzato per indare il nome del database 
	 * utilizzato 
	 * @param string Nome del database
	 */
	public void setDatabase(String string)
	{
		database = string;
	}

	/* (non-Javadoc)
	 * @see database.interfacce.IMsSql#esegui(java.lang.String)
	 */
	public int esegui(String comando) throws SQLException, Exception
	{
		return 0;
	}

  /**
	 * Questo metodo viene utilizzato per indicare il numero Totali dei Record del RecordSet
   * 
   * @see mx.database.interfacce.IMsSql#getRecTot()
   */
  public int getRecTot()
  {
    int ris = -1;
    return ris;
  }

  public String genJoinLeft(String table, String condition)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Questo metodo viene utilizzato per indicare se attivare o disattivare la procedura
   * di AutoCommit
   * @param autoCommit
   * @throws SQLException 
   */
  public void setAutoCommit(boolean autoCommit) throws SQLException
  {
    conn.setAutoCommit(autoCommit);
  }

  /**
   * Questo metodo viene utilizzato per eseguire l'operazione di Commit sul Database
   * @throws SQLException 
   */
  public void commit() throws SQLException
  {
    conn.commit();
  }

  /**
   * Questo metodo viene utilizzato per eseguire l'operazione di Rollback sul
   * Database
   * @throws SQLException 
   */
  public void rollback() throws SQLException
  {
    conn.rollback();
  }

  /**
   * Indica se la connessione risulta ancora valida
   * 
   * @see mx.database.interfacce.IMsSql#validate()
   */
	public boolean validate()
	{
		try
		{
			conn.getMetaData();
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}
}

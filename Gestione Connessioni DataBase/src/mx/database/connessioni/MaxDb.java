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
import java.sql.Statement;
import java.sql.Types;
import java.util.GregorianCalendar;
import java.util.Properties;

import mx.database.MsSqlException;
import mx.database.interfacce.IMsSql;
import mx.log4j.Logger;

/**
 * Questa classe viene utilizzata per gli accessi al database utilizzando le
 * librerie per i database MaxDb.
 * 
 *  @author Randazzo Massimiliano
 * @version 1.0
 *
 */
public class MaxDb implements IMsSql
{

  /**
   * Questa variabile viene utilizzata per eseguire lo log delle applicazioni
   */
  private static Logger log = new Logger(MaxDb.class, "mx.database");
  
	/**
	 * Questa variabile viene utilizzata per indicare il numero di tentativi di connessione prima di rinunciare
	 */
	private int tentativi=0;

	/**
	 * Nome del Server SQL
	 */
	private String serverName;

	/**
	 * Porta relativa al Server SQL
	 */
	private String portName="7210";
	
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
	
	private ResultSet res = null;
	/**
	 * Connessione verso il Database
	 */
	protected Connection conn=null;

	/**
	 * Costruttore della classe semplice 
	 */
	public MaxDb()
	{
		super();
	}

	/**
	 * Costruttore della classe nella quale è possibile valorizzare le 
	 * informazioni relative alla connessione con il database
	 * 
	 * @param sName Nome del Server SQL da contattare
	 * @param pName Porta del Server SQL da contattare
	 * @param uName Nome dell'utente utilizzato per l'accesso al database
	 * @param pwd Password ell'utente utilizzato per l'accesso al database
	 * @param dBase Nome del database sul quale lavorare 
	 */
	public MaxDb(String sName, String pName, String uName, String pwd,
	             String dBase)
	{
		serverName = sName;
		portName = pName;
		userName = uName;
		password = pwd;
		database = dBase;
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
        log.debug("Apertura connessione con il Database");
				strCon = "jdbc:sapdb://" +
											serverName+":"+
											portName+"/"+
											database;
        log.debug("conn: "+strCon);
				Class.forName("com.sap.dbtech.jdbc.DriverSapDB"); 
				Properties prop = new Properties();
        log.debug("User: "+userName);
				prop.setProperty("user",userName);
        log.debug("Password: "+password);
				prop.setProperty("password",password);
				prop.setProperty("sqlmode","INTERNAL");
				conn = DriverManager.getConnection(strCon,prop);
				tentativi =0;
        log.debug("Connessione Aperta");
			}
		}
		catch (ClassNotFoundException e)
		{
      log.fatal(e);
			throw new Exception(e);
		}
		catch (SQLException e)
		{
			if ((e.getMessage().indexOf("Cannot connect to jdbc")>-1 || e.getSQLState().equals("08004")) && tentativi<20)
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
	 * @return Restituisce il recordset della ricerca
	 * @throws SQLException
	 * @throws Exception
	 */
	public ResultSet StartSelect(String SQL) throws SQLException, Exception
	{
		GregorianCalendar tIni = null;
		GregorianCalendar tFin = null;
		
		try
		{
      log.info("Eseguo la Select: "+SQL);
			pStat = conn.prepareStatement(SQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pStat.setQueryTimeout(10);
			tIni = new GregorianCalendar();
			res = pStat.executeQuery();
			tFin = new GregorianCalendar();
			if ((tFin.getTimeInMillis()-tIni.getTimeInMillis())>3000)
				log.fatal("La Select ["+SQL+"] è stat eseguita per "+(tFin.getTimeInMillis()-tIni.getTimeInMillis())+" milliSec.");
		}
		catch (SQLException e)
		{
			if (e.getSQLState().equals("08000"))
			{
				this.openDb();
				this.StartSelect(SQL);
			}
			else
			{
				log.error("Eseguo la Select: "+SQL);
        log.error(e);
				throw new SQLException(e.getSQLState(),e.getMessage());
			}
		}
		catch (Exception e)
		{
			log.error("Eseguo la Select: "+SQL);
      log.error(e);
			throw new Exception(e);
		}
		return  res;
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
		GregorianCalendar tIni = null;
		GregorianCalendar tFin = null;
		CallableStatement cs = null;
		try
		{
      log.info("Eseguo la Stored Procedure: "+NomeSp);
			cs = conn.prepareCall(NomeSp);
			
			cs.registerOutParameter(1,Types.INTEGER);
			cs.registerOutParameter(2,Types.VARCHAR,100);
			
			tIni = new GregorianCalendar();
			cs.execute();
			tFin = new GregorianCalendar();
			if ((tFin.getTimeInMillis()-tIni.getTimeInMillis())>3000)
				log.fatal("La Stored Procedure ["+NomeSp+"] è stat eseguita per "+(tFin.getTimeInMillis()-tIni.getTimeInMillis())+" milliSec.");
			
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
			if (e.getSQLState().equals("08000"))
			{
				this.openDb();
				this.eseguiSP(NomeSp);
			}
			else
			{
	      log.error("Eseguo la Stored Procedure: "+NomeSp);
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
      log.error("Eseguo la Stored Procedure: "+NomeSp);
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
		GregorianCalendar tIni = null;
		GregorianCalendar tFin = null;
		int ris = -1;
		CallableStatement cs = null;
		try
		{
      log.info("Eseguo la Stored Procedure: "+NomeSp);
			cs = conn.prepareCall(NomeSp);
			
			cs.registerOutParameter(1,Types.INTEGER);
			cs.registerOutParameter(2,Types.INTEGER);
			cs.registerOutParameter(3,Types.VARCHAR,100);
			
			tIni = new GregorianCalendar();
			cs.execute();
			tFin = new GregorianCalendar();
			if ((tFin.getTimeInMillis()-tIni.getTimeInMillis())>3000)
				log.fatal("La Stored Procedure ["+NomeSp+"] è stat eseguita per "+(tFin.getTimeInMillis()-tIni.getTimeInMillis())+" milliSec.");
			
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
			if (e.getSQLState().equals("08000"))
			{
				this.openDb();
				ris=this.eseguiSPRis(NomeSp);
			}
			else
			{
	      log.error("Eseguo la Stored Procedure: "+NomeSp);
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
      log.error("Eseguo la Stored Procedure: "+NomeSp);
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
		  log.debug("Fermo la Select");
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
      log.debug("Chiudo la connessione con il database");
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

	/**
	 * Questo metodo viene utilizzato per eseguire le operazioni di Insert o Update
	 * @see mx.database.interfacce.IMsSql#esegui(java.lang.String)
	 */
	public int esegui(String comando) throws SQLException, Exception
	{
		GregorianCalendar tIni = null;
		GregorianCalendar tFin = null;
		int ris = 0;
		Statement stat = null;
		
		try
		{
      log.info("Eseguo il comando SQL: "+comando);
	 		stat = conn.createStatement();
			tIni = new GregorianCalendar();
			ris = stat.executeUpdate(comando);
			tFin = new GregorianCalendar();
			if ((tFin.getTimeInMillis()-tIni.getTimeInMillis())>3000)
				log.fatal("L'esecuzuine ["+comando+"] è stata eseguita per "+(tFin.getTimeInMillis()-tIni.getTimeInMillis())+" milliSec.");
		}
		catch (SQLException e)
		{
			if (e.getSQLState().equals("08000"))
			{
				this.openDb();
				this.esegui(comando);
			}
			else
			{
	      log.error("Eseguo il comando SQL: "+comando);
        log.error(e);
				throw e;
			}
		}
		catch (Exception e)
		{
      log.error("Eseguo il comando SQL: "+comando);
      log.error(e);
			throw e;
		}
		finally
		{
			try
			{
				if (stat != null)stat.close();
			}
			catch (SQLException e)
			{
        log.error(e);
				throw e;
			}
		}
		return ris;
	}

  /**
	 * Questo metodo viene utilizzato per indicare il numero Totali dei Record del RecordSet
   * 
   * @see mx.database.interfacce.IMsSql#getRecTot()
   */
  public int getRecTot()
  {
    int ris = 0;
		try
    {
      res.last();
      ris = res.getRow();
      res.beforeFirst();
    }
    catch (SQLException e)
    {
      log.error(e);
    }
    catch (Exception e)
    {
      log.error(e);
    }
    return ris;
  }

  /**
   * Questo metodo viene utilizzato per l'implementazione delle Join di tipo Left
   * @param table Nome della Tabella 
   * @param condition Condizione da adottare
   * 
   */
  public String genJoinLeft(String table, String condition)
  {
    String ris = "";
    ris = "LEFT OUTER JOIN "+table+" ON "+condition;
    return ris;
  }

  /**
   * Questo metodo viene utilizzato per indicare se attivare o disattivare la procedura
   * di AutoCommit
   * @param autoCommit
   * @throws SQLException 
   */
  public void setAutoCommit(boolean autoCommit) throws SQLException
  {
    log.debug("Modifico lo stato della procedura di Auto Commit in "+autoCommit);
    conn.setAutoCommit(autoCommit);
  }

  /**
   * Questo metodo viene utilizzato per eseguire l'operazione di Commit sul Database
   * @throws SQLException 
   */
  public void commit() throws SQLException
  {
    log.debug("Eseguo il commit Manuale");
    conn.commit();
  }

  /**
   * Questo metodo viene utilizzato per eseguire l'operazione di Rollback sul
   * Database
   * @throws SQLException 
   */
  public void rollback() throws SQLException
  {
    log.debug("Eseguo il rollback Manuale");
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

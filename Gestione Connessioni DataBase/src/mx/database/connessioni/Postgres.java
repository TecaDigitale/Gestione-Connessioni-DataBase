/**
 * 
 */
package mx.database.connessioni;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ParameterMetaData;
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
 * Questa classe viene utilizzata per implementare l'accesso al database Postgress
 * 
 * @author Massimiliano Randazzo
 * @version 1.0
 *
 */
public class Postgres implements IMsSql
{

  /**
   * Questa variabile viene utilizzata per eseguire lo log delle applicazioni
   */
  private static Logger log = new Logger(Postgres.class, "mx.database");


  /**
   * Questa variabile viene utilizzata per indicare il numero di tentativi di
   * connessione prima di rinunciare
   */
  private int tentativi = 0;

  /**
   * Nome del Server SQL
   */
  private String serverName;

  /**
   * Porta relativa al Server SQL
   */
  private String portName = "5432";

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

  /**
   * Questa variabile viene utilizzata per le gestione del risultato di una ricerca
   */
  private ResultSet res = null;

  /**
   * Connessione verso il Database
   */
  protected Connection conn = null;

	/**
	 * Costruttore della classe
	 */
	public Postgres()
	{
	}

	/**
   * Questo metodo viene utilizzato per eseguire l'operazione di Select verso il
   * Database
   * 
   * @param SQL
   * @return Restituisce il recordSet della select
   * @throws SQLException
   * @throws Exception
	 * @see mx.database.interfacce.IMsSql#StartSelect(java.lang.String)
	 */
	public ResultSet StartSelect(String SQL) throws SQLException, Exception
	{

    try
    {
      log.info("Eseguo la Select: " + SQL);
      pStat = conn.prepareStatement(SQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      res = pStat.executeQuery();
    }
    catch (SQLException e)
    {
      if (testConn(e))
      {
        this.openDb();
        this.StartSelect(SQL);
      }
      else
      {
        log.error("SQL: "+SQL);
        log.error(e);
        throw new SQLException(e.getSQLState(), e.getMessage());
      }
    }
    catch (Exception e)
    {
      log.error(e);
      throw new Exception(e);
    }
    return res;
	}

	/**
   * Questo metodo server per chiudere le connessioni con il database
   * 
	 * @see mx.database.interfacce.IMsSql#StopSelect()
	 */
	public void StopSelect() throws SQLException
	{
    try
    {
      log.debug("Chiudo la select con il database");
      if (res != null)
        res.close();
      if (pStat != null)
        pStat.close();
    }
    catch (SQLException e1)
    {
      log.error(e1);
      throw new SQLException(e1.getSQLState(), e1.getMessage());
    }
	}

	/**
   * Questo metodo viene utilizzato per chiudere la connessione con il Server
   * 
   * @throws SQLException
	 * @see mx.database.interfacce.IMsSql#closeDb()
	 */
	public void closeDb() throws SQLException
	{
    try
    {
      log.debug("Chiudo la connessione con il Database");
      if (conn != null)
        conn.close();
    }
    catch (SQLException e)
    {
      log.error(e);
      throw new SQLException(e.getSQLState(), e.getMessage());
    }
	}

	/**
   * Questo metodo viene utilizzato per eseguire l'operazione di Commit sul
   * Database
   * 
   * @throws SQLException
	 * @see mx.database.interfacce.IMsSql#commit()
	 */
	public void commit() throws SQLException
	{
    conn.commit();
	}

	/**
   * Questo metodo viene utilizzato per eseguire le operazioni di Insert o
   * Update
   * 
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
				log.fatal("L'esecuzuine ["+comando+"] � stata eseguita per "+(tFin.getTimeInMillis()-tIni.getTimeInMillis())+" milliSec.");
    }
    catch (SQLException e)
    {
      if (testConn(e))
      {
        this.openDb();
        this.esegui(comando);
      }
      else
      {
        log.error("esegui = " + comando);
        log.error(e);
        throw e;
      }
    }
    catch (Exception e)
    {
      log.error("esegui = " + comando);
      log.error(e);
      throw e;
    }
    finally
    {
      try
      {
        if (stat != null)
          stat.close();
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
   * Questo metodo viene utilizzato per l'esecuzione delle Stored Procedure
   * nelle quali viene anche indicato il codice di errore, nel caso in cui la
   * Stored Procedure generasse un errore tale errore viene riportato come
   * eccezione di tipo MsSqlException
   * 
   * @param NomeSp
   *          Nome della Stored Procedure da eseguire ({call CheckStampe ('' ,
   *          '', ?, ?)})
   * @throws MsSqlException
   * @throws SQLException
   * @throws Exception
	 * @see mx.database.interfacce.IMsSql#eseguiSP(java.lang.String)
	 */
	public void eseguiSP(String NomeSp) throws MsSqlException, SQLException,
			Exception
	{
		GregorianCalendar tIni = null;
		GregorianCalendar tFin = null;
    CallableStatement cs = null;
    try
    {
      log.info("Eseguo la Stored Procedure: "+NomeSp);
      cs = conn.prepareCall(NomeSp);

      cs.registerOutParameter(1, Types.INTEGER);
      cs.registerOutParameter(2, Types.VARCHAR, 100);

			tIni = new GregorianCalendar();
      cs.execute();
			tFin = new GregorianCalendar();
			if ((tFin.getTimeInMillis()-tIni.getTimeInMillis())>3000)
				log.fatal("La Stored Procedure ["+NomeSp+"] � stata eseguita per "+(tFin.getTimeInMillis()-tIni.getTimeInMillis())+" milliSec.");

      if (cs.getInt(1) != 0) { throw new MsSqlException(cs.getInt(1), cs
          .getString(2)); }
    }
    catch (NumberFormatException e)
    {
      log.error("EseguiSP = " + NomeSp);
      log.error(e);
      throw new Exception(e.getMessage());
    }
    catch (SQLException e)
    {
      if (testConn(e))
      {
        this.openDb();
        this.eseguiSP(NomeSp);
      }
      else
      {
        log.error("EseguiSP = " + NomeSp);
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
      log.error("EseguiSP = " + NomeSp);
      log.error(e);
      throw e;
    }
    finally
    {
      try
      {
        if (cs != null)
          cs.close();
      }
      catch (SQLException e1)
      {
        log.error(e1);
        throw e1;
      }
    }
	}

	/**
   * Questo metodo viene utilizzato per l'esecuzione delle Stored Procedure
   * nelle quali viene anche indicato il codice di errore, nel caso in cui la
   * Stored Procedure generasse un errore tale errore viene riportato come
   * eccezione di tipo MsSqlException
   * 
   * @param NomeSp
   *          Nome della Stored Procedure da eseguire ({call CheckStampe ('' ,
   *          '', ? ,? , ?)})
   * @throws MsSqlException
   * @throws SQLException
   * @throws Exception
	 * @see mx.database.interfacce.IMsSql#eseguiSPRis(java.lang.String)
	 */
	public int eseguiSPRis(String NomeSp) throws MsSqlException, SQLException,
			Exception
	{
		GregorianCalendar tIni = null;
		GregorianCalendar tFin = null;
    int ris = -1;
    CallableStatement cs = null;
    ParameterMetaData pmd = null;
    try
    {
      log.info("Esegui Procedura: "+NomeSp);
      cs = conn.prepareCall(NomeSp);

      pmd = cs.getParameterMetaData();
      for (int x = 1; x <= pmd.getParameterCount(); x++)
      {
        if (pmd.getParameterMode(x) == ParameterMetaData.parameterModeOut)
          if (pmd.getParameterType(x) == Types.INTEGER)
            cs.registerOutParameter(x, Types.INTEGER);
          else
            cs
                .registerOutParameter(x, pmd.getParameterType(x), pmd
                    .getScale(x));
      }

			tIni = new GregorianCalendar();
      cs.execute();
			tFin = new GregorianCalendar();
			if ((tFin.getTimeInMillis()-tIni.getTimeInMillis())>3000)
				log.fatal("La Stored Procedure ["+NomeSp+"] � stata eseguita per "+(tFin.getTimeInMillis()-tIni.getTimeInMillis())+" milliSec.");

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
      log.error("EseguiSPRis = " + NomeSp);
      log.error(e);
      throw new Exception(e.getMessage());
    }
    catch (SQLException e)
    {
      if (testConn(e))
      {
        this.openDb();
        ris = this.eseguiSPRis(NomeSp);
      }
      else
      {
        log.error("EseguiSPRis = " + NomeSp);
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
      log.error("EseguiSPRis = " + NomeSp);
      log.error(e);
      throw e;
    }
    finally
    {
      try
      {
        if (cs != null)
          cs.close();
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
   * Questo metodo viene utilizzato per l'implementazione delle Join di tipo
   * Left
   * 
   * @param table
   *          Nome della Tabella
   * @param condition
   *          Condizione da adottare
   * 
	 * @see mx.database.interfacce.IMsSql#genJoinLeft(java.lang.String, java.lang.String)
	 */
	public String genJoinLeft(String table, String condition)
	{
    String ris = "";
    ris = "LEFT OUTER JOIN " + table + " ON " + condition;
    return ris;
	}

	/**
   * Questo metodo viene utilizzato per leggere il nome del Datbase
   * 
   * @return Nome del Database
	 * @see mx.database.interfacce.IMsSql#getDatabase()
	 */
	public String getDatabase()
	{
    return database;
	}

	/**
   * Questo metodo viene utilizzato per leggere la password utilizzata per
   * l'accesso al database
   * 
   * @return Password
	 * @see mx.database.interfacce.IMsSql#getPassword()
	 */
	public String getPassword()
	{
    return password;
	}

	/**
   * Questo metodo viene utilizzato per leggere la porta utilizzata per accedere
   * al server SQL
   * 
   * @return porta
	 * @see mx.database.interfacce.IMsSql#getPortName()
	 */
	public String getPortName()
	{
    return portName;
	}

	/**
   * Questo metodo viene utilizzato per indicare il numero Totali dei Record del
   * RecordSet
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
   * Questo metodo viene utilizzato per leggere il nome del server SQL
   * utilizzato per l'accesso
   * 
   * @return Nome Server
	 * @see mx.database.interfacce.IMsSql#getServerName()
	 */
	public String getServerName()
	{
    return serverName;
	}

	/**
   * Questo metodo viene utilizzato per leggere il nome dell'utente utilizzato
   * per l'accesso al database
   * 
   * @return Nome Utente
	 * @see mx.database.interfacce.IMsSql#getUserName()
	 */
	public String getUserName()
	{
    return userName;
	}

	/**
   * Metodo utilizzarto per l'apertura della connessione con il database
   * 
	 * @see mx.database.interfacce.IMsSql#openDb()
	 */
	public void openDb() throws SQLException, Exception
	{
		String strCon = "";
		
		log.debug("openDb");
    try
    {
      if (conn == null || conn.isClosed())
      {
    		strCon =	"jdbc:postgresql://" + serverName + ":" + portName + "/" + database;
        log.debug("Connessione: " + strCon);
        Class.forName("org.postgresql.Driver").newInstance();
        log.debug("org.postgresql.Driver Caricata");
        Properties prop = new Properties();
        log.debug("User: " + userName);
        prop.setProperty("user", userName);
        log.debug("Password: " + password);
        prop.setProperty("password", password);
        conn = DriverManager.getConnection(strCon, prop);
        log.debug("Connessione PostGres Stabilita");
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
      if (testConn(e, tentativi))
      {
        tentativi++;
        log.warn("[" + tentativi
            + "/20] Problemi di connessione con il server [" + strCon
            + "] aspetto 10 sec. e riprovo");
        log.warn(e.getMessage());
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
   * Questo metodo viene utilizzato per eseguire l'operazione di Rollback sul
   * Database
   * 
   * @throws SQLException
	 * @see mx.database.interfacce.IMsSql#rollback()
	 */
	public void rollback() throws SQLException
	{
    conn.rollback();
	}

	/**
   * Questo metodo viene utilizzato per indicare se attivare o disattivare la
   * procedura di AutoCommit
   * 
   * @param autoCommit
   * @throws SQLException
	 * @see mx.database.interfacce.IMsSql#setAutoCommit(boolean)
	 */
	public void setAutoCommit(boolean autoCommit) throws SQLException
	{
    conn.setAutoCommit(autoCommit);
    conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
	}

	/**
   * Questo metodo viene utilizzato per indare il nome del database utilizzato
   * 
   * @param string
   *          Nome del database
	 * @see mx.database.interfacce.IMsSql#setDatabase(java.lang.String)
	 */
	public void setDatabase(String string)
	{
    database = string;
	}

	/**
   * Questo metodo viene utilizzato per indicare la password da utilizzare per
   * l'accesso al database
   * 
   * @param string
   *          Password dell'utente
	 * @see mx.database.interfacce.IMsSql#setPassword(java.lang.String)
	 */
	public void setPassword(String string)
	{
    password = string;
	}

	/**
   * Questo metodo viene utilizzato per indicare la porta del server SQL
   * 
   * @param string
   *          Porta del Server SQL
	 * @see mx.database.interfacce.IMsSql#setPortName(java.lang.String)
	 */
	public void setPortName(String string)
	{
    portName = string;
	}

	/**
   * Questo metodo viene utilizzato per indicare il nome del Server SQL
   * 
   * @param string
   *          Nome del Server SQL
	 * @see mx.database.interfacce.IMsSql#setServerName(java.lang.String)
	 */
	public void setServerName(String string)
	{
    serverName = string;
	}

	/**
   * Questo metodo viene utilizzato per indicare l'utente da utilizzare per
   * l'accesso al database
   * 
   * @param string
   *          nome utente
	 * @see mx.database.interfacce.IMsSql#setUserName(java.lang.String)
	 */
	public void setUserName(String string)
	{
    userName = string;
	}

  /**
   * Questa classe viene utilizzata per testare l'errore e verificare che
   * l'errore sia dovuto alla perdita della connessione
   * 
   * @param e
   * 
   */
  private boolean testConn(SQLException e, int tentativi)
  {
    boolean ris = false;
    log.error(e.getMessage());
    if ((e.getMessage().indexOf("Cannot connect to jdbc") > -1 || (e
        .getSQLState().equals("08004")
        || e.getSQLState().equals("08003") || e.getSQLState().equals("08S01") || e.getSQLState().equals("08006")))
        && tentativi < 20)
      ris = true;
    return ris;
  }

  /**
   * Questa classe viene utilizzata per testare l'errore e verificare che
   * l'errore sia dovuto alla perdita della connessione
   * 
   * @param e
   * 
   */
  private boolean testConn(SQLException e)
  {
    boolean ris = false;
    if (e.getSQLState().equals("08000") || e.getSQLState().equals("08003")
        || e.getSQLState().equals("08S01") || e.getSQLState().equals("08006"))
      ris = true;
    return ris;
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

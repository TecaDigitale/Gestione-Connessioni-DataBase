/*
 * Created on 7-dic-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package mx.database.interfacce;

import java.sql.ResultSet;
import java.sql.SQLException;

import mx.database.MsSqlException;

/**
 * Questa interfaccia viene utilizzata per la definizione dei modulo dedicati
 * all'accesso al database
 * 
 * @author randazzo
 * 
 */
public interface IMsSql
{

  /**
   * Metodo utilizzarto per l'apertura della connessione con il database
   * 
   */
  public abstract void openDb() throws SQLException, Exception;

  /**
   * Questo metodo viene utilizzato per eseguire l'operazione di Select verso il
   * Database
   * 
   * @param SQL
   * @return Restituisce il recordSet della ricerca
   * @throws SQLException
   * @throws Exception
   */
  public abstract ResultSet StartSelect(String SQL) throws SQLException,
      Exception;

  /**
   * Questo metodo viene utilizzato per eseguire le operazioni di insert e
   * update
   * 
   * @param comando
   * @throws SQLException
   * @throws Exception
   */
  public abstract int esegui(String comando) throws SQLException, Exception;

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
   * @throws SqlException
   * @throws Exception
   */
  public abstract void eseguiSP(String NomeSp) throws MsSqlException,
      SQLException, Exception;

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
   * @throws SqlException
   * @throws Exception
   */
  public abstract int eseguiSPRis(String NomeSp) throws MsSqlException,
      SQLException, Exception;

  /**
   * Questo metodo server per chiudere le connessioni con il database
   * 
   */
  public abstract void StopSelect() throws SQLException;

  /**
   * Questo metodo viene utilizzato per chiudere la connessione con il Server
   * 
   * @throws SQLException
   */
  public abstract void closeDb() throws SQLException;

  /**
   * Questo metodo viene utilizzato per leggere la password utilizzata per
   * l'accesso al database
   * 
   * @return Password
   */
  public abstract String getPassword();

  /**
   * Questo metodo viene utilizzato per leggere la porta utilizzata per accedere
   * al server SQL
   * 
   * @return porta
   */
  public abstract String getPortName();

  /**
   * Questo metodo viene utilizzato per leggere il nome del server SQL
   * utilizzato per l'accesso
   * 
   * @return Nome Server
   */
  public abstract String getServerName();

  /**
   * Questo metodo viene utilizzato per leggere il nome dell'utente utilizzato
   * per l'accesso al database
   * 
   * @return Nome Utente
   */
  public abstract String getUserName();

  /**
   * Questo metodo viene utilizzato per indicare la password da utilizzare per
   * l'accesso al database
   * 
   * @param string
   *          Password dell'utente
   */
  public abstract void setPassword(String string);

  /**
   * Questo metodo viene utilizzato per indicare la porta del server SQL
   * 
   * @param string
   *          Porta del Server SQL
   */
  public abstract void setPortName(String string);

  /**
   * Questo metodo viene utilizzato per indicare il nome del Server SQL
   * 
   * @param string
   *          Nome del Server SQL
   */
  public abstract void setServerName(String string);

  /**
   * Questo metodo viene utilizzato per indicare l'utente da utilizzare per
   * l'accesso al database
   * 
   * @param string
   *          nome utente
   */
  public abstract void setUserName(String string);

  /**
   * Questo metodo viene utilizzato per leggere il nome del Datbase
   * 
   * @return Nome del Database
   */
  public abstract String getDatabase();

  /**
   * Questo metodo viene utilizzato per indare il nome del database utilizzato
   * 
   * @param string
   *          Nome del database
   */
  public abstract void setDatabase(String string);

  /**
   * Questo metodo viene utilizzato per indicare il numero Totali dei Record del
   * RecordSet
   * 
   * @return Restituisce il numero totale di record presenti
   */
  public abstract int getRecTot();

  /**
   * Questo metodo viene utilizzato per l'implementazione delle Join di tipo
   * Left
   * 
   * @param table
   *          Nome della Tabella
   * @param condition
   *          Condizione da adottare
   * 
   */
  public String genJoinLeft(String table, String condition);

  /**
   * Questo metodo viene utilizzato per indicare se attivare o disattivare la
   * procedura di AutoCommit
   * 
   * @param autoCommit
   * @throws SQLException
   */
  public void setAutoCommit(boolean autoCommit) throws SQLException;

  /**
   * Questo metodo viene utilizzato per eseguire l'operazione di Commit sul
   * Database
   * @throws SQLException 
   */
  public void commit() throws SQLException;

  /**
   * Questo metodo viene utilizzato per eseguire l'operazione di Rollback sul
   * Database
   * @throws SQLException 
   */
  public void rollback() throws SQLException;

  /**
   * Indica se la conenssione è ancora valida
   * 
   * @return
   */
  public boolean validate();
}

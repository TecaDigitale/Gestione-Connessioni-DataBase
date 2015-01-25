/*
 * Created on 14-nov-2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package mx.database;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;

import mx.database.interfacce.IMsSql;
import mx.randalf.tools.Utils;

import org.apache.log4j.Logger;

/**
 * Questa classe viene utilizzata per gli accessi al database utilizzando le
 * librerie per i database Microsoft SQL.
 * 
 * @author Randazzo Massimiliano
 * @version 1.0
 * 
 */
public class MsSql {

	/**
	 * Questa variabile viene utilizzata per eseguire lo log delle applicazioni
	 */
	private static Logger log = Logger.getLogger(MsSql.class);

	/**
	 * Questa variabile statica viene utilizzata per indicare il database
	 * Microsoft Database Access
	 */
	public static String DBACCESSFILE = "MS-DBAccessFile";

	/**
	 * Questa variabile statica viene utilizzata per indicare il database
	 * Microsoft Database Access
	 */
	public static String DBACCESS = "MS-DBAccess";

	/**
	 * Questa variabile statica viene utilizzata per indicare il database
	 * Microsoft SQL Server
	 */
	public static String MSSQL = "MS-SQL";

	/**
	 * Questa variabile statica viene utilizzata per indicare il database
	 * Microsoft SQL Server
	 */
	public static String MSSQL2005 = "MS-SQL2005";

	/**
	 * Questa variabile statica viene utilizzata per indicare il database MaxDB
	 * Server
	 */
	public static String MAXDB = "MaxDB";

	/**
	 * Questa variabile statica viene utilizzata per indicare il database MySQL
	 * Server
	 */
	public static String MYSQL = "MySql";

	/**
	 * Questa variabile statica viene utilizzata per indicare il database
	 * Postgres Server
	 */
	public static String POSTGRES = "Postgres";

	/**
	 * Questa variabile statica viene utilizzata per indicare il database
	 * Postgres Server
	 */
	public static String ORACLE = "Oracle";

	/**
	 * Quresta variabile viene utilizzata per indicare il tipo di Database
	 * Selezionato, la variabile pu� assumere i seguenti valori:<BR>
	 * <B>MS-SQL</B> (default) Microsoft Sql Server.<BR>
	 * <B>MaxDB</B> MaxDB
	 */
	private String tipoDb = "MS-SQL";

	/**
	 * Questa variabile viene utilizzata per indicare le classi implementate per
	 * il Database
	 */
	private Hashtable<String, String> className = null;

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
	 * Questa variabile viene utilizzata per indicare la connessione
	 * inizializzata e aperta
	 * 
	 */
	private IMsSql connesione = null;

	/**
	 * Questa variabile viene utilizzata per indicare il numero di record
	 * trovati
	 */
	private int recTot = 0;

	/**
	 * Costruttore della classe semplice
	 */
	public MsSql() {
		super();
		this.initClassName();
	}

	/**
	 * Costruttore della classe nella quale � possibile valorizzare le
	 * informazioni relative alla connessione con il database
	 * 
	 * @param sName
	 *            Nome del Server SQL da contattare
	 * @param pName
	 *            Porta del Server SQL da contattare
	 * @param uName
	 *            Nome dell'utente utilizzato per l'accesso al database
	 * @param pwd
	 *            Password ell'utente utilizzato per l'accesso al database
	 * @param dBase
	 *            Nome del database sul quale lavorare
	 */
	public MsSql(String sName, String pName, String uName, String pwd,
			String dBase) {
		super();
		this.initClassName();
		serverName = sName;
		portName = pName;
		userName = uName;
		password = pwd;
		database = dBase;
	}

	/**
	 * Questo metodo viene utilizzato per inizializzare la lista delle classi
	 * per la connessione al database gestite
	 * 
	 */
	private void initClassName() {
		className = new Hashtable<String, String>();
		className.put(MsSql.MAXDB, "mx.database.connessioni.MaxDb");
		className.put(MsSql.MSSQL, "mx.database.connessioni.MicrosoftSql");
		className.put(MsSql.MSSQL2005,
				"mx.database.connessioni.MicrosoftSql2005");
		className.put(MsSql.MYSQL, "mx.database.connessioni.MySql");
		className.put(MsSql.POSTGRES, "mx.database.connessioni.Postgres");
		className.put(MsSql.ORACLE, "mx.database.connessioni.Oracle");
		className.put(MsSql.DBACCESS, "mx.database.connessioni.DbAccess");
		className.put(MsSql.DBACCESSFILE,
				"mx.database.connessioni.DbAccessFile");
	}

	/**
	 * Metodo utilizzarto per l'apertura della connessione con il database
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public void openDb() throws SQLException, Exception {
		String className = "";
		Class myClass;
		try {
			log.debug("Apro la connessione con il Database");
			className = (String) this.className.get(tipoDb);
			if (className == null)
				throw new Exception("Tipologia di Database [" + tipoDb
						+ "]indicato non gestito");
			myClass = Class.forName(className);
			connesione = (IMsSql) myClass.newInstance();
		} catch (NullPointerException e) {
			log.error(new Exception("Tipologia di database non Specificata"));
			throw new Exception("Tipologia di database non Specificata");
		}
		connesione.setDatabase(database);
		connesione.setPassword(password);
		if (!portName.equals(""))
			connesione.setPortName(portName);
		connesione.setServerName(serverName);
		connesione.setUserName(userName);
		connesione.openDb();
	}

	/**
	 * Questo metodo viene utilizzato per esequire l'accesso di tipo Select al
	 * database
	 * 
	 * @param SQL
	 * @return Restituisce il risultato della ricerca
	 * @throws SQLException
	 * @throws Exception
	 */
	public ResultSet StartSelect(String SQL) throws SQLException, Exception {
		ResultSet rs = null;
		rs = connesione.StartSelect(SQL);
		recTot = connesione.getRecTot();
		return rs;
	}

	/**
	 * Questo metodo viene utilizzato per eseguire le operazioni di Insert e
	 * Update sul database
	 * 
	 * @param comando
	 * @return Restituisce il numero di record inserti/modificati o cancellati
	 * @throws SQLException
	 * @throws Exception
	 */
	public int esegui(String comando) throws SQLException, Exception {
		return connesione.esegui(comando);
	}

	/**
	 * Questo metodo viene utilizzato per l'esecuzione delle Stored Procedure
	 * nelle quali viene anche indicato il codice di errore, nel caso in cui la
	 * Stored Procedure generasse un errore tale errore viene riportato come
	 * eccezione di tipo MsSqlException
	 * 
	 * @param NomeSp
	 *            Nome della Stored Procedure da eseguire ({call CheckStampe (''
	 *            , '', ?, ?)})
	 * @throws MsSqlException
	 * @throws SqlException
	 * @throws Exception
	 */
	public void eseguiSP(String NomeSp) throws MsSqlException, SQLException,
			Exception {
		connesione.eseguiSP(NomeSp);
	}

	/**
	 * Questo metodo viene utilizzato per l'esecuzione delle Stored Procedure
	 * nelle quali viene anche indicato il codice di errore, nel caso in cui la
	 * Stored Procedure generasse un errore tale errore viene riportato come
	 * eccezione di tipo MsSqlException
	 * 
	 * @param NomeSp
	 *            Nome della Stored Procedure da eseguire ({call CheckStampe (''
	 *            , '',? , ?, ?)})
	 * @throws MsSqlException
	 * @throws SqlException
	 * @throws Exception
	 */
	public int eseguiSPRis(String NomeSp) throws MsSqlException, SQLException,
			Exception {
		return connesione.eseguiSPRis(NomeSp);
	}

	/**
	 * Questo metodo server per chiudere le connessioni con il database
	 * 
	 */
	public void StopSelect() throws SQLException {
		connesione.StopSelect();
	}

	public void closeDb() throws SQLException {
		connesione.closeDb();
	}

	/**
	 * Questo metodo viene utilizzato per leggere la password utilizzata per
	 * l'accesso al database
	 * 
	 * @return Password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Questo metodo viene utilizzato per leggere la porta utilizzata per
	 * accedere al server SQL
	 * 
	 * @return porta
	 */
	public String getPortName() {
		return portName;
	}

	/**
	 * Questo metodo viene utilizzato per leggere il nome del server SQL
	 * utilizzato per l'accesso
	 * 
	 * @return Nome Server
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * Questo metodo viene utilizzato per leggere il nome dell'utente utilizzato
	 * per l'accesso al database
	 * 
	 * @return Nome Utente
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Questo metodo viene utilizzato per indicare la password da utilizzare per
	 * l'accesso al database
	 * 
	 * @param string
	 *            Password dell'utente
	 */
	public void setPassword(String string) {
		password = string;
	}

	/**
	 * Questo metodo viene utilizzato per indicare la porta del server SQL
	 * 
	 * @param string
	 *            Porta del Server SQL
	 */
	public void setPortName(String string) {
		portName = string;
	}

	/**
	 * Questo metodo viene utilizzato per indicare il nome del Server SQL
	 * 
	 * @param string
	 *            Nome del Server SQL
	 */
	public void setServerName(String string) {
		serverName = string;
	}

	/**
	 * Questo metodo viene utilizzato per indicare l'utente da utilizzare per
	 * l'accesso al database
	 * 
	 * @param string
	 *            nome utente
	 */
	public void setUserName(String string) {
		userName = string;
	}

	/**
	 * Questo metodo viene utilizzato per leggere il nome del Datbase
	 * 
	 * @return Nome del Database
	 */
	public String getDatabase() {
		return database;
	}

	/**
	 * Questo metodo viene utilizzato per indare il nome del database utilizzato
	 * 
	 * @param string
	 *            Nome del database
	 */
	public void setDatabase(String string) {
		database = string;
	}

	/**
	 * Questo metodo statico serve per convetire i campi TimeStamp del database
	 * in formato Stringa riportando la data e l'ora in formatoItaliano
	 * 
	 * @param data
	 * @return Restituisce la data e l'ora convertita nel formato italiano
	 */
	static public String conveDateTimeIta(Timestamp data) {
		GregorianCalendar myData = new GregorianCalendar();
		myData.setTimeInMillis(data.getTime());
		return conveDateTimeIta(myData);
	}

	/**
	 * Questo metodo statico serve per convetire i campi TimeStamp del database
	 * in formato Stringa riportando la data e l'ora in formatoItaliano
	 * 
	 * @param myData
	 *            Data da convertire
	 * @return Restituisce la data e l'ora convertita nel formato italiano
	 */
	static public String conveDateTimeIta(GregorianCalendar myData) {
		String dataITA = "";
		try {
			if (myData.get(Calendar.DAY_OF_MONTH) < 10) {
				dataITA += "0";
			}
			dataITA += myData.get(Calendar.DAY_OF_MONTH);
			dataITA += "/";
			if (myData.get(Calendar.MONTH) + 1 < 10) {
				dataITA += "0";
			}
			dataITA += myData.get(Calendar.MONTH) + 1;
			dataITA += "/";
			dataITA += myData.get(Calendar.YEAR);
			dataITA += " ";
			if (myData.get(Calendar.HOUR_OF_DAY) < 10) {
				dataITA += "0";
			}
			dataITA += myData.get(Calendar.HOUR_OF_DAY);
			dataITA += ":";
			if (myData.get(Calendar.MINUTE) < 10) {
				dataITA += "0";
			}
			dataITA += myData.get(Calendar.MINUTE);
			dataITA += ":";
			if (myData.get(Calendar.SECOND) < 10) {
				dataITA += "0";
			}
			dataITA += myData.get(Calendar.SECOND);
		} catch (Exception exc) {
			log.error(exc);
		}

		return dataITA;
	}

	/**
	 * Questo metodo statico serve per convetire un GregorianCalendar in formato
	 * Stringa riportando la data e l'ora in formatoItaliano
	 * 
	 * @param data
	 * @return Restituisce la data e l'ora convertita nel formato italiano
	 */
	static public String conveDateTimeEng(Timestamp data) {
		String ris = "";
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(data.getTime());
		ris = conveDateTimeEng(gc);
		return ris;
	}

	/**
	 * Questo metodo statico serve per convetire i campi TimeStamp del database
	 * in formato Stringa riportando la data e l'ora in formatoItaliano
	 * 
	 * @param gc
	 *            Data da convertire
	 * @return Restituisce la data e l'ora convertita nel formato italiano
	 */
	static public String conveDateTimeEng(GregorianCalendar gc) {
		String ris = "";
		try {
			ris = gc.get(Calendar.YEAR) + "-";
			ris += (gc.get(Calendar.MONTH) + 1) < 10 ? "0" : "";
			ris += (gc.get(Calendar.MONTH) + 1) + "-";
			ris += (gc.get(Calendar.DAY_OF_MONTH)) < 10 ? "0" : "";
			ris += (gc.get(Calendar.DAY_OF_MONTH)) + " ";
			ris += (gc.get(Calendar.HOUR_OF_DAY)) < 10 ? "0" : "";
			ris += (gc.get(Calendar.HOUR_OF_DAY)) + ":";
			ris += (gc.get(Calendar.MINUTE)) < 10 ? "0" : "";
			ris += (gc.get(Calendar.MINUTE)) + ":";
			ris += (gc.get(Calendar.SECOND)) < 10 ? "0" : "";
			ris += (gc.get(Calendar.SECOND)) + ".000000";
		} catch (Exception exc) {
			log.error(exc);
		}

		return ris;
	}

	/**
	 * Questo metodo statico serve per convetire i campi TimeStamp del database
	 * in formato Stringa riportando la data e l'ora in formatoItaliano
	 * 
	 * @param gc
	 *            Data da convertire
	 * @return Restituisce la data e l'ora convertita nel formato italiano
	 */
	static public String conveTimeEng(GregorianCalendar gc) {
		String ris = "";
		try {
			ris = (gc.get(Calendar.HOUR_OF_DAY)) < 10 ? "0" : "";
			ris += (gc.get(Calendar.HOUR_OF_DAY)) + ":";
			ris += (gc.get(Calendar.MINUTE)) < 10 ? "0" : "";
			ris += (gc.get(Calendar.MINUTE)) + ":";
		} catch (Exception exc) {
			log.error(exc);
		}

		return ris;
	}

	/**
	 * Questo metodo statico serve per convetire i campi TimeStamp del database
	 * in formato Stringa riportando la data e l'ora in formatoItaliano
	 * 
	 * @param gc
	 *            Data da convertire
	 * @return Restituisce la data e l'ora convertita nel formato italiano
	 */
	static public String conveDateEng(GregorianCalendar gc) {
		String ris = "";
		try {
			ris = gc.get(Calendar.YEAR) + "-";
			ris += (gc.get(Calendar.MONTH) + 1) < 10 ? "0" : "";
			ris += (gc.get(Calendar.MONTH) + 1) + "-";
			ris += (gc.get(Calendar.DAY_OF_MONTH)) < 10 ? "0" : "";
			ris += (gc.get(Calendar.DAY_OF_MONTH));
		} catch (Exception exc) {
			log.error(exc);
		}

		return ris;
	}

	/**
	 * Questo metodo statico serve per convetire i campi TimeStamp del database
	 * in formato Stringa riportando la data in formato Italiano
	 * 
	 * @param data
	 * @return Restituisce la data convertita nel formato italiano
	 */
	static public String conveDateIta(Timestamp data) {
		String dataITA = "";
		try {
			GregorianCalendar myData = new GregorianCalendar();
			myData.setTimeInMillis(data.getTime());
			dataITA = conveDateIta(myData);
		} catch (Exception exc) {
			log.error(exc);
		}

		return dataITA;
	}

	/**
	 * Questo metodo statico serve per convetire i campi TimeStamp del database
	 * in formato Stringa riportando la data in formato Inglese
	 * 
	 * @param data
	 * @return Restituisce la data convertita nel formato italiano
	 */
	public static String conveDateEng(Timestamp data) {
		String dataENG = "";
		try {
			GregorianCalendar myData = new GregorianCalendar();
			myData.setTimeInMillis(data.getTime());
			dataENG = conveDateEng(myData);
		} catch (Exception exc) {
			log.error(exc);
		}

		return dataENG;
	}

	/**
	 * Questo metodo statico serve per convetire i campi TimeStamp del database
	 * in formato Stringa riportando la data in formato Inglese
	 * 
	 * @param data
	 * @return Restituisce la data convertita nel formato italiano
	 */
	public static String conveDateEng(Date data) {
		String dataENG = "";
		try {
			GregorianCalendar myData = new GregorianCalendar();
			myData.setTimeInMillis(data.getTime());
			dataENG = conveDateEng(myData);
		} catch (Exception exc) {
			log.error(exc);
		}

		return dataENG;
	}

	/**
	 * Questo metodo statico serve per convetire i campi TimeStamp del database
	 * in formato Stringa riportando la data in formato Inglese
	 * 
	 * @param data
	 * @return Restituisce la data convertita nel formato italiano
	 */
	public static String conveTimeEng(Timestamp data) {
		String dataENG = "";
		try {
			GregorianCalendar myData = new GregorianCalendar();
			myData.setTimeInMillis(data.getTime());
			dataENG = conveTimeEng(myData);
		} catch (Exception exc) {
			log.error(exc);
		}

		return dataENG;
	}

	public static String conveTimeIta(Time time) {
		GregorianCalendar gc = null;

		gc = new GregorianCalendar();
		gc.setTimeInMillis(time.getTime());
		return conveTimeIta(gc);
	}

	public static String conveTimeIta(GregorianCalendar gc) {
		String ris = "";

		ris = (gc.get(Calendar.HOUR_OF_DAY)) < 10 ? "0" : "";
		ris += (gc.get(Calendar.HOUR_OF_DAY)) + ":";
		ris += (gc.get(Calendar.MINUTE)) < 10 ? "0" : "";
		ris += (gc.get(Calendar.MINUTE)) + ":";
		ris += (gc.get(Calendar.SECOND)) < 10 ? "0" : "";
		ris += (gc.get(Calendar.SECOND));
		return ris;
	}

	/**
	 * Questo metodo statico serve per convetire i campi TimeStamp del database
	 * in formato Stringa riportando la data in formato Italiano
	 * 
	 * @param myData
	 *            Data da convertire
	 * @return Restituisce la data convertita nel formato italiano
	 */
	static public String conveDateIta(GregorianCalendar myData) {
		String dataITA = "";
		try {
			if (myData.get(Calendar.DAY_OF_MONTH) < 10) {
				dataITA += "0";
			}
			dataITA += myData.get(Calendar.DAY_OF_MONTH);
			dataITA += "/";
			if (myData.get(Calendar.MONTH) + 1 < 10) {
				dataITA += "0";
			}
			dataITA += myData.get(Calendar.MONTH) + 1;
			dataITA += "/";
			dataITA += myData.get(Calendar.YEAR);

		} catch (Exception exc) {
			log.error(exc);
		}

		return dataITA;
	}

	/**
	 * Questo metodo viene utilizzato per calcolare la data e l'ora per la
	 * registrazione nel DB
	 * 
	 * @return Restituisce la data e ora attuale in formato Inglese
	 */
	public static String genDateTime() {
		String ris = "";
		GregorianCalendar gc = new GregorianCalendar();
		ris = gc.get(Calendar.YEAR) + "-";
		ris += (gc.get(Calendar.MONTH) + 1) < 10 ? "0" : "";
		ris += (gc.get(Calendar.MONTH) + 1) + "-";
		ris += (gc.get(Calendar.DAY_OF_MONTH)) < 10 ? "0" : "";
		ris += (gc.get(Calendar.DAY_OF_MONTH)) + " ";
		ris += (gc.get(Calendar.HOUR_OF_DAY)) < 10 ? "0" : "";
		ris += (gc.get(Calendar.HOUR_OF_DAY)) + ":";
		ris += (gc.get(Calendar.MINUTE)) < 10 ? "0" : "";
		ris += (gc.get(Calendar.MINUTE)) + ":";
		ris += (gc.get(Calendar.SECOND)) < 10 ? "0" : "";
		ris += (gc.get(Calendar.SECOND)) + ".000000";
		return ris;
	}

	/**
	 * Questo metodo viene utilizzato per calcolare la data e l'ora per la
	 * registrazione nel DB
	 * 
	 * @return Restituisce la data e ora attuale in formato Inglese
	 */
	public static String genDate() {
		String ris = "";
		GregorianCalendar gc = new GregorianCalendar();
		ris = gc.get(Calendar.YEAR) + "-";
		ris += (gc.get(Calendar.MONTH) + 1) < 10 ? "0" : "";
		ris += (gc.get(Calendar.MONTH) + 1) + "-";
		ris += (gc.get(Calendar.DAY_OF_MONTH)) < 10 ? "0" : "";
		ris += (gc.get(Calendar.DAY_OF_MONTH));
		return ris;
	}

	/**
	 * Questo metodo viene utilizzato per verificare le stringhe da passare gli
	 * script SQL togliento tutti i caratteri che potrebbero andare in conflitto
	 * con la sintassi SQL
	 * 
	 * @param testo
	 * @return Rewstituisce la stringa normalizzata
	 */
	static public String normString(String testo) {
		String ris = "";
		ris = testo;
		ris = Utils.Replace(ris, "\\", "\\\\");
		ris = Utils.Replace(ris, "'", "''");
		ris = Utils.Replace(ris, "\u0000", " ");
		return ris;
	}

	/**
	 * Questo metodo viene utilizzato per leggere il tipo di Database
	 * selezionato pu� assumere i seguenti valori:<BR>
	 * <B>MS-SQL</B> (default) Microsoft Sql Server.<BR>
	 * <B>MaxDB</B> MaxDB <B>MySql</B> MySql
	 * 
	 * @return restituisce il tipo di database da gestire
	 */
	public String getTipoDb() {
		return tipoDb;
	}

	/**
	 * Questo metodo viene utilizzato per indicare il tipo di Database
	 * selezionato pu� assumere i seguenti valori:<BR>
	 * <B>MS-SQL</B> (default) Microsoft Sql Server.<BR>
	 * <B>MaxDB</B> MaxDB <B>MySql</B> MySql
	 * 
	 * @param string
	 */
	public void setTipoDb(String string) {
		tipoDb = string;
	}

	/**
	 * Questo metodo viene utilizzato per indicare il numero Totali dei Record
	 * del RecordSet
	 * 
	 * @return Restituisce il totale dei record
	 */
	public int getRecTot() {
		return recTot;
	}

	/**
	 * Questo metodo viene utilizzato per l'implementazione delle Join di tipo
	 * Left
	 * 
	 * @param table
	 *            Nome della Tabella
	 * @param condition
	 *            Condizione da adottare
	 * @return String join ricalcolata asseconda della classe utilizzata
	 */
	public String genJoinLeft(String table, String condition) {
		return connesione.genJoinLeft(table, condition);
	}

	/**
	 * Questo metodo viene utilizzato per indicare se attivare o disattivare la
	 * procedura di AutoCommit
	 * 
	 * @param autoCommit
	 * @throws SQLException
	 */
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		connesione.setAutoCommit(autoCommit);
	}

	/**
	 * Questo metodo viene utilizzato per eseguire l'operazione di Commit sul
	 * Database
	 * 
	 * @throws SQLException
	 */
	public void commit() throws SQLException {
		connesione.commit();
	}

	/**
	 * Questo metodo viene utilizzato per eseguire l'operazione di Rollback sul
	 * Database
	 * 
	 * @throws SQLException
	 */
	public void rollback() throws SQLException {
		connesione.rollback();
	}

	/**
	 * Questo metodo viene utilizzato per verificare la validit� della
	 * conenssione
	 * 
	 * @return
	 */
	public boolean validate() {
		return connesione.validate();
	}
}

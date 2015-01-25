/*
 * Created on 13-apr-2004
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package mx.database;

import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * Questa classe ha il compito di gestire la multi connessione verso un database
 * 
 * @author Randazzo Massimiliano
 * 
 */
public class ConnectionPool {

	/**
	 * Questa variabile viene utilizzata per eseguire lo log delle applicazioni
	 */
	private static Logger log = Logger.getLogger(ConnectionPool.class);

	/**
	 * Quresta variabile viene utilizzata per indicare il tipo di Database
	 * Selezionato, la variabile pu� assumere i seguenti valori:<BR>
	 * <B>MS-SQL</B> (default) Microsoft Sql Server.<BR>
	 * <B>MaxDB</B> MaxDB
	 */
	private String tipoDb = "MS-SQL";

	/**
	 * In questa variabile vengono registrate la lista delle connessioni aperte
	 */
	private Vector<MsSqlPool> pool = null;

	/**
	 * Tramite questa variabile viene registrato il nome del Server del database
	 */
	private String sName = "";

	/**
	 * In questa variabile viene indicata la porta da utilizzare per la
	 * connessione
	 */
	private String pName = "";

	/**
	 * In questa variabile viene indicato l'utente per la connessione con il
	 * database
	 */
	private String uName = "";

	/**
	 * In questa variabile viene indicata la password per la connessione del
	 * database
	 */
	private String pwd = "";

	/**
	 * In questa variabile viene indicato il nome del database da contattare
	 */
	private String dBase = "";

	/**
	 * In questa variabile viene indicato il numero di connessioni minime da
	 * aprire contemporeneamente
	 */
	private int nConn = 0;

	/**
	 * Indica il tempo di inattivit� prima che la connessione venga disattivata
	 */
	private long timeout = 60000;

	/**
	 * Questa variabile viene utilizzata per indicare se le connessioni sono
	 * aperte
	 */
	private boolean connection = false;

	/**
	 * Questa variabile viene utilizzata per testare la connessione
	 */
	private ConnectionCheck connectionCheck = null;

	/**
	 * Costruttore
	 * 
	 */
	public ConnectionPool() {
		super();
	}

	/**
	 * Costruttore
	 * 
	 * @param serverName
	 *            Nome del server da contattare
	 * @param userName
	 *            Nome Utente da utilizzare per la connessione
	 * @param password
	 *            Password dell'utente da utilizzare per la connessione
	 * @param tipoDatabase
	 *            Tipo di database da contattare
	 * @param numConnessioni
	 *            Numero minimo delle connessioni da tenere aperte
	 *            contemporaneamente
	 * @param serverPort
	 *            Numero della porta da utilizzare per la connessione con il
	 *            database
	 * @throws SQLException
	 */
	public ConnectionPool(String serverName, String userName, String password,
			String tipoDatabase, String nomeDatabase, int numConnessioni,
			String serverPort) throws SQLException {
		super();
		this.sName = serverName;
		this.pName = serverPort;
		this.uName = userName;
		this.pwd = password;
		this.dBase = nomeDatabase;
		this.tipoDb = tipoDatabase;
		this.nConn = numConnessioni;
		this.loadConn();
	}

	/**
	 * Costruttore
	 * 
	 * @param serverName
	 *            Nome del server da contattare
	 * @param userName
	 *            Nome Utente da utilizzare per la connessione
	 * @param password
	 *            Password dell'utente da utilizzare per la connessione
	 * @param tipoDatabase
	 *            Tipo di database da contattare
	 * @param numConnessioni
	 *            Numero minimo delle connessioni da tenere aperte
	 *            contemporaneamente
	 * @throws SQLException
	 */
	public ConnectionPool(String serverName, String userName, String password,
			String tipoDatabase, String nomeDatabase, int numConnessioni)
			throws SQLException {
		super();
		this.sName = serverName;
		this.uName = userName;
		this.pwd = password;
		this.dBase = nomeDatabase;
		this.tipoDb = tipoDatabase;
		this.nConn = numConnessioni;
		this.loadConn();
	}

	/**
	 * Questo metodo viene utilizzato per sapere se le connessioni risultano
	 * aperte
	 * 
	 * @return Returns the connection.
	 */
	public boolean isConnection() {
		return connection;
	}

	/**
	 * Questo metodo viene utilizzato per la inizializzazione del pool di
	 * connessioni.
	 * 
	 */
	public synchronized void loadConn() throws SQLException {

		if (connectionCheck == null) {
			connectionCheck = new ConnectionCheck(this);
			connectionCheck.start();
			connection = true;
		}
		/*
		 * if (pool == null && !isConnection()) { pool = new Vector();
		 * 
		 * try { log.debug("Apro il pool di connessioni con il database");
		 * log.debug("sName: " + sName); log.debug("pName: " + pName);
		 * log.debug("uName: " + uName); log.debug("dBase: " + dBase);
		 * log.info("Server Name: "
		 * +sName+" Database: "+dBase+" Num Connesioni: "+nConn); for (int x =
		 * 0; x < nConn; x++) { log.info("Connessione n.: " + x); MsSqlPool conn
		 * = new MsSqlPool(sName, pName, uName, pwd, dBase);
		 * conn.setTipoDb(tipoDb); conn.openDb(); conn.setNCon(x);
		 * conn.setInUse(false); conn.setOpen(true); pool.add(conn); connection
		 * = true; } } catch (SQLException e) { log.error(e); throw e; } catch
		 * (Exception e) { log.error(e); } }
		 */
	}

	/**
	 * Tramite questo metodo si prende la prima connessione disponibile, nel
	 * caso in cui non siano disponibili nessuna connessione il programma si
	 * prende il compito di aprirne un'altra
	 * 
	 * @return Restituisce il pool di connessioni
	 */
	public synchronized MsSqlPool getConn() {
		MsSqlPool conn = null;
		int x = 0;
		try {
			log.debug("Ricerco una connessione disponibile");
			if (pool == null)
				pool = new Vector<MsSqlPool>();
			for (x = 0; x < pool.size(); x++) {
				if (!((MsSqlPool) pool.get(x)).isInUse()) {
					conn = (MsSqlPool) pool.get(x);
					if (!conn.isOpen()) {
						conn.openDb();
						conn.setOpen(true);
					}
					conn.setInUse(true);
					break;
				}
			}
			if (conn == null) {
				log.info("Apro nuova connessione con il Database");
				conn = new MsSqlPool(sName, pName, uName, pwd, dBase);
				conn.setTipoDb(tipoDb);
				conn.openDb();
				conn.setNCon(x);
				conn.setInUse(true);
				conn.setOpen(true);
				pool.add(conn);
				connection = true;
			}
			log.info("Connessione assegnata n. " + conn.getNCon());
		} catch (SQLException e) {
			log.error(e);
		} catch (Exception e) {
			log.error(e);
		}
		return conn;
	}

	/**
	 * Questo metodo viene utilizzato per rilasciare la connessione del database
	 * 
	 * @param conn
	 * 
	 */
	public void releaseConn(MsSqlPool conn) {
		try {

			log.info("Rilascio la connessione n. " + conn.getNCon());
			if (conn.getNCon() >= nConn) {
				conn.closeDb();
				conn.setOpen(false);
			}
			conn.setInUse(false);
			/*
			 * if (conn.getNCon() >= nConn) { for (int x = 0; x < pool.size();
			 * x++) { if (pool.get(x) != null && ((MsSqlPool)
			 * pool.get(x)).getNCon() == conn.getNCon()) { pool.remove(x); } } }
			 */
		} catch (SQLException e) {
			log.error(e);
		}
	}

	/**
	 * Metodo richiamato al momento della distruzione della variabile
	 */
	protected void finalize() {
		closeAll();
	}

	/**
	 * Tramite questo metodo vengono chiuse tutte le connessioni aperte
	 * 
	 */
	public void closeAll() {
		try {
			log.debug("Chiudo tutte le connessioni");
			if (pool != null) {
				for (int x = 0; x < pool.size(); x++) {
					if (((MsSqlPool) pool.get(x)).isOpen()) {
						((MsSqlPool) pool.get(x)).closeDb();
					}
				}
			}
			if (connectionCheck != null)
				connectionCheck.end();

		} catch (SQLException e) {
			log.error(e);
		}
	}

	/**
	 * Questo metodo viene utilizzato per leggere il nome del database
	 * utilizzato per la connessione
	 * 
	 * @return Restituisce il nome del database
	 */
	public String getDBase() {
		return dBase;
	}

	/**
	 * Questo metodo viene utilizzato per leggere la porta del database
	 * utilizzato per la connessione
	 * 
	 * @return Restituisce la porta del database Server
	 */
	public String getPName() {
		return pName;
	}

	/**
	 * Questo metodo viene utilizzato per leggere la password del database
	 * utilizzato per la connessione
	 * 
	 * @return Restituisce la password del database
	 */
	public String getPwd() {
		return pwd;
	}

	/**
	 * Questo metodo viene utilizzato per leggere il nome del server utilizzato
	 * per la connessione
	 * 
	 * @return Restituisce il nome del server
	 */
	public String getSName() {
		return sName;
	}

	/**
	 * Questo metodo viene utilizzato per leggere l'utente del database
	 * utilizzato per la connessione
	 * 
	 * @return Restituisce il nome dell'utente del database
	 */
	public String getUName() {
		return uName;
	}

	/**
	 * Questo metodo viene utilizzato per settare il nome del database
	 * utilizzato per la connessione
	 * 
	 * @param string
	 */
	public void setDBase(String string) {
		dBase = string;
	}

	/**
	 * Questo metodo viene utilizzato per settare la porta del database
	 * utilizzato per la connessione
	 * 
	 * @param string
	 */
	public void setPName(String string) {
		pName = string;
	}

	/**
	 * Questo metodo viene utilizzato per settare la password del database
	 * utilizzato per la connessione
	 * 
	 * @param string
	 */
	public void setPwd(String string) {
		pwd = string;
	}

	/**
	 * Questo metodo viene utilizzato per settare il nome del server utilizzato
	 * per la connessione
	 * 
	 * @param string
	 */
	public void setSName(String string) {
		sName = string;
	}

	/**
	 * Questo metodo viene utilizzato per settare l'utente del database
	 * utilizzato per la connessione
	 * 
	 * @param string
	 */
	public void setUName(String string) {
		uName = string;
	}

	/**
	 * Tramite questo metodo si legge il numero di connessioni da aprire
	 * 
	 * @return Restituisce il numero minimo di connessioni aperte
	 *         contemporaneamente
	 */
	public int getNConn() {
		return nConn;
	}

	/**
	 * tramite questo metodi si indica il numero di connessioni da aprire
	 * 
	 * @param i
	 */
	public void setNConn(int i) {
		nConn = i;
	}

	/**
	 * Questo metodo viene utilizzato per leggere il tipo di Database
	 * selezionato pu� assumere i seguenti valori:<BR>
	 * <B>MS-SQL</B> (default) Microsoft Sql Server.<BR>
	 * <B>MaxDB</B> MaxDB
	 * 
	 * @return Restituisce il tipo di Database utilizzato
	 */
	public String getTipoDb() {
		return tipoDb;
	}

	/**
	 * Questo metodo viene utilizzato per indicare il tipo di Database
	 * selezionato pu� assumere i seguenti valori:<BR>
	 * <B>MS-SQL</B> (default) Microsoft Sql Server.<BR>
	 * <B>MaxDB</B> MaxDB
	 * 
	 * @param string
	 */
	public void setTipoDb(String string) {
		tipoDb = string;
	}

	/**
	 * Questo metodo viene utilizzato per la generazione della condiaione Join
	 * Left rispetto al tipo di Driver Selezionato
	 * 
	 * @param table
	 *            Nome della tabella
	 * @param condition
	 *            Consizione di Join
	 * @return String join ricalcolata asseconda della classe utilizzata
	 */
	public String genJoinLeft(String table, String condition) {
		MsSqlPool conn = null;
		String ris = "";

		try {
			conn = getConn();
			ris = conn.genJoinLeft(table, condition);
		} catch (Exception e) {
			log.error(e);
		} finally {
			if (conn != null)
				releaseConn(conn);
		}
		return ris;
	}

	/**
	 * Questo metodo viene utilizzato per ripulire il pool di connessioni dalle
	 * connessioni inattive o non utilizzate da tanto tempo
	 */
	protected synchronized void clearConnections() {
		long stale = 0;
		Enumeration<MsSqlPool> connlist = null;

		try {
			stale = System.currentTimeMillis() - timeout;
			if (pool != null) {
				connlist = pool.elements();

				log.info("Testo le connessioni da ripulire");
				while ((connlist != null) && (connlist.hasMoreElements())) {
					MsSqlPool conn = (MsSqlPool) connlist.nextElement();

					if (conn != null) {
						log.debug("Connessione n.: " + conn.getNCon());
						log.debug("inUse: " + conn.isInUse());
						log.debug("stale: " + stale);
						log.debug("lastUse: " + conn.getLastUse());
						log.debug("check Time: " + (stale > conn.getLastUse()));
						log.debug("check: "
								+ ((!conn.isInUse()) && (stale > conn
										.getLastUse())));
						log.debug("Validate: " + conn.validate());
						if (((!conn.isInUse()) && (stale > conn.getLastUse()))
								|| (!conn.validate())) {
							log.info("Chiudo la connessione n.: "
									+ conn.getNCon());
							try {
								if (conn.isOpen()) {
									conn.closeDb();
								}
							} catch (SQLException e) {
								log.error(e);
							}
							pool.removeElement(conn);
						}
					}
				}
			}
		} catch (NullPointerException e) {

		} catch (Exception e) {
			log.error(e);

		}
	}
}

/**
 * Queste thread viene utilizzato per eseguire il test delle connessioni
 * inutizzate o inattive
 * 
 * @author Massimilaino Randazzo
 *
 */
class ConnectionCheck extends Thread {

	/**
	 * Questa variabile viene utilizzata per gestire il pool di connessioni
	 * 
	 */
	private ConnectionPool pool = null;

	private boolean ciclo = true;

	/**
	 * Costruttore
	 * 
	 * @param pool
	 *            Pool di connessioni
	 */
	public ConnectionCheck(ConnectionPool pool) {
		this.pool = pool;
	}

	public void run() {
		while (ciclo) {
			try {
				sleep(10000);
			} catch (InterruptedException e) {
			}
			if (pool != null)
				pool.clearConnections();
		}
	}

	public void end() {
		ciclo = false;
	}
}
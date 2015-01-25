/*
 * Created on 25-mar-2006
 *
 */
package mx.database.table;

import java.util.Map;

import mx.database.ConnectionPool;
import mx.database.MsSqlPool;

/**
 * Questa classe viene utilizzata per implementare le tabelle in sola lettura o
 * con Join
 * 
 * @author Randazzo
 *
 */
public abstract class Query extends DataSet {

	/**
	 * Costruttore con la gestione del Pool di connessioni con lista dei
	 * parametri da caricare
	 * 
	 * @param conn
	 *            Pool di connessioni con il Database
	 * @param map
	 *            Lista parametri da caricare
	 */
	public Query(ConnectionPool conn, Map<Object, Object> map) {
		super(conn, map);
		defFrom();
		defWhere();
	}

	public Query(ConnectionPool conn, MsSqlPool msp) {
		super(conn, msp);
		defFrom();
		defWhere();
	}

	/**
	 * Costruttore con definizione del Pool di connessione
	 * 
	 * @param conn
	 *            Poll di connessioni con il database
	 */
	public Query(ConnectionPool conn) {
		super(conn);
		defFrom();
		defWhere();
	}

	/**
	 * Costruttore con definizione la connessione con il database
	 * 
	 * @param msp
	 *            Connessioni con il database
	 */
	public Query(MsSqlPool msp) {
		super(msp);
	}

	/**
	 * Costruttore
	 */
	public Query() {
		super();
		defFrom();
		defWhere();
	}

	/**
	 * Questo metodo viene utilizzato per eseguire l'operazione di ripulitura di
	 * tutte le variabili
	 * 
	 */
	public void resetAll() {
		super.resetAll();
		defFrom();
		defWhere();
	}

	/**
	 * Questo metodo viene utilizzato per indicare tutte le condizioni di
	 * default per la Form
	 *
	 */
	protected abstract void defFrom();

	/**
	 * Questo metodo viene utilizzato per indicare tutte le condizioni di
	 * default per la Where
	 *
	 */
	protected abstract void defWhere();

	/**
	 * Questo metodo viene utilizzato per definire i campi da utilizzare nella
	 * query
	 * 
	 */
	protected abstract void defCampi();

	/**
	 * Questo metodo viene utilizzata per indicare le condizioni di from della
	 * select
	 * 
	 * @param from
	 *            The from to set.
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * Questo metodo viene utilizzato per aggiungere una condizione all form
	 * della Select
	 * 
	 * @param from
	 *            Condizione da aggiungere
	 */
	public void addFrom(String from) {
		this.from += " " + from;
	}
}

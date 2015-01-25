/*
 * Created on 24-mar-2006
 *
 */
package mx.database.table;

import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import mx.database.ConnectionPool;
import mx.database.MsSqlException;
import mx.database.MsSqlPool;

/**
 * Questa classe viene utilizzata per gestire
 * 
 * @author Randazzo
 * 
 */
public abstract class Table extends DataSet {

	/**
	 * Questa variabile viene utilizzata per eseguire lo log delle applicazioni
	 */
	private static Logger log = Logger.getLogger(Table.class);

	/**
	 * Costruttore con definizione del Pool di connessione e connesione
	 * preselezionata per le operazioni di modifica della tabella con lista dei
	 * parametri da caricare
	 * 
	 * @param conn
	 *            Poll di connessioni con il database
	 * @param msp
	 *            Connessione da utilizzare nelle operazioni di modifica della
	 *            tabella (insert, update, delete)
	 * @param map
	 *            Lista parametri da caricare
	 */
	public Table(ConnectionPool conn, MsSqlPool msp, Map<Object, Object> map) {
		super(conn, msp, map);
	}

	/**
	 * Costruttore con la gestione del Pool di connessioni con lista dei
	 * parametri da caricare
	 * 
	 * @param conn
	 *            Pool di connessioni con il Database
	 * @param map
	 *            Lista parametri da caricare
	 */
	public Table(ConnectionPool conn, Map<Object, Object> map) {
		super(conn, map);
	}

	/**
	 * Costruttore con definizione del Pool di connessione e connesione
	 * preselezionata per le operazioni di modifica della tabella
	 * 
	 * @param conn
	 *            Poll di connessioni con il database
	 * @param msp
	 *            Connessione da utilizzare nelle operazioni di modifica della
	 *            tabella (insert, update, delete)
	 */
	public Table(ConnectionPool conn, MsSqlPool msp) {
		super(conn, msp);
	}

	/**
	 * Costruttore Principale
	 * 
	 */
	public Table() {
		super();
	}

	/**
	 * Costruttore con definizione del Pool di connessione
	 * 
	 * @param conn
	 *            Poll di connessioni con il database
	 */
	public Table(ConnectionPool conn) {
		super(conn);
	}

	/**
	 * Costruttore con definizione del Pool di connessione
	 * 
	 * @param msp
	 *            Poll di connessioni con il database
	 */
	public Table(MsSqlPool msp) {
		super(msp);
	}

	/**
	 * Questo metodo viene utilizzato per definire i campi da utilizzare nella
	 * query
	 * 
	 */
	protected abstract void defCampi();

	/**
	 * Questo metodo viene utilizzato per implementare i controlli da eseguire
	 * prima dell'esecuzuibne delle operazioni di insert e update
	 *
	 */
	protected abstract void preUpdate();

	/**
	 * Questo metodo viene utilizzato per implementare i controlli da eseguire
	 * dopo dell'esecuzuibne delle operazioni di insert e update
	 *
	 */
	protected abstract void postUpdate();

	/**
	 * Questo metodo viene utilizzato per eseguire l'operazione di
	 * inserimento/modifica della tabella.<BR>
	 * Nel caso in cui la colonna di tipo chiave primaria avesse il valore nullo
	 * o 0 viene eseguito l'inserimeto di un nuovo record dopo aver generato una
	 * nuova chiave primaria, altrimenti si procede con l'aggiornamento.
	 * 
	 * @throws MsSqlException
	 */
	public int insert() throws MsSqlException {
		// MsSqlPool msp = null;
		String sql = "";
		String sql2 = "";
		String key;
		Column col;
		Vector<Column> cols;
		int ris = 0;

		try {
			preUpdate();
			// if (this.msp == null)
			// msp = conn.getConn();
			// else
			// msp = this.msp;
			cols = getPrimaryKey();
			if (cols != null) {
				for (int x = 0; x < cols.size(); x++) {
					col = (Column) cols.get(x);
					if (col.getValue() == null)
						col.genNewID();
					else if (col.getValue().getClass().getName()
							.equals("java.lang.Integer")) {
						if (((Integer) col.getValue()).intValue() == 0)
							col.genNewID();
					}
				}
			} else
				throw new MsSqlException(
						"Non risulta indicato alcuna chiave primaria");
			for (Enumeration<String> iter = campi.keys(); iter
					.hasMoreElements();) {
				key = iter.nextElement();
				col = campi.get(key);
				if (col.getValue() != null && col.isView()) {
					log.debug("insert encodingUtf8: " + encodingUtf8);
					if (sql.equals("")) {
						sql = "INSERT INTO " + col.getTable() + " ("
								+ col.getColumn();
						sql2 = ") VALUES (" + col.getValueSql(encodingUtf8);
					} else {
						sql += ", " + col.getColumn();
						sql2 += ", " + col.getValueSql(encodingUtf8);
					}
				}
			}
			sql = sql + sql2 + ")";
			ris = esegui(sql);
		} catch (MsSqlException e) {
			throw e;
		} catch (Exception e) {
			log.error(e);
		} finally {
			// if (this.msp ==null)
			// if (msp != null)
			// conn.releaseConn(msp);
			postUpdate();
		}
		return ris;
	}

	/**
	 * Questo metodo viene utilizzato per eseguire l'operazione di
	 * inserimento/modifica della tabella.<BR>
	 * Nel caso in cui la colonna di tipo chiave primaria avesse il valore nullo
	 * o 0 viene eseguito l'inserimeto di un nuovo record dopo aver generato una
	 * nuova chiave primaria, altrimenti si procede con l'aggiornamento.
	 * 
	 * @throws MsSqlException
	 */
	public int update() throws MsSqlException {
		return update(false);
	}

	/**
	 * Questo metodo viene utilizzato per eseguire l'operazione di
	 * inserimento/modifica della tabella.<BR>
	 * Nel caso in cui la colonna di tipo chiave primaria avesse il valore nullo
	 * o 0 viene eseguito l'inserimeto di un nuovo record dopo aver generato una
	 * nuova chiave primaria, altrimenti si procede con l'aggiornamento.
	 * 
	 * @param writeNull
	 *            Questa variabile viene utilizzata per forzare l'aggiornamento
	 *            del campi null
	 * @throws MsSqlException
	 */
	public int update(boolean writeNull) throws MsSqlException {
		String sql = "";
		String sql2 = "";
		String key;
		Column col;
		int ris = 0;
		// MsSqlPool msp = null;

		try {
			preUpdate();
			// if (this.msp == null)
			// msp = conn.getConn();
			// else
			// msp = this.msp;
			for (Enumeration<String> iter = campi.keys(); iter
					.hasMoreElements();) {
				key = iter.nextElement();
				col = campi.get(key);
				if ((col.getValue() != null || writeNull)
						&& (col.getColumn() != null && !col.getColumn().equals(
								""))) {
					log.debug("update encodingUtf8: " + encodingUtf8);
					if (col.isPrimaryKey() || col.isForceWhere()) {
						sql2 += sql2.equals("") ? " WHERE " : " AND ";
						sql2 += col.getColumn() + "="
								+ col.getValueSql(encodingUtf8);
					} else {
						sql += sql.equals("") ? "UPDATE " + col.getTable()
								+ " SET " : ", ";
						sql += col.getColumn()
								+ "="
								+ (col.getValue() == null ? "NULL" : col
										.getValueSql(encodingUtf8));
					}
				}
			}
			sql = sql + sql2;

			ris = esegui(sql);
		} catch (MsSqlException e) {
			throw e;
		} catch (Exception e) {
			log.error(e);
		} finally {
			// if (this.msp == null)
			// if (msp != null)
			// conn.releaseConn(msp);
			postUpdate();
		}
		return ris;
	}

	/**
	 * Questo metodo viene utilizzato per canellare uno o pi� record dalla
	 * tabella
	 * 
	 * @return Numero di record cancellati
	 * @throws MsSqlException
	 */
	public int delete() throws MsSqlException {
		return delete(false);
	}

	/**
	 * Questo metodo viene utilizzato per canellare uno o pi� record dalla
	 * tabella
	 * 
	 * @return Numero di record cancellati
	 * @throws MsSqlException
	 */
	public int delete(boolean force) throws MsSqlException {
		int ris = 0;
		Column col;
		Vector<Column> cols;
		// MsSqlPool msp = null;
		cols = getPrimaryKey();
		String sql = "";
		boolean isWhere = false;

		try {
			// if (this.msp == null)
			// msp = conn.getConn();
			// else
			// msp = this.msp;
			if (cols != null) {
				for (int x = 0; x < cols.size(); x++) {
					col = (Column) cols.get(x);
					if (col.isEmpty() && (!force))
						throw new MsSqlException(
								"Campi chiave primaria non valorizzati");
					else {
						sql += sql.equals("") ? "DELETE FROM " + col.getTable()
								: "";
						if (!col.isEmpty()
								|| col.getTipoRicerca().trim()
										.equalsIgnoreCase("is null")) {
							sql += isWhere ? " AND " : " WHERE ";
							if (col.getTipoRicerca().trim()
									.equalsIgnoreCase("is null"))
								sql += col.getColumn() + col.getTipoRicerca();
							else
								sql += col.getColumn() + col.getTipoRicerca()
										+ col.getValueSql();
							isWhere = true;
						}
					}
				}
				log.info(sql);
				ris = esegui(sql);
			} else
				throw new MsSqlException(
						"Non risulta indicato alcuna chiave primaria");
		} catch (MsSqlException e) {
			throw e;
		} catch (Exception e) {
			log.error(e);
		}
		// finally
		// {
		// if (this.msp == null)
		// if (msp != null)
		// conn.releaseConn(msp);
		// }

		return ris;
	}

	/**
	 * Questo metodo viene utilizzato per eseguire l'operazione di insert,
	 * update, delete
	 * 
	 * @param sql
	 *            Stringa SQL sa eseguire
	 * @return Numero di record modificati
	 * @throws MsSqlException
	 */
	protected int esegui(String sql) throws MsSqlException {
		int ris = 0;
		MsSqlPool msp = null;

		try {
			if (this.msp != null)
				msp = this.msp;
			else
				msp = conn.getConn();
			log.info("esegui sql:" + sql);
			ris = msp.esegui(sql);
		} catch (MsSqlException e) {
			throw e;
		} catch (SQLException e) {
			log.error("esegui sql:" + sql);
			log.error(e);
			throw new MsSqlException(e.getMessage(), e.getCause());
		} catch (Exception e) {
			log.error("esegui sql:" + sql);
			log.error(e);
		} finally {
			if (this.msp == null)
				if (msp != null)
					conn.releaseConn(msp);
		}
		return ris;
	}
}

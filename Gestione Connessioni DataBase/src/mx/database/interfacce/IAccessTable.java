/*
 * Created on 22-giu-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package mx.database.interfacce;

import java.sql.ResultSet;

import mx.database.ConnectionPool;
import mx.database.MsSqlException;


/**
 * Questa interfaccia viene utilizzata per normallizzare tutti accessi alle tabelle
 * @author Randazzo
 *
 */
public interface IAccessTable
{

	/**
	 * Questo metodo viene utilizzato per eseguire la select sulla tabella
	 * @return Elenco dei record che soddisfano la ricerca
	 */
	public abstract ResultSet StartSelect();
	
	/**
	 * Questo metodo viene utilizzato per terminare l'operazione di Select 
	 *
	 */
	public abstract void StopSelect();
	
	/**
	 * Questo medoto viene utilizzato per le operazione di inserimento/modifica dei dati della tabella
	 *
	 */
	public abstract void Insert() throws MsSqlException;

	/**
	 * Questo medoto viene utilizzato per le operazione di cancellazione dei dati della tabella
	 *
	 */
	public abstract void delete() throws MsSqlException;

	/**
	 * Questo medoto viene utilizzato per azzerare le varibili 
	 *
	 */
	public abstract void reset();

	/**
	 * Questo metodo viene utilizzato per valorizzare la variabile utilizzata per la gestione del pool di connessione
	 * con il Database
	 * @param pool
	 */
	public abstract void setConn(ConnectionPool pool);

}

/*
 * Created on 13-apr-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package mx.database;


/**
 * @author Randazzo
 *
 */
public class MsSqlPool extends MsSql
{
	/**
	 * Tramite questa variabile si verifica se questa connessione è in uso da un'altro utente
	 */
	private boolean inUse = false;
	
	/**
	 * Tramite questa variabile viene indicato il nummero della connessione.
	 */
	private int nCon = -1;
	
	/**
	 * TRamite questa variabile vine indicato del la connessione in questione è aperta
	 */
	private boolean open = false;

	/**
	 * Indica l'orario dell'ultimo utilizzo della connessione
	 */
	private long lastUse = 0;

	/**
	 * Costruttore della classe semplice 
	 */
	public MsSqlPool()
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
	public MsSqlPool(
		String sName,
		String pName,
		String uName,
		String pwd,
		String dBase)
	{
		super(sName, pName, uName, pwd, dBase);
	}

	/**
	 * Tramite questa variabile di verifica lo stato della connessione
	 * @return Indica se la connessione è in uso
	 */
	public boolean isInUse()
	{
		return inUse;
	}

	/**
	 * Tramite quuesta variabile si varia lo stato della connessione
	 * @param b
	 */
	public void setInUse(boolean b)
	{
		inUse = b;
		lastUse = System.currentTimeMillis();
	}

	/**
	 * Indica l'ora dell'ultimo utilizzo della connessione
	 * 
	 * @return
	 */
	public long getLastUse()
	{
		return lastUse;
	}

	/**
	 * Tramite questo metodo viene indicato il numero della connessione che stiamo gestendo
	 * @return Restituisce il numero minimo di connessioni contemporanee aperte sul DB
	 */
	public int getNCon()
	{
		return nCon;
	}

	/**
	 * Tramite questo metodo viene settato il numero della variabile che stiamo gestendo
	 * @param i
	 */
	public void setNCon(int i)
	{
		nCon = i;
	}

	/**
	 * Tramite questo metodo verifico se la connessione è aperta
	 * @return Indica se la connessione è aperta
	 */
	public boolean isOpen()
	{
		return open;
	}

	/**
	 * Tramite questo metodo setto lo stato della connessione
	 * @param b
	 */
	public void setOpen(boolean b)
	{
		open = b;
	}

}

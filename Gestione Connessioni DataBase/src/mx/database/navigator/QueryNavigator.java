/**
 * 
 */
package mx.database.navigator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import mx.database.ConnectionPool;
import mx.database.table.Query;
import mx.randalf.converter.text.ConvertToURI;
import mx.randalf.converter.text.ConvertToUTF8;

import org.apache.axis.message.MessageElement;
import org.apache.log4j.Logger;

/**
 * Questa classe viene utilizzata per la gestione della navigazione delle
 * tabelle
 * 
 * @author Randazzo
 *
 */
public abstract class QueryNavigator extends Query {

	/**
	 * Questa variabile viene utilizzata per eseguire lo log delle applicazioni
	 */
	private static Logger log = Logger.getLogger(QueryNavigator.class);

	/**
	 * Questa variabile viene utilizzata per gestire la navigazione del
	 * risultato della ricerca
	 */
	protected Navigator navigator = null;

	/**
	 * Costruttore
	 */
	public QueryNavigator() {
		super();
		navigator = new Navigator();
	}

	/**
	 * Costruttore con la gestione del Pool di connessioni
	 * 
	 * @param conn
	 */
	public QueryNavigator(ConnectionPool conn) {
		super(conn);
		navigator = new Navigator();
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
	public QueryNavigator(ConnectionPool conn, Map<Object, Object> map) {
		super(conn, map);
		this.setNavigatorMap(map);
	}

	public void setNavigatorMap(Map<Object, Object> map) {
		navigator = new Navigator(map);
	}

	/**
	 * Questo metodo viene uitilizzato per indicare il numero di paggine che
	 * devono essere visualizzate alla volta
	 * 
	 * @param i
	 */
	public void setNumPagVisual(int i) {
		navigator.setNumPagVisual(i);
	}

	/**
	 * Questo metodo viene utilizzato per indicare il numero di record che
	 * devono essere visualizzate alla volta
	 * 
	 * @param i
	 */
	public void setNumRecVisual(int i) {
		navigator.setNumRecVisual(i);
	}

	/**
	 * Tramite questo metodo viene valorizzato il contenuto della variabile
	 * pathImgNav che indica la path dove si trovano le imamgini per il
	 * navigatore (Default: .\images\)
	 * 
	 * @param string
	 */
	public void setPathImgNav(String string) {
		navigator.setPathImgNav(string);
	}

	/**
	 * Questo metodo viene utilizzato per indicare il numero della pagina
	 * attualmente visualizzata
	 * 
	 * @param pagAttVisual
	 */
	public void setPagAttVisual(int pagAttVisual) {
		navigator.setPagAttVisual(pagAttVisual);
	}

	/**
	 * Questo metodo disegna i bottoni della navigazione
	 * 
	 * 
	 */
	public String viewNavigatore() {
		String ris = "";
		ris = navigator.viewNavigatore(this.campi);
		return ris;
	}

	/**
	 * Questo metodo disegna i bottoni della navigazione
	 * 
	 * 
	 */
	public MessageElement viewNavigatore(ConvertToUTF8 convert,
			ConvertToURI convertURI, String modulo, String azione) {
		MessageElement ris = null;
		ris = navigator.viewNavigatore(this.campi, this.campiOri, convert,
				convertURI, modulo, azione);
		return ris;
	}

	/**
	 * Questo metodo viene utilizzato per eseguire la ricerca nel database
	 * 
	 * @see mx.database.table.DataSet#startSelect()
	 */
	public ResultSet startSelect() {
		ResultSet rs = null;
		try {
			rs = super.startSelect();
			if (this.getRecTot() > 0) {
				navigator.calcNum(this.getRecTot());
				if (this.getRecIni() > 1) {
					rs.absolute(this.getRecIni() - 1);
				}
			}
		} catch (SQLException e) {
			log.error(e);
		}
		return rs;
	}

	/**
	 * Questo metodo viene utilizzato per visualizzare il numero del record di
	 * fine della visualizzazione
	 * 
	 */
	public int getRecFin() {
		return navigator.getRecFin();
	}

	/**
	 * Questo metodo viene utilizzato per visualizzare il numero di record di
	 * partenza della visualizzazione
	 * 
	 */
	public int getRecIni() {
		return navigator.getRecIni();
	}

}

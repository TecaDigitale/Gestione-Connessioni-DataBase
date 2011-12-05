/**
 * 
 */
package mx.database.navigator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.apache.axis.message.MessageElement;

import mx.database.ConnectionPool;
import mx.database.MsSqlPool;
import mx.database.table.Table;
import mx.log4j.Logger;
import mx.magEdit.mag.convert.ConvertToURI;
import mx.magEdit.mag.convert.ConvertToUTF8;

/**
 * Questa classe viene utilizzata per la gestione della navigazione delle tabelle
 * @author Randazzo
 *
 */
public abstract class TableNavigator extends Table
{

	/**
	 * Questa variabile viene utilizzata per loggare l'aplicaione
	 */
	private static Logger log = new Logger(TableNavigator.class, "mx.database");
	
  /**
   * Questa variabile viene utilizzata per gestire la navigazione del risultato della
   * ricerca
   */
  private Navigator navigator = null;

  /**
   * Costruttore
   */
  public TableNavigator()
  {
    super();
    navigator = new Navigator();
  }

  /**
   * Costruttore con la gestione del Pool di connessioni
   * @param conn
   */
  public TableNavigator(ConnectionPool conn)
  {
    super(conn);
    navigator = new Navigator();
  }
  
  /**
   * Costruttore con la gestione del Pool di connessioni con lista dei parametri da caricare
   * @param conn Pool di connessioni con il Database
   * @param map Lista parametri da caricare
   */
  @SuppressWarnings("unchecked")
	public TableNavigator(ConnectionPool conn, Map map)
  {
    super(conn, map);
    this.setNavigatorMap(map);
  }

  @SuppressWarnings("unchecked")
	public void setNavigatorMap(Map map)
  {
    navigator = new Navigator(map);
  }

  /**
   * Costruttore con la relativa della connessione aperta in precedenza
   * @param msp
   */
  public TableNavigator(MsSqlPool msp)
  {
    super(msp);
    navigator = new Navigator();
  }

  /**
   * Costruttore con definizione del Pool di connessione e connesione preselezionata per le 
   * operazioni di modifica della tabella
   * @param conn Poll di connessioni con il database
   * @param msp Connessione da utilizzare nelle operazioni di modifica della tabella (insert, 
   * update, delete)
   */
  public TableNavigator(ConnectionPool conn, MsSqlPool msp)
  {
    super(conn, msp);
    navigator = new Navigator();
  }

  /**
   * Costruttore con definizione del Pool di connessione e connesione
   * preselezionata per le operazioni di modifica della tabella con lista dei parametri da caricare
   * 
   * @param conn
   *          Poll di connessioni con il database
   * @param msp
   *          Connessione da utilizzare nelle operazioni di modifica della
   *          tabella (insert, update, delete)
   * @param map Lista parametri da caricare
   */
  @SuppressWarnings("unchecked")
	public TableNavigator(ConnectionPool conn, MsSqlPool msp, Map map)
  {
    super(conn, msp, map);
    navigator = new Navigator();
  }

  /**
   * Questo metodo viene uitilizzato per indicare il numero di paggine che devono essere visualizzate alla volta
   * @param i
   */
  public void setNumPagVisual(int i)
  {
    navigator.setNumPagVisual(i);
  }

  /**
   * Questo metodo viene utilizzato per indicare il numero di record che devono essere visualizzate alla volta
   * @param i
   */
  public void setNumRecVisual(int i)
  {
    navigator.setNumRecVisual(i);
  }

  /**
   * Tramite questo metodo viene valorizzato il contenuto della variabile pathImgNav che indica la path dove si 
   * trovano le imamgini per il navigatore (Default: .\images\)
   * @param string
   */
  public void setPathImgNav(String string)
  {
    navigator.setPathImgNav(string);
  }


  /**
   * Questo metodo disegna i bottoni della navigazione
   * 
   */
  public String viewNavigatore()
  {
    String ris = "";
    ris =navigator.viewNavigatore(this.campi);
    return ris;
  }

  /**
   * Questo metodo disegna i bottoni della navigazione
   * 
   * 
   */
  public MessageElement viewNavigatore(ConvertToUTF8 convert, ConvertToURI convertURI, String modulo, String azione)
  {
    MessageElement ris = null;
    ris =navigator.viewNavigatore(this.campi, this.campiOri, convert, convertURI, modulo, azione);
    return ris;
  }

  /**
   * Questo metodo viene utilizzato per eseguire la ricerca nel database
   * @see mx.database.table.DataSet#startSelect()
   */
  public ResultSet startSelect()
  {
    ResultSet rs = null;
    try
		{
			rs = super.startSelect();
			if (this.getRecTot()>0)
			{
			  navigator.calcNum(this.getRecTot());
			  if (this.getRecIni()>1)
			  {
			    rs.absolute(this.getRecIni()-1);
			  }
			}
		}
		catch (SQLException e)
		{
			log.error(e);
		}
    return rs;
  }

  /**
   * Questo metodo viene utilizzato per visualizzare il numero del record di fine della visualizzazione 
   * 
   */
  public int getRecFin()
  {
    return navigator.getRecFin();
  }

  /**
   * Questo metodo viene utilizzato per visualizzare il numero di record di partenza della visualizzazione
   * 
   */
  public int getRecIni()
  {
    return navigator.getRecIni();
  }

}

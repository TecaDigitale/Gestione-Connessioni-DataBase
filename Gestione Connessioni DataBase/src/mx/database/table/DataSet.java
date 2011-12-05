/*
 * Created on 24-mar-2006
 *
 */
package mx.database.table;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.xml.soap.SOAPException;

import mx.database.ConnectionPool;
import mx.database.MsSql;
import mx.database.MsSqlPool;
import mx.log4j.Logger;
import mx.xml.convert.ConvertToUTF8;

import org.apache.axis.message.MessageElement;
import org.w3c.dom.DOMException;

/**
 * Questa classe viene utilizzata per implementare tutti i metodi necessari per
 * gestire le tabelle
 * 
 * @author Randazzo Massimiliano
 * 
 */
public abstract class DataSet
{

	/**
	 * Questa variabile viene utilizzata per eseguire lo log delle applicazioni
	 */
	private static Logger log = new Logger(DataSet.class,"mx.database");

	/**
	 * Questa variabile viene utilizzata per dichiarare se eseguire l'encoding prima di fare l'operazione di Insert o Updare
	 */
	protected boolean encodingUtf8 = false;

  /**
   * Questa variabile viene utilizzata per indicare se stampare i messaggi in
   * Debug.
   */
  private boolean debug = false;

  /**
   * Questa variabile viene utilizzata per indicare se utilizzare la condizione
   * DISTINCT nella select
   */
  private boolean distinct = false;

  /**
   * Questa variabile viene utilizzata per indicare il numero totale di record
   * trovati
   */
  private int recTot = 0;

  /**
   * Questa variabile viene utilizzata per gestire il Pool di Connessioni
   */
  protected ConnectionPool conn = null;

  /**
   * Questa variabile viene utilizzata per gestire la lista di campi presenti
   */
  @SuppressWarnings("unchecked")
	protected Hashtable campi = null;

  /**
   * Questa variabile viene utilizzata per gestire la lista di campi presenti con la descrizione Originale
   */
  @SuppressWarnings("unchecked")
	protected Hashtable campiOri = null;

  /**
   * Questa variabile viene utilizzata per indicare le condizioni di from della
   * select
   */
  protected String from = "";

  /**
   * Questa variabile viene utilizzata per indicare le condizioni di where della
   * select
   */
  protected String where = "";

  /**
   * Questa variabile viene utilizzata per gestire il risultato della Select
   */
  private ResultSet rsSelect = null;

  /**
   * Questa variabile viene utilizzata per indicare la connessione selezionata
   * per la ricerca
   */
  protected MsSqlPool msp = null;

  /**
   * Questa variabile viene utilizzata per indicare la connessione selezionata
   * per la ricerca
   */
  private MsSqlPool mspSelect = null;

  /**
   * Questa variabile viene utilizzata per indicare se utilizzare la chiave per
   * indicare il nome della colonna
   */
  private boolean colKey = false;

  /**
   * Questo metodo viene utilizzato per verificare lo stato del Debug
   * @return Returns the debug.
   */
  protected boolean isDebug()
  {
    return debug;
  }

  /**
   * Costruttore
   */
  public DataSet()
  {
    super();
    defCampi();
  }

  /**
   * Costruttore con definizione del Pool di connessione e connesione
   * preselezionata per le operazioni di modifica della tabella
   * 
   * @param conn
   *          Poll di connessioni con il database
   * @param msp
   *          Connessione da utilizzare nelle operazioni di modifica della
   *          tabella (insert, update, delete)
   */
  public DataSet(ConnectionPool conn, MsSqlPool msp)
  {
    super();
    this.conn = conn;
    this.msp = msp;
    defCampi();
  }

  /**
   * Costruttore con definizione del Pool di connessione e connesione
   * preselezionata per le operazioni di modifica della tabella con lista dei
   * parametri da caricare
   * 
   * @param conn
   *          Poll di connessioni con il database
   * @param msp
   *          Connessione da utilizzare nelle operazioni di modifica della
   *          tabella (insert, update, delete)
   * @param map
   *          Lista parametri da caricare
   */
  @SuppressWarnings("unchecked")
	public DataSet(ConnectionPool conn, MsSqlPool msp, Map map)
  {
    super();
    this.conn = conn;
    this.msp = msp;
    defCampi();
    readMap(map);
  }

  /**
   * Costruttore con la gestione del Pool di connessioni con lista dei parametri
   * da caricare
   * 
   * @param conn
   *          Pool di connessioni con il Database
   * @param map
   *          Lista parametri da caricare
   */
  @SuppressWarnings("unchecked")
	public DataSet(ConnectionPool conn, Map map)
  {
    super();
    this.conn = conn;
    defCampi();
    readMap(map);

  }

  /**
   * Questo metodo viene utilizzato per leggere i parametri da caricare
   * 
   * @param map
   */
  @SuppressWarnings("unchecked")
	private void readMap(Map map)
  {
    String[] stringArray = null;
    String key = "";

    for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();)
    {
      Map.Entry entry = (Map.Entry) iterator.next();
      key = (String) entry.getKey();
      log.debug("key: " + key);

      stringArray = (String[]) entry.getValue();
      log.debug("value: "
          + stringArray[0]);
      if (!stringArray[0].equals(""))
        if (this.getCampo(key) != null)
        {
          try
          {
            this.setCampoValue(key, new Integer(stringArray[0]));
          }
          catch (NumberFormatException e)
          {
            this.setCampoValue(key, stringArray[0]);
          }
        }
    }
  }

  /**
   * Costruttore con definizione del Pool di connessione
   * 
   * @param conn
   *          Poll di connessioni con il database
   */
  public DataSet(ConnectionPool conn)
  {
    super();
    this.conn = conn;
    defCampi();
  }

  /**
   * Costruttore con definizione della connessione
   * 
   * @param msp
   *          Connessioni con il database
   */
  public DataSet(MsSqlPool msp)
  {
    super();
    this.msp = msp;
    defCampi();
  }

  /**
   * Questo metodo viene utilizzato per definire i campi da utilizzare nella
   * query
   * 
   */
  protected abstract void defCampi();

  /**
   * Questo metodo viene utilizzato per gestire l'operazione di reset dei campi
   * della tabella
   * 
   * @deprecated Metodo Deprecariato utilizzare resetAll()
   */
  public void reset()
  {
    this.resetAll();
  }

  /**
   * Questo metodo viene utilizzato per ripulire tutte le variabili d'ambiente
   * 
   */
  @SuppressWarnings("unchecked")
	public void resetAll()
  {
    String key = "";
    for (Enumeration e = campi.keys(); e.hasMoreElements();)
    {
      key = (String) e.nextElement();
      this.setCampoValue(key, null);
      this.setCampoTipoRicerca(key, "=");
    }
    recTot = 0;
    from = "";
    where = "";
  }

  /**
   * Questo metodo viene utilizzato per ricercare il campo della tabella
   * utilizzato come chiave primaria
   * 
   * @return restituisce la colonna che viene utilizzata come chiave primaria
   */
  @SuppressWarnings("unchecked")
	protected Vector getPrimaryKey()
  {
    Vector ris = new Vector();
    String key = "";
    Column col;
    for (Enumeration iter = campi.keys(); iter.hasMoreElements();)
    {
      key = (String) iter.nextElement();
      col = (Column) campi.get(key);
      if (col.isPrimaryKey())
      {
        ris.add(col);
      }
    }

    return ris;
  }

  /**
   * Questo metodo viene utilizzato per generare la lista del campi per la
   * select
   * 
   * @return ritorna la lista dei campi che compongono la select
   */
  @SuppressWarnings("unchecked")
	protected String genColumns()
  {
    String ris = "";
    String key = "";
    Column col;
    for (Enumeration iter = campi.keys(); iter.hasMoreElements();)
    {
      key = (String) iter.nextElement();
      col = (Column) campi.get(key);
      if (col.isView())
      {
        ris += ris.equals("") ? distinct ? "DISTINCT " : "" : ", ";
        if (!col.getTable().equals(""))
          ris += col.getTable() + ".";
        ris += col.getColumn();
        if (colKey)
          ris += " as " + key;
      }
    }
    return ris;
  }

  /**
   * Questo metodo viene utilizzato per generare la lista delle tabelle della
   * condizione from della select
   * 
   * @return ritorna la lista delle tabelle che compongono la select
   */
  @SuppressWarnings("unchecked")
	private String genFrom()
  {
    String ris = "";
    String key = "";
    Column col;
    Vector tables = new Vector();
    if (from.equals(""))
    {
      for (Enumeration iter = campi.keys(); iter.hasMoreElements();)
      {
        key = (String) iter.nextElement();
        col = (Column) campi.get(key);
        if ((!tables.contains(col.getTable())) && (!col.getTable().trim().equals("")))
        {
          ris += ris.equals("") ? "" : ", ";
          ris += col.getTable();
          tables.add(col.getTable());
        }
      }
    }
    else
      ris = from;
    return ris;
  }

  /**
   * Questo metodo viene utilizzato per generare la lista delle chiavi della
   * condizione di Where della select
   * 
   * @return ritorna la lista dei campi della where che compongono la select
   */
  @SuppressWarnings("unchecked")
	protected String genWhere()
  {
    String ris = "";
    String key = "";
    Column col;
    if (!where.equals(""))
      ris = where;
    for (Enumeration iter = campi.keys(); iter.hasMoreElements();)
    {
      key = (String) iter.nextElement();
      col = (Column) campi.get(key);
      if (col.isWhere())
      {
        if (col.getValue() != null)
        {
          ris += ris.equals("") ? "" : " " + col.getOperatore() + " ";
          if (col.getTipoRicerca().trim().equalsIgnoreCase("is null"))
          {
          	ris += "(";
          }
          ris += col.getTable() + "." + col.getColumn() + " "
              + col.getTipoRicerca();
          if ((!col.getTipoRicerca().trim().equalsIgnoreCase("is null")) &&
          		(!col.getTipoRicerca().trim().equalsIgnoreCase("is not null")))
          	ris += " " + col.getValueSql();
          
          if (col.getTipoRicerca().trim().equalsIgnoreCase("is null"))
          {
          	ris += " OR ";
            ris += col.getTable() + "." + col.getColumn() + " = '')";
          }
        }
      }
    }

    return ris;
  }

  /**
   * Questo metodo viene utilizzato per generare le chiavi di raggruppamento
   * 
   * 
   */
  @SuppressWarnings("unchecked")
	protected String genGroupBy()
  {
    String ris = "";
    String key = "";
    Column col;
    for (Enumeration iter = campi.keys(); iter.hasMoreElements();)
    {
      key = (String) iter.nextElement();
      col = (Column) campi.get(key);
      if (col.isGroupBy())
      {
        ris += ris.equals("") ? "" : ", ";
        ris += col.getTable() + "." + col.getColumn();
      }
    }
    return ris;
  }
  
  /**
   * Questo metodo viene utilizzato per generare la chiave per l'ordinamento
   * della ricerca
   * 
   * @return Chiave per l'ordinamento
   */
  @SuppressWarnings("unchecked")
	protected String genOrderBy()
  {
    String ris = "";
    String key = "";
    Column col;
    Hashtable ord = new Hashtable();
    Vector v;
    for (Enumeration iter = campi.keys(); iter.hasMoreElements();)
    {
      key = (String) iter.nextElement();
      col = (Column) campi.get(key);
      if (col.getOrderBy() != Column.ORDERBY_NONE)
      {
      	log.info("genOrdryBy: "+col.getTable()+" - "+col.getColumn()+"-"+col.getOrderBy()+" ("+(col.getTable() + "."
            + col.getColumn()
            + (col.getOrderBy() == Column.ORDERBY_DESC ? " DESC" : ""))+")");
        ord.put(col.getOrderByPosString(), col.getTable() + "."
            + col.getColumn()
            + (col.getOrderBy() == Column.ORDERBY_DESC ? " DESC" : ""));
      }
    }
    v = new Vector(ord.keySet());
    Collections.sort(v);
    for (Iterator iter = v.iterator(); iter.hasNext();)
    {
      ris += ris.equals("") ? "" : ", ";
      ris += (String) ord.get(iter.next());
    }
    return ris;
  }

  /**
   * Questo metodo viene utilizzato per eseguire la ricerca sul database
   * 
   * @return Restituisce il RecordSet relativo alla ricerca eseguita
   */
  public ResultSet startSelect()
  {
    String sql = "";
    String tmp = "";

    rsSelect = null;
    try
    {
      if (conn != null)
        mspSelect = conn.getConn();

      sql = "SELECT " + genColumns() + " FROM " + genFrom();

      tmp = genWhere();
      if (!tmp.equals(""))
        sql += " WHERE " + tmp;

      tmp = genGroupBy();
      if (!tmp.equals(""))
        sql += " GROUP BY " + tmp;

      tmp = genOrderBy();
      if (!tmp.equals(""))
        sql += " ORDER BY " + tmp;

      log.info("startSelect sql: " + sql);
      if (conn != null)
      {
        rsSelect = mspSelect.StartSelect(sql);
        this.recTot = mspSelect.getRecTot();
      }
      else
      {
        rsSelect = msp.StartSelect(sql);
        this.recTot = msp.getRecTot();
      }
    }
    catch (SQLException e)
    {
    	log.error(e);
    }
    catch (Exception e)
    {
    	log.error(e);
    }
    return rsSelect;
  }

  /**
   * Questo metodo viene utilizzato per eseguire la ricerca sul database
   * 
   * @return Restituisce il recordSet
   */
  public ResultSet open()
  {
    return startSelect();
  }

  /**
   * Questo metodo viene utilizzato per terminare l'operazione di Select
   * 
   */
  public void stopSelect()
  {
    try
    {
      if (rsSelect != null)
        rsSelect.close();
      if (mspSelect != null)
      {
        if (rsSelect != null)
          mspSelect.StopSelect();
        conn.releaseConn(mspSelect);
      }
      else if (msp != null && rsSelect != null)
        msp.StopSelect();
    }
    catch (SQLException e)
    {
    	log.error(e);
    }
  }

  /**
   * Questo metodo viene utilizzato per terminare l'operazione di Select
   * 
   */
  public void close()
  {
    stopSelect();
  }

  /**
   * Questo metodo viene utilizzato per aggiungere un nuovo campo nella ricerca
   * 
   * @param key
   *          Nome della chiave da associare all'oggetto
   * @param value
   *          Valore da associare all'oggetto
   */
  @SuppressWarnings("unchecked")
	protected void addCampi(String key, Column value)
  {
    if (campi == null)
      campi = new Hashtable();

    campi.put(key.toLowerCase(), value);

    if (campiOri == null)
      campiOri = new Hashtable();

    campiOri.put(key, value);
  }

  public void removeCampo(String campo)
  {
  	if (campi != null)
  		campi.remove(campo.toLowerCase());
  	if (campiOri != null)
  		campiOri.remove(campo);
  }
  /**
   * Questo metodo viene utilizzato per aggiungere un nuovo campo nella ricerca
   * 
   * @param key
   *          Nome della chiave da associare all'oggetto
   * @param table
   *          Nome della tabella del Database
   * @param column
   *          Nome della colonna della tabella del Database
   */
  protected void addCampi(String key, String table, String column)
  {
    this.addCampi(key, table, column, false);
  }

  /**
   * Questo metodo viene utilizzato per aggiungere un nuovo campo nella ricerca
   * 
   * @param key
   *          Nome della chiave da associare all'oggetto
   * @param table
   *          Nome della tabella del Database
   * @param column
   *          Nome della colonna della tabella del Database
   * @param primaryKey
   *          Indica se la colonna è una chiave primaria (default: false)
   */
  protected void addCampi(String key, String table, String column,
      boolean primaryKey)
  {
    this.addCampi(key, table, column, primaryKey, true, true);
  }

  /**
   * Questo metodo viene utilizzato per aggiungere un nuovo campo nella ricerca
   * 
   * @param key
   *          Nome della chiave da associare all'oggetto
   * @param table
   *          Nome della tabella del Database
   * @param column
   *          Nome della colonna della tabella del Database
   * @param primaryKey
   *          Indica se la colonna è una chiave primaria (default: false)
   * @param where
   *          Indica se la colonna deve essere utilizzata nella condizione di
   *          where (default: true)
   * @param view
   *          Indica se la colanna viene utilizzata come risultato della ricerca
   *          (default: true)
   */
  protected void addCampi(String key, String table, String column,
      boolean primaryKey, boolean where, boolean view)
  {
    this.addCampi(key, new Column(table, column, primaryKey, where, view));
  }

  /**
   * Questo metodo viene utilizzato per indicare il Pool di connessioni da
   * utilizzare
   * 
   * @param conn
   *          The conn to set.
   */
  public void setConn(ConnectionPool conn)
  {
    this.conn = conn;
  }

  /**
   * Questo metodo viene utilizzata per visualizzare il numero totale di record
   * trovati
   * 
   * @return Returns the recTot.
   */
  public int getRecTot()
  {
    return recTot;
  }

  /**
   * Questo mertodo viene utilizzato per leggere la lista del chiavi presenti
   * nel campo
   * 
   * 
   */
  @SuppressWarnings("unchecked")
	public Enumeration getCampoKeys()
  {
    return campi.keys();
  }

  /**
   * Questo metodo viene utilizzato per leggere il contenuto del campo che fa
   * riferimento alla chiave
   * 
   * @param key
   *          Chiave da ricercare
   * @return Returns the campi.
   */
  public Column getCampo(String key)
  {
    Column ris = null;
    try
    {
      ris = (Column) campi.get(key.toLowerCase());
    }
    catch (NullPointerException e)
    {
    	log.error(e);
    }
    return ris;
  }

  /**
   * Questo metodo viene utilizzato per valorizzare il valore di un determinato
   * campo
   * 
   * @param key
   *          Nome del campo da valorizzare
   * @param value
   *          Valore da inserire
   */
  public void setCampoValue(String key, Object value)
  {
    this.getCampo(key).setForceWhere(false);
    this.getCampo(key).setValue(value);
  }

  /**
   * Questo metodo viene utilizzato per valorizzare il valore di un determinato
   * campo
   * 
   * @param key
   *          Nome del campo da valorizzare
   * @param value
   *          Valore da inserire
   */
  public void setCampoValue(String key, int value)
  {
    this.getCampo(key).setForceWhere(false);
    this.getCampo(key).setValue(Integer.valueOf(value));
  }

  /**
   * Questo metodo viene utilizzato per valorizzare il valore di un determinato
   * campo
   * 
   * @param key
   *          Nome del campo da valorizzare
   * @param value
   *          Valore da inserire
   */
  public void setWhereValue(String key, Object value)
  {
    this.getCampo(key).setForceWhere(true);
    this.getCampo(key).setValue(value);
  }

  /**
   * Questo metodo viene utilizzato per valorizzare il valore di un determinato
   * campo
   * 
   * @param key
   *          Nome del campo da valorizzare
   * @param value
   *          Valore da inserire
   */
  public void setWhereValue(String key, int value)
  {
    this.getCampo(key).setForceWhere(true);
    this.getCampo(key).setValue(Integer.valueOf(value));
  }

  /**
   * Questo metodo viene utilizzato per indicare il tipo di ricerca da
   * utilizzare durante la condizione di Where
   * 
   * @param key
   *          Nome del campo da Modificare
   * @param tipoRic
   *          Tipo di ricerca da utilizzare default "="
   */
  public void setCampoTipoRicerca(String key, String tipoRic)
  {
    this.getCampo(key).setTipoRicerca(tipoRic);
  }

  public String get(String key)
  {
    String ris = "";
    Object risObj = null;
    risObj = this.getCampoValue(key);
    if (risObj != null)
    {
      if (risObj.getClass().getName().equals("java.lang.String"))
        ris = (String) risObj;
      else if (risObj.getClass().getName().equals("java.lang.Integer"))
        ris = ((Integer) risObj).toString();
      else if (risObj.getClass().getName().equals("java.sql.Timestamp"))
        ris = MsSql.conveDateTimeIta((Timestamp) risObj);
      else if (risObj.getClass().getName().equals("java.sql.Time"))
      	ris = MsSql.conveTimeIta((Time) risObj);
    }
    return ris;
  }

  /**
   * Questo metodo viene utilizzato per leggere il valore di un determinato
   * campo
   * 
   * @param key
   *          Nome del campo da valorizzare
   * 
   */
  public Object getCampoValue(String key)
  {
    return this.getCampo(key).getValue();
  }

  /**
   * Questo metodo viene utilizzato per leggere il valore di un determinato
   * campo
   * 
   * @param key
   *          Nome del campo da valorizzare
   * 
   */
  public int getIntCampoValue(String key)
  {
    return this.getCampo(key).getIntValue();
  }

  /**
   * Questo metodo viene utilizzato per leggere il valore di un determinato
   * campo
   * 
   * @param key
   *          Nome del campo da valorizzare
   * 
   */
  public int intValue(String key)
  {
    return this.getCampo(key).getIntValue();
  }

  /**
   * Quesrto mertodo viene utilizzato per indicare se stampare i messaggi di
   * Debug
   * 
   * @param debug
   */
  public void setDebug(boolean debug)
  {
    this.debug = debug;
  }

  /**
   * Questo metodo viene utilizzato per stampare il messaggio di Debug
   * 
   * @param posClas
   *          Classe e relativo metodo del messaggio
   * @param msg
   *          Messaggio
   */
  protected void writeDebug(String posClas, String msg)
  {
  	log.debug(msg);
  }

  /**
   * Questo metodo viene utilizzato per verificare se utilizzare la condizione
   * DISTINCT nella select
   * 
   * 
   */
  public boolean isDistinct()
  {
    return distinct;
  }

  /**
   * Questo metodo viene utilizzato per indicare se utilizzare la condizione
   * DISTINCT nella select
   * 
   * @param distinct
   */
  public void setDistinct(boolean distinct)
  {
    this.distinct = distinct;
  }

  /**
   * Questo metodo viene utilizzato per indicare se utilizzare la chiave per
   * indicare il nome della colonna
   * 
   * @param colKey
   */
  public void setColKey(boolean colKey)
  {
    this.colKey = colKey;
  }

  /**
   * Questo metodo viene utilizzato per indicare la connessione selezionata per
   * la ricerca
   * 
   * @param msp
   *          The msp to set.
   */
  public void setMsp(MsSqlPool msp)
  {
    this.msp = msp;
  }

  /**
   * Questo metodo viene utilizzato per leggere il contenuto di un singolo
   * record
   * 
   */
  @SuppressWarnings("unchecked")
	public void read()
  {
    ResultSet rs = null;
    String key = null;
    Vector pk = null;
    boolean esegui = true;
    try
    {
      pk = this.getPrimaryKey();
      if (pk.size() > 0)
      {
        for (int x = 0; x < pk.size(); x++)
        {
          log.debug("read Pk: "
              + ((Column) pk.get(x)).getValue());
          if (((Column) pk.get(x)).getValue() == null
              || ((Column) pk.get(x)).isEmpty())
            esegui = false;
        }
      }
      else
        esegui = false;
      log.debug("read esegui: " + esegui);
      if (esegui)
      {
        rs = this.startSelect();
        if (rs.next())
        {
          for (Enumeration e = this.getCampoKeys(); e.hasMoreElements();)
          {
          	try
          	{
              key = (String) e.nextElement();
              if (!this.getCampo(key).getTable().equals(""))
              	this.setCampoValue(key, rs.getObject(key));
          	}
          	catch (SQLException ex)
          	{
          		log.error(ex);
          	}
          }
        }
      }
    }
    catch (SQLException e)
    {
    	log.error(e);
    }
    finally
    {
      try
      {
        if (rs != null)
        {
          rs.close();
          this.stopSelect();
        }
      }
      catch (SQLException e)
      {
      	log.error(e);
      }
    }
  }

  /**
   * Questo metodo restituisce il risultato di una ricerca in formato XML
   * 
   * 
   */
  public MessageElement selectToXml()
  {
    return selectToXml(0);
  }

  /**
   * Questo metodo restituisce il risultato di una ricerca in formato XML
   * indicando il record a cui fermarsi nella ricerca
   * 
   * @param endRow
   *          Record a cui fermarsi nella ricerca del risultato
   * 
   */
  protected MessageElement selectToXml(int endRow)
  {
    MessageElement ris = null;
    ResultSet rs = null;

    try
    {
      rs = this.startSelect();

      if (endRow == 0)
        endRow = this.getRecTot();

      ris = new MessageElement();
      ris.setName("records");
      while (rs.next())
      {
        if (rs.getRow() <= endRow)
        {
          ris.addChildElement(rowToXml(rs));
        }
      }
    }
    catch (SQLException e)
    {
    	log.error(e);
    }
    catch (SOAPException e)
    {
    	log.error(e);
    }
    finally
    {
      try
      {
        if (rs != null)
        {
          rs.close();
          this.stopSelect();
        }
      }
      catch (SQLException e)
      {
      	log.error(e);
      }
    }

    return ris;
  }
  
  /**
   * Questo metodo viene utilizzato per convertire un record in XML
   * @param rs Record da convertire
   * @return Record convertito
   */
  @SuppressWarnings("unchecked")
	private MessageElement rowToXml(ResultSet rs)
  {
    MessageElement ris = null;
    String name = "";
    ArrayList nodes = new ArrayList();
    Object value = null;
    ConvertToUTF8 convertToUTF8 = null;

    try
    {
      convertToUTF8 = new ConvertToUTF8(null, "record");
      
      ris = new MessageElement();
      ris.setName("record");
      ris.setAttribute("row",Integer.toString(rs.getRow()));
      for(Enumeration e = this.getCampoKeys(); e.hasMoreElements(); )
      {
        name = (String) e.nextElement();
        log.debug("rowToXml Name:"+name);
        value = rs.getObject(name);
        if (value != null)
        {
          log.debug("rowToXml Tipo Class:"+value.getClass().getName());
          if (value.getClass().getName().equals("java.lang.String"))
            convertToUTF8.addChildElement(ris, nodes, name, (String)value);
          else if (value.getClass().getName().equals("java.lang.Integer"))
            convertToUTF8.addChildElement(ris, nodes, name, ((Integer)value).toString());
          else if (value.getClass().getName().equals("java.sql.Timestamp"))
            convertToUTF8.addChildElement(ris, nodes, name, MsSql.conveDateTimeIta((Timestamp)value));
          else if (value.getClass().getName().equals("java.math.BigDecimal"))
            convertToUTF8.addChildElement(ris, nodes, name, ((BigDecimal)value).toString());
        }
      }
    }
    catch (DOMException e)
    {
    	log.error(e);
    }
    catch (SQLException e)
    {
    	log.error(e);
    }
    catch (SOAPException e)
    {
    	log.error(e);
    }
    return ris;
  }

  /**
   * Questo metodo viene utilizzata per indicare le condizioni di where della select
   * @param where The where to set.
   */
  public void setWhere(String where)
  {
    this.where = where;
  }

  /**
   * Questo metodo viene utilizzato per verificare se eseguire l'operazione di Encoding Utf8 al momento della insert e update oppure no
   * @return
   */
	public boolean isEncodingUtf8()
	{
		return encodingUtf8;
	}

  /**
   * Questo metodo viene utilizzato per dichiarare se eseguire l'operazione di Encoding Utf8 al momento della insert e update oppure no
   * @return
   */
	public void setEncodingUtf8(boolean encodingUtf8)
	{
		log.debug("setEncodingUtf8: "+encodingUtf8);
		this.encodingUtf8 = encodingUtf8;
		log.debug("this.encodingUtf8: "+this.encodingUtf8);
	}

  /**
   * Questo metodo viene utilizzata per aggiungere le condizioni di where della select
   * @param where The where to set.
   */
  public void addWhere(String where)
  {
    this.where += " "+where;
  }
}

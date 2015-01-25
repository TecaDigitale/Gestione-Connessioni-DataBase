/*
 * Created on 24-mar-2006
 *
 */
package mx.database.table;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.Vector;

import org.apache.log4j.Logger;

import mx.database.MsSql;
import mx.database.table.interfacce.iGenID;

/**
 * Questa classe viene utilizzata per gestire il contenuto di ogni singola
 * colonna della tabella
 * 
 * @author Randazzo
 * 
 */
public class Column {

	private static Logger log = Logger.getLogger(Column.class);

	/**
	 * Questa variabile viene utilizzata per indicare il nome della tabella
	 */
	private String table = "";

	/**
	 * Questa variabile viene utilizzata per indicare il nome della colonna
	 * nella tabella
	 */
	private String column = "";

	/**
	 * Questa variabile viene utilizzata per indicare il contenuto della colonna
	 */
	private Object value = null;

	/**
	 * Questa variabile viene utilizzata per indicare se il campo � una chiave
	 * primaria.<BR>
	 * Default false.
	 */
	private boolean primaryKey = false;

	/**
	 * Questa variabile viene utilizzata per indicare la classe con la quale si
	 * ricalcola la chiave primaria
	 */
	private iGenID genID = null;

	/**
	 * Questa variabile viene utilizzata per indicare il codice da utilizzare
	 * per il calcolo della chiave primaria
	 */
	private String codeID = "";

	/**
	 * Questa variabile viene utilizzata per indicare la lunghezza della chiave
	 * da utilizzare per il calcolo della chiave primaria
	 */
	// private int lengthID = 0;
	/**
	 * Questa variabile viene utilizzata per indicare se il campo deve essere
	 * utilizzato come campo di ricerca nella Select.<BR>
	 * Default true.
	 */
	private boolean where = true;

	/**
	 * Questa variabile viene utilizzata per forzare il campo nella condizione
	 * di Where
	 */
	private boolean forceWhere = false;

	/**
	 * Questa variabile viene utilizzata per indicare se il campo deve essere
	 * utilizzato come campo del risultato della Select.<BR>
	 */
	private boolean view = true;

	/**
	 * Questa variabile viene utilizzata per indicare se il campo deve essere
	 * utilizzato come ordine nella select
	 */
	private int orderBy = 0;

	/**
	 * Questa variabile viene utilizzata per indicare l'ordine utilizzare per il
	 * sort della select
	 */
	private int orderByPos = 0;

	/**
	 * Questa variabile viene utilizzata per indicare il tipo di operatore
	 * boolean da adottare nella where.<BR>
	 * Default Column.AND
	 */
	private String operatore = "AND";

	/**
	 * Questa variabile viene utilizzata per indicare se il campo fa parte di un
	 * regruppamento
	 */
	private boolean groupBy = false;

	/**
	 * Questa variabile viene utilizzata per indicare la condizione da adottare
	 * per la ricerca.<BR>
	 * Default "="
	 */
	private String tipoRicerca = "=";

	/**
	 * variabile statica utilizzata per indicare che il campo viene usato per
	 * l'ordine decrescente della select
	 */
	public static int ORDERBY_DESC = -1;

	/**
	 * variabile statica utilizzata per indicare che il campo non viene
	 * utilizzato per l'ordinamento della select
	 */
	public static int ORDERBY_NONE = 0;

	/**
	 * variabile statica utilizzata per indicare che il campo viene utilizzato
	 * per l'ordinamento crescente della select
	 */
	public static int ORDERBY_CRES = 1;

	/**
	 * Variabile statica utilizzata per indicare l'operatore Booleano AND da
	 * adottare nella ricerca
	 */
	public static String AND = "AND";

	/**
	 * Variabile statica utilizzata per indicare l'operatore Booleano OR da
	 * adottare nella ricerca
	 */
	public static String OR = "OR";

	/**
	 * Variabile statica utilizzata per indicare l'operatore Booleano NOT da
	 * adottare nella ricerca
	 */
	public static String NOT = "NOT";

	/**
	 * Questo metodo viene utilizzato per verificare se il campo fa parte di un
	 * regruppamento
	 * 
	 * @return the groupBy
	 */
	public boolean isGroupBy() {
		return groupBy;
	}

	/**
	 * Questo metodo viene utilizzato per indicare se il campo fa parte di un
	 * regruppamento
	 * 
	 * @param groupBy
	 *            the groupBy to set
	 */
	public void setGroupBy(boolean groupBy) {
		this.groupBy = groupBy;
	}

	/**
	 * Costruttore
	 * 
	 * @param table
	 *            Nome della tabella da gestire
	 * @param column
	 *            Nome della colonna da gestire
	 */
	public Column(String table, String column) {
		super();
		this.table = table;
		this.column = column;
	}

	/**
	 * Costruttore
	 * 
	 * @param table
	 *            Nome della tabella da gestire
	 * @param column
	 *            Nome della colonna da gestire
	 * @param primaryKey
	 *            Indica se la colonna � una chiave primaria (default: false)
	 */
	public Column(String table, String column, boolean primaryKey) {
		super();
		this.table = table;
		this.column = column;
		this.primaryKey = primaryKey;
	}

	/**
	 * Costruttore
	 * 
	 * @param table
	 *            Nome della tabella da gestire
	 * @param column
	 *            Nome della colonna da gestire
	 * @param primaryKey
	 *            Indica se la colonna � una chiave primaria (default: false)
	 * @param where
	 *            Indica se la colonna deve essere utilizzata nella condizione
	 *            di where (default: true)
	 * @param view
	 *            Indica se la colanna viene utilizzata come risultato della
	 *            ricerca (default: true)
	 */
	public Column(String table, String column, boolean primaryKey,
			boolean where, boolean view) {
		super();
		this.table = table;
		this.column = column;
		this.primaryKey = primaryKey;
		this.where = where;
		this.view = view;
	}

	/**
	 * Costruttore
	 */
	public Column() {
	}

	/**
	 * Questo metodo viene utilizzata per leggere il nome della colonna nella
	 * tabella
	 * 
	 * @return Returns the column.
	 */
	public String getColumn() {
		return column;
	}

	/**
	 * Questo metodo viene utilizzata per indicare il nome della colonna nella
	 * tabella
	 * 
	 * @param column
	 *            The column to set.
	 */
	public void setColumn(String column) {
		this.column = column;
	}

	/**
	 * Questo metodo viene utilizzata per leggere il tipo di operatore boolean
	 * da adottare nella where.<BR>
	 * Default AND
	 * 
	 * @return Returns the operatore.
	 */
	public String getOperatore() {
		return operatore;
	}

	/**
	 * Questo metodo viene utilizzata per indicare il tipo di operatore boolean
	 * da adottare nella where.<BR>
	 * Default AND
	 * 
	 * @param operatore
	 *            The operatore to set.
	 */
	public void setOperatore(String operatore) {
		this.operatore = operatore;
	}

	/**
	 * Questo metodo viene utilizzata per verificare se il campo � una chiave
	 * primaria.<BR>
	 * Default false.
	 * 
	 * @return Returns the primaryKey.
	 */
	public boolean isPrimaryKey() {
		return primaryKey;
	}

	/**
	 * Questo metodo viene utilizzata per leggere il nome della tabella
	 * 
	 * @return Returns the table.
	 */
	public String getTable() {
		return table;
	}

	/**
	 * Questo metodo viene utilizzata per indicare il nome della tabella
	 * 
	 * @param table
	 *            The table to set.
	 */
	public void setTable(String table) {
		this.table = table;
	}

	/**
	 * Questo metodo viene utilizzata per leggere il contenuto della colonna
	 * 
	 * @return Returns the value.
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Questo metodo viene utilizzata per leggere il contenuto della colonna
	 * 
	 * @return Returns the value.
	 */
	public int getIntValue() {
		int ris = 0;
		if (value != null)
			if (value.getClass().getName().equals("java.lang.Integer"))
				ris = ((Integer) value).intValue();
			else if (value.getClass().getName().equals("java.lang.String")
					&& ((String) value).equals(""))
				ris = Integer.parseInt((String) value);
		return ris;
	}

	/**
	 * Questo metodo viene utilizzato per verificare se il contenuto di una
	 * colonna � vuoto
	 * 
	 * 
	 */
	public boolean isEmpty() {
		boolean ris = false;
		if (value == null)
			ris = true;
		else if (value.getClass().getName().equals("java.lang.Integer")
				&& ((Integer) value).intValue() == 0)
			ris = true;
		else if (value.getClass().getName().equals("java.lang.String")
				&& ((String) value).equals(""))
			ris = true;
		return ris;
	}

	/**
	 * Questo metodo viene utilizzata per leggere il contenuto della colonna
	 * 
	 * @return Returns the value.
	 */
	public String getValueSql() {
		return getValueSql(false);
	}

	/**
	 * Questo metodo viene utilizzata per leggere il contenuto della colonna
	 * 
	 * @param encodingUtf8
	 *            Questa variabile viene utilizzata per definire se utilizzare
	 *            la codifica in utf-8
	 * @return Returns the value.
	 */
	@SuppressWarnings("unchecked")
	public String getValueSql(boolean encodingUtf8) {
		String ris = "";
		Vector<String> valore = null;
		String myValue = null;
		String[] values = null;

		if (value.getClass().getName().equals("java.lang.String")) {
			log.debug("String: " + value);
			myValue = (String) value;
			log.debug("Encoding Utf8: " + encodingUtf8);

			try {
				log.debug("Convert - 1: "
						+ (new String(myValue.getBytes(), "UTF8")));
				log.debug("Convert - 2: "
						+ (new String(myValue.getBytes("UTF8"))));
				log.debug("Convert - 3: "
						+ (new String(myValue.getBytes("UTF8"), "UTF8")));
				log.debug("Convert - 4: "
						+ (new String(myValue.getBytes(), "UTF-8")));
				log.debug("Convert - 5: "
						+ (new String(myValue.getBytes("UTF-8"))));
				log.debug("Convert - 6: "
						+ (new String(myValue.getBytes("UTF-8"), "UTF-8")));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (encodingUtf8) {
				try {
					myValue = new String(myValue.getBytes(), "UTF8");
				} catch (UnsupportedEncodingException e) {
					log.error(e);
				}
			}
			/*
			 * if (encodingUtf8) { try { myValue = new
			 * String(myValue.getBytes(),"UTF8");
			 * log.debug("myValue: "+myValue); } catch
			 * (UnsupportedEncodingException e) { log.error(e); } }
			 */

			ris = "'" + MsSql.normString(myValue) + "'";
			log.debug("ris: " + ris);
		} else if (value.getClass().getName()
				.equals("java.util.GregorianCalendar"))
			ris = "'"
					+ MsSql.conveDateTimeEng(new Timestamp(
							((GregorianCalendar) value).getTimeInMillis()))
					+ "'";
		else if (value.getClass().getName().equals("java.sql.Timestamp"))
			ris = "'" + MsSql.conveDateTimeEng((Timestamp) value) + "'";
		else if (value.getClass().getName().equals("java.sql.Date"))
			ris = "'" + MsSql.conveDateEng((Date) value) + "'";
		else if (value.getClass().getName().equals(Time.class.getName()))
			ris = "'" + value.toString() + "'";
		else if (value.getClass().getName().equals("java.util.Vector")) {
			ris = "(";
			valore = ((Vector<String>) value);
			for (int x = 0; x < valore.size(); x++) {
				if ((!((String) valore.get(x)).equals(""))) {
					if (!ris.equals("("))
						ris += ", ";

					myValue = (String) valore.get(x);
					if (encodingUtf8) {
						try {
							myValue = new String(myValue.getBytes(), "UTF8");
						} catch (UnsupportedEncodingException e) {
							log.error(e);
						}
					}
					ris += "'" + MsSql.normString(myValue) + "'";
				}
			}
			ris += ")";
		} else if (value.getClass().getName().equals("[Ljava.lang.String;")) {
			ris = "(";
			values = (String[]) value;
			for (int x = 0; x < values.length; x++) {
				if ((!((String) values[x]).equals(""))) {
					if (!ris.equals("("))
						ris += ", ";

					myValue = (String) values[x];
					if (encodingUtf8) {
						try {
							myValue = new String(myValue.getBytes(), "UTF8");
						} catch (UnsupportedEncodingException e) {
							log.error(e);
						}
					}
					ris += "'" + MsSql.normString(myValue) + "'";
				}
			}
			ris += ")";
		} else {
			myValue = value.toString();
			if (encodingUtf8) {
				try {
					myValue = new String(myValue.getBytes(), "UTF8");
				} catch (UnsupportedEncodingException e) {
					log.error(e);
				}
			}
			ris = myValue;
		}
		return ris;
	}

	/**
	 * Questo metodo viene utilizzata per indicare il contenuto della colonna
	 * 
	 * @param value
	 *            The value to set.
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * Questo metodo viene utilizzata per verificare se il campo deve essere
	 * utilizzato nella come campo di ricerca nella Select.<BR>
	 * Default true.
	 * 
	 * @return Returns the where.
	 */
	public boolean isWhere() {
		return where;
	}

	/**
	 * Questo metodo viene utilizzata per indicare se il campo deve essere
	 * utilizzato nella come campo di ricerca nella Select.<BR>
	 * Default true.
	 * 
	 * @param where
	 *            The where to set.
	 */
	public void setWhere(boolean where) {
		this.where = where;
	}

	/**
	 * Questo metodo viene utilizzata per verificare se il campo deve essere
	 * utilizzato come campo del risultato della Select.<BR>
	 * 
	 * @return Returns the view.
	 */
	public boolean isView() {
		return view;
	}

	/**
	 * Questo metodo viene utilizzata per indicare se il campo deve essere
	 * utilizzato come campo del risultato della Select.<BR>
	 * 
	 * @param view
	 *            The view to set.
	 */
	public void setView(boolean view) {
		this.view = view;
	}

	/**
	 * Questo metodo viene utilizzato per verificare se al campo � applicato un
	 * ordinamento
	 * 
	 * @return Returns the orderBy.
	 */
	public int getOrderBy() {
		return orderBy;
	}

	/**
	 * Questo metodo viene utilizzato per indicare se il campo deve essere
	 * ordinato, in chemodo la posizione
	 * 
	 * @param orderBy
	 *            Questo campo viene utilizzato per indicare il tipo di
	 *            Ordinamento (Column.ORDERBY_DESC, Column.ORDERBY_NONE,
	 *            Column.ORDERBY_CRES)
	 * @param orderByPos
	 *            Questo campo viene utilizzato per indicare la posizione di
	 *            Ordinamento
	 */
	public void setOrderBy(int orderBy, int orderByPos) {
		log.info("setOrder: " + column + " -> " + orderBy + " = " + orderByPos);
		this.orderBy = orderBy;
		this.orderByPos = orderByPos;
	}

	/**
	 * Questo metodo viene utilizzto per verificare la posizione di ordinamento
	 * 
	 * @return Returns the orderByPos.
	 */
	public int getOrderByPos() {
		return orderByPos;
	}

	/**
	 * Questo metodo viene utilizzto per verificare la posizione di ordinamento
	 * 
	 * @return Returns the orderByPos.
	 */
	public String getOrderByPosString() {
		String ris = "";
		ris = Integer.toString(orderByPos);
		while (ris.length() < 10)
			ris = "0" + ris;
		return ris;
	}

	/**
	 * Questo metodo viene utilizzato per calcolare il nuovo Identificativo
	 * 
	 */
	public void genNewID() {
		if (value == null || ((Integer) value).intValue() == 0) {
			if (genID != null && !codeID.equals("")) {
				value = genID.genID(codeID);
			}
		}
	}

	/**
	 * Questo metodo viene utilizzato per indicare la classe utilizzata per il
	 * calcolo della chiave primaria e il relativo codice di associazione con la
	 * tabella
	 * 
	 * @param genID
	 *            The genID to set.
	 * @param codeID
	 *            Chiave della tabella
	 */
	public void setGenID(iGenID genID, String codeID) {
		this.genID = genID;
		this.codeID = codeID;
	}

	/**
	 * Questo metodo viene utilizzato per indicare la classe utilizzata per il
	 * calcolo della chiave primaria e il relativo codice di associazione con la
	 * tabella
	 * 
	 * @param genID
	 *            The genID to set. public void setGenID(iGenID genID, String
	 *            codeID, int lengthID) { this.setGenID(genID, codeID);
	 * 
	 *            this.lengthID = lengthID; }
	 */

	/**
	 * Questo metodo viene utilizzato per leggerela condizione da adottare per
	 * la ricerca.<BR>
	 * Default "="
	 * 
	 * @return Restituisce il valore richiesto
	 */
	public String getTipoRicerca() {
		return tipoRicerca;
	}

	/**
	 * Questo metodo viene utilizzato per indicare la condizione da adottare per
	 * la ricerca.<BR>
	 * Default "="
	 * 
	 * @param tipoRicerca
	 */
	public void setTipoRicerca(String tipoRicerca) {
		this.tipoRicerca = tipoRicerca;
	}

	/**
	 * Questo metodo viene utilizzata per indicare il campo nella condizione di
	 * Where
	 * 
	 * 
	 */
	public boolean isForceWhere() {
		return forceWhere;
	}

	/**
	 * Questo metodo viene utilizzata per valorizzare il campo nella condizione
	 * di Where
	 * 
	 * @param forceWhere
	 */
	public void setForceWhere(boolean forceWhere) {
		this.forceWhere = forceWhere;
	}

	/**
	 * Questo metodo viene utilizzato per indicare se il campi è una chiave
	 * primaria o pure no
	 * 
	 * @param primaryKey
	 *            The primaryKey to set.
	 */
	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}
}

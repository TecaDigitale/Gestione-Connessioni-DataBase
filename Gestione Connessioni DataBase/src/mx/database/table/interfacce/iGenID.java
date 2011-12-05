/*
 * Created on 24-mar-2006
 *
 */
package mx.database.table.interfacce;

/**
 * Questa interfaccia viene utilizzata per indicare i metodi necessari per la generazione 
 * della classe che verrà utilizzata per il calcolo degli identificativi
 * 
 * @author Randazzo Massimiliano
 *
 */
public interface iGenID
{

  /**
   * Questo metodo viene utilizzato per calcolare l'identificativo univoco per la chiave 
   * primaria
   * @param code codice che indica la tabella per la quale generare il codice
   * @return Identificativo calcolato
   */
  public Integer genID(String code);

}

/*
 * Created on 25-mar-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package mx.database.table.test;

import java.sql.ResultSet;
import java.sql.SQLException;

import mx.database.table.Column;
import mx.database.table.Query;
import mx.database.table.interfacce.iGenID;

/**
 * @author Randazzo
 *
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class CalcID extends Query implements iGenID {

	/**
   * 
   */
	public CalcID() {
		super();
		this.setFrom("tb_test");
		this.addCampi("ID", new Column("tb_test", "id_test", true));
		this.addCampi("Descrizione", new Column("tb_test", "descrizione"));
		this.addCampi("Data", new Column("tb_test", "Data"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.database.table.DataSet#reset()
	 */
	public void reset() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.database.table.interfacce.iGenID#genID(java.lang.String)
	 */
	public Integer genID(String code) {
		ResultSet rs;
		rs = null;
		int ris = 0;
		try {
			// System.out.println("genID");
			rs = this.startSelect();
			if (rs.next())
				ris = this.getRecTot();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				this.stopSelect();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		ris++;
		// System.out.println(code+" -> "+ris);
		return new Integer(ris);
	}

	protected void defFrom() {
		// TODO Auto-generated method stub

	}

	protected void defWhere() {
		// TODO Auto-generated method stub

	}

	protected void defCampi() {
		// TODO Auto-generated method stub

	}

}

/*
 * Created on 25-mar-2006
 *
 */
package mx.database.table.test;

import java.sql.ResultSet;
import java.sql.SQLException;

import mx.database.ConnectionPool;
import mx.database.MsSql;
import mx.database.MsSqlException;
import mx.database.table.Column;
import mx.database.table.Table;
import junit.framework.TestCase;

/**
 * @author Randazzo
 *
 */
public class TableTest extends TestCase
{

  private ConnectionPool conn =  null;
  private Table table = null;
  
  public static void main(String[] args)
  {
    junit.swingui.TestRunner.run(TableTest.class);
  }

  /**
   * Constructor for TableTest.
   * @param arg0
   */
  public TableTest(String arg0)
  {
    super(arg0);
  }

  /*
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception
  {
    super.setUp();
//    System.out.println("setUp");
    conn = new ConnectionPool();
    conn.setDBase("Test");
    conn.setNConn(2);
    conn.setPName("7210");
    conn.setPwd("TestUser");
    conn.setSName("localhost");
    conn.setTipoDb(MsSql.MAXDB);
    conn.setUName("TestUser");
    conn.loadConn();
    assertTrue("Connessione Aperta",true);

    table = new Table()
    {
      public void reset()
      {
      }

      protected void defCampi()
      {
        addCampi("ID", new Column("tb_test", "id_test",true));
        addCampi("Descrizione", new Column("tb_test", "descrizione"));
        addCampi("Data", new Column("tb_test", "Data"));
      }

			protected void postUpdate()
			{
				// TODO Auto-generated method stub
				
			}

			protected void preUpdate()
			{
				// TODO Auto-generated method stub
				
			}
    };
    table.setConn(conn);
  }

  /*
   * @see TestCase#tearDown()
   */
  protected void tearDown() throws Exception
  {
    super.tearDown();
//    System.out.println("tearDown");
    conn.closeAll();
  }

  public final void testTable()
  {
    ResultSet rs = null;
    try
    {
      table.getCampo("Data").setOrderBy(Column.ORDERBY_DESC,1);

      rs = table.startSelect();
      assertNotNull("Query eseguita",rs);
      assertTrue("Record Trovati",table.getRecTot()>0);
      while (rs.next())
      {
        System.out.println("===================================================");
        System.out.println("Record: "+rs.getRow()+"/"+table.getRecTot());
        System.out.println("id_Test: "+rs.getInt("id_test"));
        System.out.println("Descrizione: "+rs.getString("descrizione"));
        if (rs.getTimestamp("Data") != null)
          System.out.println("Data: "+rs.getTimestamp("Data").toString());
        System.out.println();
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    finally
    {
    	try
			{
				if (rs != null)
					rs.close();
				if (table != null)
					table.stopSelect();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
    }
  }

  public final void testInsert()
  {
    CalcID calcID;
    Column col;
    try
    {
      System.out.println("Insert");
      calcID = new CalcID();
      calcID.setConn(conn);

      col = table.getCampo("Descrizione");
      col.setValue("Prova di Inserimento");
      
      col = table.getCampo("ID");
      col.setGenID(calcID,"tb_test");
      assertTrue("Inserimento non eseguito",table.insert()>0);
    }
    catch (MsSqlException e)
    {
      e.printStackTrace();
    }
  }

  public final void testUpdate()
  {
    CalcID calcID;
    Column col;
    try
    {
      System.out.println("Update");
      calcID = new CalcID();
      calcID.setConn(conn);

      col = table.getCampo("Descrizione");
      col.setValue("Prova di Modifica");
      
      col = table.getCampo("ID");
      col.setGenID(calcID,"tb_test");
      col.setValue(new Integer(4));
      assertTrue("Inserimento non eseguito",table.insert()>0);
    }
    catch (MsSqlException e)
    {
      e.printStackTrace();
    }
  }

  public final void testDelete()
  {
    Column col;
    try
    {
      System.out.println("Delete");
      
      col = table.getCampo("ID");
      col.setValue(new Integer(4));
      assertTrue("Inserimento non eseguito",table.delete()>0);
    }
    catch (MsSqlException e)
    {
      e.printStackTrace();
    }
  }

}

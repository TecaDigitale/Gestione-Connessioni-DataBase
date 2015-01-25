/*
 * Created on 25-mar-2006
 *
 */
package mx.database.table.test;

import java.sql.ResultSet;
import java.sql.SQLException;
import mx.database.ConnectionPool;
import mx.database.MsSql;
import mx.database.table.Column;
import mx.database.table.Query;
import junit.framework.TestCase;

/**
 * Questa classe viene utilizzata per testare la classe Query
 * 
 * @author Randazzo Massimiliano
 *
 */
public class QueryTest extends TestCase
{

  private ConnectionPool conn =  null;
  private Query query = null;
  
  public static void main(String[] args)
  {
//    junit.swingui.TestRunner.run(QueryTest.class);
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

    query = new Query()
    {
      public void reset()
      {
      }

      protected void defFrom()
      {
        setFrom("tb_test");
      }

      protected void defWhere()
      {
        setWhere("descrizione<'pippo'");
      }

      protected void defCampi()
      {
        addCampi("ID", new Column("tb_test", "id_test",true));
        addCampi("Descrizione", new Column("tb_test2", "descrizione"));
        addCampi("Data", new Column("tb_test2", "Data"));
      }
    };
    query.setConn(conn);
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

  /**
   * Constructor for QueryTest.
   * @param arg0
   */
  public QueryTest(String arg0)
  {
    super(arg0);
 //   System.out.println("Costruttore");
  }

  public final void testQuery()
  {
    ResultSet rs = null;
    Column col = null;
    
    try
    {
      query.getCampo("Data").setOrderBy(Column.ORDERBY_CRES,1);
      col = query.getCampo("Descrizione");
      col.setTipoRicerca("<");
      col.setValue("pippo");
      query.setDebug(true);
      rs = query.startSelect();
      assertNotNull("Query eseguita",rs);
      assertTrue("Record Trovati",query.getRecTot()>0);
      while (rs.next())
      {
        System.out.println("===================================================");
        System.out.println("Record: "+rs.getRow()+"/"+query.getRecTot());
        System.out.println("id_Test: "+rs.getInt("id_test"));
        System.out.println("Descrizione: "+rs.getString("descrizione"));
        System.out.println("Data: "+rs.getString("Data")==null?"":rs.getTimestamp("Data").toString());
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
				if (query != null)
					query.stopSelect();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
    }
  }

}

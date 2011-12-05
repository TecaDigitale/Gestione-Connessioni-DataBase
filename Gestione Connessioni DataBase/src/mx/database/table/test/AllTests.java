/*
 * Created on 25-mar-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package mx.database.table.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Randazzo
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AllTests
{

  public static void main(String[] args)
  {
    junit.swingui.TestRunner.run(AllTests.class);
  }

  public static Test suite()
  {
    TestSuite suite = new TestSuite("Test for mx.database.table.test");
    //$JUnit-BEGIN$
    suite.addTestSuite(QueryTest.class);
    suite.addTestSuite(TableTest.class);
    //$JUnit-END$
    return suite;
  }
}

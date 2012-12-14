package com.sforce.ws.tools;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/*******************************************************************************
 * DOCUMENT ME!
 *
 * @author Victor Itkin
 * @version 1.0-SNAPSHOT
 */
public class wsdlcTest extends TestCase {
  //~ Constructors -------------------------------------------------------------

/*****************************************************************************
   * Create the test case
   * 
   * @param testName
   *            name of the test case
   */
  public wsdlcTest(String testName) {
    super(testName);
  }

  //~ Methods ------------------------------------------------------------------

  /*****************************************************************************
   * DOCUMENT ME!
   *
   * @throws Exception DOCUMENT ME!
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  /*****************************************************************************
   * DOCUMENT ME!
   *
   * @return the suite of tests being tested
   */
  public static Test suite() {
    return new TestSuite(wsdlcTest.class);
  }

  /*****************************************************************************
   * DOCUMENT ME!
   *
   * @throws Exception DOCUMENT ME!
   */
  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  /*****************************************************************************
   * Test of run method, of class wsdlc.
   *
   * @throws Exception DOCUMENT ME!
   */
  public void testRun() throws Exception {
    if (Boolean.parseBoolean(System.getProperty("runTests", "false"))) {
      System.out.println("run");

      System.setProperty("compileTarget", "1.6");

      String[] args = new String[] {
                                     "enterprise.wsdl",
                                     "target/sfdc-wsc-ws-enterprise.jar"
      };

      wsdlc.run(args);
    }
  }
}

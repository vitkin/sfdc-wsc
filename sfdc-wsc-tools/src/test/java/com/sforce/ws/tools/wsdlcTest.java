/*
 * #%L
 * sfdc-wsc-ws
 * %%
 * Copyright (C) 2013 The Apache Software Foundation.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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

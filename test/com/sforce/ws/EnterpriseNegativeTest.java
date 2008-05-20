/*
 * Copyright (c) 2005, salesforce.com, inc.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided 
 * that the following conditions are met:
 * 
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the 
 *    following disclaimer.
 *  
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and 
 *    the following disclaimer in the documentation and/or other materials provided with the distribution. 
 *    
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or 
 *    promote products derived from this software without specific prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED 
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A 
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR 
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED 
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.sforce.ws;

import com.sforce.soap.enterprise.fault.InvalidSObjectFault;
import com.sforce.soap.enterprise.fault.LoginFault;
import com.sforce.soap.enterprise.fault.UnexpectedErrorFault;
import com.sforce.soap.enterprise.fault.ExceptionCode;
import com.sforce.soap.enterprise.*;
import com.sforce.soap.enterprise.sobject.SObject;
import com.sforce.soap.enterprise.sobject.Account;

import junit.framework.TestCase;

/**
 * This class represents
 *
 * @author http://cheenath.com
 * @version 1.0
 * @since 1.0  Dec 8, 2005
 */
public class EnterpriseNegativeTest extends TestCase {
  private EnterpriseConnection connection;

  private String username = TestConfig.get("username");
  private String password = TestConfig.get("password");

  public EnterpriseNegativeTest() throws ConnectionException {
    connection = EnterpriseClientTest.enterpriseConnection(username, password);
  }

  public void testSetPassword() {
      try {
          connection.setPassword(null, "123456");
      } catch (ConnectionException e) {
          //found expected exception
      }
  }

  public void testLoginInException() {
    try {
      EnterpriseConnection c = EnterpriseClientTest.enterpriseConnection(username, password+10);
      fail("did not throw expected login fault");
    } catch (LoginFault e) {
        ExceptionCode code = e.getExceptionCode();
        if (ExceptionCode.INVALID_LOGIN != code) {
            fail("did not return expected exception code:" + ExceptionCode.INVALID_LOGIN);
        }
    } catch (ConnectionException e) {
      e.printStackTrace();
      fail("got connection exception instead of login fault:" + e);
    }
  }

  public void testDescribeSObject() throws ConnectionException {
    try {
      DescribeSObjectResult result = connection.describeSObject("Account" + "some junk object that is not available");
      fail("Did not throw expected invalid sobject fault");
    } catch(InvalidSObjectFault e) {
      //got the right exception
    } catch (UnexpectedErrorFault unexpectedErrorFault) {
      throw unexpectedErrorFault;
    } catch (ConnectionException e) {
      throw e;
    }
  }

  public void testDouble() throws ConnectionException {
      Account account = new Account();
      account.setName("test double");
      account.setAnnualRevenue(Double.NaN);
      connection.getConfig().setTraceMessage(true);
      createAccount(account);
      account.setAnnualRevenue(Double.POSITIVE_INFINITY);
      createAccount(account);
      account.setAnnualRevenue(Double.NEGATIVE_INFINITY);
      createAccount(account);
  }

    private void createAccount(Account account) throws ConnectionException {
        SaveResult[] result = connection.create(new SObject[]{account});
        if (result[0].getSuccess()) {
            fail("request did not fail");
        }
    }
}

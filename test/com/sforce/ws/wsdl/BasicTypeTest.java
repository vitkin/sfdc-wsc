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
package com.sforce.ws.wsdl;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.PartnerClientTest;
import junit.framework.TestCase;

/**
 * BasicTypeTest
 *
 * @author http://cheenath.com
 * @version 1.0
 * @since 1.0  Apr 13, 2006
 */
public class BasicTypeTest extends TestCase {
    private static PartnerConnection connection = PartnerClientTest.getConnection();


    public void testBasicTypes() throws ConnectionException {
        SObject account = new SObject();
        account.setType("Account");
        account.setField("Name", "Basic Type test");
        account.setField("AccountNumber", "972948548");
        account.setField("BillingCity", "Wichita");
        account.setField("BillingCountry", "US");
        account.setField("BillingState", "KA");
        account.setField("AnnualRevenue", 100.928);
        account.setField("BillingStreet", "4322 Haystack Boulevard");
        account.setField("BillingPostalCode", "87901");
        SaveResult[] result = connection.create(new SObject[]{account});
        assertEquals(result.length, 1);
        assertTrue(result[0].getSuccess());
        account.setId(result[0].getId());

        SObject[] newAccount = connection.retrieve("Name,Id,AnnualRevenue", "Account", new String[]{account.getId()});
        assertEquals(newAccount.length, 1);
        //SERVER SIDE BUG double annualRevenue = (Double)newAccount[0].getField("AnnualRevenue");
    }

}

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

import com.sforce.soap.partner.*;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.util.Verbose;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import junit.framework.TestCase;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * IntegrationTests
 *
 * @author http://cheenath.com
 * @version 1.0
 * @since 1.0  Mar 28, 2006
 */
public class IntegrationTests extends TestCase {
    private PartnerConnection connection = PartnerClientTest.getConnection();


    public void testNoLogin() throws ConnectionException {
        ConnectorConfig config = new ConnectorConfig();
        config.setManualLogin(true);
        config.setAuthEndpoint(TestConfig.get("partner-endpoint"));
        config.setServiceEndpoint(TestConfig.get("partner-endpoint"));
        PartnerConnection connection = Connector.newConnection(config);
        try {
            connection.getUserInfo();
            fail("Did not throw invalid Session ID exception");
        } catch(ConnectionException e) {
            //got expected exception
        }
    }

    public void testManualLogin() throws ConnectionException {
        ConnectorConfig config = new ConnectorConfig();
        config.setManualLogin(true);
        PartnerConnection connection = Connector.newConnection(config);
        config.setAuthEndpoint(TestConfig.get("partner-endpoint"));
        config.setServiceEndpoint(TestConfig.get("partner-endpoint"));
        connection.setCallOptions("some other client", null, null, false, null, null);
        LoginResult result = connection.login(TestConfig.get("username"), TestConfig.get("password"));
        connection.setSessionHeader(result.getSessionId());
        config.setServiceEndpoint(result.getServerUrl());
        connection.getUserInfo();
    }

    public void testNullDelete() throws ConnectionException {
        connection.delete(null);
        connection.delete(new String[]{null});
        connection.delete(new String[0]);
    }

    public void testDateTime() {
        XMLGregorianCalendar cal = XMLGregorianCalendarImpl.parse("2006-04-04T08:54:05.111-08:00");
        Verbose.log(cal);
    }

    public void testChildObjects() throws ConnectionException {
        SObject parent = new SObject();
        parent.setType("Account");
        parent.setField("Name", "Foo bar");
        SObject child = new SObject();
        child.setType("Account");
        child.setField("ExternalId__c", "12345");
        parent.addField("Parent", child);
        parent.setField("Id", "asdjfkasdj");

        //connection.getConfig().setTraceMessage(true);
        // not a bug, no external id
        //connection.upsert("ExternalId__c", new SObject[]{parent});
    }


    public void testCaseHistory() throws ConnectionException {
        QueryResult result = connection.query("select caseid, createdById, CreatedDate, field, " +
                                              "isDeleted, NewValue, OldValue from CaseHistory");
        System.out.println(result);
    }
}

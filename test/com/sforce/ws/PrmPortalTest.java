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

import java.util.Calendar;
import java.util.Iterator;

import com.sforce.soap.partner.*;
import com.sforce.soap.partner.fault.InvalidFieldFault;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.bind.XmlObject;
import com.sforce.ws.util.Verbose;

import junit.framework.TestCase;

/**
 * PrmPortalTest
 *
 * @author http://cheenath.com
 * @version 1.0
 * @since 1.0  Mar 23, 2006
 */
public class PrmPortalTest extends TestCase {
    private static PartnerConnection connection = PartnerClientTest.getConnection();

    public void testQuery() throws ConnectionException {
        connection.setCallOptions("PRMPortal/1.0", null, null, false, null, null);
        QueryResult result = connection.query("SELECT Id, Contact.Account.Id, Contact.Account.Name, " +
                                              "Contact.Account.OwnerId, Contact.Account.Owner.FirstName, Contact.Account.Owner.LastName " +
                                              "FROM User");

        SObject[] accounts = result.getRecords();

        for (SObject a : accounts) {
            Iterator<XmlObject> nodes = a.evaluate("Contact/Account/Owner/FirstName");
            while (nodes.hasNext()) Verbose.log(nodes.next());
        }
    }

    public void testBadQuery() {
        try {
            connection.query("select id,foo,bar from account");
        } catch (InvalidFieldFault e) {
            Verbose.log(e.getExceptionCode());
        } catch (ConnectionException e) {
            fail("got wrong exception");
        }
    }

    public void testCreateDocument() throws ConnectionException {
        String folderId;
        QueryResult qr = connection.query("select id from folder where name='TestFolder'");
        if (qr.getRecords() != null && qr.getRecords().length > 0) {
            folderId = qr.getRecords()[0].getId();
        } else {
            SObject f = new SObject();
            f.setField("Name", "TestFolder");
            f.setType("Document");
            f.setField("AccessType", "Shared");
            SaveResult fsr = connection.create(new SObject[]{f})[0];
            folderId = fsr.getId();
        }

        SObject d = new SObject();
        d.setType("Document");
        byte[] tooLargeDoc = new byte[1024 * 21]; // 21k
        for (int i = 0; i < tooLargeDoc.length; i++) {
            tooLargeDoc[i] = 99; // Just junk.
        }
        d.setField("Name", "TooLargeDoc");
        d.setField("FolderId", folderId);
        d.setField("Body", tooLargeDoc);
        SaveResult[] result = connection.create(new SObject[]{d});
        assertNotNull(result);

        QueryResult docQueryResult = connection.query("select Id, Name, Body from Document");
        SObject[] records = docQueryResult.getRecords();
        for (int i = 0; i < records.length; i++) {
            assertNotNull(records[i].getField("Name"));
        }
    }

    public void testFieldsToNull() throws ConnectionException {
        SObject contact = new SObject();
        contact.setType("Contact");
        contact.setField("Email", "foo@bar.com");
        contact.setField("LastName", "last-name");
        contact.setField("birthdate", Calendar.getInstance());
        contact.setField("AssistantPhone", "123 334 3344");
        contact.setField("HasOptedOutOfEmail", true);
        contact.setField("Fax", "828 383 3945");
        contact.setField("LeadSource", "Employee Referral");
        contact.setField("Title", "Title .....");
        SaveResult[] result = connection.create(new SObject[]{contact});

        contact.setId(result[0].getId());
        contact.removeField("Fax");
        contact.removeField("Title");
        contact.setFieldsToNull(new String[]{"Fax", "Title"});
        connection.update(new SObject[]{contact});
    }

    public void SoapHeaderResponse() throws ConnectionException {
        ConnectorConfig config = new ConnectorConfig();
        config.setSessionId("foobar");
        //config.setManualLogin(true);
        config.setTraceMessage(true);
        config.setPrettyPrintXml(true);
        //config.setAuthEndpoint("http://localhost:5555/getUserInfo.xml");
        config.setServiceEndpoint("http://localhost:5555/getUserInfo.php?/services/Soap/u/");
        PartnerConnection connection = Connector.newConnection(config);
        GetUserInfoResult userinfo = connection.getUserInfo();
        Verbose.log(userinfo);
        Verbose.log(connection.getSessionHeader().getSessionId());
    }

    public void testRetrieveNull() throws ConnectionException {
        SObject account1 = new SObject();
        account1.setType("Account");
        account1.setField("Name", "Foo Bar Company");
        account1.setField("BillingCountry", "UK");

        account1.setField("BillingCity", "Wichita");
        account1.setField("BillingState", "KA");
        account1.setField("BillingStreet", "4322 Haystack Boulevard");
        account1.setField("BillingPostalCode", "87901");

        SaveResult[] result = connection.create(new SObject[]{account1, account1});
        if (result.length != 2) fail("create failed");

        DeleteResult[] deleteResult = connection.delete(new String[]{result[0].getId(), result[1].getId()});
        if (deleteResult.length != 2 || deleteResult[0].getErrors().length != 0) fail("delete failed");

        SObject[] retrieveResult = connection.retrieve("Name,BillingCountry", "Account",
                                                       new String[]{result[0].getId(), result[1].getId()});

        System.out.println(retrieveResult);
    }
}

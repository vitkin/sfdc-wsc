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

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.bind.XmlObject;
import junit.framework.TestCase;

import java.util.Iterator;

/**
 * JoinQueryTest
 *
 * @author http://cheenath.com
 * @version 1.0
 * @since 1.0  Apr 18, 2006
 */
public class JoinQueryTest extends TestCase {
    private static PartnerConnection connection = PartnerClientTest.getConnection();

    public void testToLabelQuery() throws ConnectionException {
        QueryResult result = connection.query("select id, Fax, Name, salutation from Contact");
        assertNotNull(result);
    }

    public void testJoinQuery() throws ConnectionException {
        QueryResult result = connection.query("SELECT Id, IsDeleted, (SELECT Id, IsDeleted, " +
                "LastName FROM Contacts) FROM Account LIMIT 20");

        SObject[] sobjs = result.getRecords();
        for (SObject sobj : sobjs) {
            XmlObject contacts = (XmlObject) sobj.getField("Contacts");
            if (contacts != null) {
                checkContacts(contacts);
            }
        }
    }

    public void testMaxDouble() {
        double d = Double.MAX_VALUE;
        System.out.println((d/9223372036854775807L));
    }

    private void checkContacts(XmlObject contacts) {
        Iterator<XmlObject> records = contacts.getChildren("records");
        while(records.hasNext()) {
            XmlObject contact = records.next();
            System.out.println(contact.getField("Id"));
            System.out.println(contact.getField("LastName"));
        }
    }
}

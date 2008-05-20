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
package partner;

import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.Connector;

/**
 * This example shows how to connect to salesforce.com using
 * the partner WSDL.
 *
 * @author http://cheenath.com
 * @version 1.0
 * @since 1.0  Dec 16, 2005
 */
public class PartnerClient {

    private String username = "admin@training_mcheenath.com";
    private String password = "123456";

    private PartnerConnection connection;

    public PartnerClient() throws ConnectionException {
        ConnectorConfig config = new ConnectorConfig();
        config.setTraceMessage(false);
        config.setUsername(username);
        config.setPassword(password);
        //config.setAuthEndpoint("http://localhost:81/services/Soap/u/7.0");

        connection = Connector.newConnection(config);
        testCreate();
        queryAccount();
    }

    public void testCreate() throws ConnectionException {
        for (int i=0; i<5; i++) {
            create(i);
        }
    }

    private void create(int batch) throws ConnectionException {
        int count = 200;
        SObject[] accounts = new SObject[count];
        for (int i = 0; i < accounts.length; i++) {
            SObject account = new SObject();
            account.setType("Account");
            account.setField("Name", "Foo Bar Company " + i + ":" + batch);
            account.setField("AccountNumber", "972948548");
            account.setField("BillingCity", "Wichita");
            account.setField("BillingCountry", "US");
            account.setField("BillingState", "KA");
            account.setField("BillingStreet", "4322 Haystack Boulevard");
            account.setField("BillingPostalCode", "87901");
            accounts[i] = account;
        }
        SaveResult[] result = connection.create(accounts);
        System.out.println(batch + ":" + result.length);
    }

    private void createAccount() throws ConnectionException {
        System.out.println("Creating new Account");
        SObject account = new SObject();
        account.setType("Account");

        account.setField("Name", "My Account");
        account.setField("Phone", "123 244 3455");
        connection.create(new SObject[]{account});
        System.out.println("Account created");
    }

    private void queryAccount() throws ConnectionException {
        System.out.println("Query Account");

        QueryResult result = connection.query(
                "select id, name, phone from Account where name = 'My Account'");

        SObject[] accounts = result.getRecords();

        for (SObject account : accounts) {
            String phone = (String)account.getField("Phone");
            System.out.println(phone);
        }
    }

    public static void main(String[] args) throws ConnectionException {
        new PartnerClient();
    }
}

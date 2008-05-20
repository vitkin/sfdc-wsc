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
package custom;

import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import com.sforce.soap.enterprise.fault.test142.InvalidSObjectFault;
import com.sforce.soap.enterprise.sobject.test142.SObject;
import com.sforce.soap.enterprise.sobject.test142.MyCustomObject__c;
import com.sforce.soap.enterprise.test142.QueryResult;
import com.sforce.soap.enterprise.test142.EnterpriseConnection;
import com.sforce.soap.enterprise.test142.Connector;
import com.sforce.soap.enterprise.EnterpriseConnection;

/**
 * This example shows how to use custom objects
 *
 * @author http://cheenath.com
 * @version 1.0
 * @since 1.0  Dec 16, 2005
 */
public class CustomClient {

    private String username = "admin@training_mcheenath.com";
    private String password = "123456";
    private String endpoint = "https://www.salesforce.com/services/Soap/c/7.0";

    private EnterpriseConnection connection;

    public CustomClient() throws ConnectionException {
        ConnectorConfig config = new ConnectorConfig();
        config.setAuthEndpoint(endpoint);
        config.setUsername(username);
        config.setPassword(password);
        connection = Connector.newConnection(config);
        createMyCustomObject();
        queryMyCustomObject();
    }

    private void createMyCustomObject() throws ConnectionException, InvalidSObjectFault {
        System.out.println("Creating custom object");
        MyCustomObject__c customObject = new MyCustomObject__c();
        customObject.setName("My Custom Object");
        connection.create(new SObject[]{customObject});
        System.out.println("Custom object created");
    }

    private void queryMyCustomObject() throws ConnectionException {
        System.out.println("Query custome object");

        QueryResult result = connection.query(
                "select id, name from MyCustomObject__c where name = 'My Custom Object'");

        SObject[] objects = result.getRecords();

        for (SObject a : objects) {
            MyCustomObject__c customObject = (MyCustomObject__c) a;
            String name = customObject.getName();
            System.out.println(name);
        }
    }

    public static void main(String[] args) throws ConnectionException {
        try {
            new CustomClient();
        } catch (ConnectionException e) {
            e.printStackTrace();
            throw e;
        }
    }
}

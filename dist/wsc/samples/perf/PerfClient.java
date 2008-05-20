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
package perf;

import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
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
public class PerfClient {

    private String partnerUrl = "http://localhost:81/services/Soap/u/7.0";

    private String username = "mcheenath@mcheenath.com";
    private String password = "123456";

    private PartnerConnection connection;

    public PerfClient() throws ConnectionException {
        ConnectorConfig config = new ConnectorConfig();
        config.setAuthEndpoint(partnerUrl);
        config.setTraceMessage(true);
        config.setCompression(false);
        config.setUsername(username);
        config.setPassword(password);

        connection = Connector.newConnection(config);
        perf();
    }

    private void perf() throws ConnectionException {
        int count = 10;
        int size = 10;

        for (int i = 0; i < count; i++) {
            long start = System.currentTimeMillis();
            query(size);
            long end = System.currentTimeMillis();
            System.out.print((end-start)/(double)size);
            System.out.print(",");
        }
    }

    private void query(int size) throws ConnectionException {
        for (int i = 0; i < size; i++) {
            connection.query("select id, name, phone from Account");
            System.out.print(".");
        }
    }

    public static void main(String[] args) throws ConnectionException {
        new PerfClient();
    }
}

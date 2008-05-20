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

import com.sforce.soap.apex.*;
import com.sforce.ws.util.Verbose;

import junit.framework.TestCase;

/**
 * PlsoqlExecTest
 *
 * @author http://cheenath.com
 * @version 144
 * @since 144  Sep 20, 2006
 */
public class PlsoqlExecTest extends TestCase {

    private static SoapConnection connection = getConnection();

    private static SoapConnection getConnection() {
        ConnectorConfig config = new ConnectorConfig();
        //config.setServiceEndpoint("http://localhost:80/services/Soap/s/8.0");
        config.setServiceEndpoint(TestConfig.get("apex-endpoint"));
        config.setUsername(TestConfig.get("username"));
        config.setPassword(TestConfig.get("password"));
        config.setTraceMessage(Boolean.parseBoolean(TestConfig.get("trace")));
        config.setPrettyPrintXml(true);
        config.setSessionId(PartnerClientTest.getConnection().getSessionHeader().getSessionId());
        try {
            return Connector.newConnection(config);
        } catch (ConnectionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void testExecuteAnonymous() throws ConnectionException {
        connection.setDebuggingHeader(null, LogType.Db);
        ExecuteAnonymousResult result = connection.executeAnonymous("Integer i = 0; i++; System.assert(i == 1);");
        Verbose.log(connection.getDebuggingInfo().getDebugLog());
        //connection.compileTriggers()
        //connection.compileTriggers("foo bar");
    }

}

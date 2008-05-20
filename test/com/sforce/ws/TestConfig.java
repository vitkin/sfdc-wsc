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

import java.util.Map;
import java.util.Properties;
import java.io.*;

import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.partner.PartnerConnection;

/**
 * This class represents
 *
 * @author http://cheenath.com
 * @version 1.0
 * @since 1.0  Dec 8, 2005
 */
public class TestConfig {

    private static Map properties = loadProperties();

    private static Map loadProperties() {
        Properties map = new Properties();
        String file = "test.config";

        try {
            map.load(new FileInputStream(file));
        } catch (IOException e) {
            try {
                map.load(new FileInputStream(new File(new File("test"), file)));
            } catch (IOException e1) {
                e1.printStackTrace();
                throw new InternalError("Failed to load properties " + file);
            }
        }

        return map;
    }

    public static String get(String key) {
        return (String) properties.get(key);
    }

    public ConnectorConfig getPartnerConfig() throws ConnectionException {
        String username = TestConfig.get("username");
        String password = TestConfig.get("password");
        ConnectorConfig config = new ConnectorConfig();
        config.setAuthEndpoint(TestConfig.get("partner-endpoint"));
        config.setTraceMessage(Boolean.parseBoolean(TestConfig.get("trace")));
        config.setCompression(true);
        config.setUsername(username);
        config.setPassword(password);
        config.setPrettyPrintXml(true);
        return config;
    }

    public ConnectorConfig getEnterpriseConfig() throws ConnectionException {
        String username = TestConfig.get("username");
        String password = TestConfig.get("password");
        ConnectorConfig config = new ConnectorConfig();
        config.setAuthEndpoint(TestConfig.get("enterprise-endpoint"));
        config.setTraceMessage(Boolean.parseBoolean(TestConfig.get("trace")));
        config.setCompression(true);
        config.setUsername(username);
        config.setPassword(password);
        config.setPrettyPrintXml(true);
        return config;
    }

    public EnterpriseConnection getEnterpriseConnection() {
        try {
            return new EnterpriseConnection(getEnterpriseConfig());
        } catch (ConnectionException e) {
            throw new RuntimeException(e);
        }
    }

    public PartnerConnection getPartnerConnection() {
        try {
            return new PartnerConnection(getPartnerConfig());
        } catch (ConnectionException e) {
            throw new RuntimeException(e);
        }
    }
}

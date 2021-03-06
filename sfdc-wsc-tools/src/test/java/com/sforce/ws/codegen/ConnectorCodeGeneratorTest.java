/*
 * #%L
 * sfdc-wsc-tools
 * %%
 * Copyright (C) 2013 salesforce.com, inc.
 * %%
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 * 
 * 1) Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 * 
 * 2) Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * 3) Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
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
 * #L%
 */
package com.sforce.ws.codegen;

import org.stringtemplate.v4.*;

import com.sforce.ws.codegen.metadata.ConnectorMetadata;
import com.sforce.ws.tools.wsdlc;

import junit.framework.TestCase;

public class ConnectorCodeGeneratorTest extends TestCase {

    public void testGenerateConnectorSource() throws Exception {
        String expectedSource = CodeGeneratorTestUtil.fileToString("Connector.java");

        String className = "EnterpriseConnection";
        String packageName = "com.sforce.soap.enterprise";
        String endpoint = "https://login.salesforce.com/services/Soap/c/16.0";

        STGroupDir templates = new STGroupDir(wsdlc.TEMPLATE_DIR, '$', '$');
        ST template = templates.getInstanceOf(wsdlc.CONNECTOR);
        assertNotNull(template);
        template.add("gen", new ConnectorMetadata(packageName, className, endpoint));
        assertEquals(expectedSource, template.render());
    }
}

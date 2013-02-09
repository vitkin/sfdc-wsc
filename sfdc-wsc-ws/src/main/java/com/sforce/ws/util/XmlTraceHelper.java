/*
 * Copyright (c) 2013, Jesper Udby, judby.dk.
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
 *    Neither the name of judby.dk nor the names of its contributors may be used to endorse or 
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
package com.sforce.ws.util;

import java.io.IOException;
import java.io.PrintStream;

public class XmlTraceHelper {
    private final PrintStream traceStream;
    private final boolean prettyPrintXml;
    
    public XmlTraceHelper(PrintStream traceStream, boolean prettyPrintXml) {
		super();
		this.traceStream = traceStream;
		this.prettyPrintXml = prettyPrintXml;
	}

	public void trace(byte[] bytes) throws IOException {
        traceStream.println("------------ Response start ----------");

        String xml = new String(bytes, "UTF-8");
        if (prettyPrintXml) {
            prettyPrint(xml);
        } else {
            traceStream.print(xml);
        }

        traceStream.println();
        traceStream.println("------------ Response end   ----------");    	
    }
    
    private void prettyPrint(String xml) {
        final int length = xml.length();
    	int indent = 0;
        boolean newline = false;
        for (int i = 0; i < length; i++) {
			if (i < length - 1 && xml.charAt(i) == '<' && xml.charAt(i + 1) == '/') {
				indent--;
			}

			if (newline) {
                for (int j = 0; j < indent; j++) {
                    traceStream.print("  ");
                }
                newline = false;
            }
			
            if (i < length - 1 && xml.charAt(i) == '<' && xml.charAt(i + 1) != '?' && xml.charAt(i + 1) != '!' && xml.charAt(i + 1) != '/') {
                indent++;
            }

            traceStream.print(xml.substring(i, i+1));

            if (xml.charAt(i) == '>') {
                if (i < length - 1 && xml.charAt(i + 1) == '<') {
                    traceStream.println();
                    newline = true;
                }
                if (i > 0 && xml.charAt(i - 1) == '/') {
                	indent--;
                }
            }
        }
    }
}

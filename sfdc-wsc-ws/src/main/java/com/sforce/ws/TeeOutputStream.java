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
package com.sforce.ws;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class TeeOutputStream extends OutputStream {
    private final OutputStream out;
    private final PrintStream traceStream;

    public TeeOutputStream(OutputStream out, PrintStream traceStream) {
        this.out = out;
        this.traceStream = traceStream;
        this.traceStream.println("------------ Request start   ----------");
    }

    @Override
    public void write(int b) throws IOException {
    	traceStream.write((char) b);
        out.write(b);
    }

    @Override
    public void write(byte b[]) throws IOException {
    	traceStream.write(b);
        out.write(b);
    }

    @Override
    public void write(byte b[], int off, int len) throws IOException {
    	traceStream.write(b, off, len);
        out.write(b, off, len);
    }

    @Override
    public void close() throws IOException {
    	traceStream.println();
    	traceStream.flush();
        out.close();
        traceStream.println("------------ Request end   ----------");
    }
}

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
package com.sforce.ws.transport;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.Iterator;

import com.sforce.ws.ConnectorConfig;
import com.sforce.ws.MessageHandler;
import com.sforce.ws.tools.version;
import com.sforce.ws.util.FileUtil;
import com.sforce.ws.util.Base64;

/**
 * This class is an implementation of Transport using the build in
 * JDK URLConnection.
 *
 * @author http://cheenath.com
 * @version 1.0
 * @since 1.0  Nov 30, 2005
 */
public class JdkHttpTransport implements Transport {
    private HttpURLConnection connection;
    private boolean successful;
    private final ConnectorConfig config;
		private URL url;
		
    public JdkHttpTransport(ConnectorConfig config) {
        this.config = config;
    }

    public OutputStream connect(String uri, String soapAction) throws IOException {
        if (config.isTraceMessage()) {
            config.getTraceStream().println("WSC: Creating a new connection to " + uri +
                " Proxy = " + config.getProxy() +  " username " + config.getProxyUsername());
        }

        url = new URL(uri);
        connection = (HttpURLConnection) url.openConnection(config.getProxy());

        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.addRequestProperty("Content-Type", "text/xml; charset=UTF-8");
        connection.addRequestProperty("Accept", "text/xml");
        connection.addRequestProperty("User-Agent", version.info());

        if (config.useChunkedPost()) {
            connection.setChunkedStreamingMode(4096);
        }

        /*
         * Add all the client specific headers here
         */
        if (config.getHeaders() != null) {
            for (Entry<String, String> ent : config.getHeaders().entrySet()) {
                connection.setRequestProperty(ent.getKey(), ent.getValue());
            }
        }

        if (config.isCompression()) {
            connection.addRequestProperty("Content-Encoding", "gzip");
            connection.addRequestProperty("Accept-Encoding", "gzip");
        }

        if (config.getProxyUsername() != null) {
            String token = config.getProxyUsername() + ":" + config.getProxyPassword();
            String auth = "Basic " + new String(Base64.encode(token.getBytes()));
            connection.addRequestProperty("Proxy-Authorization", auth);
            connection.addRequestProperty("Https-Proxy-Authorization", auth);
        }

        if (soapAction == null) {
            soapAction = "";
        }

        connection.addRequestProperty("SOAPAction", "\"" + soapAction + "\"");

        if (config.getReadTimeout() != 0) {
            connection.setReadTimeout(config.getReadTimeout());
        }

        if (config.getConnectionTimeout() != 0) {
            connection.setConnectTimeout(config.getConnectionTimeout());
        }

        OutputStream output = connection.getOutputStream();

        if (config.getMaxRequestSize() > 0) {
            output = new LimitingOutputStream(config.getMaxRequestSize(), output);
        }

        if (config.isCompression()) {
            output = new GZIPOutputStream(output);
        }

        if (config.isTraceMessage()) {
            output = new TeeOutputStream(output);
        }

        if (config.hasMessageHandlers()) {
            output = new MessageHandlerOutputStream(output);
        }

        return output;
    }

    public InputStream getContent() throws IOException {
        InputStream in;

        try {
            successful = true;
            in = connection.getInputStream();
        } catch (IOException e) {
            successful = false;
            in = connection.getErrorStream();
            if (in == null) {
                throw e;
            }
        }

        String encoding = connection.getHeaderField("Content-Encoding");

        if (config.getMaxResponseSize() > 0) {
            in = new LimitingInputStream(config.getMaxResponseSize(), in);
        }

        if ("gzip".equals(encoding)) {
            in = new GZIPInputStream(in);
        }

        if (config.hasMessageHandlers() || config.isTraceMessage()) {
            byte[] bytes = FileUtil.toBytes(in);
            in = new ByteArrayInputStream(bytes);

            if (config.hasMessageHandlers()) {
                Iterator<MessageHandler> it = config.getMessagerHandlers();
                while(it.hasNext()) {
                    MessageHandler handler = it.next();
                    handler.handleResponse(url, bytes);
                }
            }

            if (config.isTraceMessage()) {
                new TeeInputStream(bytes);
            }
        }

        return in;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public class TeeInputStream {
        private int level = 0;

        TeeInputStream(byte[] bytes) {
            config.getTraceStream().println("------------ Response start ----------");

            if (config.isPrettyPrintXml()) {
                prettyPrint(bytes);
            } else {
                config.getTraceStream().print(new String(bytes));
            }

            config.getTraceStream().println();
            config.getTraceStream().println("------------ Response end   ----------");
        }

        private void prettyPrint(byte[] bytes) {
            boolean newLine = true;
            for (int i = 0; i < bytes.length; i++) {
                if (bytes[i] == '<') {
                    if (i + 1 < bytes.length) {
                        if (bytes[i + 1] == '/') {
                            level--;
                        } else {
                            level++;
                        }
                    }
                    for (int j = 0; newLine && j < level; j++) {
                        config.getTraceStream().print("  ");
                    }
                }

                config.getTraceStream().write(bytes[i]);

                if (bytes[i] == '>') {
                    if (i + 1 < bytes.length && bytes[i + 1] == '<') {
                        config.getTraceStream().println();
                        newLine = true;
                    } else {
                        newLine = false;
                    }
                }
            }
        }
    }

    public class MessageHandlerOutputStream extends OutputStream {
        private ByteArrayOutputStream bout = new ByteArrayOutputStream();
        private OutputStream output;

        public MessageHandlerOutputStream(OutputStream output) {
            this.output = output;
        }

        @Override
        public void write(int b) throws IOException {
            bout.write((char) b);
            output.write(b);
        }

        @Override
        public void write(byte b[]) throws IOException {
            bout.write(b);
            output.write(b);
        }

        @Override
        public void write(byte b[], int off, int len) throws IOException {
            bout.write(b, off, len);
            output.write(b, off, len);
        }

        @Override
        public void close() throws IOException {
            bout.close();
            output.close();

            Iterator<MessageHandler> it = config.getMessagerHandlers();

            while(it.hasNext()) {
                MessageHandler handler = it.next();
                handler.handleRequest(url, bout.toByteArray());
            }
        }
    }

    public class LimitingInputStream extends InputStream {

        private int maxSize;
        private int size;
        private InputStream in;

        public LimitingInputStream(int maxSize, InputStream in) {
            this.in = in;
            this.maxSize = maxSize;
        }

        private void checkSizeLimit() throws IOException {
            if (size > maxSize) {
                throw new IOException("Exceeded max size limit of " +
                        maxSize + " with response size " + size);
            }
        }

        @Override
        public int read() throws IOException {
            int result = in.read();
            size++;
            checkSizeLimit();
            return result;
        }

        @Override
        public int read(byte b[]) throws IOException {
            int len = in.read(b);
            size += len;
            checkSizeLimit();
            return len;
        }

        @Override
        public int read(byte b[], int off, int len) throws IOException {
            int length = in.read(b, off, len);
            size += length;
            checkSizeLimit();
            return length;
         }

        @Override
        public long skip(long n) throws IOException {
            long len = in.skip(n);
            size += len;
            checkSizeLimit();
            return len;
        }

        @Override
        public int available() throws IOException {
            return in.available();
        }

        @Override
        public void close() throws IOException {
            in.close();
        }

        @Override
        public synchronized void mark(int readlimit) {
            in.mark(readlimit);
        }

        @Override
        public synchronized void reset() throws IOException {
            in.reset();

        }

        @Override
        public boolean markSupported() {
            return in.markSupported();
        }
    }

    public class LimitingOutputStream extends OutputStream {
        private int size = 0;
        private int maxSize;
        private OutputStream out;

        public LimitingOutputStream(int maxSize, OutputStream out) {
            this.maxSize = maxSize;
            this.out = out;
        }

        @Override
        public void write(int b) throws IOException {
            size++;
            checkSizeLimit();
            out.write(b);
        }

        private void checkSizeLimit() throws IOException {
            if (size > maxSize) {
                throw new IOException("Exceeded max size limit of " +
                        maxSize + " with request size " + size);
            }
        }

        @Override
        public void write(byte b[]) throws IOException {
            size += b.length;
            checkSizeLimit();
            out.write(b);
        }

        @Override
        public void write(byte b[], int off, int len) throws IOException {
            size += len;
            checkSizeLimit();
            out.write(b, off, len);
        }
    }

    public class TeeOutputStream extends OutputStream {
        private OutputStream out;

        public TeeOutputStream(OutputStream out) {
            config.getTraceStream().println("------------ Request start   ----------");
            this.out = out;
        }

        @Override
        public void write(int b) throws IOException {
            config.getTraceStream().write((char) b);
            out.write(b);
        }

        @Override
        public void write(byte b[]) throws IOException {
            config.getTraceStream().write(b);
            out.write(b);
        }

        @Override
        public void write(byte b[], int off, int len) throws IOException {
            config.getTraceStream().write(b, off, len);
            out.write(b, off, len);
        }

        @Override
        public void close() throws IOException {
            config.getTraceStream().println();
            config.getTraceStream().flush();
            out.close();
            config.getTraceStream().println("------------ Request end   ----------");
        }
    }
}

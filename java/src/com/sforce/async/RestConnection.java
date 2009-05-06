/*
 * Copyright, 1999-2008, SALESFORCE.com
 * All Rights Reserved
 * Company Confidential
 */
package com.sforce.async;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.net.URL;
import java.net.HttpURLConnection;
import javax.xml.namespace.QName;

import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import com.sforce.ws.parser.XmlInputStream;
import com.sforce.ws.parser.PullParserException;
import com.sforce.ws.parser.XmlOutputStream;
import com.sforce.ws.bind.TypeMapper;
import com.sforce.ws.transport.JdkHttpTransport;
import com.sforce.ws.util.FileUtil;

/**
 * RestConnection
 *
 * @author mcheenath
 * @since 160
 */
public class RestConnection {

    public static final String NAMESPACE = "http://www.force.com/2009/06/asyncapi/dataload";
    private ConnectorConfig config;
    private HashMap<String, String> headers;
    public static final String SESSION_ID = "X-SFDC-Session";
    public static final QName JOB_QNAME = new QName(NAMESPACE, "jobInfo");
    public static final QName BATCH_QNAME = new QName(NAMESPACE, "batchInfo");
    public static final QName BATCH_LIST_QNAME = new QName(NAMESPACE, "batchInfoList");
    public static final QName ERROR_QNAME = new QName(NAMESPACE, "error");
    public static final String CONTENT_TYPE = "application/xml";
    public static final TypeMapper typeMapper = new TypeMapper();



    public RestConnection(ConnectorConfig config) throws AsyncApiException {
        if (config == null) {
            throw new AsyncApiException("config can not be null", AsyncExceptionCode.ClientInputError);
        }

        if (config.getRestEndpoint() == null) {
            throw new AsyncApiException("rest endpoint cannot be null", AsyncExceptionCode.ClientInputError);
        }

        this.config = config;

        if (config.getSessionId() == null) {
            try {
                login();
            } catch (IOException e) {
                throw new AsyncApiException("Failed to login", AsyncExceptionCode.ClientInputError, e);
            }
        }
        
        if (config.getSessionId() == null) {
            throw new AsyncApiException("session ID not found after login", AsyncExceptionCode.ClientInputError);
        }
    }

    private void login() throws IOException, AsyncApiException {
        URL restUrl = new URL(getRestEndpoint());

        String loginEndpoint = restUrl.getProtocol() + "://" + restUrl.getHost();

        if (restUrl.getPort() != -1) {
            loginEndpoint += ":" + restUrl.getPort();
        }
        
        loginEndpoint += "/login.jsp?un=" + config.getUsername() + "&pw=" + config.getPassword();

        HttpURLConnection connection = (HttpURLConnection) new URL(loginEndpoint).openConnection();

        Map<String, List<String>> map = connection.getHeaderFields();
        List<String> cookies = map.get("Set-Cookie");

        if (cookies == null) {
            throw new AsyncApiException("Failed to login to endpoint " + loginEndpoint, AsyncExceptionCode.ClientInputError);
        }

        for (String cookie : cookies) {
            if (cookie.startsWith("sid=")) {
                int index = cookie.indexOf(';');
                cookie = cookie.substring(0, index);
                cookie = cookie.substring("sid=".length());
                config.setSessionId(cookie);
            }
        }
    }

    public JobInfo createJob(String object, String operation) throws AsyncApiException {
        JobInfo job = new JobInfo();
        job.setObject(object);
        job.setOperation(OperationEnum.valueOf(operation));
        return createJob(job);
    }

    public JobInfo createJob(JobInfo job) throws AsyncApiException {
        String endpoint = getRestEndpoint();
        endpoint = endpoint + "job/";
        return createOrUpdateJob(job, endpoint);
    }

    private JobInfo createOrUpdateJob(JobInfo job, String endpoint) throws AsyncApiException {
        try {
            JdkHttpTransport transport = new JdkHttpTransport(config);
            HashMap<String, String> headers = getHeaders();
            OutputStream out = transport.connect(endpoint, headers);
            XmlOutputStream xout = new AsyncXmlOutputStream(out, true);
            job.write(JOB_QNAME, xout, typeMapper);
            xout.close();

            InputStream in = transport.getContent();


            if (transport.isSuccessful()) {
                XmlInputStream xin = new XmlInputStream();
                xin.setInput(in, "UTF-8");
                JobInfo result = new JobInfo();
                result.load(xin, typeMapper);
                return result;
            } else {
                parseAndThrowException(in);
            }
        } catch (PullParserException e) {
            throw new AsyncApiException("Failed to create job ", AsyncExceptionCode.ClientInputError, e);
        } catch (IOException e) {
            throw new AsyncApiException("Failed to create job ", AsyncExceptionCode.ClientInputError, e);
        } catch (ConnectionException e) {
            throw new AsyncApiException("Failed to create job ", AsyncExceptionCode.ClientInputError, e);
        }
        return null;
    }

    static void parseAndThrowException(InputStream in) throws AsyncApiException {
        try {
            XmlInputStream xin = new XmlInputStream();
            xin.setInput(in, "UTF-8");
            AsyncApiException exception = new AsyncApiException();
            exception.load(xin, typeMapper);
            throw exception;
        } catch (PullParserException e) {
            throw new AsyncApiException("Failed to parse exception ", AsyncExceptionCode.ClientInputError, e);
        } catch (IOException e) {
            throw new AsyncApiException("Failed to parse exception", AsyncExceptionCode.ClientInputError, e);
        } catch (ConnectionException e) {
            throw new AsyncApiException("Failed to parse exception ", AsyncExceptionCode.ClientInputError, e);
        }
    }

    private HashMap<String, String> getHeaders() {
        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        if (!headers.containsKey(SESSION_ID)) {
            headers.put(SESSION_ID, config.getSessionId());
        }
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public void addHeader(String headerName, String headerValue) {
        if (this.headers == null) {
            this.headers = new HashMap<String, String>();
        }
        this.headers.put(headerName, headerValue);
    }

    private String getRestEndpoint() {
        String endpoint = config.getRestEndpoint();
        endpoint = endpoint.endsWith("/") ? endpoint : endpoint + "/";
        return endpoint;
    }

    public BatchRequest createBatch(String jobId) throws AsyncApiException {
        try {
            String endpoint = getRestEndpoint();
            JdkHttpTransport transport = new JdkHttpTransport(config);
            endpoint = endpoint + "job/" + jobId + "/batch";
            OutputStream out = transport.connect(endpoint, getHeaders());

            return new BatchRequest(transport, out);
        } catch (IOException e) {
            throw new AsyncApiException("Failed to create batch", AsyncExceptionCode.ClientInputError, e);
        }
    }

    public BatchInfoList getBatchInfoList(String jobId) throws AsyncApiException {
        try {
            String endpoint  = getRestEndpoint() + "job/" + jobId + "/batch/";
            URL url = new URL(endpoint);
            InputStream stream = doHttpGet(url);

            XmlInputStream xin = new XmlInputStream();
            xin.setInput(stream, "UTF-8");
            BatchInfoList result = new BatchInfoList();
            result.load(xin, typeMapper);
            return result;
        } catch (IOException e) {
            throw new AsyncApiException("Failed to get batch info list ", AsyncExceptionCode.ClientInputError, e);
        } catch (PullParserException e) {
            throw new AsyncApiException("Failed to get batch info list ", AsyncExceptionCode.ClientInputError, e);
        } catch (ConnectionException e) {
            throw new AsyncApiException("Failed to get batch info list ", AsyncExceptionCode.ClientInputError, e);
        }
    }

    public BatchInfo getBatchInfo(String jobId, String batchId) throws AsyncApiException {
        try {
            String endpoint  = getRestEndpoint() + "job/" + jobId + "/batch/" + batchId;
            URL url = new URL(endpoint);
            InputStream stream = doHttpGet(url);

            XmlInputStream xin = new XmlInputStream();
            xin.setInput(stream, "UTF-8");
            BatchInfo result = new BatchInfo();
            result.load(xin, typeMapper);
            return result;
        } catch (IOException e) {
            throw new AsyncApiException("Failed to parse batch info ", AsyncExceptionCode.ClientInputError, e);
        } catch (PullParserException e) {
            throw new AsyncApiException("Failed to parse batch info ", AsyncExceptionCode.ClientInputError, e);
        } catch (ConnectionException e) {
            throw new AsyncApiException("Failed to parse batch info ", AsyncExceptionCode.ClientInputError, e);
        }
    }


    public BatchResult getBatchResult(String jobId, String batchId) throws AsyncApiException {
        try {
            String endpoint  = getRestEndpoint() + "job/" + jobId + "/batch/" + batchId + "/result";
            URL url = new URL(endpoint);
            InputStream stream = doHttpGet(url);

            XmlInputStream xin = new XmlInputStream();
            xin.setInput(stream, "UTF-8");
            BatchResult result = new BatchResult();
            result.load(xin, typeMapper);
            return result;
        } catch (PullParserException e) {
            throw new AsyncApiException("Failed to parse result ", AsyncExceptionCode.ClientInputError, e);
        } catch (IOException e) {
            throw new AsyncApiException("Failed to get result ", AsyncExceptionCode.ClientInputError, e);
        } catch (ConnectionException e) {
            throw new AsyncApiException("Failed to get result ", AsyncExceptionCode.ClientInputError, e);
        }
    }

    private InputStream doHttpGet(URL url) throws IOException, AsyncApiException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty(SESSION_ID, config.getSessionId());

        boolean success = true;
        InputStream in;
        try {
            in = connection.getInputStream();
        } catch(IOException e) {
            success = false;
            in = connection.getErrorStream();
        }

        if (config.isTraceMessage()) {
            byte[] bytes = FileUtil.toBytes(in);
            new JdkHttpTransport.TeeInputStream(config, bytes);
            in = new ByteArrayInputStream(bytes);
        }

        if (!success) {
            parseAndThrowException(in);
        }

        return in;
    }

    public JobInfo getJobStatus(String jobId) throws AsyncApiException {
        try {
            String endpoint = getRestEndpoint();
            endpoint += "/job/" + jobId;
            URL url = new URL(endpoint);

            InputStream in = doHttpGet(url);
            JobInfo result = new JobInfo();
            XmlInputStream xin = new XmlInputStream();
            xin.setInput(in, "UTF-8");
            result.load(xin, typeMapper);
            return result;
        } catch (PullParserException e) {
            throw new AsyncApiException("Failed to get job status ", AsyncExceptionCode.ClientInputError, e);
        } catch (IOException e) {
            throw new AsyncApiException("Failed to get job status ", AsyncExceptionCode.ClientInputError, e);
        } catch (ConnectionException e) {
            throw new AsyncApiException("Failed to get job status ", AsyncExceptionCode.ClientInputError, e);
        }
    }

    public JobInfo abortJob(String jobId) throws AsyncApiException {
        JobInfo job = new JobInfo();
        job.setId(jobId);
        job.setState(JobStateEnum.Aborted);
        return updateJob(job);
    }

    public JobInfo closeJob(String jobId) throws AsyncApiException {
        JobInfo job = new JobInfo();
        job.setId(jobId);
        job.setState(JobStateEnum.Closed);
        return updateJob(job);
    }

    public JobInfo updateJob(JobInfo job) throws AsyncApiException {
        String endpoint = getRestEndpoint();
        endpoint += "/job/" + job.getId();
        return createOrUpdateJob(job, endpoint);
    }

    public ConnectorConfig getConfig() {
        return config;
    }
}

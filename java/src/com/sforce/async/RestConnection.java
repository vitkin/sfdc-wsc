/*
 * Copyright, 1999-2008, SALESFORCE.com
 * All Rights Reserved
 * Company Confidential
 */
package com.sforce.async;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.zip.GZIPInputStream;

import javax.xml.namespace.QName;

import com.sforce.ws.*;
import com.sforce.ws.bind.TypeMapper;
import com.sforce.ws.parser.*;
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
    public static final String SESSION_ID = "X-SFDC-Session";
    public static final String XML_CONTENT_TYPE = "application/xml";
    public static final String CSV_CONTENT_TYPE = "text/csv";

    public static final QName JOB_QNAME = new QName(NAMESPACE, "jobInfo");
    public static final QName BATCH_QNAME = new QName(NAMESPACE, "batchInfo");
    public static final QName BATCH_LIST_QNAME = new QName(NAMESPACE, "batchInfoList");
    public static final QName ERROR_QNAME = new QName(NAMESPACE, "error");

    private ConnectorConfig config;
    private HashMap<String, String> headers = new HashMap<String, String>();
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
            throw new AsyncApiException("session ID not found", AsyncExceptionCode.ClientInputError);
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
            OutputStream out = transport.connect(endpoint, getHeaders(XML_CONTENT_TYPE));
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

    public void addHeader(String headerName, String headerValue) {
        headers.put(headerName, headerValue);
    }

    private String getRestEndpoint() {
        String endpoint = config.getRestEndpoint();
        endpoint = endpoint.endsWith("/") ? endpoint : endpoint + "/";
        return endpoint;
    }

    public BatchInfo createBatchFromStream(JobInfo jobInfo, InputStream input) throws AsyncApiException {
        try {
            String endpoint = getRestEndpoint();
            JdkHttpTransport transport = new JdkHttpTransport(config);
            endpoint = endpoint + "job/" + jobInfo.getId() + "/batch";
            String contentType = jobInfo.getContentType() == ContentType.CSV ? CSV_CONTENT_TYPE : XML_CONTENT_TYPE;
            OutputStream out = transport.connect(endpoint, getHeaders(contentType));

            FileUtil.copy(input, out);

            InputStream result = transport.getContent();
            return BatchRequest.loadBatchInfo(result);
        } catch (IOException e) {
            throw new AsyncApiException("Failed to create batch", AsyncExceptionCode.ClientInputError, e);
        } catch (PullParserException e) {
            throw new AsyncApiException("Failed to create batch", AsyncExceptionCode.ClientInputError, e);
        } catch (ConnectionException e) {
            throw new AsyncApiException("Failed to create batch", AsyncExceptionCode.ClientInputError, e);
        }
    }

    private HashMap<String, String> getHeaders(String contentType) {
        HashMap<String, String> newMap = new HashMap<String, String>();

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            newMap.put(entry.getKey(), entry.getValue());
        }

        newMap.put("Content-Type", contentType);
        newMap.put(SESSION_ID, config.getSessionId());
        return newMap;
    }

    public BatchRequest createBatch(JobInfo job) throws AsyncApiException {
        try {
            String endpoint = getRestEndpoint();
            JdkHttpTransport transport = new JdkHttpTransport(config);
            endpoint = endpoint + "job/" + job.getId() + "/batch";

            if (job.getContentType() == ContentType.CSV) {
                throw new AsyncApiException("This method can only be used with xml content type",
                        AsyncExceptionCode.ClientInputError);
            }

            OutputStream out = transport.connect(endpoint, getHeaders(XML_CONTENT_TYPE));
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

    public InputStream getBatchResultStream(String jobId, String batchId) throws AsyncApiException {
        try {
            String endpoint  = getRestEndpoint() + "job/" + jobId + "/batch/" + batchId + "/result";
            URL url = new URL(endpoint);
            return doHttpGet(url);
        } catch (IOException e) {
            throw new AsyncApiException("Failed to get result ", AsyncExceptionCode.ClientInputError, e);
        }
    }


    private InputStream doHttpGet(URL url) throws IOException, AsyncApiException {
        HttpURLConnection connection = JdkHttpTransport.createConnection(config, url, null);
        connection.setRequestProperty(SESSION_ID, config.getSessionId());

        boolean success = true;
        InputStream in;
        try {
            in = connection.getInputStream();
        } catch(IOException e) {
            success = false;
            in = connection.getErrorStream();
        }

        String encoding = connection.getHeaderField("Content-Encoding");
        if ("gzip".equals(encoding)) {
            in = new GZIPInputStream(in);
        }

        if (config.isTraceMessage() || config.hasMessageHandlers()) {
            byte[] bytes = FileUtil.toBytes(in);
            in = new ByteArrayInputStream(bytes);

            if (config.hasMessageHandlers()) {
                Iterator<MessageHandler> it = config.getMessagerHandlers();
                while (it.hasNext()) {
                    MessageHandler handler = it.next();
                    if (handler instanceof MessageHandlerWithHeaders) {
                        ((MessageHandlerWithHeaders) handler)
                                .handleRequest(url, new byte[0], null);
                        ((MessageHandlerWithHeaders) handler).handleResponse(url, bytes, connection.getHeaderFields());
                    } else {
                        handler.handleRequest(url, new byte[0]);
                        handler.handleResponse(url, bytes);
                    }
                }
            }

            if (config.isTraceMessage()) {
                new JdkHttpTransport.TeeInputStream(config, bytes);
            }
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

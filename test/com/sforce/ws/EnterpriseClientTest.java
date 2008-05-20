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

import junit.framework.TestCase;

import com.sforce.soap.enterprise.sobject.*;
import com.sforce.ws.util.Verbose;
import com.sforce.soap.enterprise.*;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * This class represents
 *
 * @author http://cheenath.com
 * @version 1.0
 * @since 1.0  Dec 2, 2005
 */
public class EnterpriseClientTest extends TestCase {
    private EnterpriseConnection connection;

    public EnterpriseClientTest() throws ConnectionException {
        String username = TestConfig.get("username");
        String password = TestConfig.get("password");
        connection = enterpriseConnection(username, password);
    }

    static EnterpriseConnection enterpriseConnection(String username, String password) throws ConnectionException {
        ConnectorConfig config = new ConnectorConfig();
        config.setAuthEndpoint(TestConfig.get("enterprise-endpoint"));
        config.setTraceMessage(Boolean.parseBoolean(TestConfig.get("trace")));
        config.setCompression(true);
        config.setUsername(username);
        config.setPassword(password);
        config.setPrettyPrintXml(true);
        return new EnterpriseConnection(config);
    }

    public void testHeaderAttributes() throws ConnectionException {
        connection.setDebuggingHeader(DebugLevel.None);
        CallOptions_element header = new CallOptions_element();
        header.setClient("foo bar");
        header.setMustUnderstand(true);
        header.setActor("some actor");
        connection.__setCallOptions(header);
        GetUserInfoResult user = connection.getUserInfo();
        connection.clearDebuggingHeader();
        connection.clearCallOptions();
    }

    public void testCreateDocument() throws ConnectionException {
        String folderId;
        QueryResult qr = connection.query("select id from folder where name='TestFolder'");// where name='My Personal Documents'");
        if (qr.getRecords() != null && qr.getRecords().length > 0) {
            folderId = qr.getRecords()[0].getId();
        } else {
            Folder f = new Folder();
            f.setName("TestFolder");
            f.setType("Document");
            f.setAccessType("Shared");
            SaveResult fsr = connection.create(new SObject[]{f})[0];
            folderId = fsr.getId();
        }

        Document d = new Document();
        byte[] tooLargeDoc = new byte[1024 * 21]; // 21k
        for (int i = 0; i < tooLargeDoc.length; i++) {
            tooLargeDoc[i] = 99; // Just junk.
        }
        d.setName("TooLargeDoc");
        d.setFolderId(folderId);
        d.setBody(tooLargeDoc);
        SaveResult[] result = connection.create(new SObject[]{d});
        assertNotNull(result);

        SObject[] res = connection.retrieve("name,bodyLength", "document", new String[]{result[0].getId()});
        Document resDoc = (Document) res[0];
        Verbose.log(resDoc.getBodyLength());
    }

    public void testCreate() throws ConnectionException {
        Account account1 = new Account();

        account1.setName("Foo Bar Company");
        account1.setAccountNumber("002DF99ELK9");
        account1.setBillingCity("Wichita");
        account1.setBillingCountry("US");
        account1.setBillingState("KA");
        account1.setBillingStreet("4322 Haystack Boulevard");
        account1.setBillingPostalCode("87901");
        account1.setAnnualRevenue(null);

        SaveResult[] result = connection.create(new SObject[]{account1});
        assertEquals(result.length, 1);
        account1.setId(result[0].getId());

        Contact contact = new Contact();
        contact.setAccountId(account1.getId());
        contact.setDepartment("foo bar department");
        contact.setEmail("my@contact.com");
        contact.setFirstName("first");
        contact.setLastName("last");
        result = connection.create(new SObject[]{contact});
        assertEquals(result.length, 1);
        contact.setId(result[0].getId());

        AccountContactRole role = new AccountContactRole();
        role.setAccountId(account1.getId());
        role.setContactId(contact.getId());
        result = connection.create(new SObject[]{role});
        assertEquals(result.length, 1);
    }

    public void testUpdate() throws ConnectionException {
        Account account1 = new Account();

        account1.setName("Foo Bar Company");
        account1.setBillingCountry("UK");

        SaveResult[] result = connection.create(new SObject[]{account1});

        if (result.length == 1) {
            String id = result[0].getId();
            account1.setId(id);
            account1.setBillingCountry("India");
            SaveResult[] updateResult = connection.update(new SObject[]{account1});
            if (updateResult.length == 1 && updateResult[0].getErrors().length == 0) {
                //ok
            } else {
                fail("update failed");
            }
        } else {
            fail("create failed");
        }
    }


    public void testDelete() throws ConnectionException {
        Account account1 = new Account();

        account1.setName("Foo Bar Company");
        account1.setBillingCountry("UK");

        SaveResult[] result = connection.create(new SObject[]{account1});

        if (result.length == 1) {
            String id = result[0].getId();
            DeleteResult[] deleteResult = connection.delete(new String[]{id});
            if (deleteResult.length == 1 && deleteResult[0].getErrors().length == 0) {
                //ok Verbose.log(deleteResult);
            } else {
                fail("delete failed");
            }
        } else {
            fail("create failed");
        }
    }

    public void testRetrieve() throws ConnectionException {
        Account account1 = new Account();
        account1.setName("Foo Bar Company");
        account1.setBillingCountry("UK");

        SaveResult[] result = connection.create(new SObject[]{account1});

        if (result.length == 1) {
            String id = result[0].getId();
            SObject[] retrieveResult = connection.retrieve("Name", "Account", new String[]{id});
            if (retrieveResult.length == 1) {
                Account account = (Account) retrieveResult[0];
                //ok Verbose.log(account);
            } else {
                fail("retrieve failed");
            }
        } else {
            fail("create failed");
        }
    }

    public void testConvertLead() throws ConnectionException {
        Account account = new Account();
        account.setName("Foo Bar Company");
        account.setBillingCountry("UK");

        createOne(account);

        Lead lead = new Lead();
        lead.setCountry("US");
        lead.setDescription("This is a description");
        lead.setEmail("someone@somewhere.com");
        lead.setFirstName("first");
        lead.setLastName("last");
        lead.setCompany(account.getName());

        createOne(lead);

        LeadConvert convert = new LeadConvert();
        convert.setAccountId(account.getId());
        convert.setLeadId(lead.getId());
        convert.setConvertedStatus("Qualified");
        //convert.setConvertedStatus("Closed - Converted");

        LeadConvertResult[] converted = connection.convertLead(new LeadConvert[]{convert});

        if (converted.length != 1 || converted[0].getErrors().length != 0) {
            fail("lead convert failed: " + Verbose.toString(converted));
        }
    }

    private void createOne(SObject object) throws ConnectionException {
        SaveResult[] result = connection.create(new SObject[]{object});

        if (result.length != 1) {
            fail("result != 1");
        }

        if (result[0].getErrors().length != 0) {
            fail("failed to create: " + object + " due to: " + result[0]);
        }

        object.setId(result[0].getId());
    }

    public void testGetUpdated() throws ConnectionException {
        Calendar end = new GregorianCalendar();
        Calendar start = new GregorianCalendar();
        start.add(Calendar.HOUR_OF_DAY, -1);
        GetUpdatedResult result = connection.getUpdated("Account", start, end);
        assertNotNull(result);
    }

    public void testGetDeleted() throws ConnectionException {
        Calendar end = new GregorianCalendar();
        Calendar start = new GregorianCalendar();
        start.add(Calendar.HOUR_OF_DAY, -1);
        GetDeletedResult result = connection.getDeleted("Account", start, end);
        assertNotNull(result);
    }

    public void testQuery() throws ConnectionException {
        QueryResult result = connection.query("SELECT Name from Account");
        SObject[] accounts = result.getRecords();

        for (SObject a : accounts) {
            Account account = (Account) a;
            assertNotNull(account);
        }
    }

    public void testSearch() throws ConnectionException {
        com.sforce.soap.enterprise.SearchResult result = connection.search("find {Foo Bar} in Name fields");
        SearchRecord[] records = result.getSearchRecords();
        //ok Verbose.log(records);
    }

    public void testSetPassword() throws ConnectionException {
        QueryResult result = connection.query("SELECT ID from User WHERE User.username='" +
                                              TestConfig.get("username") + "'");

        if (result.getSize() != 1) {
            fail("unable to find testuser");
        }

        User user = (User) result.getRecords()[0];
        String id = user.getId();
        SetPasswordResult setResult = connection.setPassword(id, TestConfig.get("password"));
        assertNotNull(setResult);
    }

    public void testResetPassword() throws ConnectionException {
        QueryResult result = connection.query("SELECT ID from User WHERE User.username='manoj@cheenath.com'");

        boolean skipReset = true;
        if (/*result.getSize() != 1*/ skipReset) { //if we reset password then, we cant run the rest of the test.
            System.out.println("Unable to find test user");
            return;
        }

        User user = (User) result.getRecords()[0];
        String id = user.getId();
        ResetPasswordResult resetResult = connection.resetPassword(id);
        assertNotNull(resetResult);
        testSetPassword();
    }


    @SuppressWarnings({"UNUSED_SYMBOL"})
    public void testQueryMore() throws ConnectionException {
        boolean gotMore = true;
        //connection.setQueryOptions(5);
        QueryResult result = connection.query("SELECT ID,name from Account");

        while (gotMore) {
            SObject[] accounts = result.getRecords();

            for (SObject a : accounts) {
                Account account = (Account) a;
            }

            if (result.getDone()) {
                gotMore = false;
            } else {
                result = connection.queryMore(result.getQueryLocator());
            }
        }
    }

    public void testDate() throws ConnectionException {
        QueryResult result = connection.query("select LastActivityDate from account");

        for (SObject sobj : result.getRecords()) {
            Account account = (Account) sobj;
            Verbose.log(account.getLastActivityDate());
        }
    }

    public void testDescribeTabs() throws ConnectionException {
        DescribeTabSetResult[] result = connection.describeTabs();
        assertNotNull(result);
    }

    public void testDescribeLayout() throws ConnectionException {
        DescribeLayoutResult result = connection.describeLayout("Contact", new String[0]);
        assertNotNull(result);
    }

    public void testDescribeGlobal() throws ConnectionException {
        DescribeGlobalResult result = connection.describeGlobal();
        assertNotNull(result);
    }

    public void testDescribeSObject() throws ConnectionException {
        DescribeSObjectResult result = connection.describeSObject("Account");
        assertNotNull(result);
    }

    public void testDescribeSObjects() throws ConnectionException {
        DescribeSObjectResult[] result = connection.describeSObjects(new String[]{"Account", "Case", "Lead"});
        assertNotNull(result);
    }

    public void testGetUserInfo() throws ConnectionException {
        GetUserInfoResult userInfo = connection.getUserInfo();
        assertNotNull(userInfo);
    }

    public void testPerf() throws ConnectionException {
        long start = System.currentTimeMillis();
        int count = 10;
        for (int i = 0; i < count; i++) {
            DescribeSObjectResult result = connection.describeSObject("Account");
        }
        long end = System.currentTimeMillis();
        System.out.println("time per message:" + ((end - start) / (double) count));
        double time = (end - start) / 1000.0; //time in sec

        System.out.println("mps:" + (count / time));
    }

    public void testGetServerTime() throws ConnectionException {
        GetServerTimestampResult timestamp = connection.getServerTimestamp();
        assertNotNull(timestamp);
        Calendar cal = timestamp.getTimestamp();
        Date date = cal.getTime();
        assertNotNull("Server time: " + date);
    }

    public void testCaseHistory() throws ConnectionException {
        QueryResult result = connection.query("select caseid, createdById, CreatedDate, field, " +
                                              "isDeleted, NewValue, OldValue from CaseHistory");
        SObject[] records = result.getRecords();
        for (SObject record : records) {
            CaseHistory history = (CaseHistory) record;
            Verbose.log(history.getIsDeleted());
        }
    }

    public void testNullInputField() throws ConnectionException {
        DeleteResult[] res = connection.delete(new String[] {"zanzibar", null, "foo"});
        if (res.length != 3) {
            fail("length != 3");
        }

        try {
            connection.setPassword(null, "byebye");
            fail("failed to throw exception");
        } catch (ConnectionException e) {
            //expected
        }
    }
}

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

import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.util.Verbose;
import com.sforce.ws.util.Base64;
import com.sforce.ws.bind.XmlObject;
import com.sforce.soap.partner.*;

import java.util.*;


/**
 * This class represents
 *
 * @author http://cheenath.com
 * @version 1.0
 * @since 1.0  Dec 2, 2005
 */
public class PartnerClientTest extends TestCase {
    private static PartnerConnection connection = getConnection();

    public static PartnerConnection getConnection() {
        synchronized (PartnerClientTest.class) {
            if (connection != null) {
                return connection;
            }
        }

        String username = TestConfig.get("username");
        String password = TestConfig.get("password");

        ConnectorConfig config = new ConnectorConfig();
        config.setAuthEndpoint(TestConfig.get("partner-endpoint"));
        config.setTraceMessage(Boolean.parseBoolean(TestConfig.get("trace")));
        config.setPrettyPrintXml(true);
        config.setUsername(username);
        config.setPassword(password);

        try {
            return new PartnerConnection(config);
        } catch (ConnectionException e) {
            throw new RuntimeException("Failed to create connection", e);
        }
    }

    public void testDecode() {
       String token = "dGhhc2VnYXdhOnRoYXNlZ2F3YQ==";
        //String token = "amh1cnN0OmpoYXNkMDk4Jg==";
        Verbose.log(new String(Base64.decode(token.getBytes())));
    }

    public void LookupCreate() throws ConnectionException {
        //connection.query("select id from user");
        SObject lookup = new SObject();
        lookup.setType("LookUptoAccOpp__c");
        lookup.setField("Name", "KIT Test Cust Obj 2");
        lookup.setField("Account__c", "001x0000003ARPk");
        lookup.setField("Opportunity__c", "006x0000001W73F");
        lookup.setField("OwnerId", "005x0000000fYOv");
        SaveResult[] result = connection.create(new SObject[]{lookup});
        //Verbose.log(result);
    }

    public void testCreateWithDate() throws ConnectionException {
        SObject contact = new SObject();
        contact.setType("Contact");
        contact.setField("Email", "foo@bar.com");
        contact.setField("LastName", "last-name");
        contact.setField("birthdate", Calendar.getInstance());
        contact.setField("AssistantPhone", "123 334 3344");
        contact.setField("HasOptedOutOfEmail", true);
        contact.setField("Fax", "828 383 3945");
        contact.setField("LeadSource", "Employee Referral");
        contact.setField("Title", "Title .....");
        SaveResult[] result = connection.create(new SObject[]{contact});
        assertEquals(result.length, 1);
    }

    public void perfTestCreate() throws ConnectionException {
        for (int i = 0; i < 10000; i++) {
            create(i);
        }
    }

    private void create(int batch) throws ConnectionException {
        int count = 100;
        SObject[] accounts = new SObject[count];
        for (int i = 0; i < accounts.length; i++) {
            SObject account = new SObject();
            account.setType("Account");
            account.setField("Name", "Foo Bar Company " + i + ":" + batch);
            account.setField("AccountNumber", "972948548");
            account.setField("BillingCity", "Wichita");
            account.setField("BillingCountry", "US");
            account.setField("BillingState", "KA");
            account.setField("BillingStreet", "4322 Haystack Boulevard");
            account.setField("BillingPostalCode", "87901");
            accounts[i] = account;
        }
        SaveResult[] result = connection.create(accounts);
        assertEquals(result.length, 1);
    }

    public void testUpdate() throws ConnectionException {
        SObject account1 = new SObject();
        account1.setType("Account");

        account1.setField("Name", "Foo Bar Company");
        account1.setField("BillingCountry", "UK");

        SaveResult[] result = connection.create(new SObject[]{account1});

        if (result.length == 1) {
            String id = result[0].getId();
            account1.setId(id);
            account1.setField("BillingCountry", "India");
            SaveResult[] updateResult = connection.update(new SObject[]{account1});

            if (updateResult.length == 1 && updateResult[0].getErrors().length == 0) {
                //ok Verbose.log(updateResult.length);
            } else {
                fail("update failed");
            }
        } else {
            fail("create failed");
        }
    }

    public void testDelete() throws ConnectionException {
        SObject account1 = new SObject();

        account1.setType("Account");
        account1.setField("Name", "Foo Bar Company");
        account1.setField("BillingCountry", "US");


        SaveResult[] result = connection.create(new SObject[]{account1});

        if (result.length == 1) {
            String id = result[0].getId();
            DeleteResult[] deleteResult = connection.delete(new String[]{id});
            if (deleteResult.length == 1 && deleteResult[0].getErrors().length == 0) {
                //ok Verbose.log(deleteResult.length);
            } else {
                fail("delete failed");
            }
        } else {
            fail("create failed");
        }
    }

    public void testRetrieve() throws ConnectionException {
        SObject account1 = new SObject();
        account1.setType("Account");
        account1.setField("Name", "Foo Bar Company");
        account1.setField("BillingCountry", "UK");

        account1.setField("BillingCity", "Wichita");
        account1.setField("BillingState", "KA");
        account1.setField("BillingStreet", "4322 Haystack Boulevard");
        account1.setField("BillingPostalCode", "87901");

        SaveResult[] result = connection.create(new SObject[]{account1});

        if (result.length == 1) {
            String id = result[0].getId();
            SObject[] retrieveResult = connection.retrieve("Name,BillingCountry", "Account", new String[]{id});
            if (retrieveResult.length == 1) {
                SObject account = retrieveResult[0];
                assertNotNull(account.getField("Name"));
            } else {
                fail("retrieve failed");
            }
        } else {
            fail("create failed");
        }
    }


    public void testConvertLead() throws ConnectionException {
        SObject account = new SObject();
        account.setType("Account");
        account.setField("Name", "Foo Bar Company");
        account.setField("BillingCountry", "UK");

        createOne(account);

        SObject lead = new SObject();
        lead.setType("Lead");
        lead.setField("Country", "US");
        lead.setField("Description", "This is a description");
        lead.setField("Email", "someone@somewhere.com");
        lead.setField("FirstName", "first");
        lead.setField("LastName", "last");
        lead.setField("Company", account.getField("Name"));

        createOne(lead);

        LeadConvert convert = new LeadConvert();
        convert.setAccountId(account.getId());
        convert.setLeadId(lead.getId());
        convert.setConvertedStatus("Qualified");

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
        GetDeletedResult result = connection.getDeleted("Event", start, end);
        //Verbose.log(result);
    }

    public void testQuery() throws ConnectionException {
        QueryResult result = connection.query("SELECT id,Name from Account");
        SObject[] accounts = result.getRecords();

        if (accounts != null) {
            for (SObject a : accounts) {
                String name = (String) a.getField("Name");
                assertNotNull(name);
            }
        } else {
            Verbose.log("No object found");
        }
    }


    public void testQueryComplex() throws ConnectionException {
        QueryResult result = connection.query("Select Account.Name, LastName, FirstName from Contact LIMIT 1");
        SObject[] contacts = result.getRecords();

        if (contacts != null) {
            for (SObject contact : contacts) {
                XmlObject account = (XmlObject) contact.getField("Account");
                if (account != null) {
                  String name = (String) account.getField("Name");
                  assertNotNull(name);
                }
                assertNotNull(contact.getField("LastName"));
                //assertNotNull(contact.getField("FirstName"));
            }
        } else {
            Verbose.log("No object found");
        }
    }

    public void testLookupQuery() throws ConnectionException {
        QueryResult result = connection.query("Select Name, Owner.FirstName, Owner.LastName from Account limit 1");
        SObject[] accounts = result.getRecords();

        if (accounts != null) {
            for (SObject account : accounts) {
                //Verbose.log(account);
                //Verbose.log(account.getField("Name"));
                XmlObject owner = (XmlObject) account.getField("Owner");
                assertNotNull(owner.getField("LastName"));
                assertNotNull(owner.getField("FirstName"));
            }
        } else {
            Verbose.log("No object found");
        }
    }

    public void testAggregateQuery() throws ConnectionException {
        QueryResult result =
                connection.query("Select Name, (Select FirstName, LastName From Contacts) from Account limit 1");
        SObject[] accounts = result.getRecords();

        if (accounts != null) {
            for (SObject account : accounts) {
                //Verbose.log(account);
                //Verbose.log(account.getField("Name"));
                //XmlObject owner = (XmlObject) account.getField("Owner");
                //assertNotNull(owner.getField("LastName"));
                //assertNotNull(owner.getField("FirstName"));
            }
        } else {
            Verbose.log("No object found");
        }
    }

    public void testMixedQuery() throws ConnectionException {
        QueryResult result = connection.query("Select AccountId, Account.Name, " +
                                              "(Select Contact.FirstName, Contact.LastName From OpportunityContactRoles)" +
                                              " from Opportunity limit 1");

        SObject[] opportunitys = result.getRecords();

        if (opportunitys != null) {
            for (SObject opportunity : opportunitys) {
                //Verbose.log(opportunity.getField("AccountId"));
                XmlObject account = opportunity.getChild("Account");
                if (account != null) {
                    //Verbose.log(account.getField("Name"));
                    //Verbose.log(account.getXmlType());
                }
                XmlObject roles = opportunity.getChild("OpportunityContactRoles");
                if (roles != null) {
                    //Verbose.log(roles.getXmlType());
                    Iterator<XmlObject> it = roles.getChildren("records");
                    while (it.hasNext()) {
                        XmlObject role = it.next();
                        XmlObject contact = (XmlObject) role.getField("Contact");
                        //Verbose.log(contact.getField("LastName"));
                        //Verbose.log(contact.getField("FirstName"));
                    }
                }
            }
        } else {
            Verbose.log("No object found");
        }
    }

    public void testQueryXpath() throws ConnectionException {
        QueryResult result = connection.query("Select AccountId, Account.Name, " +
                                              "(Select Contact.FirstName, Contact.LastName From OpportunityContactRoles)" +
                                              " from Opportunity");

        SObject[] opportunitys = result.getRecords();

        if (opportunitys != null) {
            for (SObject opportunity : opportunitys) {
                Iterator<XmlObject> it = opportunity.evaluate("OpportunityContactRoles/records/Contact/LastName");
                while (it.hasNext()) {
                    it.next().getValue();
                }
            }
        } else {
            Verbose.log("No object found");
        }
    }

    public void testSearch() throws ConnectionException {
        SearchResult result = connection.search("find {Foo Bar} in Name fields");
        SearchRecord[] records = result.getSearchRecords();
        //ok Verbose.log(records.length);
    }


    public void testSetPassword() throws ConnectionException {
        QueryResult result = connection.query("SELECT ID from User WHERE User.username='" +
                                              TestConfig.get("username") +
                                              "'");

        if (result.getSize() != 1) {
            fail("unable to find testuser");
        }

        SObject user = result.getRecords()[0];
        String id = user.getId();
        SetPasswordResult setResult = connection.setPassword(id, TestConfig.get("password"));
        assertNotNull(setResult);
    }


    public void testResetPassword() throws ConnectionException {
        QueryResult result = connection.query("SELECT ID from User WHERE User.username='manoj@cheenath.com'");

        boolean skipReset = true;
        if (skipReset) {
            return;
        }

        if (result.getSize() != 1) {
            fail("unable to find testuser");
        }

        SObject user = result.getRecords()[0];
        String id = user.getId();
        ResetPasswordResult resetResult = connection.resetPassword(id);
        testSetPassword();
        assertNotSame(resetResult.getPassword(), null);
    }

    public void delete200KAccounts() throws ConnectionException {
        boolean gotMore = true;
        QueryResult result = connection.query("SELECT ID from Account");

        int total = 0;
        while (gotMore) {
            SObject[] accounts = result.getRecords();

            if (accounts != null) {
                ArrayList<String> ids = new ArrayList();

                for (SObject a : accounts) {
                    ids.add(a.getId());
                    if (ids.size() == 199) {
                        total = deleteIds(ids, total);
                    }
                }

                if (ids.size() > 0) {
                    total = deleteIds(ids, total);
                }
            }

            if (total > 200000) {
                return;
            }
            if (result.getDone()) {
                gotMore = false;
            } else {
                result = connection.queryMore(result.getQueryLocator());
            }
        }
    }

    private int deleteIds(ArrayList<String> ids, int total) throws ConnectionException {
        connection.delete(ids.toArray(new String[ids.size()]));
        total += ids.size();
        System.out.println(total);
        ids.clear();
        return total;
    }

    public void testQueryMore() throws ConnectionException {
        boolean gotMore = true;
        QueryResult result = connection.query("SELECT ID from Account");

        while (gotMore) {
            SObject[] accounts = result.getRecords();

            for (SObject a : accounts) {
                assertNotNull(a.getField("Id"));
            }

            if (result.getDone()) {
                gotMore = false;
            } else {
                result = connection.queryMore(result.getQueryLocator());
            }
        }
    }

    public void testDescribeTabs() throws ConnectionException {
        DescribeTabSetResult[] result = connection.describeTabs();
        assertNotNull(result);
    }

    public void testDescribeLayout() throws ConnectionException {
        DescribeLayoutResult result;
        result = connection.describeLayout("OpportunityLineItem", new String[0]);
        assertNotNull(result);
        result = connection.describeLayout("Contact", new String[0]);
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

    public void testCreatPerf() throws ConnectionException {
        long start = System.currentTimeMillis();
        int count = 100;
        for (int i = 0; i < count; i++) {
            //SObject sobject = new SObject();
            //sobject.setType("Contact");
            //sobject.setField("Email", "foo@bar.com");
            //sobject.setField("LastName", "last-name" + i);
            //SaveResult[] result = connection.create(new SObject[]{sobject});
            //if (result.length != 1) fail("perf test failed");
            connection.describeGlobal();
        }
        long end = System.currentTimeMillis();
        System.out.println("time per message:" + ((end - start) / (double) count));
        double time = (end - start) / 1000.0; //time in sec

        System.out.println("mps:" + (count / time));
    }

    public void testPerf() throws ConnectionException {
        long start = System.currentTimeMillis();
        int count = 100;
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
        Calendar cal = timestamp.getTimestamp();
        Date date = cal.getTime();
    }

    public void testIdCreate() throws ConnectionException {
        SObject account = new SObject();
        account.setType("Account");
        account.setField("Name", "manoj");
        account.setField("Phone", "2837484894");
        SaveResult[] result = connection.create(new SObject[]{account});

        SObject account2 = new SObject();
        account2.setType("Account");
        account2.setField("Name", "cheenath");
        account2.setField("Phone", "938475950");
        account2.setField("MasterRecordId", result[0].getId());
        result = connection.create(new SObject[]{account2});
    }

    public void testEmailTemplate() throws ConnectionException {
        /*
        {
        QueryResult result = connection.query("Select e.TimesUsed, e.TemplateType, e.TemplateStyle from EmailTemplate e order by TimesUsed");
            for (int i=0; i<result.getRecords().length; i++) {
                SObject s = result.getRecords()[i];
                Verbose.log(s.getField("TemplateType"));
                Verbose.log(s.getField("TemplateStyle"));
            }
        }*/

        QueryResult fres = connection.query("select id from folder");
        String folderId = null;

        if (fres.getRecords() != null && fres.getRecords().length > 0) {
            folderId = fres.getRecords()[0].getId();
        } else {
            throw new RuntimeException("Failed to find folder");
        }

        for (int i=0; i<50; i++) {
            //Verbose.log(i);
            //createEmailTemplates(folderId);
        }

        //connection.setQueryOptions(200);

        //QueryResult result = connection.query("Select e.TimesUsed from EmailTemplate e order by TimesUsed");
        //QueryResult result = connection.query("Select TimesUsed from EmailTemplate order by TimesUsed");

        //QueryResult result = connection.query("Select e.TimesUsed from EmailTemplate e order by TimesUsed limit 50000");
        QueryResult result = connection.query("Select e.TimesUsed from EmailTemplate e");
        //QueryResult result = connection.query("Select TimesUsed from EmailTemplate");
        //QueryResult result = connection.query("Select e.TimesUsed from EmailTemplate e order by e.TimesUsed");

        Verbose.log(result.getSize());
        Verbose.log(result.getQueryLocator());
    }

    private void createEmailTemplates(String folderId) throws ConnectionException {
        int count = 100;
        SObject[] templates = new SObject[count];

        for (int i=0; i<templates.length; i++) {
            templates[i] = new SObject();
            templates[i].setType("EmailTemplate");
            templates[i].setField("Name", "et - " + System.currentTimeMillis() + " count " +  i);
            templates[i].setField("FolderId", folderId);
            templates[i].setField("TemplateStyle", "none");
            templates[i].setField("TemplateType", "text");
        }
        SaveResult[] res = connection.create(templates);

        for (int i=0; i<res.length; i++) {
            if (!res[i].getSuccess()) {
                Verbose.log(res[i].getErrors()[0]);
            }
        }
    }
}

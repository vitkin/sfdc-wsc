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
package com.sforce.ws.bind;

import com.sforce.ws.parser.XmlInputStream;
import com.sforce.ws.parser.PullParserException;
import com.sforce.ws.parser.XmlOutputStream;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.TestConfig;
import com.sforce.ws.util.Verbose;
import com.sforce.ws.wsdl.Constants;

import java.io.FileInputStream;
import java.io.IOException;

import junit.framework.TestCase;

import javax.xml.namespace.QName;

/**
 * This class represents
 *
 * @author http://cheenath.com
 * @version 1.0
 * @since 1.0  Dec 5, 2005
 */
public class TestDescribeSObjectResult extends TestCase {

  public void testDescribeSObject() throws IOException, PullParserException, ConnectionException {
    FileInputStream fio = new FileInputStream(TestConfig.get("test-root") +
                                              "/com/sforce/ws/bind/describe-sobject-result.xml");

    XmlInputStream xin = new XmlInputStream();
    xin.setInput(fio, "UTF-8");
    TestObject1 object = new TestObject1();
    object.load(xin, new TypeMapper());
  }

  public static class TestObject1 implements XMLizable {

    public void write(QName element, XmlOutputStream out, TypeMapper typeMapper) throws IOException {
      throw new InternalError("NIY");
    }

    private String name;
    private static TypeInfo nameInfo = new TypeInfo(Constants.ENTERPRISE_NS, "name", Constants.SCHEMA_NS, "string", 1, 1, true);

    private int age;
    private static TypeInfo ageInfo = new TypeInfo(Constants.ENTERPRISE_NS, "age", Constants.SCHEMA_NS, "int", 1, 1, true);

    private TestObject2[] object2;
    private static TypeInfo object2Info = new TypeInfo(Constants.ENTERPRISE_NS, "object2", Constants.ENTERPRISE_NS, "TestObject2", 1, -1, true);

    public void load(XmlInputStream in, TypeMapper typeMapper) throws IOException, ConnectionException {
      typeMapper.consumeStartTag(in);

      in.peekTag();
      if (typeMapper.verifyElement(in, nameInfo)) {
        name = typeMapper.readString(in, nameInfo, String.class);
        Verbose.log(name);
      }

      in.peekTag();
      if (typeMapper.isElement(in, ageInfo)) {
        age = typeMapper.readInt(in, ageInfo, int.class);
        Verbose.log(age);
      }

      in.peekTag();
      if (typeMapper.isElement(in, object2Info)) {
        object2 = (TestObject2[]) typeMapper.readObject(in, object2Info, TestObject2[].class);
        Verbose.log(object2);
      }

      typeMapper.consumeEndTag(in);
    }
  }

  public static class TestObject2 implements XMLizable {
    private String name2;
    private static TypeInfo name2Info = new TypeInfo(Constants.ENTERPRISE_NS, "name2", Constants.SCHEMA_NS, "string", 1, 1, true);

    public void write(QName element, XmlOutputStream out, TypeMapper typeMapper) throws IOException {
      throw new InternalError("NIY");
    }

    public void load(XmlInputStream in, TypeMapper typeMapper) throws IOException, ConnectionException {
      typeMapper.consumeStartTag(in);

      in.peekTag();
      if (typeMapper.verifyElement(in, name2Info)) {
        name2 = typeMapper.readString(in, name2Info, String.class);
        Verbose.log(name2);
      }

      typeMapper.consumeEndTag(in);
    }
  }
}

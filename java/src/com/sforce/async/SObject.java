/*
 * Copyright, 1999-2008, SALESFORCE.com
 * All Rights Reserved
 * Company Confidential
 */
package com.sforce.async;

import java.util.*;
import java.io.IOException;

import com.sforce.ws.parser.XmlOutputStream;

/**
 * SObject
 *
 * @author mcheenath
 * @since 160
 */
public class SObject {

    private HashMap<String, String> fields = new HashMap<String, String>();

    public Set<String> getFieldNames() {
        return fields.keySet();
    }

    public String getField(String name) {
        return fields.get(name);
    }

    public void setField(String name, String value) {
        fields.put(name, value);
    }

    public void write(XmlOutputStream out) throws IOException {
        out.writeStartTag(RestConnection.NAMESPACE, "sObject");

        for (Map.Entry<String, String> entry : fields.entrySet()) {
            out.writeStringElement(RestConnection.NAMESPACE, entry.getKey(), entry.getValue());
        }

        out.writeEndTag(RestConnection.NAMESPACE, "sObject");
    }
}

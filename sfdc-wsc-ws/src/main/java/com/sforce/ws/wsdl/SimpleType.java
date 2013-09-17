/*
 * #%L
 * sfdc-wsc-ws
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
package com.sforce.ws.wsdl;

import com.sforce.ws.parser.XmlInputStream;

/**
 * This class represents WSDL->Definitions->types->schema->simpleType
 *
 * @author http://cheenath.com
 * @version 1.0
 * @since 1.0  Nov 9, 2005
 */
public class SimpleType implements Constants {
    private String name;
    private Restriction restriction;
    private Schema schema;

    public SimpleType(Schema schema) {
        this.schema = schema;
    }

    public Restriction getRestriction() {
        return restriction;
    }

    public Schema getSchema() {
        return schema;
    }

    @Override
    public String toString() {
        return "SimpleType{" +
                "name='" + name + '\'' +
                ", restriction=" + restriction +
                '}';
    }

    public void read(WsdlParser parser, String elementName) throws WsdlParseException {

        name = parser.getAttributeValue(null, NAME);

        if (name == null) {
            name = elementName;
        }

        if (name == null) {
            throw new WsdlParseException("simpleType->element Name can not be null. " + parser.getPositionDescription());
        }

        int eventType = parser.getEventType();

        while (true) {
            if (eventType == XmlInputStream.START_TAG) {
                String name = parser.getName();
                String namespace = parser.getNamespace();

                if (RESTRICTION.equals(name) && SCHEMA_NS.equals(namespace)) {
                    restriction = new Restriction(schema);
                    restriction.read(parser);
                }
           } else if (eventType == XmlInputStream.END_TAG) {
                String name = parser.getName();
                String namespace = parser.getNamespace();

                if (SIMPLE_TYPE.equals(name) && SCHEMA_NS.equals(namespace)) {
                    return;
                }
            } else if (eventType == XmlInputStream.END_DOCUMENT) {
                throw new WsdlParseException("Failed to find end tag for 'simpleType'");
            }

            eventType = parser.next();
        }
    }

    public String getName() {
        return name;
    }
}

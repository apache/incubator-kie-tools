/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.model;

import java.util.Objects;

import org.kie.workbench.common.stunner.core.util.HashUtil;

public class MetaDataAttribute {

    private String attribute;
    private String value;
    private static final String DELIMITER = "ß";

    public MetaDataAttribute() {
    }

    public MetaDataAttribute(final String attribute) {
        this.attribute = attribute;
    }

    public MetaDataAttribute(final String attribute,
                             final String value) {
        this.attribute = attribute;
        this.value = value;
    }

    public MetaDataAttribute(final MetaDataRow row) {
        this.attribute = row.getAttribute();
        this.value = row.getValue();
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(final String attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public String toString() {
        if (attribute != null && !attribute.isEmpty()) {
            StringBuilder sb = new StringBuilder().append(attribute);
            if (value != null && !value.isEmpty()) {
                sb.append(DELIMITER).append(value);
            }
            return sb.toString();
        }
        return "";
    }

    public static MetaDataAttribute deserialize(final String s) {
        MetaDataAttribute att = new MetaDataAttribute();
        String[] attParts = s.split(DELIMITER);
        if (attParts.length > 0) {
            String name = attParts[0];
            if (!name.isEmpty()) {
                att.setAttribute(name);
                if (attParts.length == 2 || attParts.length == 3) {
                    String value = attParts[1];
                    if (!value.isEmpty()) {
                        att.setValue(value);
                    }
                }
            }
        }
        return att;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MetaDataAttribute) {
            MetaDataAttribute other = (MetaDataAttribute) o;
            return Objects.equals(attribute, other.attribute) &&
                    Objects.equals(value, other.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(attribute),
                                         Objects.hashCode(value));
    }
}
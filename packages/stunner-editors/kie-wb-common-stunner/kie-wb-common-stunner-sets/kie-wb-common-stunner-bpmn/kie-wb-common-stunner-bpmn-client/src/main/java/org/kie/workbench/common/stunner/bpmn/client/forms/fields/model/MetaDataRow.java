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

import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Bindable
public class MetaDataRow {

    private long id;

    private String attribute;

    private String value;

    // Field which is incremented for each row.
    // Required to implement equals function which needs a unique field
    private static long lastId = 0;

    public MetaDataRow() {
        this(null, null);
        this.id = lastId++;
    }

    public MetaDataRow(final String attribute,
                       final String value) {
        this.id = lastId++;
        this.attribute = attribute;
        this.value = value;
    }

    public MetaDataRow(final MetaDataAttribute metaData) {
        this.id = lastId++;
        this.attribute = metaData.getAttribute();
        this.value = metaData.getValue();
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
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

    @Override
    public boolean equals(final Object o) {
        if (o instanceof MetaDataRow) {
            MetaDataRow other = (MetaDataRow) o;
            return Objects.equals(id, other.id) &&
                    Objects.equals(attribute, other.attribute) &&
                    Objects.equals(value, other.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(id),
                                         Objects.hashCode(attribute),
                                         Objects.hashCode(value));
    }

    @Override
    public String toString() {
        return "MetaDataRow [attribute=" + attribute + ", value=" + value + "]";
    }
}
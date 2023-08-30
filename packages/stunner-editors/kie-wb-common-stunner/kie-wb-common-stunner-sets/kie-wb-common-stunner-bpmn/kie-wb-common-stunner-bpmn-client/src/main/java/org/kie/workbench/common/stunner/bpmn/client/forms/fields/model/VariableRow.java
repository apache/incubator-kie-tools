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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.errai.databinding.client.api.Bindable;

@Bindable
public class VariableRow {

    long id;

    String name;

    Variable.VariableType variableType = Variable.VariableType.PROCESS;

    String dataTypeDisplayName;

    String customDataType;

    List<String> tags;

    // Field which is incremented for each row.
    // Required to implement equals function which needs a unique field
    static long lastId = 0;

    public VariableRow() {
        this(Variable.VariableType.PROCESS, null, null, null, new ArrayList<>());
    }

    public VariableRow(final Variable.VariableType variableType,
                       final String name,
                       final String dataTypeDisplayName,
                       final String customDataType) {
        this(variableType, name, dataTypeDisplayName, customDataType, new ArrayList<>());
    }

    public VariableRow(final Variable variable,
                       final Map<String, String> mapDataTypeNamesToDisplayNames) {
        this(variable.getVariableType(), variable.getName(), null, variable.getCustomDataType(), variable.getTags());
        if (variable.getDataType() != null && mapDataTypeNamesToDisplayNames.containsKey(variable.getDataType())) {
            this.dataTypeDisplayName = mapDataTypeNamesToDisplayNames.get(variable.getDataType());
        } else {
            this.dataTypeDisplayName = variable.getDataType();
        }
    }

    public VariableRow(final Variable.VariableType variableType,
                       final String name,
                       final String dataTypeDisplayName,
                       final String customDataType,
                       final List<String> tags) {
        this.id = lastId++;
        this.variableType = variableType;
        this.name = name;
        this.dataTypeDisplayName = dataTypeDisplayName;
        this.customDataType = customDataType;
        this.tags = tags;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public Variable.VariableType getVariableType() {
        return variableType;
    }

    public void setVariableType(final Variable.VariableType variableType) {
        this.variableType = variableType;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDataTypeDisplayName() {
        return dataTypeDisplayName;
    }

    public void setDataTypeDisplayName(final String dataTypeDisplayName) {
        this.dataTypeDisplayName = dataTypeDisplayName;
    }

    public String getCustomDataType() {
        return customDataType;
    }

    public void setCustomDataType(final String customDataType) {
        this.customDataType = customDataType;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        VariableRow other = (VariableRow) obj;
        return (id == other.id);
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "VariableRow [variableType=" + variableType.toString() + ", name=" + name + ", dataTypeDisplayName=" + dataTypeDisplayName + ", customDataType=" + customDataType + ", tags=" + String.join(",", tags) + "]";
    }
}

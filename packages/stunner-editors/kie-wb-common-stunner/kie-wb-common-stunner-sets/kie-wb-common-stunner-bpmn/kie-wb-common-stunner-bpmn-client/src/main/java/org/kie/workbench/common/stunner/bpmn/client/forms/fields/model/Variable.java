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
import java.util.Arrays;
import java.util.List;

import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;

public class Variable {

    static final String DIVIDER = ":";
    static final String TAG_DIVIDER = ";";

    public enum VariableType {
        INPUT,
        OUTPUT,
        PROCESS
    }

    private VariableType variableType;

    private String name;

    private String dataType;

    private String customDataType;

    private List<String> tags;

    public Variable(VariableType variableType) {
        this(null, variableType, null, null, null);
    }

    public Variable(final String name,
                    final VariableType variableType) {
        this(name, variableType, null, null, null);
    }

    public Variable(final String name,
                    final VariableType variableType,
                    final String dataType,
                    final String customDataType) {
        this(name, variableType, dataType, customDataType, null);
    }

    public Variable(final String name,
                    final VariableType variableType,
                    final String dataType,
                    final String customDataType,
                    final List<String> tags) {
        this.name = name;
        this.variableType = variableType;
        this.dataType = dataType;
        this.customDataType = customDataType;
        this.tags = tags;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public VariableType getVariableType() {
        return variableType;
    }

    public void setVariableType(final VariableType variableType) {
        this.variableType = variableType;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(final String dataType) {
        this.dataType = dataType;
    }

    public String getCustomDataType() {
        return customDataType;
    }

    public void setCustomDataType(final String customDataType) {
        this.customDataType = customDataType;
    }

    public String toString() {
        if (name != null && !name.isEmpty()) {
            StringBuilder sb = new StringBuilder().append(name);

            sb.append(DIVIDER);
            if (customDataType != null && !customDataType.isEmpty()) {
                sb.append(customDataType);
            } else if (dataType != null && !dataType.isEmpty()) {
                sb.append(dataType);
            }

            sb.append(DIVIDER);
            if (this.getVariableType() == Variable.VariableType.PROCESS && tags != null) {
                sb.append(String.join(TAG_DIVIDER, tags));
            }

            return sb.toString();
        }
        return null;
    }

    /**
     * Deserializes a variable, checking whether the datatype is custom or not
     *
     * @param s
     * @param variableType
     * @param dataTypes
     * @return
     */
    public static Variable deserialize(final String s,
                                       final VariableType variableType,
                                       final List<String> dataTypes) {
        Variable var = new Variable(variableType);
        String[] varParts = s.split(DIVIDER, -1);

        String name = varParts[0];
        var.setName(name);

        String dataType = varParts.length > 1 ? varParts[1] : null;
        if (dataTypes != null && dataTypes.contains(dataType)) {
            var.setDataType(dataType);
        } else {
            var.setCustomDataType(dataType);
        }

        var.tags = new ArrayList<>();
        String tags = varParts.length > 2 ? varParts[2] : "";
        final String strippedDownTags = tags.replace("[", "").replace("]", "");
        String[] elements = strippedDownTags.split(TAG_DIVIDER);
        if (!strippedDownTags.isEmpty()) {
            var.tags.addAll(Arrays.asList(elements));
        }

        return var;
    }

    /**
     * Deserializes a variable, NOT checking whether the datatype is custom
     *
     * @param s
     * @param variableType
     * @return
     */
    public static Variable deserialize(final String s,
                                       final VariableType variableType) {
        return deserialize(s,
                           variableType,
                           null);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Variable)) {
            return false;
        }

        Variable variable = (Variable) o;

        if (getVariableType() != variable.getVariableType()) {
            return false;
        }

        if (!StringUtils.isEmpty(getName()) ? !getName().equals(variable.getName()) : !StringUtils.isEmpty(variable.getName())) {
            return false;
        }

        if (!StringUtils.isEmpty(getDataType()) ? !getDataType().equals(variable.getDataType()) : !StringUtils.isEmpty(variable.getDataType())) {
            return false;
        }

        if (!StringUtils.isEmpty(getCustomDataType()) ? !getCustomDataType().equals(variable.getCustomDataType()) : !StringUtils.isEmpty(variable.getCustomDataType())) {
            return false;
        }

        return tags != null && !tags.isEmpty() ? tags.equals(variable.tags) : variable.getTags() == null || variable.getTags().isEmpty();
    }

    @Override
    public int hashCode() {
        int result = getVariableType() != null ? getVariableType().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getDataType() != null ? getDataType().hashCode() : 0);
        result = 31 * result + (getCustomDataType() != null ? getCustomDataType().hashCode() : 0);
        result = 31 * result + (tags != null && !tags.isEmpty() ? tags.hashCode() : 0);

        return result;
    }
}

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


package org.kie.workbench.common.stunner.bpmn.client.util;

import java.util.Objects;

import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.util.HashUtil;

public class VariableUsage {

    public enum USAGE_TYPE {
        INPUT_VARIABLE,
        OUTPUT_VARIABLE,
        INPUT_OUTPUT_VARIABLE,
        MULTIPLE_INSTANCE_INPUT_COLLECTION,
        MULTIPLE_INSTANCE_DATA_INPUT,
        MULTIPLE_INSTANCE_DATA_OUTPUT,
        MULTIPLE_INSTANCE_OUTPUT_COLLECTION
    }

    private String variableName;
    private USAGE_TYPE usageType;
    private Element element;
    private String elementName;

    public VariableUsage(String variableName, USAGE_TYPE usageType, Element element, String elementName) {
        this.variableName = variableName;
        this.usageType = usageType;
        this.element = element;
        this.elementName = elementName;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public USAGE_TYPE getUsageType() {
        return usageType;
    }

    public void setUsageType(USAGE_TYPE usageType) {
        this.usageType = usageType;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(variableName),
                                         Objects.hashCode(usageType),
                                         Objects.hashCode(element),
                                         Objects.hashCode(elementName));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof VariableUsage) {
            VariableUsage other = (VariableUsage) o;
            return Objects.equals(variableName, other.variableName) &&
                    Objects.equals(usageType, other.usageType) &&
                    Objects.equals(element, other.element) &&
                    Objects.equals(elementName, other.elementName);
        }
        return false;
    }
}

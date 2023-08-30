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


package org.kie.workbench.common.forms.adf.processors;

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.forms.model.FieldType;

public class FormDefinitionFieldData {

    private final String modelClass;
    private final String name;

    private String preferredType = FieldType.class.getName();

    private String label;
    private String helpMessage = "";
    private String readOnly = Boolean.FALSE.toString();
    private String required = Boolean.FALSE.toString();
    private String binding;

    private String className;

    private String afterElement = "";
    private String wrap = Boolean.FALSE.toString();
    private String horizontalSpan = "1";
    private String verticalSpan = "1";

    private String methodName;
    private String type;
    private String list;
    private String fieldModifier;

    private Map<String, String> params = new HashMap<>();

    public FormDefinitionFieldData(String modelClass, String name) {
        this.modelClass = modelClass;
        this.name = name;
    }

    public String getModelClass() {
        return modelClass;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getHelpMessage() {
        return helpMessage;
    }

    public void setHelpMessage(String helpMessage) {
        this.helpMessage = helpMessage;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public void setAfterElement(String afterElement) {
        this.afterElement = afterElement;
    }

    public String getAfterElement() {
        return afterElement;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setList(String list) {
        this.list = list;
    }

    public String getList() {
        return list;
    }

    public void setFieldModifier(String fieldModifier) {
        this.fieldModifier = fieldModifier;
    }

    public String getFieldModifier() {
        return fieldModifier;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setPreferredType(String preferredType) {
        this.preferredType = preferredType;
    }

    public String getPreferredType() {
        return preferredType;
    }

    public void setReadOnly(String readOnly) {
        this.readOnly = readOnly;
    }

    public String getReadOnly() {
        return readOnly;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public String getRequired() {
        return required;
    }

    public String getWrap() {
        return wrap;
    }

    public void setWrap(String wrap) {
        this.wrap = wrap;
    }

    public String getHorizontalSpan() {
        return horizontalSpan;
    }

    public void setHorizontalSpan(String horizontalSpan) {
        this.horizontalSpan = horizontalSpan;
    }

    public String getVerticalSpan() {
        return verticalSpan;
    }

    public void setVerticalSpan(String verticalSpan) {
        this.verticalSpan = verticalSpan;
    }
}

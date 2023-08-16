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


package org.kie.workbench.common.forms.adf.service.definitions.elements;

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.forms.model.FieldType;
import org.kie.workbench.common.forms.model.TypeInfo;

/**
 * Definition of a FormElement that represents a field on the form.
 */
public class FieldElement extends AbstractFormElement {

    private Class<? extends FieldType> preferredType = FieldType.class;

    private TypeInfo typeInfo;

    private String labelKey;

    private String helpMessageKey;

    private boolean required = false;

    private boolean readOnly = false;

    private String binding;

    private Map<String, String> params = new HashMap<>();

    public FieldElement(String name,
                        String binding,
                        TypeInfo typeInfo) {
        this.name = name;
        this.binding = binding;
        this.typeInfo = typeInfo;
    }

    public String getBinding() {
        return binding;
    }

    public TypeInfo getTypeInfo() {
        return typeInfo;
    }

    public Class<? extends FieldType> getPreferredType() {
        return preferredType;
    }

    public void setPreferredType(Class<? extends FieldType> preferredType) {
        this.preferredType = preferredType;
    }

    public String getLabelKey() {
        return labelKey;
    }

    public void setLabelKey(String labelKey) {
        this.labelKey = labelKey;
    }

    public String getHelpMessageKey() {
        return helpMessageKey;
    }

    public void setHelpMessageKey(String helpMessageKey) {
        this.helpMessageKey = helpMessageKey;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}

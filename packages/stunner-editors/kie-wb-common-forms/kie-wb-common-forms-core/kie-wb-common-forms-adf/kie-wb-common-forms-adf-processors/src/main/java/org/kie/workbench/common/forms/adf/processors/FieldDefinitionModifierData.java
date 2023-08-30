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

public class FieldDefinitionModifierData {

    private final String modelClassName;
    private final String fieldModifierName;

    private String value;
    private String readOnlyGetter;
    private String requiredGetter;
    private String labelGetter;
    private String helpMessageGetter;

    public FieldDefinitionModifierData(String modelClassName, String fieldModifierName) {
        this.modelClassName = modelClassName;
        this.fieldModifierName = fieldModifierName;
    }

    public String getModelClassName() {
        return modelClassName;
    }

    public String getFieldModifierName() {
        return fieldModifierName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getReadOnlyGetter() {
        return readOnlyGetter;
    }

    public void setReadOnlyGetter(String readOnlyGetter) {
        this.readOnlyGetter = readOnlyGetter;
    }

    public String getRequiredGetter() {
        return requiredGetter;
    }

    public void setRequiredGetter(String requiredGetter) {
        this.requiredGetter = requiredGetter;
    }

    public String getLabelGetter() {
        return labelGetter;
    }

    public void setLabelGetter(String labelGetter) {
        this.labelGetter = labelGetter;
    }

    public String getHelpMessageGetter() {
        return helpMessageGetter;
    }

    public void setHelpMessageGetter(String helpMessageGetter) {
        this.helpMessageGetter = helpMessageGetter;
    }
}

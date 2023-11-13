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


package org.kie.workbench.common.stunner.bpmn.forms.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.fields.shared.AbstractFieldDefinition;
import org.kie.workbench.common.forms.model.FieldDefinition;

@Portable
@Bindable
public class VariablesEditorFieldDefinition extends AbstractFieldDefinition {

    public static final VariablesEditorFieldType FIELD_TYPE = new VariablesEditorFieldType();

    private String defaultValue;

    private boolean caseFileVariable;

    public VariablesEditorFieldDefinition() {
        super(String.class.getName());
    }

    @Override
    public VariablesEditorFieldType getFieldType() {
        return FIELD_TYPE;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isCaseFileVariable() {
        return caseFileVariable;
    }

    public void setCaseFileVariable(boolean caseFileVariable) {
        this.caseFileVariable = caseFileVariable;
    }

    @Override
    protected void doCopyFrom(FieldDefinition other) {
        if (other instanceof VariablesEditorFieldDefinition) {
            this.setDefaultValue(((VariablesEditorFieldDefinition) other).getDefaultValue());
            this.setCaseFileVariable(((VariablesEditorFieldDefinition) other).isCaseFileVariable());
        }
    }
}

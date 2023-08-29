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

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.SkipFormField;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.SelectorFieldBaseDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.StringSelectorOption;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FieldType;

@Portable
@Bindable
public class ComboBoxFieldDefinition extends SelectorFieldBaseDefinition<StringSelectorOption, String> {

    public static final ComboBoxFieldType FIELD_TYPE = new ComboBoxFieldType();

    protected String defaultValue;

    protected List<StringSelectorOption> options = new ArrayList<>();

    @SkipFormField
    protected Boolean allowCustomValue = Boolean.TRUE;

    public ComboBoxFieldDefinition() {
        super(String.class.getName());
    }

    @Override
    public FieldType getFieldType() {
        return FIELD_TYPE;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public void setOptions(List<StringSelectorOption> options) {
        this.options = options;
    }

    @Override
    public List<StringSelectorOption> getOptions() {
        return options;
    }

    @Override
    protected void doCopyFrom(FieldDefinition other) {
        if (other instanceof ComboBoxFieldDefinition) {
            this.setDefaultValue(((ComboBoxFieldDefinition) other).getDefaultValue());
        }
    }

    public Boolean isAllowCustomValue() {
        return allowCustomValue;
    }

    public void setAllowCustomValue(Boolean allowCustomValue) {
        this.allowCustomValue = allowCustomValue;
    }
}

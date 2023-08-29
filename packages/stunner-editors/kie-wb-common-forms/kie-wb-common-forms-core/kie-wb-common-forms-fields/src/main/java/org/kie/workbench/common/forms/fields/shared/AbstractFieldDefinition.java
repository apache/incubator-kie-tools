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


package org.kie.workbench.common.forms.fields.shared;

import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.SkipFormField;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;
import org.kie.workbench.common.forms.model.util.IDGenerator;
import org.kie.workbench.common.forms.service.shared.FieldManager;

public abstract class AbstractFieldDefinition implements FieldDefinition {

    public static final String ID_PREFFIX = "field" + FieldManager.FIELD_NAME_SEPARATOR;

    @SkipFormField
    private String id;

    @SkipFormField
    protected String name;

    @FormField(
            labelKey = "label"
    )
    protected String label;

    @FormField(
            labelKey = "required",
            afterElement = "label"
    )
    protected Boolean required = Boolean.FALSE;

    @FormField(
            labelKey = "readOnly",
            afterElement = "required"
    )
    protected Boolean readOnly = Boolean.FALSE;

    @FormField(
            afterElement = "readOnly",
            labelKey = "validateOnChange"
    )
    protected Boolean validateOnChange = Boolean.TRUE;

    @FormField(
            type = TextAreaFieldType.class,
            afterElement = "validateOnChange",
            labelKey = "helpMessage",
            helpMessageKey = "helpMessage.helpMessage"
    )
    private String helpMessage;

    @SkipFormField
    protected String binding;

    @SkipFormField
    protected String standaloneClassName;

    public AbstractFieldDefinition(String className) {
        id = ID_PREFFIX + IDGenerator.generateRandomId();
        standaloneClassName = className;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public Boolean getRequired() {
        return required;
    }

    @Override
    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public String getHelpMessage() {
        return helpMessage;
    }

    @Override
    public void setHelpMessage(String helpMessage) {
        this.helpMessage = helpMessage;
    }

    @Override
    public String getBinding() {
        return binding;
    }

    @Override
    public void setBinding(String binding) {
        this.binding = binding;
    }

    @Override
    public String getStandaloneClassName() {
        return standaloneClassName;
    }

    @Override
    public void setStandaloneClassName(String standaloneClassName) {
        this.standaloneClassName = standaloneClassName;
    }

    @Override
    public TypeInfo getFieldTypeInfo() {
        return new TypeInfoImpl(standaloneClassName);
    }

    @Override
    public Boolean getValidateOnChange() {
        return validateOnChange;
    }

    @Override
    public void setValidateOnChange(Boolean validateOnChange) {
        this.validateOnChange = validateOnChange;
    }

    public void copyFrom(FieldDefinition other) {
        if (other == null) {
            return;
        }
        setLabel(other.getLabel());

        setStandaloneClassName(other.getStandaloneClassName());
        setBinding(other.getBinding());

        setRequired(other.getRequired());
        setReadOnly(other.getReadOnly());
        setValidateOnChange(other.getValidateOnChange());
        setHelpMessage(other.getHelpMessage());

        doCopyFrom(other);
    }

    protected abstract void doCopyFrom(FieldDefinition other);

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractFieldDefinition that = (AbstractFieldDefinition) o;

        if (!id.equals(that.id)) {
            return false;
        }
        if (!name.equals(that.name)) {
            return false;
        }
        if (label != null ? !label.equals(that.label) : that.label != null) {
            return false;
        }
        if (required != null ? !required.equals(that.required) : that.required != null) {
            return false;
        }
        if (readOnly != null ? !readOnly.equals(that.readOnly) : that.readOnly != null) {
            return false;
        }
        if (validateOnChange != null ? !validateOnChange.equals(that.validateOnChange) : that.validateOnChange != null) {
            return false;
        }
        if (helpMessage != null ? !helpMessage.equals(that.helpMessage) : that.helpMessage != null) {
            return false;
        }
        if (binding != null ? !binding.equals(that.binding) : that.binding != null) {
            return false;
        }
        return standaloneClassName != null ? standaloneClassName.equals(that.standaloneClassName) : that.standaloneClassName == null;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = ~~result;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (required != null ? required.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (readOnly != null ? readOnly.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (validateOnChange != null ? validateOnChange.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (helpMessage != null ? helpMessage.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (binding != null ? binding.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (standaloneClassName != null ? standaloneClassName.hashCode() : 0);
        result = ~~result;
        return result;
    }
}

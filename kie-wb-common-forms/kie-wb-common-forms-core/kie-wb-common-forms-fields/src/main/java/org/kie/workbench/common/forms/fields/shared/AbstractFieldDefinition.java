/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.fields.shared;

import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.SkipFormField;
import org.kie.workbench.common.forms.model.FieldDataType;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FieldType;
import org.kie.workbench.common.forms.model.util.IDGenerator;
import org.kie.workbench.common.forms.service.FieldManager;

public abstract class AbstractFieldDefinition<FIELD_TYPE extends FieldType> implements FieldDefinition<FIELD_TYPE> {

    public static final String ID_PREFFIX = "field" + FieldManager.FIELD_NAME_SEPARATOR;

    @SkipFormField
    protected boolean annotatedId;

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
            labelKey = "validateOnChange",
            afterElement = "readOnly"
    )
    protected Boolean validateOnChange = Boolean.TRUE;

    @SkipFormField
    protected String binding;

    @SkipFormField
    protected String standaloneClassName;

    protected AbstractFieldDefinition() {
        id = ID_PREFFIX + IDGenerator.generateRandomId();
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
    public boolean isAnnotatedId() {
        return annotatedId;
    }

    @Override
    public void setAnnotatedId(boolean annotatedId) {
        this.annotatedId = annotatedId;
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
    public FieldDataType getFieldTypeInfo() {
        return new FieldDataType(standaloneClassName);
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

        setAnnotatedId(other.isAnnotatedId());
        if (!other.isAnnotatedId()) {
            setReadOnly(other.getReadOnly());
        }

        setStandaloneClassName(other.getStandaloneClassName());
        setBinding(other.getBinding());

        setRequired(other.getRequired());
        setReadOnly(other.getReadOnly());
        setValidateOnChange(other.getValidateOnChange());

        doCopyFrom(other);
    }

    protected abstract void doCopyFrom(FieldDefinition other);
}

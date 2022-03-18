/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.forms.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.fields.shared.AbstractFieldDefinition;
import org.kie.workbench.common.forms.model.FieldDefinition;

@FormDefinition
@Portable
@Bindable
public class AssigneeEditorFieldDefinition extends AbstractFieldDefinition {

    public static final AssigneeEditorFieldType FIELD_TYPE = new AssigneeEditorFieldType();

    private String defaultValue;

    private AssigneeType type;

    private Integer max;

    public AssigneeEditorFieldDefinition() {
        super(String.class.getName());
        max = new Integer(-1);
    }

    @Override
    public AssigneeEditorFieldType getFieldType() {
        return FIELD_TYPE;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public AssigneeType getType() {
        return type;
    }

    public void setType(AssigneeType type) {
        this.type = type;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    @Override
    protected void doCopyFrom(FieldDefinition other) {
        if (other instanceof AssigneeEditorFieldDefinition) {
            this.setDefaultValue(((AssigneeEditorFieldDefinition) other).getDefaultValue());
            this.setType(((AssigneeEditorFieldDefinition) other).getType());
            this.setMax(((AssigneeEditorFieldDefinition) other).getMax());
        }
    }
}

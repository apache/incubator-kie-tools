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

package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition;

import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.SelectorFieldBaseDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.SelectorOption;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;
import org.kie.workbench.common.forms.model.FieldDefinition;

public abstract class ListBoxBaseDefinition<OPTIONS extends SelectorOption<TYPE>, TYPE> extends SelectorFieldBaseDefinition<OPTIONS, TYPE> {

    public static final ListBoxFieldType FIELD_TYPE = new ListBoxFieldType();

    @FormField(
            labelKey = "addEmptyOption",
            afterElement = "label"
    )
    private Boolean addEmptyOption = Boolean.TRUE;

    public ListBoxBaseDefinition(String className) {
        super(className);
    }

    @Override
    public ListBoxFieldType getFieldType() {
        return FIELD_TYPE;
    }

    public Boolean getAddEmptyOption() {
        return addEmptyOption;
    }

    public void setAddEmptyOption(Boolean addEmptyOption) {
        this.addEmptyOption = addEmptyOption;
    }

    @Override
    protected void doCopyFrom(FieldDefinition other) {
        super.doCopyFrom(other);
        if(other instanceof ListBoxBaseDefinition) {
            ListBoxBaseDefinition otherListBox = (ListBoxBaseDefinition) other;
            if(getStandaloneClassName().equals(otherListBox.getStandaloneClassName())) {
                setDefaultValue((TYPE) otherListBox.getDefaultValue());
            }
            setAddEmptyOption(otherListBox.addEmptyOption);
        }
    }
}

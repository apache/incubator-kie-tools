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
package org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.fields.shared.AbstractFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.HasNestedForm;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.type.SubFormFieldType;
import org.kie.workbench.common.forms.model.FieldDefinition;

@Portable
@Bindable
@FormDefinition(
        i18n = @I18nSettings(keyPreffix = "FieldProperties"),
        startElement = "label"
)
public class SubFormFieldDefinition extends AbstractFieldDefinition implements HasNestedForm {

    public static final SubFormFieldType FIELD_TYPE = new SubFormFieldType();

    @FormField(
            labelKey = "nestedForm",
            type = ListBoxFieldType.class,
            afterElement = "label"
    )
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.REMOTE,
            className = "org.kie.workbench.common.forms.editor.backend.dataProviders.VFSSelectorFormProvider")
    @NotNull
    @NotEmpty
    protected String nestedForm = "";

    public SubFormFieldDefinition() {
        super(Object.class.getName());
    }

    @Override
    public SubFormFieldType getFieldType() {
        return FIELD_TYPE;
    }

    @Override
    public String getNestedForm() {
        return nestedForm;
    }

    @Override
    public void setNestedForm(String nestedForm) {
        this.nestedForm = nestedForm;
    }

    @Override
    protected void doCopyFrom(FieldDefinition other) {
        if (other instanceof SubFormFieldDefinition) {
            SubFormFieldDefinition otherForm = (SubFormFieldDefinition) other;
            otherForm.setNestedForm(nestedForm);
        }
        setStandaloneClassName(other.getStandaloneClassName());
    }
}

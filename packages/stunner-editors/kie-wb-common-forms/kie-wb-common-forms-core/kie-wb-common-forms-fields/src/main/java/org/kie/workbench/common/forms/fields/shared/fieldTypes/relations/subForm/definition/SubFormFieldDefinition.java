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

package org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.SkipFormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.fields.shared.AbstractFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.Container;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.HasNestedForm;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.type.SubFormFieldType;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.forms.model.TypeKind;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;

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
    protected String nestedForm = null;

    @SkipFormField
    protected Container container = Container.FIELD_SET;

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
    public Container getContainer() {
        return container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    protected void doCopyFrom(FieldDefinition other) {
        if (other instanceof SubFormFieldDefinition) {
            SubFormFieldDefinition otherForm = (SubFormFieldDefinition) other;
            setNestedForm(otherForm.getNestedForm());
            setContainer(otherForm.getContainer());
        }
        setStandaloneClassName(other.getStandaloneClassName());
    }

    @Override
    public TypeInfo getFieldTypeInfo() {
        return new TypeInfoImpl(TypeKind.OBJECT,
                                standaloneClassName,
                                false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        SubFormFieldDefinition that = (SubFormFieldDefinition) o;

        if (nestedForm != null ? !nestedForm.equals(that.nestedForm) : that.nestedForm != null) {
            return false;
        }
        return container == that.container;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        result = 31 * result + (nestedForm != null ? nestedForm.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (container != null ? container.hashCode() : 0);
        result = ~~result;
        return result;
    }
}

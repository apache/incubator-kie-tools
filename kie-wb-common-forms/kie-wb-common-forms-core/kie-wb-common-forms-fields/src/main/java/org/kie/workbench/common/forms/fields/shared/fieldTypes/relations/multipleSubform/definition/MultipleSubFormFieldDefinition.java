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
package org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.definition;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.fields.shared.AbstractFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.IsCRUDDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.type.MultipleSubFormFieldType;
import org.kie.workbench.common.forms.model.FieldDataType;
import org.kie.workbench.common.forms.model.FieldDefinition;

@Portable
@Bindable
@FormDefinition(
        i18n = @I18nSettings(keyPreffix = "FieldProperties.multipleSubform"),
        startElement = "label"
)
public class MultipleSubFormFieldDefinition extends AbstractFieldDefinition implements IsCRUDDefinition {

    public static final MultipleSubFormFieldType FIELD_TYPE = new MultipleSubFormFieldType();

    @FormField(
            labelKey = "creationForm",
            type = ListBoxFieldType.class,
            afterElement = "label"
    )
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.REMOTE,
            className = "org.kie.workbench.common.forms.editor.backend.dataProviders.VFSSelectorFormProvider")
    @NotEmpty
    protected String creationForm = "";

    @FormField(
            labelKey = "editionForm",
            type = ListBoxFieldType.class,
            afterElement = "creationForm"
    )
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.REMOTE,
            className = "org.kie.workbench.common.forms.editor.backend.dataProviders.VFSSelectorFormProvider")
    @NotEmpty
    protected String editionForm = "";

    @FormField(
            labelKey = "columns",
            afterElement = "editionForm"
    )
    private List<TableColumnMeta> columnMetas = new ArrayList<TableColumnMeta>();

    @Override
    public MultipleSubFormFieldType getFieldType() {
        return FIELD_TYPE;
    }

    public MultipleSubFormFieldDefinition() {
        super(Object.class.getName());
    }

    @Override
    public List<TableColumnMeta> getColumnMetas() {
        return columnMetas;
    }

    @Override
    public void setColumnMetas(List<TableColumnMeta> columnMetas) {
        this.columnMetas = columnMetas;
    }

    @Override
    public String getCreationForm() {
        return creationForm;
    }

    @Override
    public void setCreationForm(String creationForm) {
        this.creationForm = creationForm;
    }

    @Override
    public String getEditionForm() {
        return editionForm;
    }

    @Override
    public void setEditionForm(String editionForm) {
        this.editionForm = editionForm;
    }

    @Override
    public FieldDataType getFieldTypeInfo() {
        return new FieldDataType(standaloneClassName,
                                 true,
                                 false);
    }

    @Override
    protected void doCopyFrom(FieldDefinition other) {
        if (other instanceof MultipleSubFormFieldDefinition) {
            MultipleSubFormFieldDefinition otherForm = (MultipleSubFormFieldDefinition) other;
            otherForm.setCreationForm(creationForm);
            otherForm.setEditionForm(editionForm);
            otherForm.setColumnMetas(columnMetas);
        }
        setStandaloneClassName(other.getStandaloneClassName());
    }
}

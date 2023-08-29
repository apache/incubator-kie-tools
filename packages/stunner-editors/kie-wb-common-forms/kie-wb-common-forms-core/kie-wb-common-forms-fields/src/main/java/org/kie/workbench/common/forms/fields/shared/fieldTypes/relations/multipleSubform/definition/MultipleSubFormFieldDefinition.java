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

package org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.definition;

import java.util.ArrayList;
import java.util.List;

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
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.IsCRUDDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.type.MultipleSubFormFieldType;
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
public class MultipleSubFormFieldDefinition extends AbstractFieldDefinition implements IsCRUDDefinition {

    public static final MultipleSubFormFieldType FIELD_TYPE = new MultipleSubFormFieldType();

    @FormField(
            labelKey = "multipleSubform.creationForm",
            type = ListBoxFieldType.class,
            afterElement = "label"
    )
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.REMOTE,
            className = "org.kie.workbench.common.forms.editor.backend.dataProviders.VFSSelectorFormProvider")
    protected String creationForm = null;

    @FormField(
            labelKey = "multipleSubform.editionForm",
            type = ListBoxFieldType.class,
            afterElement = "creationForm"
    )
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.REMOTE,
            className = "org.kie.workbench.common.forms.editor.backend.dataProviders.VFSSelectorFormProvider")
    protected String editionForm = null;

    @FormField(
            labelKey = "multipleSubform.columns",
            afterElement = "editionForm"
    )
    private List<TableColumnMeta> columnMetas = new ArrayList<TableColumnMeta>();

    @SkipFormField
    protected Container container = Container.FIELD_SET;

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
    public Container getContainer() {
        return container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public TypeInfo getFieldTypeInfo() {
        return new TypeInfoImpl(TypeKind.OBJECT,
                                standaloneClassName,
                                true);
    }

    @Override
    protected void doCopyFrom(FieldDefinition other) {
        if (other instanceof MultipleSubFormFieldDefinition) {
            MultipleSubFormFieldDefinition otherForm = (MultipleSubFormFieldDefinition) other;
            setCreationForm(otherForm.getCreationForm());
            setEditionForm(otherForm.getEditionForm());
            setColumnMetas(otherForm.getColumnMetas());
            setContainer(otherForm.getContainer());
        }
        setStandaloneClassName(other.getStandaloneClassName());
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

        MultipleSubFormFieldDefinition that = (MultipleSubFormFieldDefinition) o;

        if (creationForm != null ? !creationForm.equals(that.creationForm) : that.creationForm != null) {
            return false;
        }
        if (editionForm != null ? !editionForm.equals(that.editionForm) : that.editionForm != null) {
            return false;
        }
        if (columnMetas != null ? !columnMetas.equals(that.columnMetas) : that.columnMetas != null) {
            return false;
        }
        return container == that.container;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        result = 31 * result + (creationForm != null ? creationForm.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (editionForm != null ? editionForm.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (columnMetas != null ? columnMetas.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (container != null ? container.hashCode() : 0);
        result = ~~result;
        return result;
    }
}

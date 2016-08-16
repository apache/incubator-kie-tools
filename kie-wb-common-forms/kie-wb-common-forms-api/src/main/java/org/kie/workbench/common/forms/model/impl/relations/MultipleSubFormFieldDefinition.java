/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.forms.model.impl.relations;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.metaModel.ListBox;
import org.kie.workbench.common.forms.metaModel.SelectorDataProvider;
import org.kie.workbench.common.forms.model.DefaultFieldTypeInfo;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FieldTypeInfo;
import org.kie.workbench.common.forms.model.MultipleField;
import org.kie.workbench.common.forms.metaModel.FieldDef;

@Portable
@Bindable
public class MultipleSubFormFieldDefinition extends FieldDefinition implements EmbeddedFormField, MultipleField {
    public static final String CODE = "MultipleSubForm";

    @FieldDef( label = "Create Form", position = 4 )
    @ListBox
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.REMOTE,
            className = "org.kie.workbench.common.forms.editor.backend.dataProviders.VFSSelectorFormProvider")
    @NotEmpty
    protected String creationForm = "";

    @FieldDef( label = "Edit Form", position = 5 )
    @ListBox
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.REMOTE,
            className = "org.kie.workbench.common.forms.editor.backend.dataProviders.VFSSelectorFormProvider")
    @NotEmpty
    protected String editionForm = "";

    @FieldDef( label = "Table Columns")
    private List<TableColumnMeta> columnMetas = new ArrayList<TableColumnMeta>();

    public MultipleSubFormFieldDefinition() {
        super( CODE );
    }

    public List<TableColumnMeta> getColumnMetas() {
        return columnMetas;
    }

    public void setColumnMetas( List<TableColumnMeta> columnMetas ) {
        this.columnMetas = columnMetas;
    }

    public String getCreationForm() {
        return creationForm;
    }

    public void setCreationForm( String creationForm ) {
        this.creationForm = creationForm;
    }

    public String getEditionForm() {
        return editionForm;
    }

    public void setEditionForm( String editionForm ) {
        this.editionForm = editionForm;
    }

    @Override
    public FieldTypeInfo getFieldTypeInfo() {
        return new DefaultFieldTypeInfo( standaloneClassName, true, false );
    }

    @Override
    protected void doCopyFrom(FieldDefinition other) {
        if ( other instanceof MultipleSubFormFieldDefinition ) {
            MultipleSubFormFieldDefinition otherForm = (MultipleSubFormFieldDefinition) other;
            otherForm.setCreationForm( creationForm );
            otherForm.setEditionForm( editionForm );
            otherForm.setColumnMetas( columnMetas );
        }
        setStandaloneClassName( other.getStandaloneClassName() );
    }
}

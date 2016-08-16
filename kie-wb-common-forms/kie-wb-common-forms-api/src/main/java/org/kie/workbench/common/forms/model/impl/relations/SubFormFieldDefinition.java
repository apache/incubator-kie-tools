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

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.metaModel.ListBox;
import org.kie.workbench.common.forms.metaModel.SelectorDataProvider;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.metaModel.FieldDef;

@Portable
@Bindable
public class SubFormFieldDefinition extends FieldDefinition implements EmbeddedFormField {
    public static final String CODE = "SubForm";


    @FieldDef( label = "Nested Form" )
    @ListBox
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.REMOTE,
            className = "org.kie.workbench.common.forms.editor.backend.dataProviders.VFSSelectorFormProvider")
    @NotNull
    @NotEmpty
    protected String nestedForm = "";

    public SubFormFieldDefinition() {
        super( CODE );
    }

    public String getNestedForm() {
        return nestedForm;
    }

    public void setNestedForm( String nestedForm ) {
        this.nestedForm = nestedForm;
    }

    @Override
    protected void doCopyFrom( FieldDefinition other ) {
        if ( other instanceof SubFormFieldDefinition ) {
            SubFormFieldDefinition otherForm = (SubFormFieldDefinition) other;
            otherForm.setNestedForm( nestedForm );
        }
        setStandaloneClassName( other.getStandaloneClassName() );
    }
}

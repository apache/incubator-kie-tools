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

package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.statik.impl.fieldInitializers;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.statik.impl.DMOBasedTransformerContext;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.statik.impl.FieldSetting;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.impl.relations.EntityRelationField;
import org.kie.workbench.common.forms.model.impl.relations.MultipleSubFormFieldDefinition;
import org.kie.workbench.common.forms.model.impl.relations.TableColumnMeta;

@Dependent
public class MultipleSubFormFieldInitializer extends FormAwareFieldInitializer<MultipleSubFormFieldDefinition> {

    @Override
    public boolean supports( FieldDefinition field ) {
        return field instanceof MultipleSubFormFieldDefinition;
    }

    @Override
    public void initializeField( MultipleSubFormFieldDefinition field, FieldSetting setting, DMOBasedTransformerContext context ) {
        FormDefinition form = context.getRenderingContext().getAvailableForms().get( field.getStandaloneClassName() );
        if ( form == null ) {
            form = formGenerator.generateFormDefinitionForType( setting.getType(), context );
            context.getRenderingContext().getAvailableForms().put( field.getStandaloneClassName(), form );
        }

        List<TableColumnMeta> metas = new ArrayList<TableColumnMeta>();
        for ( FieldDefinition fieldDefinition : form.getFields() ) {
            if ( !( fieldDefinition instanceof EntityRelationField ) ) {
                metas.add( new TableColumnMeta( fieldDefinition.getLabel(),
                        fieldDefinition.getBinding() ) );
            }
        }

        field.setCreationForm( form.getId() );
        field.setEditionForm( form.getId() );
        field.setColumnMetas( metas );
        field.setReadonly( setting.getTypeInfo().isEnum() );
    }
}

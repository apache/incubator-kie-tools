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

package org.kie.workbench.common.forms.jbpm.server.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMVariable;
import org.kie.workbench.common.forms.model.DefaultFieldTypeInfo;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.service.FieldManager;
import org.kie.workbench.common.forms.service.impl.AbstractFormModelHandler;

public abstract class AbstractJBPMFormModelHandler<M extends JBPMFormModel<? extends JBPMVariable>> extends AbstractFormModelHandler<M> {

    protected FieldManager fieldManager;

    public AbstractJBPMFormModelHandler( FieldManager fieldManager ) {
        this.fieldManager = fieldManager;
    }

    @Override
    protected void initialize() {
        super.checkInitialized();
    }

    @Override
    protected List<FieldDefinition> doGenerateModelFields() {
        List<FieldDefinition> fields = new ArrayList<>();

        formModel.getVariables().forEach( variable -> {
            FieldDefinition field = fieldManager.getDefinitionByValueType( new DefaultFieldTypeInfo( variable.getType() ) );

            if ( field != null ) {
                field.setName( variable.getName() );
                field.setBinding( variable.getName() );
                field.setLabel( variable.getName() );
                fields.add( field );
            }
        } );

        return fields;
    }

    @Override
    protected FieldDefinition doCreateFieldDefinition( String fieldName ) {

        for ( JBPMVariable variable : formModel.getVariables() ) {
            FieldDefinition field = fieldManager.getDefinitionByValueType( new DefaultFieldTypeInfo( variable.getType() ) );

            if ( field != null ) {
                field.setName( variable.getName() );
                field.setBinding( variable.getName() );
                field.setLabel( variable.getName() );
                return field;
            }
        }
        return null;
    }
}

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

package org.kie.workbench.common.forms.data.modeller.service.impl;

import org.apache.commons.lang3.ArrayUtils;
import org.kie.workbench.common.forms.model.DefaultFieldTypeInfo;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.impl.basic.HasPlaceHolder;
import org.kie.workbench.common.forms.service.FieldManager;
import org.kie.workbench.common.screens.datamodeller.model.maindomain.MainDomainAnnotations;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class DataModellerFieldGenerator {
    public static final String[] RESTRICTED_PROPERTY_NAMES = new String[]{"serialVersionUID"};

    private FieldManager fieldManager;

    @Inject
    public DataModellerFieldGenerator( FieldManager fieldManager ) {
        this.fieldManager = fieldManager;
    }

    public List<FieldDefinition> getFieldsFromDataObject( String holderName, DataObject dataObject) {
        List<FieldDefinition> result = new ArrayList<FieldDefinition>( );
        if (dataObject != null) {
            for (ObjectProperty property : dataObject.getProperties()) {
                if ( ArrayUtils.contains( RESTRICTED_PROPERTY_NAMES, property.getName() ) ) continue;

                FieldDefinition field = createFieldDefinition( holderName, property );
                result.add( field );
            }
        }
        return result;
    }

    public FieldDefinition createFieldDefinition( String holderName, ObjectProperty property ) {
        String propertyName = holderName + "_" + property.getName();

        FieldDefinition field = null;
        if (property.getBag() == null) field = fieldManager.getDefinitionByValueType( new DefaultFieldTypeInfo( property.getClassName(), false, false ) );
        else field = fieldManager.getDefinitionByValueType( new DefaultFieldTypeInfo( property.getClassName(), true, false ) );

        if (field == null) return null;

        // TODO: improve this
        field.setAnnotatedId( property.getAnnotation( "javax.persistence.Id" ) != null );
        field.setReadonly( field.isAnnotatedId() );

        field.setName( propertyName );
        String label = getPropertyLabel( property );
        field.setLabel( label );
        field.setBinding( property.getName() );

        if (field instanceof HasPlaceHolder ) {
            ((HasPlaceHolder) field).setPlaceHolder( label );
        }
        return  field;
    }

    private String getPropertyLabel( ObjectProperty property ) {
        Annotation labelAnnotation = property.getAnnotation( MainDomainAnnotations.LABEL_ANNOTATION );
        if ( labelAnnotation != null ) return labelAnnotation.getValue( MainDomainAnnotations.VALUE_PARAM ).toString();

        return property.getName();
    }
}

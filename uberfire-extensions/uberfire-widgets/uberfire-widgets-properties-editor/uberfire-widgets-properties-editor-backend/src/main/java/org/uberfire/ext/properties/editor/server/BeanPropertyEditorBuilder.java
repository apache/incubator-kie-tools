/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.properties.editor.server;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.ext.properties.editor.model.PropertyEditorType;
import org.uberfire.ext.properties.editor.service.BeanPropertyEditorBuilderService;

@Service
@Dependent
public class BeanPropertyEditorBuilder implements BeanPropertyEditorBuilderService {

    @Override
    public PropertyEditorCategory extract( String fqcn ) {
        return extractOnlyBeanInfo( fqcn );
    }

    @Override
    public PropertyEditorCategory extract( String fqcn,
                                           Object instance ) {
        return extractBeanInfoAndValues( fqcn, instance );
    }

    private PropertyEditorCategory extractOnlyBeanInfo( String fqcn ) {
        return extractBeanInfoAndValues( fqcn, null );
    }

    private PropertyEditorCategory extractBeanInfoAndValues( String fqcn,
                                                             Object instance ) {
        Class targetClass;
        try {
            targetClass = Class.forName( fqcn );
        } catch ( Exception e ) {
            throw new NullBeanException();
        }

        PropertyEditorCategory beanCategory = new PropertyEditorCategory( targetClass.getSimpleName() );
        extractFieldInformationAndValues( targetClass, beanCategory, instance );
        return beanCategory;
    }

    private void extractFieldInformationAndValues( Class targetClass,
                                                   PropertyEditorCategory beanCategory,
                                                   Object instance ) throws ErrorReadingFieldInformationAndValues {
        for ( Field declaredField : targetClass.getDeclaredFields() ) {
            PropertyEditorType type = PropertyEditorType.getFromType( declaredField.getType() );
            if ( isAHandledType( type ) ) {
                PropertyEditorFieldInfo field = createPropertyEditorInfo( instance, declaredField, type );
                if ( isACombo( field ) ) {
                    generateComboValues( declaredField, field );
                }
                beanCategory.withField( field );
            }
        }
    }

    private PropertyEditorFieldInfo createPropertyEditorInfo( Object instance,
                                                              Field declaredField,
                                                              PropertyEditorType type ) {
        if ( needToExtractValues( instance ) ) {
            return new PropertyEditorFieldInfo( declaredField.getName(), extractFieldValue( instance, declaredField ), type );
        } else {
            return new PropertyEditorFieldInfo( declaredField.getName(), type );
        }
    }

    private boolean needToExtractValues( Object instance ) {
        return instance != null;
    }

    private boolean isACombo( PropertyEditorFieldInfo field ) {
        return field.getType().equals( PropertyEditorType.COMBO );
    }

    private String extractFieldValue( Object instance,
                                      Field field ) {
        try {
            return extractStringValue( instance, field );
        } catch ( IllegalAccessException e ) {
            throw new ErrorReadingFieldInformationAndValues();
        }
    }

    private String extractStringValue( Object instance,
                                       Field field ) throws IllegalAccessException {
        field.setAccessible( true );
        Object value = field.get( instance );
        if ( value != null ) {
            return value.toString();
        } else {
            return "";
        }
    }

    private void generateComboValues( Field declaredField,
                                      PropertyEditorFieldInfo field ) {
        List<String> values = new ArrayList<String>();
        for ( Object constant : declaredField.getType().getEnumConstants() ) {
            values.add( constant.toString() );
        }
        field.withComboValues( values );

    }

    public boolean isAHandledType( PropertyEditorType type ) {
        return type != null;
    }

    public class NullBeanException extends RuntimeException {

    }

    private class ErrorReadingFieldInformationAndValues extends RuntimeException {

    }
}

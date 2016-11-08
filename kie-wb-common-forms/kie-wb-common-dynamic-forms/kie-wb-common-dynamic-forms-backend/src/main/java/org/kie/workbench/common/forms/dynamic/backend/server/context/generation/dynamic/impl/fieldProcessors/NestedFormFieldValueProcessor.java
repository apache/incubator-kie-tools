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
package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.fieldProcessors;

import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.FieldValueProcessor;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.FormValuesProcessor;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.slf4j.Logger;

public abstract class NestedFormFieldValueProcessor<F extends FieldDefinition, RAW_VALUE, FLAT_VALUE> implements FieldValueProcessor<F, RAW_VALUE, FLAT_VALUE> {

    protected FormValuesProcessor processor;

    public abstract Logger getLogger();

    public void init( FormValuesProcessor processor ) {
        this.processor = processor;
    }

    protected void prepareNestedRawValues( final Map<String, Object> valuesMap,
                                           final FormDefinition nestedForm,
                                           final Object value ) {
        nestedForm.getFields().forEach( field -> {
            if ( !valuesMap.containsKey( field.getBinding() ) ) {
                valuesMap.put( field.getBinding(),
                               readValue( field.getBinding(), value ) );
            }
        } );
    }

    protected Object writeObjectValues( Object originalValue,
                                        Map<String, Object> formValues,
                                        FieldDefinition field,
                                        BackendFormRenderingContext context ) {
        if ( originalValue != null && originalValue.getClass().getName().equals( field.getStandaloneClassName() ) ) {
            writeValues( formValues, originalValue );
        } else {
            Class clazz = null;
            try {
                clazz = context.getClassLoader().loadClass( field.getStandaloneClassName() );
            } catch ( ClassNotFoundException e ) {
                // Maybe the nested class it is not on the classLoader context... let's try on the app classloader
                try {
                    clazz = Class.forName( field.getStandaloneClassName() );
                } catch ( ClassNotFoundException e1 ) {
                    getLogger().warn( "Unable to find class '{}' on classLoader for field '{}'",
                                      field.getStandaloneClassName(),
                                      field.getBinding() );
                }
            }
            if ( clazz != null ) {
                originalValue = writeValues( formValues, clazz );
            }
        }
        return originalValue;
    }

    protected Object writeValues( Map<String, Object> values, Class clazz ) {
        try {
            Object value = ConstructorUtils.invokeConstructor( clazz, null );
            writeValues( values, value );
            return value;
        } catch ( Exception e ) {
            getLogger().warn( "Unable to create instance for class {}: ", clazz.getName() );
        }
        return null;
    }

    protected void writeValues( Map<String, Object> values, Object model ) {
        if ( model == null ) {
            return;
        }
        values.forEach( ( property, value ) -> {
            try {
                if ( property.equals( MapModelRenderingContext.FORM_ENGINE_OBJECT_IDX ) || property.equals(
                        MapModelRenderingContext.FORM_ENGINE_EDITED_OBJECT ) ) {
                    return;
                }
                if ( PropertyUtils.getPropertyDescriptor( model, property ) != null ) {
                    BeanUtils.setProperty( model, property, value );
                }
            } catch ( Exception e ) {
                getLogger().warn( "Error modifying object '{}': cannot set value '{}' to property '{}'", model, value, property );
                getLogger().warn( "Caused by:", e );
            }
        } );
    }

    protected Object readValue( String property, Object model ) {
        try {
            if ( PropertyUtils.getPropertyDescriptor( model, property ) != null ) {
                return PropertyUtils.getProperty( model, property );
            }
        } catch ( Exception e ) {
            getLogger().warn( "Error getting property '{}' from object '{}'", property, model );
            getLogger().warn( "Caused by:", e );
        }
        return null;
    }
}

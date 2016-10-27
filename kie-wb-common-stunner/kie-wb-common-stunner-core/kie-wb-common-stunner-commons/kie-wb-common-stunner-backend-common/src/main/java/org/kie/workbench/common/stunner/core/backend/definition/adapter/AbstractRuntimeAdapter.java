/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.backend.definition.adapter;

import org.kie.workbench.common.stunner.core.definition.adapter.PriorityAdapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;

public abstract class AbstractRuntimeAdapter<T> implements PriorityAdapter {

    @SuppressWarnings( "unchecked" )
    protected <A extends Annotation, V> V getAnnotatedFieldValue( T object,
                                                                  Class<A> annotationType ) throws IllegalAccessException {
        return RuntimeAdapterUtils.getAnnotatedFieldValue( object, annotationType );
    }

    protected <V> Set<V> getFieldValues( T object, Set<String> fieldNames ) throws IllegalAccessException {
        return RuntimeAdapterUtils.getFieldValues( object, fieldNames );
    }

    protected <V> V getFieldValue( T object, String fieldName ) throws IllegalAccessException {
        return RuntimeAdapterUtils.getFieldValue( object, fieldName );
    }

    @SuppressWarnings( "unchecked" )
    protected <A extends Annotation, V> V getAnnotatedFieldValue( T object,
                                                                  Class<?> sourceType,
                                                                  Class<A> annotationType ) throws IllegalAccessException {
        return RuntimeAdapterUtils.getAnnotatedFieldValue( object, sourceType, annotationType );
    }

    protected <V> V getFieldValue( T object,
                                   Class<?> sourceType,
                                   String fieldName ) throws IllegalAccessException {
        return RuntimeAdapterUtils.getFieldValue( object, sourceType, fieldName );
    }

    protected Field getField( T object, String fieldName ) throws IllegalAccessException {
        return RuntimeAdapterUtils.getField( object, fieldName );
    }

    protected Field getField( Class<?> sourceType,
                              String fieldName ) throws IllegalAccessException {
        return RuntimeAdapterUtils.getField( sourceType, fieldName );
    }

    protected static <T extends Annotation> T getClassAnnotation( Class<?> type, Class<T> annotationType ) {
        return RuntimeAdapterUtils.getClassAnnotation( type, annotationType );
    }

    protected static <T extends Annotation> Collection<Field> getFieldAnnotations( Class<?> type, Class<T> annotationType ) {
        return RuntimeAdapterUtils.getFieldAnnotations( type, annotationType );
    }

    protected String getDefinitionId( final Class<?> type ) {
        return RuntimeAdapterUtils.getDefinitionId( type );
    }

    protected String getPropertyId( final Object pojo ) {
        return RuntimeAdapterUtils.getPropertyId( pojo.getClass() );
    }

    @Override
    public boolean isPojoModel() {
        return true;
    }

    @Override
    public int getPriority() {
        return 100;
    }

}

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

import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

public class RuntimeAdapterUtils {

    @SuppressWarnings( "unchecked" )
    public static <T, A extends Annotation, V> V getAnnotatedFieldValue( T object,
                                                                         Class<A> annotationType ) throws IllegalAccessException {
        Class<?> c = object.getClass();
        while ( !c.getName().equals( Object.class.getName() ) ) {
            V result = getAnnotatedFieldValue( object, c, annotationType );
            if ( null != result ) {
                return result;
            }
            c = c.getSuperclass();
        }
        return null;
    }

    public static <T, V> Set<V> getFieldValues( T object, Set<String> fieldNames ) throws IllegalAccessException {
        Set<V> result = new LinkedHashSet<V>();
        for ( String fieldName : fieldNames ) {
            Class<?> c = object.getClass();
            while ( !c.getName().equals( Object.class.getName() ) ) {
                V result1 = getFieldValue( object, c, fieldName );
                if ( null != result1 ) {
                    result.add( result1 );
                }
                c = c.getSuperclass();
            }

        }
        return result;
    }

    public static <T, V> V getFieldValue( T object, String fieldName ) throws IllegalAccessException {
        Class<?> c = object.getClass();
        while ( !c.getName().equals( Object.class.getName() ) ) {
            V result = getFieldValue( object, c, fieldName );
            if ( null != result ) {
                return result;
            }
            c = c.getSuperclass();
        }
        return null;

    }

    @SuppressWarnings( "unchecked" )
    public static <T, A extends Annotation, V> V getAnnotatedFieldValue( T object,
                                                                         Class<?> sourceType,
                                                                         Class<A> annotationType ) throws IllegalAccessException {
        V result = null;
        Field[] fields = sourceType.getDeclaredFields();
        if ( null != fields ) {
            for ( Field field : fields ) {
                A annotation = field.getAnnotation( annotationType );
                if ( null != annotation ) {
                    field.setAccessible( true );
                    result = ( V ) field.get( object );
                    break;
                }
            }
        }
        return result;
    }

    public static <T, V> V getFieldValue( T object,
                                          Class<?> sourceType,
                                          String fieldName ) throws IllegalAccessException {
        V result = null;
        Field[] fields = sourceType.getDeclaredFields();
        if ( null != fields ) {
            for ( Field field : fields ) {
                if ( field.getName().equals( fieldName ) ) {
                    field.setAccessible( true );
                    result = ( V ) field.get( object );
                    break;
                }
            }
        }
        return result;
    }

    public static <T> Field getField( T object, String fieldName ) throws IllegalAccessException {
        Class<?> c = object.getClass();
        while ( !c.getName().equals( Object.class.getName() ) ) {
            Field result = getField( c, fieldName );
            if ( null != result ) {
                return result;
            }
            c = c.getSuperclass();
        }
        return null;

    }

    public static Field getField( Class<?> sourceType,
                                  String fieldName ) throws IllegalAccessException {
        Field[] fields = sourceType.getDeclaredFields();
        if ( null != fields ) {
            for ( Field field : fields ) {
                if ( field.getName().equals( fieldName ) ) {
                    return field;
                }
            }
        }
        return null;
    }

    public static <T extends Annotation> T getClassAnnotation( Class<?> type, Class<T> annotationType ) {
        Class<?> c = type;
        while ( !c.getName().equals( Object.class.getName() ) ) {
            T result = c.getAnnotation( annotationType );
            if ( null != result ) {
                return result;

            }
            c = c.getSuperclass();
        }
        return null;
    }

    public static <T extends Annotation> Collection<Field> getFieldAnnotations( Class<?> type, Class<T> annotationType ) {
        if ( null != type && null != annotationType ) {
            Collection<Field> result = new LinkedList<>();
            Class<?> c = type;
            while ( !c.getName().equals( Object.class.getName() ) ) {
                Collection<Field> fields = _getFieldAnnotations( c, annotationType );
                if ( null != fields && !fields.isEmpty() ) {
                    result.addAll( fields );

                }
                c = c.getSuperclass();
            }
            return result;

        }
        return null;
    }

    private static <T extends Annotation> Collection<Field> _getFieldAnnotations( Class<?> type, Class<T> annotationType ) {
        Field[] fields = type.getDeclaredFields();
        if ( null != fields ) {
            Collection<Field> result = new LinkedList<>();
            for ( Field field : fields ) {
                T annotation = field.getAnnotation( annotationType );
                if ( null != annotation ) {
                    result.add( field );

                }
            }
            return result;
        }
        return null;
    }

    public static String getDefinitionId( final Class<?> type ) {
        return BindableAdapterUtils.getDefinitionId( type );
    }

    public static String getPropertyId( final Object pojo ) {
        return BindableAdapterUtils.getPropertyId( pojo.getClass() );
    }

}

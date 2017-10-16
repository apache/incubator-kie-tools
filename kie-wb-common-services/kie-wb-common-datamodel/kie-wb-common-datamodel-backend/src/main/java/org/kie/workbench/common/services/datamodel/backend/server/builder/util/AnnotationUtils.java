/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.datamodel.backend.server.builder.util;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jboss.errai.config.rebind.EnvUtil;

/**
 * Utilities for handling Java Annotations
 */
public class AnnotationUtils {

    /**
     * Retrieve the value of an Annotation's attribute
     * @param annotation
     * @param attributeName
     * @return
     */
    public static Object getAnnotationAttributeValue( final Annotation annotation,
                                                      final String attributeName ) {
        Object value = null;
        if ( annotation != null ) {
            try {
                value = annotation.annotationType().getMethod( attributeName ).invoke( annotation );

                final Class valueType = value.getClass();
                final Class componentType = valueType.getComponentType();
                final Class portableType = componentType != null ? componentType : valueType;
                if ( !EnvUtil.isPortableType( portableType ) ) {
                    value = value.toString();
                }

            } catch ( Exception ex ) {
                //Swallow
            }
        }
        return value;
    }

    /**
     * Retrieve the annotations on the class signature.
     * @param clazz
     * @return
     */
    public static Set<org.kie.soup.project.datamodel.oracle.Annotation> getClassAnnotations( Class<?> clazz ) {

        if ( clazz == null ) {
            return Collections.EMPTY_SET;
        }

        return getAnnotations( clazz.getAnnotations(), false );
    }

    /**
     * Retrieve the annotations on the field signature.
     * @param field
     * @return
     */
    public static Set<org.kie.soup.project.datamodel.oracle.Annotation> getFieldAnnotations( Field field ) {

        return getFieldAnnotations( field, false );
    }

    /**
     * Retrieve the annotations on the field signature.
     * @param field
     * @param inherited
     * @return
     */
    public static Set<org.kie.soup.project.datamodel.oracle.Annotation> getFieldAnnotations( Field field,
                                                                                                    boolean inherited ) {

        if ( field == null ) {
            return Collections.EMPTY_SET;
        }

        return getAnnotations( field.getDeclaredAnnotations(), inherited );
    }

    private static Set<org.kie.soup.project.datamodel.oracle.Annotation> getAnnotations( final java.lang.annotation.Annotation[] annotations,
                                                                                                boolean checkInheritance ) {
        final Set<org.kie.soup.project.datamodel.oracle.Annotation> fieldAnnotations = new LinkedHashSet<>();
        for ( java.lang.annotation.Annotation a : annotations ) {

            if ( checkInheritance ) {
                if ( !a.annotationType().isAnnotationPresent( Inherited.class ) ) {
                    continue;
                }
            }

            final org.kie.soup.project.datamodel.oracle.Annotation fieldAnnotation = new org.kie.soup.project.datamodel.oracle.Annotation( a.annotationType().getName() );
            for ( Method m : a.annotationType().getDeclaredMethods() ) {
                final String methodName = m.getName();
                fieldAnnotation.addParameter( methodName, getAnnotationAttributeValue( a, methodName ) );
            }
            fieldAnnotations.add( fieldAnnotation );
        }
        return fieldAnnotations;
    }

}

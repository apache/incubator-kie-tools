/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.util;

import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.HasAnnotations;

public class AnnotationValueHandler {

    protected Annotation annotation;

    public AnnotationValueHandler( Annotation annotation ) {
        this.annotation = annotation;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public String getClassName() {
        return annotation.getClassName();
    }

    protected void setValue( String valuePairName, Object value ) {
        if ( valuePairName != null ) {
            annotation.setValue( valuePairName, value );
        } else {
            annotation.removeValue( valuePairName );
        }
    }

    public static String getStringValue( HasAnnotations annotationsHolder, String annotationClassName ) {
        return getStringValue( annotationsHolder.getAnnotation( annotationClassName ), "value" );
    }

    public static String getStringValue( HasAnnotations annotationsHolder, String annotationClassName, String memberValue, String defaultValue ) {
        return getStringValue( annotationsHolder.getAnnotation( annotationClassName ), memberValue, defaultValue );
    }

    public static String getStringValue( HasAnnotations annotationsHolder, String annotationClassName, String memberValue ) {
        return getStringValue( annotationsHolder, annotationClassName, memberValue, null );
    }

    public static String getStringValue( Annotation annotation, String memberName ) {
        return getStringValue( annotation, memberName, null );
    }

    public static String getStringValue( Annotation annotation, String memberName, String defaultValue ) {
        Object value = getValue( annotation, memberName, defaultValue );
        return value != null ? value.toString() : null;
    }

    public static Object getValue( Annotation annotation, String memberName ) {
        return getValue( annotation, memberName, null );
    }

    public static Object getValue( Annotation annotation, String memberName, Object defaultValue ) {
        if ( annotation == null ) {
            return null;
        }
        Object value = annotation.getValue( memberName );
        if ( value != null ) {
            return value;
        } else {
            return defaultValue;
        }
    }

    public static Object getValue( HasAnnotations annotationsHolder, String annotationClassName ) {
        return getValue( annotationsHolder.getAnnotation( annotationClassName ), "value" );
    }

    public static Object getValue( HasAnnotations annotationsHolder, String annotationClassName, String memberValue, Object defaultValue ) {
        return getValue( annotationsHolder.getAnnotation( annotationClassName ), memberValue, defaultValue );
    }

    public static Object getValue( HasAnnotations annotationsHolder, String annotationClassName, String memberValue ) {
        return getValue( annotationsHolder.getAnnotation( annotationClassName ), memberValue, null );
    }

}

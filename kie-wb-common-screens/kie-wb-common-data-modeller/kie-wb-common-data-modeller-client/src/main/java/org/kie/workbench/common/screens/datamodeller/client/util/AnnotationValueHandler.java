/**
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
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
    public static String getStringValue(HasAnnotations annotationsHolder, String annotationClassName) {
        return getStringValue( annotationsHolder.getAnnotation( annotationClassName ), "value");
    }

    public static String getStringValue(HasAnnotations annotationsHolder, String annotationClassName, String memberValue, String defaultValue) {
        return getStringValue( annotationsHolder.getAnnotation( annotationClassName ), memberValue, defaultValue );
    }

    public static String getStringValue(HasAnnotations annotationsHolder, String annotationClassName, String memberValue) {
        return getStringValue(annotationsHolder, annotationClassName, memberValue, null);
    }
    
    public static String getStringValue(Annotation annotation, String memberName) {
        return getStringValue(annotation, memberName, null);
    }

    public static String getStringValue(Annotation annotation, String memberName, String defaultValue) {
        if ( annotation == null ) return null;

        Object value = annotation.getValue( memberName );
        if ( value != null ) {
            return value.toString();
        } else {
            return defaultValue;
        }
    }

}

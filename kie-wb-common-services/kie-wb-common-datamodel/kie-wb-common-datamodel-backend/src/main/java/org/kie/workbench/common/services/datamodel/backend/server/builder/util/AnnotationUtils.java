package org.kie.workbench.common.services.datamodel.backend.server.builder.util;

import java.lang.annotation.Annotation;

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
    public static String getAnnotationAttributeValue( final Annotation annotation,
                                                      final String attributeName ) {
        String value = null;
        if ( annotation != null ) {
            try {
                value = (String) annotation.annotationType().getMethod( attributeName ).invoke( annotation );
            } catch ( Exception ex ) {
                //Swallow
            }
        }
        return value;
    }

}

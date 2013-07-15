package org.kie.workbench.common.screens.datamodeller.client.util;


import org.kie.workbench.common.screens.datamodeller.model.AnnotationTO;
import org.kie.workbench.common.screens.datamodeller.model.ObjectPropertyTO;

public class AnnotationValueHandler {

    protected AnnotationValueHandler() {
    }

    public static AnnotationValueHandler getInstance() {
        return new AnnotationValueHandler();
    }

    public String getStringValue(ObjectPropertyTO propertyTO, String annotationClassName, String memberValue, String defaultValue) {
        return getStringValue(propertyTO.getAnnotation(annotationClassName), memberValue, defaultValue);
    }

    public String getStringValue(ObjectPropertyTO propertyTO, String annotationClassName, String memberValue) {
        return getStringValue(propertyTO, annotationClassName, memberValue, null);
    }
    
    public String getStringValue(AnnotationTO annotationTO, String memberName) {
        return getStringValue(annotationTO, memberName, null);
    }

    public String getStringValue(AnnotationTO annotationTO, String memberName, String defaultValue) {
        if (annotationTO == null) return null;

        Object value = annotationTO.getValue(memberName);
        if (value != null) {
            return value.toString();
        } else {
            return defaultValue;
        }
    }

}

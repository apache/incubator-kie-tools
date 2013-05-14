package org.kie.workbench.common.services.datamodeller.core.impl;

import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractAnnotation implements Annotation {

    private AnnotationDefinition annotationDefinition;

    private Map<String, Object> values = new HashMap<String, Object>();

    public AbstractAnnotation(AnnotationDefinition annotationDefinition) {
        this.annotationDefinition = annotationDefinition;
    }

    @Override
    public String getName() {
        return annotationDefinition.getName();
    }

    @Override
    public Object getValue(String annotationMemberName) {
        return values.get(annotationMemberName);
    }

    @Override
    public Map<String, Object> getValues() {
        return values;
    }

    @Override
    public void setValue(String annotationMemberName, Object value) {
        values.put(annotationMemberName, value);
    }

    @Override
    public AnnotationDefinition getAnnotationDefinition() {
        return annotationDefinition;
    }

    @Override
    public String getClassName() {
        return annotationDefinition.getClassName();
    }

}
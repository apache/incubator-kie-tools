package org.kie.workbench.common.services.datamodeller.core.impl;

import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.HasAnnotations;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractHasAnnotations implements HasAnnotations {

    private List<Annotation> annotations = new ArrayList<Annotation>();

    @Override
    public List<Annotation> getAnnotations() {
        return annotations;
    }

    @Override
    public Annotation getAnnotation(String annotationName) {
        if (annotationName != null) {
            for (Annotation annotation : annotations) {
                if (annotationName.equals(annotation.getName())) return annotation;
            }
        }
        return null;
    }

    @Override
    public void addAnnotation(Annotation annotation) {
        annotations.add(annotation);
    }

    @Override
    public Annotation removeAnnotation(String annotationName) {
        Annotation result = null;
        if (annotationName != null) {
            for (Annotation annotation : annotations) {
                if (annotationName.equals(annotation.getName())) {
                    result = annotation;
                    annotations.remove(annotation);
                    break;
                }
            }
        }
        return result;
    }
}

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
    public Annotation getAnnotation(String className) {
        if (className != null) {
            for (Annotation annotation : annotations) {
                if (className.equals(annotation.getClassName())) return annotation;
            }
        }
        return null;
    }

    @Override
    public void addAnnotation(Annotation annotation) {
        annotations.add(annotation);
    }

    @Override
    public Annotation removeAnnotation(String className) {
        Annotation result = null;
        if (className != null) {
            for (Annotation annotation : annotations) {
                if (className.equals(annotation.getClassName())) {
                    result = annotation;
                    annotations.remove(annotation);
                    break;
                }
            }
        }
        return result;
    }
}

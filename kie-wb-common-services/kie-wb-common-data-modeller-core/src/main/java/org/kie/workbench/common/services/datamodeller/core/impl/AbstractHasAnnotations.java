/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.datamodeller.core.impl;

import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.HasAnnotations;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractHasAnnotations implements HasAnnotations {

    private List<Annotation> annotations = new ArrayList<Annotation>();

    public AbstractHasAnnotations() {
        //errai marshalling
    }

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
        removeAnnotation( annotation.getClassName() );
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

    @Override public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        AbstractHasAnnotations that = ( AbstractHasAnnotations ) o;

        return !( annotations != null ? !annotations.equals( that.annotations ) : that.annotations != null );

    }

    @Override public int hashCode() {
        int result = annotations != null ? annotations.hashCode() : 0;
        result = ~~result;
        return result;
    }
}

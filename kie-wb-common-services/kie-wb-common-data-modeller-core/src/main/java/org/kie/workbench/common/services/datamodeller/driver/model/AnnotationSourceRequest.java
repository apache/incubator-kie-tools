/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.datamodeller.driver.model;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.services.datamodeller.core.Annotation;

public class AnnotationSourceRequest
        extends DriverRequest {

    private List<Annotation> annotations = new ArrayList<Annotation>( );

    public AnnotationSourceRequest() {
    }

    public AnnotationSourceRequest withAnnotation( Annotation annotation ) {
        annotations.add( annotation );
        return this;
    }

    public AnnotationSourceRequest withAnnotations( List<Annotation> annotations ) {
        if ( annotations != null ) {
            for( Annotation annotation : annotations ) {
                withAnnotation( annotation );
            }
        }
        return this;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }
}

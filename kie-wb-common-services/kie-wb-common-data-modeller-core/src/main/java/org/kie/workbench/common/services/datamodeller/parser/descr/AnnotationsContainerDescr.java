/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.services.datamodeller.parser.descr;

import java.util.ArrayList;
import java.util.List;

public class AnnotationsContainerDescr extends ModifiersContainerDescr implements HasAnnotations {

    private List<AnnotationDescr> annotations = new ArrayList<AnnotationDescr>( );

    public AnnotationsContainerDescr( ElementType elementType ) {
        super( elementType );
    }

    public AnnotationsContainerDescr( ElementType elementType, String text, int start, int line, int position ) {
        super( elementType, text, start, line, position );
    }

    public AnnotationsContainerDescr( ElementType elementType, String text, int start, int stop ) {
        super( elementType, text, start, stop );
    }

    public AnnotationsContainerDescr( ElementType elementType, String text, int start, int stop, int line, int position ) {
        super( elementType, text, start, stop, line, position );
    }

    @Override
    public List<AnnotationDescr> getAnnotations( ) {
        return annotations;
    }

    @Override
    public void addAnnotation( AnnotationDescr annotationDesc ) {
        annotations.add( annotationDesc );
    }
}

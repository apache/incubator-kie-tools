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

package org.kie.workbench.common.services.datamodeller.core.impl;

import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationRetention;
import org.kie.workbench.common.services.datamodeller.core.ElementType;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;

import java.util.ArrayList;
import java.util.List;

public class AbstractAnnotationDefinition implements AnnotationDefinition {
    
    protected String className;

    protected List<AnnotationValuePairDefinition> valuePairs = new ArrayList<AnnotationValuePairDefinition> ();

    protected boolean objectAnnotation = false;

    protected boolean propertyAnnotation = false;

    protected List<ElementType> targets = new ArrayList<ElementType>(  );

    protected AnnotationRetention retention = AnnotationRetention.CLASS;

    protected AbstractAnnotationDefinition() {
        //errai marshalling
    }

    protected AbstractAnnotationDefinition(String className) {
        this.className = className;
    }

    protected AbstractAnnotationDefinition(String className, boolean objectAnnotation, boolean propertyAnnotation) {
        this(className);
        this.objectAnnotation = objectAnnotation;
        this.propertyAnnotation = propertyAnnotation;
    }

    @Override
    public List<AnnotationValuePairDefinition> getValuePairs() {
        return valuePairs;
    }

    public void addValuePair( AnnotationValuePairDefinition annotationMember ) {
        valuePairs.add( annotationMember );
    }

    @Override
    public boolean isMarker() {
        return valuePairs == null || valuePairs.size() == 0;
    }

    @Override
    public boolean isNormal() {
        return !isMarker() && !isSingleValue();
    }

    @Override
    public boolean isSingleValue() {
        return valuePairs != null && valuePairs.size() == 1;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public boolean isTypeAnnotation() {
        return targets.contains( ElementType.TYPE );
    }

    @Override
    public boolean isFieldAnnotation() {
        return targets.contains( ElementType.FIELD );
    }

    @Override
    public AnnotationRetention getRetention() {
        return retention;
    }

    public void setRetention( AnnotationRetention retention ) {
        this.retention = retention;
    }

    @Override
    public List<ElementType> getTarget() {
        return targets;
    }

    public void addTarget( ElementType target ) {
        if ( !targets.contains( target ) ) targets.add( target );
    }

    @Override
    public boolean hasValue( String name ) {
        if ( name != null ) {
            for ( AnnotationValuePairDefinition valuePairDefinition : valuePairs ) {
                if ( name.equals( valuePairDefinition.getName() ) ) return true;
            }
        }
        return false;
    }
}

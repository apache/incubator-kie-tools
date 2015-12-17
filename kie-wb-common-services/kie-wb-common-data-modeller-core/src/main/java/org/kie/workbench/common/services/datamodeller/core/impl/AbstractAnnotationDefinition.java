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

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationRetention;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.kie.workbench.common.services.datamodeller.core.ElementType;

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

    @Override
    public AnnotationValuePairDefinition getValuePair( String valuePairName ) {
        if ( valuePairName != null ) {
            for ( AnnotationValuePairDefinition valuePairDefinition : valuePairs ) {
                if ( valuePairName.equals( valuePairDefinition.getName() ) ) {
                    return valuePairDefinition;
                }
            }
        }
        return null;
    }

    public void addValuePair( AnnotationValuePairDefinition valuePair ) {
        valuePairs.add( valuePair );
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
    public boolean hasValue( String valuePairName ) {
        if ( valuePairName != null ) {
            for ( AnnotationValuePairDefinition valuePairDefinition : valuePairs ) {
                if ( valuePairName.equals( valuePairDefinition.getName() ) ) return true;
            }
        }
        return false;
    }

    @Override public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        AbstractAnnotationDefinition that = ( AbstractAnnotationDefinition ) o;

        if ( objectAnnotation != that.objectAnnotation ) {
            return false;
        }
        if ( propertyAnnotation != that.propertyAnnotation ) {
            return false;
        }
        if ( className != null ? !className.equals( that.className ) : that.className != null ) {
            return false;
        }
        if ( valuePairs != null ? !valuePairs.equals( that.valuePairs ) : that.valuePairs != null ) {
            return false;
        }
        if ( targets != null ? !targets.equals( that.targets ) : that.targets != null ) {
            return false;
        }
        return retention == that.retention;

    }

    @Override public int hashCode() {
        int result = className != null ? className.hashCode() : 0;
        result = ~~result;
        result = 31 * result + ( valuePairs != null ? valuePairs.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( objectAnnotation ? 1 : 0 );
        result = ~~result;
        result = 31 * result + ( propertyAnnotation ? 1 : 0 );
        result = ~~result;
        result = 31 * result + ( targets != null ? targets.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( retention != null ? retention.hashCode() : 0 );
        result = ~~result;
        return result;
    }
}

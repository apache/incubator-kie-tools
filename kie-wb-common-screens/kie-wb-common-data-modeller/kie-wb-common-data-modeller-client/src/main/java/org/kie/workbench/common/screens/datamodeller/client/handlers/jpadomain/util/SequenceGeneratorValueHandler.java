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

package org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.util;

import java.util.Map;

import org.kie.workbench.common.screens.datamodeller.model.jpadomain.JPADomainAnnotations;
import org.kie.workbench.common.screens.datamodeller.client.util.AnnotationValueHandler;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;

public class SequenceGeneratorValueHandler extends AnnotationValueHandler {

    public static final String NAME = "name";

    public static final String SEQUENCE_NAME = "sequenceName";

    public static final String CATALOG = "catalog";

    public static final String SCHEMA = "schema";

    public static final String INITIAL_VALUE = "initialValue";

    public static final String ALLOCATION_SIZE = "allocationSize";

    public SequenceGeneratorValueHandler( Annotation annotation ) {
        super( annotation );
    }

    public String getName() {
        return getStringValue( annotation, NAME );
    }

    public void setName( String name ) {
        setValue( NAME, name  );
    }

    public String getSequenceName() {
        return getStringValue( annotation, SEQUENCE_NAME );
    }

    public void setSequenceName( String sequenceName ) {
        setValue( SEQUENCE_NAME, sequenceName );
    }

    public void setCatalog( String catalog ) {
        setValue( CATALOG, catalog );
    }

    public String getCatalog() {
        return getStringValue( annotation, CATALOG );
    }

    public String getSchema() {
        return getStringValue( annotation, SCHEMA );
    }

    public void setSchema( String schema ) {
        setValue( SCHEMA, schema );
    }

    public String getInitialValue() {
        Object value = annotation.getValue( INITIAL_VALUE );
        return value != null ? value.toString() : null;

    }

    public void setInitialValue( Integer initialValue ) {
        setValue( INITIAL_VALUE, initialValue );
    }

    public String getAllocationSize() {
        Object value = annotation.getValue( ALLOCATION_SIZE );
        return value != null ? value.toString() : null;
    }

    public void setAllocationSize( Integer allocationSize ) {
        setValue( ALLOCATION_SIZE, allocationSize );
    }

    public static Annotation createAnnotation( String name,
            String sequenceName,
            Integer initialValue,
            Integer allocationSize,
            Map<String, AnnotationDefinition> annotationDefinitions ) {

        SequenceGeneratorValueHandler valueHandler = new SequenceGeneratorValueHandler(
                new AnnotationImpl( annotationDefinitions.get( JPADomainAnnotations.JAVAX_PERSISTENCE_SEQUENCE_GENERATOR_ANNOTATION ) ) );
        valueHandler.setName( name );
        valueHandler.setSequenceName( sequenceName );
        valueHandler.setInitialValue( initialValue );
        valueHandler.setAllocationSize( allocationSize );
        return valueHandler.getAnnotation();
    }

    public static Annotation createAnnotation( String name, String sequenceName,
            Map<String, AnnotationDefinition> annotationDefinitions ) {

        SequenceGeneratorValueHandler valueHandler = new SequenceGeneratorValueHandler(
                new AnnotationImpl( annotationDefinitions.get( JPADomainAnnotations.JAVAX_PERSISTENCE_SEQUENCE_GENERATOR_ANNOTATION ) ) );
        valueHandler.setName( name );
        valueHandler.setSequenceName( sequenceName );
        return valueHandler.getAnnotation();
    }
}

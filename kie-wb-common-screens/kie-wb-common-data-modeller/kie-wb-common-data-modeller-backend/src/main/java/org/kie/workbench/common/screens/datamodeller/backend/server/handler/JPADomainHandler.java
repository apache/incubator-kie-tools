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

package org.kie.workbench.common.screens.datamodeller.backend.server.handler;

import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.kie.workbench.common.services.datamodeller.driver.impl.JavaRoasterModelDriver;

@ApplicationScoped
public class JPADomainHandler implements DomainHandler {

    @Override
    public void setDefaultValues( DataObject dataObject, Map<String, Object> portableParams ) {

        if ( portableParams != null ) {
            Object currentValue = portableParams.get( "persistable" );
            boolean isPersistable = Boolean.valueOf( currentValue != null ? currentValue.toString() : null );
            currentValue = portableParams.get( "tableName" );
            String tableName = currentValue != null ? currentValue.toString() : null;

            if ( isPersistable ) {

                //add default parameters for a persistable data object
                JavaRoasterModelDriver modelDriver = new JavaRoasterModelDriver();

                //mark the class as Entity
                dataObject.addAnnotation( new AnnotationImpl( modelDriver.getConfiguredAnnotation( Entity.class.getName() ) ) );

                if ( tableName != null && !"".equals( tableName.trim() ) ) {
                    Annotation tableAnnotation = new AnnotationImpl( modelDriver.getConfiguredAnnotation( Table.class.getName() ) );
                    tableAnnotation.setValue( "name", tableName.trim() );
                    dataObject.addAnnotation( tableAnnotation );
                }

                //add the by default id field
                ObjectProperty id = dataObject.addProperty( "id", Long.class.getName() );
                id.addAnnotation( new AnnotationImpl( modelDriver.getConfiguredAnnotation( Id.class.getName() ) ) );

                //set the by default generated value annotation.
                String generatorName = createDefaultGeneratorName( dataObject.getName() );
                Annotation generatedValue = new AnnotationImpl( modelDriver.getConfiguredAnnotation( GeneratedValue.class.getName() ) );
                generatedValue.setValue( "generator", generatorName );
                generatedValue.setValue( "strategy", GenerationType.AUTO.name() );
                id.addAnnotation( generatedValue );

                //set by default sequence generator
                Annotation sequenceGenerator = new AnnotationImpl( modelDriver.getConfiguredAnnotation( SequenceGenerator.class.getName() ) );

                String sequenceName = createDefaultSequenceName( dataObject.getName() );
                sequenceGenerator.setValue( "name", generatorName );
                sequenceGenerator.setValue( "sequenceName", sequenceName );
                id.addAnnotation( sequenceGenerator );

                boolean isAuditable = portableParams.containsKey( "audited" ) ?
                        Boolean.valueOf( portableParams.get( "audited" ).toString() ) : false;
                if ( isAuditable ) {
                    Annotation audited = new AnnotationImpl( modelDriver.getConfiguredAnnotation( Audited.class.getName() ) );
                    audited.setValue( "targetAuditMode", RelationTargetAuditMode.NOT_AUDITED.name() );
                    dataObject.addAnnotation( audited );
                }
            }
        }
    }

    @Override
    public List<AnnotationDefinition> getManagedAnnotations() {
        return null;
    }

    private String createDefaultGeneratorName( String objectName ) {
        return objectName.toUpperCase() +  "_ID_GENERATOR";
    }

    private String createDefaultSequenceName( String objectName ) {
        return objectName.toUpperCase() + "_ID_SEQ";
    }
}

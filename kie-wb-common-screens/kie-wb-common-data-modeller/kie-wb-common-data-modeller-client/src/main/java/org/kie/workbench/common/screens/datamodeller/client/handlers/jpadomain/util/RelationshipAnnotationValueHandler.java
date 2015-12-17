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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kie.workbench.common.screens.datamodeller.model.jpadomain.CascadeType;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.FetchMode;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.JPADomainAnnotations;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.RelationType;
import org.kie.workbench.common.screens.datamodeller.client.util.AnnotationValueHandler;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;

public class RelationshipAnnotationValueHandler extends AnnotationValueHandler {

    public static final String RELATION_TYPE = "relationType";

    public static final String CASCADE = "cascade";

    public static final String FETCH = "fetch";

    public static final String OPTIONAL = "optional";

    public static final String MAPPED_BY = "mappedBy";

    public static final String ORPHAN_REMOVAL = "orphanRemoval";

    public RelationshipAnnotationValueHandler( Annotation annotation ) {
        super( annotation );
    }

    public RelationType getRelationType() {
        if ( JPADomainAnnotations.JAVAX_PERSISTENCE_ONE_TO_ONE.equals( annotation.getClassName() ) ) {
            return RelationType.ONE_TO_ONE;
        } else if ( JPADomainAnnotations.JAVAX_PERSISTENCE_ONE_TO_MANY.equals( annotation.getClassName() ) ) {
            return RelationType.ONE_TO_MANY;
        } else if ( JPADomainAnnotations.JAVAX_PERSISTENCE_MANY_TO_ONE.equals( annotation.getClassName() ) ) {
            return RelationType.MANY_TO_ONE;
        } else if ( JPADomainAnnotations.JAVAX_PERSISTENCE_MANY_TO_MANY.equals( annotation.getClassName() ) ) {
            return RelationType.MANY_TO_MANY;
        }
        return null;
    }

    public boolean isOneToOne() {
        return RelationType.ONE_TO_ONE.equals( getRelationType() );
    }

    public boolean isOneToMany() {
        return RelationType.ONE_TO_MANY.equals( getRelationType() );
    }

    public boolean isManyToOne() {
        return RelationType.MANY_TO_ONE.equals( getRelationType() );
    }

    public boolean isManyToMany() {
        return RelationType.MANY_TO_MANY.equals( getRelationType() );
    }

    public List<CascadeType> getCascade() {
        List<Object> internalCascadeTypes = (List<Object>) annotation.getValue( CASCADE );
        List<CascadeType> cascadeTypes = internalCascadeTypes != null ? new ArrayList<CascadeType>( internalCascadeTypes.size() ) : null;
        if ( internalCascadeTypes != null ) {
            for ( Object internalCascadeType : internalCascadeTypes ) {
                try {
                    cascadeTypes.add( internalCascadeType != null ? CascadeType.valueOf( internalCascadeType.toString() ) : null );
                } catch (Exception e) {
                }
            }
        }
        return cascadeTypes;
    }

    public void setCascade( List<CascadeType> cascade ) {
        if ( cascade != null ) {
            List<Object> cascadeTypes = new ArrayList<Object>( cascade.size() );
            for ( CascadeType cascadeType : cascade ) {
                cascadeTypes.add( cascadeType.name() );
            }
            annotation.setValue( CASCADE, cascadeTypes );
        } else {
            annotation.removeValue( CASCADE );
        }
    }

    public FetchMode getFetch() {
        try {
            return FetchMode.valueOf( getStringValue( annotation, FETCH ) );
        } catch ( Exception e ) {
            return null;
        }
    }

    public void setFetch( FetchMode fetch ) {
        if ( fetch != null ) {
            annotation.setValue( FETCH, fetch.name() );
        } else {
            annotation.removeValue( FETCH );
        }
    }

    public Boolean getOptional( ) {
        String value = getStringValue( annotation, OPTIONAL );
        if ( value != null ) {
            return Boolean.parseBoolean( value );
        } else {
            return null;
        }
    }

    public void setOptional( Boolean optional ) {
        if ( optional != null ) {
            annotation.setValue( OPTIONAL, optional.toString() );
        } else {
            annotation.removeValue( OPTIONAL );
        }
    }

    public String getMappedBy() {
        return getStringValue( annotation, MAPPED_BY );
    }

    public void setMappedBy( String mappedBy ) {
        if ( mappedBy != null ) {
            annotation.setValue( MAPPED_BY, mappedBy );
        } else {
            annotation.removeValue( MAPPED_BY );
        }
    }

    public Boolean getOrphanRemoval( ) {
        String value = getStringValue( annotation, ORPHAN_REMOVAL );
        if ( value != null ) {
            return Boolean.parseBoolean( value );
        } else {
            return null;
        }
    }

    public void setOrphanRemoval( Boolean orphanRemoval ) {
        if ( orphanRemoval != null ) {
            annotation.setValue( ORPHAN_REMOVAL, orphanRemoval.toString() );
        } else {
            annotation.removeValue( ORPHAN_REMOVAL );
        }
    }

    public static Annotation createAnnotation( RelationType relationType, List<CascadeType> cascadeTypes, Map<String, AnnotationDefinition> annotationDefinitions ) {
        return createAnnotation( relationType, cascadeTypes, null, null, null, null, annotationDefinitions );
    }

    public static Annotation createAnnotation( RelationType relationType, List<CascadeType> cascadeTypes, FetchMode fetchMode,
                Boolean optional, String mappedBy, Boolean orphanRemoval, Map<String, AnnotationDefinition> annotationDefinitions) {

        if ( relationType == null ) return null;
        RelationshipAnnotationValueHandler valueHandler = null;

        switch ( relationType ) {
            case ONE_TO_ONE:
                valueHandler = new RelationshipAnnotationValueHandler( new AnnotationImpl( annotationDefinitions.get( JPADomainAnnotations.JAVAX_PERSISTENCE_ONE_TO_ONE ) ) );
                valueHandler.setOptional( optional );
                valueHandler.setMappedBy( mappedBy );
                valueHandler.setOrphanRemoval( orphanRemoval );
                break;
            case ONE_TO_MANY:
                valueHandler = new RelationshipAnnotationValueHandler( new AnnotationImpl( annotationDefinitions.get( JPADomainAnnotations.JAVAX_PERSISTENCE_ONE_TO_MANY ) ) );
                valueHandler.setMappedBy( mappedBy );
                valueHandler.setOrphanRemoval( orphanRemoval );
                break;
            case MANY_TO_ONE:
                valueHandler = new RelationshipAnnotationValueHandler( new AnnotationImpl( annotationDefinitions.get( JPADomainAnnotations.JAVAX_PERSISTENCE_MANY_TO_ONE ) ) );
                valueHandler.setOptional( optional );
                break;
            case MANY_TO_MANY:
                valueHandler = new RelationshipAnnotationValueHandler( new AnnotationImpl( annotationDefinitions.get( JPADomainAnnotations.JAVAX_PERSISTENCE_MANY_TO_MANY ) ) );
                valueHandler.setMappedBy( mappedBy );
                break;
        }

        valueHandler.setFetch( fetchMode );
        valueHandler.setCascade( cascadeTypes );

        return valueHandler.getAnnotation();
    }
}

/*
 * Copyright 2014 JBoss, by Red Hat, Inc
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
package org.drools.workbench.screens.enums.backend.server.indexing;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.kie.workbench.common.services.datamodel.backend.server.builder.util.DataEnumLoader;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.DefaultIndexBuilder;
import org.kie.workbench.common.services.refactoring.model.index.Type;
import org.kie.workbench.common.services.refactoring.model.index.TypeField;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueFieldIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueTypeIndexTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.data.Pair;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * Visitor to extract index information from a DataEnumLoader
 */
public class EnumIndexVisitor {

    private static final Logger logger = LoggerFactory.getLogger( EnumIndexVisitor.class );

    private final ProjectDataModelOracle dmo;
    private final DefaultIndexBuilder builder;
    private final DataEnumLoader enumLoader;
    private final Set<Pair<String, String>> results = new HashSet<Pair<String, String>>();

    public EnumIndexVisitor( final ProjectDataModelOracle dmo,
                             final DefaultIndexBuilder builder,
                             final DataEnumLoader enumLoader ) {
        this.dmo = PortablePreconditions.checkNotNull( "dmo",
                                                       dmo );
        this.builder = PortablePreconditions.checkNotNull( "builder",
                                                           builder );
        this.enumLoader = PortablePreconditions.checkNotNull( "enumLoader",
                                                              enumLoader );
    }

    public Set<Pair<String, String>> visit() {
        if ( enumLoader.hasErrors() ) {
            return results;
        }
        for ( Map.Entry<String, String[]> e : enumLoader.getData().entrySet() ) {
            //Add type
            final String typeName = getTypeName( e.getKey() );
            final String fullyQualifiedClassName = getFullyQualifiedClassName( typeName );

            //Add field
            final String fieldName = getFieldName( e.getKey() );
            final String fieldFullyQualifiedClassName = getFieldFullyQualifiedClassName( fullyQualifiedClassName,
                                                                                         fieldName );

            //If either type or field could not be resolved log a warning
            if ( fullyQualifiedClassName == null ) {
                logger.warn( "Index entry will not be created for '" + e.getKey() + "'. Unable to determine FQCN for '" + typeName + "'. " );

            } else {
                builder.addGenerator( new Type( new ValueTypeIndexTerm( fullyQualifiedClassName ) ) );
                if ( fieldFullyQualifiedClassName == null ) {
                    logger.warn( "Index entry will not be created for '" + e.getKey() + "'. Unable to determine FQCN for '" + typeName + "." + fieldName + "'. " );

                } else {
                    builder.addGenerator( new TypeField( new ValueFieldIndexTerm( fieldName ),
                                                         new ValueTypeIndexTerm( fieldFullyQualifiedClassName ),
                                                         new ValueTypeIndexTerm( fullyQualifiedClassName ) ) );
                }
            }
        }

        results.addAll( builder.build() );
        return results;
    }

    private String getTypeName( final String key ) {
        final int hashIndex = key.indexOf( "#" );
        return key.substring( 0,
                              hashIndex );
    }

    private String getFieldName( final String key ) {
        final int hashIndex = key.indexOf( "#" );
        return key.substring( hashIndex + 1 );
    }

    private String getFullyQualifiedClassName( final String typeName ) {
        if ( typeName.contains( "." ) ) {
            return typeName;
        }
        //Look-up FQCN in DMO, if not found return null and log a warning
        for ( Map.Entry<String, ModelField[]> e : dmo.getProjectModelFields().entrySet() ) {
            String fqcn = e.getKey();
            if ( e.getKey().contains( "." ) ) {
                fqcn = fqcn.substring( fqcn.lastIndexOf( "." ) + 1 );
            }
            if ( fqcn.equals( typeName ) ) {
                return e.getKey();
            }
        }
        return null;
    }

    private String getFieldFullyQualifiedClassName( final String fullyQualifiedClassName,
                                                    final String fieldName ) {
        //Look-up FQCN in DMO, if not found return null and log a warning
        final ModelField[] mfs = dmo.getProjectModelFields().get( fullyQualifiedClassName );
        if ( mfs != null ) {
            for ( ModelField mf : mfs ) {
                if ( mf.getName().equals( fieldName ) ) {
                    return mf.getClassName();
                }
            }
        }
        return null;
    }

}

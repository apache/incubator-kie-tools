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

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.kie.workbench.common.services.datamodel.backend.server.builder.util.DataEnumLoader;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.DefaultIndexBuilder;
import org.kie.workbench.common.services.refactoring.model.index.Type;
import org.kie.workbench.common.services.refactoring.model.index.TypeField;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueFieldIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueTypeIndexTerm;
import org.uberfire.commons.data.Pair;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * Visitor to extract index information from a DataEnumLoader
 */
public class EnumIndexVisitor {

    private final ProjectDataModelOracle dmo;
    private final DefaultIndexBuilder builder;
    private final DataEnumLoader enumLoader;
    private final String packageName;
    private final Set<Pair<String, String>> results = new HashSet<Pair<String, String>>();

    public EnumIndexVisitor( final ProjectDataModelOracle dmo,
                             final DefaultIndexBuilder builder,
                             final DataEnumLoader enumLoader,
                             final String packageName ) {
        this.dmo = PortablePreconditions.checkNotNull( "dmo",
                                                       dmo );
        this.builder = PortablePreconditions.checkNotNull( "builder",
                                                           builder );
        this.enumLoader = PortablePreconditions.checkNotNull( "enumLoader",
                                                              enumLoader );
        this.packageName = PortablePreconditions.checkNotNull( "packageName",
                                                               packageName );
    }

    public Set<Pair<String, String>> visit() {
        if ( enumLoader.hasErrors() ) {
            return results;
        }
        for ( Map.Entry<String, String[]> e : enumLoader.getData().entrySet() ) {
            //Add type
            final String typeName = getTypeName( e.getKey() );
            final String fullyQualifiedClassName = getFullyQualifiedClassName( typeName );
            builder.addGenerator( new Type( new ValueTypeIndexTerm( fullyQualifiedClassName ) ) );

            //Add field
            final String fieldName = getFieldName( e.getKey() );
            final String fieldFullyQualifiedClassName = getFieldFullyQualifiedClassName( fullyQualifiedClassName,
                                                                                         fieldName );
            builder.addGenerator( new TypeField( new ValueFieldIndexTerm( fieldName ),
                                                 new ValueTypeIndexTerm( fieldFullyQualifiedClassName ),
                                                 new ValueTypeIndexTerm( fullyQualifiedClassName ) ) );
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
        return ( !( packageName == null || packageName.isEmpty() ) ? packageName + "." + typeName : typeName );
    }

    private String getFieldFullyQualifiedClassName( final String fullyQualifiedClassName,
                                                    final String fieldName ) {
        final ModelField[] mfs = dmo.getProjectModelFields().get( fullyQualifiedClassName );
        for ( ModelField mf : mfs ) {
            if ( mf.getName().equals( fieldName ) ) {
                return mf.getClassName();
            }
        }
        return DataType.TYPE_OBJECT;
    }

}

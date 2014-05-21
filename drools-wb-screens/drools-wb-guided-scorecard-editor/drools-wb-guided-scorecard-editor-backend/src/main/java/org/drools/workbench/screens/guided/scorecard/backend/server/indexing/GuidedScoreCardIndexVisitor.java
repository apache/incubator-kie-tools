/*
 * Copyright 2012 JBoss Inc
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
package org.drools.workbench.screens.guided.scorecard.backend.server.indexing;

import java.util.Set;

import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.drools.workbench.models.guided.scorecard.shared.Characteristic;
import org.drools.workbench.models.guided.scorecard.shared.ScoreCardModel;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.DefaultIndexBuilder;
import org.kie.workbench.common.services.refactoring.model.index.Type;
import org.kie.workbench.common.services.refactoring.model.index.TypeField;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueFieldIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueTypeIndexTerm;
import org.uberfire.commons.data.Pair;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * Visitor to extract index information from a Guided Score Card Model
 */
public class GuidedScoreCardIndexVisitor {

    private final ProjectDataModelOracle dmo;
    private final DefaultIndexBuilder builder;
    private final ScoreCardModel model;

    public GuidedScoreCardIndexVisitor( final ProjectDataModelOracle dmo,
                                        final DefaultIndexBuilder builder,
                                        final ScoreCardModel model ) {
        this.dmo = PortablePreconditions.checkNotNull( "dmo",
                                                       dmo );
        this.builder = PortablePreconditions.checkNotNull( "builder",
                                                           builder );
        this.model = PortablePreconditions.checkNotNull( "model",
                                                         model );
    }

    public Set<Pair<String, String>> visit() {
        //Add type
        final String typeName = model.getFactName();
        final String fullyQualifiedClassName = getFullyQualifiedClassName( typeName );
        builder.addGenerator( new Type( new ValueTypeIndexTerm( fullyQualifiedClassName ) ) );

        //Add field
        final String fieldName = model.getFieldName();
        final String fieldFullyQualifiedClassName = getFieldFullyQualifiedClassName( fullyQualifiedClassName,
                                                                                     fieldName );
        builder.addGenerator( new TypeField( new ValueFieldIndexTerm( fieldName ),
                                             new ValueTypeIndexTerm( fieldFullyQualifiedClassName ),
                                             new ValueTypeIndexTerm( fullyQualifiedClassName ) ) );

        //Add Characteristics
        for ( Characteristic c : model.getCharacteristics() ) {
            visit( c );
        }

        return builder.build();
    }

    private void visit( final Characteristic c ) {
        //Add type
        final String typeName = c.getFact();
        final String fullyQualifiedClassName = getFullyQualifiedClassName( typeName );
        builder.addGenerator( new Type( new ValueTypeIndexTerm( fullyQualifiedClassName ) ) );

        //Add field
        final String fieldName = c.getField();
        final String fieldFullyQualifiedClassName = getFieldFullyQualifiedClassName( fullyQualifiedClassName,
                                                                                     fieldName );
        builder.addGenerator( new TypeField( new ValueFieldIndexTerm( fieldName ),
                                             new ValueTypeIndexTerm( fieldFullyQualifiedClassName ),
                                             new ValueTypeIndexTerm( fullyQualifiedClassName ) ) );
    }

    private String getFullyQualifiedClassName( final String typeName ) {
        if ( typeName.contains( "." ) ) {
            return typeName;
        }

        for ( Import i : model.getImports().getImports() ) {
            if ( i.getType().endsWith( typeName ) ) {
                return i.getType();
            }
        }
        final String packageName = model.getPackageName();
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

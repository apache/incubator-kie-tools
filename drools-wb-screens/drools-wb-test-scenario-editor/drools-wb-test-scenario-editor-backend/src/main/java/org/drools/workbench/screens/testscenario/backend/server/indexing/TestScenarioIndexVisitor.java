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
package org.drools.workbench.screens.testscenario.backend.server.indexing;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.drools.workbench.models.testscenarios.shared.FactData;
import org.drools.workbench.models.testscenarios.shared.Field;
import org.drools.workbench.models.testscenarios.shared.Fixture;
import org.drools.workbench.models.testscenarios.shared.FixtureList;
import org.drools.workbench.models.testscenarios.shared.FixturesMap;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.models.testscenarios.shared.VerifyFact;
import org.drools.workbench.models.testscenarios.shared.VerifyField;
import org.drools.workbench.models.testscenarios.shared.VerifyRuleFired;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.DefaultIndexBuilder;
import org.kie.workbench.common.services.refactoring.model.index.Rule;
import org.kie.workbench.common.services.refactoring.model.index.Type;
import org.kie.workbench.common.services.refactoring.model.index.TypeField;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueFieldIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueRuleIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueTypeIndexTerm;
import org.uberfire.commons.data.Pair;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * Visitor to extract index information from a Scenario
 */
public class TestScenarioIndexVisitor {

    private final ProjectDataModelOracle dmo;
    private final DefaultIndexBuilder builder;
    private final Scenario model;
    private final Map<String, String> factDataToFullyQualifiedClassNameMap = new HashMap<String, String>();

    public TestScenarioIndexVisitor( final ProjectDataModelOracle dmo,
                                     final DefaultIndexBuilder builder,
                                     final Scenario model ) {
        this.dmo = PortablePreconditions.checkNotNull( "dmo",
                                                       dmo );
        this.builder = PortablePreconditions.checkNotNull( "builder",
                                                           builder );
        this.model = PortablePreconditions.checkNotNull( "model",
                                                         model );
    }

    public Set<Pair<String, String>> visit() {
        visit( model );
        return builder.build();
    }

    private void visit( final Scenario scenario ) {
        for ( Fixture fixture : scenario.getFixtures() ) {
            visit( fixture );
        }
    }

    private void visit( final Fixture fixture ) {
        if ( fixture instanceof FixtureList ) {
            for ( Fixture child : ( (FixtureList) fixture ) ) {
                visit( child );
            }

        } else if ( fixture instanceof FixturesMap ) {
            for ( Fixture child : ( (FixturesMap) fixture ).values() ) {
                visit( child );
            }

        } else if ( fixture instanceof FactData ) {
            final FactData factData = (FactData) fixture;
            final String typeName = factData.getType();
            final String fullyQualifiedClassName = getFullyQualifiedClassName( typeName );
            builder.addGenerator( new Type( new ValueTypeIndexTerm( fullyQualifiedClassName ) ) );

            factDataToFullyQualifiedClassNameMap.put( factData.getName(),
                                                      fullyQualifiedClassName );

            for ( Field field : factData.getFieldData() ) {
                final String fieldName = field.getName();
                final String fieldFullyQualifiedClassName = getFieldFullyQualifiedClassName( fullyQualifiedClassName,
                                                                                             fieldName );
                builder.addGenerator( new TypeField( new ValueFieldIndexTerm( fieldName ),
                                                     new ValueTypeIndexTerm( fieldFullyQualifiedClassName ),
                                                     new ValueTypeIndexTerm( fullyQualifiedClassName ) ) );
            }

        } else if ( fixture instanceof VerifyFact ) {
            final VerifyFact verifyFact = (VerifyFact) fixture;
            final String typeName = verifyFact.getName();

            //If VerifyFact is not anonymous lookup FQCN from previous FactData elements
            String fullyQualifiedClassName = null;
            if ( !verifyFact.anonymous ) {
                fullyQualifiedClassName = factDataToFullyQualifiedClassNameMap.get( verifyFact.getName() );
            } else {
                fullyQualifiedClassName = getFullyQualifiedClassName( typeName );
            }
            if ( fullyQualifiedClassName != null ) {
                builder.addGenerator( new Type( new ValueTypeIndexTerm( fullyQualifiedClassName ) ) );
            }

            for ( VerifyField field : verifyFact.getFieldValues() ) {
                final String fieldName = field.getFieldName();
                final String fieldFullyQualifiedClassName = getFieldFullyQualifiedClassName( fullyQualifiedClassName,
                                                                                             fieldName );
                builder.addGenerator( new TypeField( new ValueFieldIndexTerm( fieldName ),
                                                     new ValueTypeIndexTerm( fieldFullyQualifiedClassName ),
                                                     new ValueTypeIndexTerm( fullyQualifiedClassName ) ) );
            }
        } else if ( fixture instanceof VerifyRuleFired ) {
            final VerifyRuleFired verifyRuleFired = (VerifyRuleFired) fixture;
            builder.addGenerator( new Rule( new ValueRuleIndexTerm( verifyRuleFired.getRuleName() ) ) );
        }
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

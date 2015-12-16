/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.drools.workbench.screens.testscenario.backend.server.indexing;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.testscenarios.shared.FactData;
import org.drools.workbench.models.testscenarios.shared.Field;
import org.drools.workbench.models.testscenarios.shared.FieldData;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.models.testscenarios.shared.VerifyFact;
import org.drools.workbench.models.testscenarios.shared.VerifyField;
import org.drools.workbench.models.testscenarios.shared.VerifyRuleFired;

public class TestScenarioFactory {

    public static Scenario makeTestScenarioWithVerifyFact( final String packageName,
                                                           final Collection<Import> imports,
                                                           final String name ) {
        final Scenario model = new Scenario();
        model.getImports().getImports().addAll( imports );
        model.setPackageName( packageName );
        model.setName( name );

        model.getFixtures().add( new FactData( "Applicant",
                                               "$a",
                                               new ArrayList<Field>() {{
                                                   add( new FieldData( "age",
                                                                       "33" ) );
                                               }},
                                               false ) );
        model.getFixtures().add( new VerifyFact( "Mortgage",
                                                 new ArrayList<VerifyField>() {{
                                                     add( new VerifyField( "amount",
                                                                           "10000",
                                                                           "==" ) );
                                                 }},
                                                 true ) );

        return model;
    }

    public static Scenario makeTestScenarioWithGlobalVerifyGlobal(final String packageName,
                                                                  final Collection<Import> imports,
                                                                  final String name) {
        final Scenario model = new Scenario();
        model.getImports().getImports().addAll(imports);
        model.setPackageName(packageName);
        model.setName(name);

        model.getGlobals().add(new FactData("Date",
                                             "day",
                                             new ArrayList<Field>(),
                                             false));
        model.getFixtures().add(new VerifyFact("day",
                                               new ArrayList<VerifyField>() {{
                                                   add(new VerifyField("minutes",
                                                                       "45",
                                                                       "=="));
                                               }},
                                               false));

        return model;
    }

    public static Scenario makeTestScenarioWithoutVerifyFact( final String packageName,
                                                              final Collection<Import> imports,
                                                              final String name ) {
        final Scenario model = new Scenario();
        model.getImports().getImports().addAll( imports );
        model.setPackageName( packageName );
        model.setName( name );

        model.getFixtures().add( new FactData( "Applicant",
                                               "$a",
                                               new ArrayList<Field>() {{
                                                   add( new FieldData( "age",
                                                                       "33" ) );
                                               }},
                                               false ) );

        return model;
    }

    //See https://bugzilla.redhat.com/show_bug.cgi?id=1179158
    public static Scenario makeTestScenarioWithVerifyRuleFired( final String packageName,
                                                                final Collection<Import> imports,
                                                                final String name ) {
        final Scenario model = new Scenario();
        model.getImports().getImports().addAll( imports );
        model.setPackageName( packageName );
        model.setName( name );

        model.getFixtures().add( new VerifyRuleFired( "test",
                                                      1,
                                                      true ) );

        return model;
    }

}

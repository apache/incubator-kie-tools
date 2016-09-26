/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.testscenario.backend.server;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;

public class ScenarioTestEditorServiceImplTest {

    private Scenario scenario;

    private ScenarioTestEditorServiceImpl service;

    private String[] fullyQualifiedClassNamesUsedByGlobals;

    private String[] fullyQualifiedClassNamesUsedByModel;

    private Path path = null;

    @Before
    public void setup() {
        scenario = new Scenario();
        service = new ScenarioTestEditorServiceImpl() {

            @Override
            Collection<String> getFullyQualifiedClassNamesUsedByGlobals( final PackageDataModelOracle dataModelOracle ) {
                return Arrays.asList( fullyQualifiedClassNamesUsedByGlobals );
            }

            @Override
            Set<String> getFullyQualifiedClassNamesUsedByModel( final Scenario scenario ) {
                return new HashSet<String>() {{
                    addAll( Arrays.asList( fullyQualifiedClassNamesUsedByModel ) );
                }};
            }

            @Override
            PackageDataModelOracle getDataModel( final Path path ) {
                return null;
            }
        };
    }

    @Test
    public void runScenarioWithDependentImports() throws Exception {
        fullyQualifiedClassNamesUsedByGlobals = new String[]{};
        fullyQualifiedClassNamesUsedByModel = new String[]{};

        service.addDependentImportsToScenario( scenario, path );

        assertEquals( 0, scenarioImports().size() );
    }

    @Test
    public void runScenarioWithoutDependentImports() throws Exception {
        scenarioImports().add( new Import( "org.junit.Test0" ) );
        scenarioImports().add( new Import( "org.junit.Test1" ) );

        fullyQualifiedClassNamesUsedByGlobals = new String[]{ "org.junit.Test1", "org.junit.Test2", "org.junit.Test3" };
        fullyQualifiedClassNamesUsedByModel = new String[]{ "org.junit.Test3", "org.junit.Test4", "org.junit.Test5" };

        service.addDependentImportsToScenario( scenario, path );

        assertEquals( 6, scenarioImports().size() );
    }

    private List<Import> scenarioImports() {
        return scenario.getImports().getImports();
    }
}

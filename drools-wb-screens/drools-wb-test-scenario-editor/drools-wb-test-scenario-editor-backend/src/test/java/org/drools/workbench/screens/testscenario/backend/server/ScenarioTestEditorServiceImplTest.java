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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.testscenarios.shared.FactData;
import org.drools.workbench.models.testscenarios.shared.Fixture;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ScenarioTestEditorServiceImplTest {

    @Mock
    Scenario scenario;

    @Mock
    Path path;

    @Mock
    PackageDataModelOracle modelOracle;

    @Mock
    DataModelService dataModelService;

    @InjectMocks
    ScenarioTestEditorServiceImpl testEditorService = new ScenarioTestEditorServiceImpl();

    @Test
    public void runScenarioWithoutDependentImports() throws Exception {
        when( dataModelService.getDataModel( path ) ).thenReturn( modelOracle );
        when( scenario.getImports() ).thenReturn( new Imports() );

        testEditorService.addDependentImportsToScenario( scenario, path );

        assertEquals( 0, scenario.getImports().getImports().size() );
    }

    @Test
    public void runScenarioWithDependentImports() throws Exception {
        final ArrayList<Fixture> fixtures = new ArrayList<Fixture>() {{
            add( factData( "java.sql.ClientInfoStatus" ) );
        }};

        final Map<String, ModelField[]> modelFields = new HashMap<String, ModelField[]>() {{
            put( "java.sql.ClientInfoStatus", new ModelField[]{ modelField( "java.sql.JDBCType" ) } );
        }};

        when( scenario.getFixtures() ).thenReturn( fixtures );
        when( dataModelService.getDataModel( path ) ).thenReturn( modelOracle );
        when( modelOracle.getProjectModelFields() ).thenReturn( modelFields );
        when( scenario.getImports() ).thenReturn( new Imports() );

        testEditorService.addDependentImportsToScenario( scenario, path );

        assertEquals( 2, scenario.getImports().getImports().size() );
    }

    @Test
    public void runScenarioWithDependentImportsAndWithoutFactData() throws Exception {
        final ArrayList<Fixture> fixtures = new ArrayList<Fixture>();
        final Imports imports = new Imports() {{
            addImport( new Import( "java.sql.ClientInfoStatus" ) );
        }};

        final Map<String, ModelField[]> modelFields = new HashMap<String, ModelField[]>() {{
            put( "java.sql.ClientInfoStatus", new ModelField[]{ modelField( "java.sql.JDBCType" ) } );
        }};

        when( scenario.getFixtures() ).thenReturn( fixtures );
        when( dataModelService.getDataModel( path ) ).thenReturn( modelOracle );
        when( modelOracle.getProjectModelFields() ).thenReturn( modelFields );
        when( scenario.getImports() ).thenReturn( imports );

        testEditorService.addDependentImportsToScenario( scenario, path );

        assertEquals( 2, scenario.getImports().getImports().size() );
    }

    private FactData factData( final String type ) {
        return new FactData( type, "", true );
    }

    private ModelField modelField( final String className ) {
        return new ModelField( null, className, null, null, null, null );
    }
}

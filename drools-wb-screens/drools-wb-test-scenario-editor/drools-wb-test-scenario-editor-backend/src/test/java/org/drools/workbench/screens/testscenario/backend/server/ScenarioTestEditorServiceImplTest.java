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

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.appformer.project.datamodel.imports.Import;
import org.appformer.project.datamodel.imports.Imports;
import org.appformer.project.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.FactData;
import org.drools.workbench.models.testscenarios.shared.Fixture;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.models.testscenarios.shared.VerifyFact;
import org.guvnor.common.services.project.model.Package;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceDotFileImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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

    @Mock
    ScenarioRunnerService scenarioRunner;

    @Mock
    KieProjectService projectService;

    @Spy
    IOService ioService = new IOServiceDotFileImpl("testIoService");

    @InjectMocks
    ScenarioTestEditorServiceImpl testEditorService = new ScenarioTestEditorServiceImpl();

    @Test
    public void runScenarioWithoutDependentImports() throws Exception {
        when(dataModelService.getDataModel(path)).thenReturn(modelOracle);
        when(scenario.getImports()).thenReturn(new Imports());

        testEditorService.addDependentImportsToScenario(scenario,
                                                        path);

        assertEquals(0,
                     scenario.getImports().getImports().size());
    }

    @Test
    public void runScenarioWithDependentImports() throws Exception {
        final ArrayList<Fixture> fixtures = new ArrayList<Fixture>() {{
            add(factData("java.sql.ClientInfoStatus"));
        }};

        final Map<String, ModelField[]> modelFields = new HashMap<String, ModelField[]>() {{
            put("java.sql.ClientInfoStatus",
                new ModelField[]{modelField("java.sql.JDBCType")});
        }};

        when(scenario.getFixtures()).thenReturn(fixtures);
        when(dataModelService.getDataModel(path)).thenReturn(modelOracle);
        when(modelOracle.getProjectModelFields()).thenReturn(modelFields);
        when(scenario.getImports()).thenReturn(new Imports());

        testEditorService.addDependentImportsToScenario(scenario,
                                                        path);

        assertEquals(2,
                     scenario.getImports().getImports().size());
    }

    @Test
    public void runScenarioWithDependentImportsAndWithoutFactData() throws Exception {
        final ArrayList<Fixture> fixtures = new ArrayList<Fixture>();
        final Imports imports = new Imports() {{
            addImport(new Import("java.sql.ClientInfoStatus"));
        }};

        final Map<String, ModelField[]> modelFields = new HashMap<String, ModelField[]>() {{
            put("java.sql.ClientInfoStatus",
                new ModelField[]{modelField("java.sql.JDBCType")});
        }};

        when(scenario.getFixtures()).thenReturn(fixtures);
        when(dataModelService.getDataModel(path)).thenReturn(modelOracle);
        when(modelOracle.getProjectModelFields()).thenReturn(modelFields);
        when(scenario.getImports()).thenReturn(imports);

        testEditorService.addDependentImportsToScenario(scenario,
                                                        path);

        assertEquals(2,
                     scenario.getImports().getImports().size());
    }

    @Test
    public void checkDependentImportsWithPrimitiveTypes() throws Exception {
        final ArrayList<Fixture> fixtures = new ArrayList<>();
        final Imports imports = new Imports() {{
            addImport(new Import("int"));
        }};

        final Map<String, ModelField[]> modelFields = new HashMap<String, ModelField[]>() {{
            put("java.sql.ClientInfoStatus",
                new ModelField[]{modelField("java.sql.JDBCType")});
        }};

        when(scenario.getFixtures()).thenReturn(fixtures);
        when(dataModelService.getDataModel(path)).thenReturn(modelOracle);
        when(modelOracle.getProjectModelFields()).thenReturn(modelFields);
        when(scenario.getImports()).thenCallRealMethod();
        doCallRealMethod().when(scenario).setImports(any(Imports.class));

        scenario.setImports(imports);

        testEditorService.addDependentImportsToScenario(scenario,
                                                        path);

        assertEquals(1,
                     scenario.getImports().getImports().size());
    }

    @Test
    public void checkSingleScenarioMultipleExecution() throws Exception {
        final ArrayList<Fixture> fixtures = new ArrayList<>();
        final Imports imports = new Imports() {{
            addImport(new Import("java.sql.ClientInfoStatus"));
        }};

        final Map<String, ModelField[]> modelFields = new HashMap<String, ModelField[]>() {{
            put("java.sql.ClientInfoStatus",
                new ModelField[]{modelField("java.sql.JDBCType")});
        }};

        when(scenario.getFixtures()).thenReturn(fixtures);
        when(dataModelService.getDataModel(path)).thenReturn(modelOracle);
        when(modelOracle.getProjectModelFields()).thenReturn(modelFields);
        when(scenario.getImports()).thenCallRealMethod();
        doCallRealMethod().when(scenario).setImports(any(Imports.class));

        scenario.setImports(imports);

        testEditorService.runScenario("userName",
                                      path,
                                      scenario);

        assertEquals(1,
                     scenario.getImports().getImports().size());

        testEditorService.runScenario("userName",
                                      path,
                                      scenario);

        assertEquals(1,
                     scenario.getImports().getImports().size());
    }

    @Test
    public void loadEmptyScenario() throws
            Exception {
        final URL scenarioResource = getClass().getResource("empty.scenario");
        final Path scenarioPath = PathFactory.newPath(scenarioResource.getFile(),
                                                      scenarioResource.toURI().toString());

        final Scenario loadedScenario = testEditorService.load(scenarioPath);

        assertNotNull(loadedScenario);
    }

    @Test
    public void loadScenario() throws
            Exception {
        final URL scenarioResource = getClass().getResource("Are they old enough.scenario");
        final Path scenarioPath = PathFactory.newPath(scenarioResource.getFile(),
                                                      scenarioResource.toURI().toString());

        final Scenario loadedScenario = testEditorService.load(scenarioPath);

        assertNotNull(loadedScenario);
        assertEquals("mortgages.mortgages",
                     loadedScenario.getPackageName());
        assertEquals(5,
                     loadedScenario.getFixtures().size());
        assertTrue(loadedScenario.getFixtures().get(0) instanceof FactData);
        assertTrue(loadedScenario.getFixtures().get(1) instanceof FactData);
        assertTrue(loadedScenario.getFixtures().get(2) instanceof FactData);
        assertTrue(loadedScenario.getFixtures().get(3) instanceof ExecutionTrace);
        assertTrue(loadedScenario.getFixtures().get(4) instanceof VerifyFact);
    }

    @Test
    public void loadBrokenScenario() throws
            Exception {
        final Package pgk = mock(Package.class);
        when(pgk.getPackageName()).thenReturn("org.test");
        when(projectService.resolvePackage(path)).thenReturn(pgk);

        final Scenario load = testEditorService.load(path);

        assertNotNull(load);
        assertEquals("org.test",
                     load.getPackageName());
        assertNotNull(load.getImports());
    }

    @Test
    public void loadBrokenScenarioNullPackage() throws
            Exception {
        when(projectService.resolvePackage(path)).thenReturn(null);

        final Scenario load = testEditorService.load(path);

        assertNotNull(load);
        assertNull(load.getPackageName());
        assertNotNull(load.getImports());
    }

    @Test
    public void checkScenarioRunnerIsRan() throws Exception {
        final Imports imports = new Imports() {{
            addImport(new Import("java.sql.ClientInfoStatus"));
        }};

        when(dataModelService.getDataModel(path)).thenReturn(modelOracle);
        when(scenario.getImports()).thenCallRealMethod();
        doCallRealMethod().when(scenario).setImports(any(Imports.class));

        scenario.setImports(imports);

        testEditorService.runScenario("userName",
                                      path,
                                      scenario);

        KieProject project = mock(KieProject.class);
        when(projectService.resolveProject(path)).thenReturn(project);

        testEditorService.runScenario("userName",
                                      path,
                                      scenario);

        verify(scenarioRunner).run("userName",
                                   scenario,
                                   project);
    }

    private FactData factData(final String type) {
        return new FactData(type,
                            "",
                            true);
    }

    private ModelField modelField(final String className) {
        return new ModelField(null,
                              className,
                              null,
                              null,
                              null,
                              null);
    }
}

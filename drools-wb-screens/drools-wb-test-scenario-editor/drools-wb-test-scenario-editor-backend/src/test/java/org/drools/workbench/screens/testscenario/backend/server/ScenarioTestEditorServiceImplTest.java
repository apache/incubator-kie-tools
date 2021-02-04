/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.testscenario.backend.server;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.FactData;
import org.drools.workbench.models.testscenarios.shared.Fixture;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.models.testscenarios.shared.VerifyFact;
import org.guvnor.common.services.backend.metadata.MetadataServerSideService;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.shared.exceptions.GenericPortableException;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.imports.Imports;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.FileAlreadyExistsException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScenarioTestEditorServiceImplTest {

    public static final String EMPTY_SCENARIO_FILENAME = "empty.scenario";
    public static final String NEW_FILE_NAME = "new" + EMPTY_SCENARIO_FILENAME;
    private static final String COMMENT = "comment";

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
    KieModuleService moduleService;

    @Mock
    CommentedOptionFactory commentedOptionFactory;

    @Mock
    MetadataServerSideService metadataService;

    @Mock
    CommentedOption commentedOption;

    @Mock
    DeleteService deleteService;

    @Mock
    RenameService renameService;

    @Mock
    CopyService copyService;

    @Mock
    SaveAndRenameServiceImpl<Scenario, Metadata> saveAndRenameService;

    @Spy
    IOService ioService = new IOServiceDotFileImpl("testIoService");

    @InjectMocks
    ScenarioTestEditorServiceImpl testEditorService = new ScenarioTestEditorServiceImpl();

    @Test
    public void testCreate() throws Exception {
        final Path scenarioPath = getEmptyScenarioPath();

        doReturn(commentedOption).when(commentedOptionFactory).makeCommentedOption(COMMENT);
        doReturn(null).when(ioService).write(any(),
                                             anyString(),
                                             any());

        testEditorService.create(scenarioPath,
                                 EMPTY_SCENARIO_FILENAME,
                                 scenario,
                                 COMMENT);

        verify(ioService).write(any(),
                                anyString(),
                                eq(commentedOption));
    }

    @Test
    public void testCreateAlreadyExists() throws Exception {
        final Path scenarioPath = getEmptyScenarioPath();

        doReturn(true).when(ioService).exists(Paths.convert(scenarioPath).resolve(EMPTY_SCENARIO_FILENAME));

        try {
            testEditorService.create(scenarioPath,
                                     EMPTY_SCENARIO_FILENAME,
                                     scenario,
                                     COMMENT);
        } catch (FileAlreadyExistsException e) {
            // ok
            verify(ioService,
                   never()).write(any(),
                                  anyString(),
                                  any());
        }
    }

    @Test(expected = GenericPortableException.class)
    public void testCreateExceptionThrown() throws Exception {
        final Path scenarioPath = getEmptyScenarioPath();

        doReturn(null).when(commentedOptionFactory).makeCommentedOption(COMMENT);
        doThrow(new IllegalArgumentException("error")).when(ioService).write(any(),
                                                                             anyString(),
                                                                             any());
        testEditorService.create(scenarioPath,
                                 EMPTY_SCENARIO_FILENAME,
                                 scenario,
                                 COMMENT);
    }

    @Test
    public void testSave() throws Exception {
        final Metadata metadata = mock(Metadata.class);
        final Path scenarioPath = getEmptyScenarioPath();
        final Map<String, Object> emptyMap = Collections.emptyMap();

        doReturn(emptyMap).when(metadataService).setUpAttributes(scenarioPath,
                                                                 metadata);
        doReturn(commentedOption).when(commentedOptionFactory).makeCommentedOption(COMMENT);

        testEditorService.save(scenarioPath,
                               scenario,
                               metadata,
                               COMMENT);

        verify(ioService).write(any(org.uberfire.java.nio.file.Path.class),
                                anyString(),
                                eq(emptyMap),
                                eq(commentedOption));
    }

    @Test(expected = GenericPortableException.class)
    public void testSaveThrowException() throws Exception {
        final Metadata metadata = mock(Metadata.class);
        final Path scenarioPath = getEmptyScenarioPath();
        final Map<String, Object> emptyMap = Collections.emptyMap();

        doReturn(emptyMap).when(metadataService).setUpAttributes(scenarioPath,
                                                                 metadata);
        doReturn(commentedOption).when(commentedOptionFactory).makeCommentedOption(COMMENT);
        doThrow(new IllegalArgumentException("error")).when(ioService).write(any(org.uberfire.java.nio.file.Path.class),
                                                                             anyString(),
                                                                             eq(emptyMap),
                                                                             eq(commentedOption));

        testEditorService.save(scenarioPath,
                               scenario,
                               metadata,
                               COMMENT);
    }

    @Test
    public void testDelete() throws Exception {
        final Path scenarioPath = getEmptyScenarioPath();
        testEditorService.delete(scenarioPath,
                                 COMMENT);

        verify(deleteService).delete(scenarioPath,
                                     COMMENT);
    }

    @Test(expected = GenericPortableException.class)
    public void testDeleteThrowException() throws Exception {
        final Path scenarioPath = getEmptyScenarioPath();
        doThrow(new IllegalArgumentException("error")).when(deleteService).delete(scenarioPath,
                                                                                  COMMENT);

        testEditorService.delete(scenarioPath,
                                 COMMENT);
    }

    @Test
    public void testRename() throws Exception {
        final Path scenarioPath = getEmptyScenarioPath();
        final String newFileName = NEW_FILE_NAME;

        testEditorService.rename(scenarioPath,
                                 newFileName,
                                 COMMENT);

        verify(renameService).rename(scenarioPath,
                                     newFileName,
                                     COMMENT);
    }

    @Test(expected = GenericPortableException.class)
    public void testRenameThrowException() throws Exception {
        final Path scenarioPath = getEmptyScenarioPath();
        final String newFileName = NEW_FILE_NAME;

        doThrow(new IllegalArgumentException("error")).when(renameService).rename(scenarioPath,
                                                                                  newFileName,
                                                                                  COMMENT);
        testEditorService.rename(scenarioPath,
                                 newFileName,
                                 COMMENT);
    }

    @Test
    public void testCopy() throws Exception {
        final Path scenarioPath = getEmptyScenarioPath();
        final String newFileName = NEW_FILE_NAME;

        testEditorService.copy(scenarioPath,
                               newFileName,
                               COMMENT);

        verify(copyService).copy(scenarioPath,
                                 newFileName,
                                 COMMENT);
    }

    @Test
    public void testCopyWithTarget() throws Exception {
        final Path scenarioPath = getEmptyScenarioPath();
        final String newFileName = NEW_FILE_NAME;

        testEditorService.copy(scenarioPath,
                               newFileName,
                               path,
                               COMMENT);

        verify(copyService).copy(scenarioPath,
                                 newFileName,
                                 path,
                                 COMMENT);
    }

    @Test(expected = GenericPortableException.class)
    public void testCopyThrowException() throws Exception {
        final Path scenarioPath = getEmptyScenarioPath();
        final String newFileName = NEW_FILE_NAME;

        doThrow(new IllegalArgumentException("error")).when(copyService).copy(scenarioPath,
                                                                              newFileName,
                                                                              COMMENT);
        testEditorService.copy(scenarioPath,
                               newFileName,
                               COMMENT);
    }

    @Test(expected = GenericPortableException.class)
    public void testCopyWithTargetThrowException() throws Exception {
        final Path scenarioPath = getEmptyScenarioPath();
        final String newFileName = NEW_FILE_NAME;

        doThrow(new IllegalArgumentException("error")).when(copyService).copy(scenarioPath,
                                                                              newFileName,
                                                                              path,
                                                                              COMMENT);
        testEditorService.copy(scenarioPath,
                               newFileName,
                               path,
                               COMMENT);
    }

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
        when(modelOracle.getModuleModelFields()).thenReturn(modelFields);
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
        when(modelOracle.getModuleModelFields()).thenReturn(modelFields);
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
        when(modelOracle.getModuleModelFields()).thenReturn(modelFields);
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
        when(modelOracle.getModuleModelFields()).thenReturn(modelFields);
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
        final Path scenarioPath = getEmptyScenarioPath();

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
        when(moduleService.resolvePackage(path)).thenReturn(pgk);

        final Scenario load = testEditorService.load(path);

        assertNotNull(load);
        assertEquals("org.test",
                     load.getPackageName());
        assertNotNull(load.getImports());
    }

    @Test
    public void loadBrokenScenarioNullPackage() throws
            Exception {
        when(moduleService.resolvePackage(path)).thenReturn(null);

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

        KieModule module = mock(KieModule.class);
        when(moduleService.resolveModule(path)).thenReturn(module);

        testEditorService.runScenario("userName",
                                      path,
                                      scenario);

        verify(scenarioRunner).run("userName",
                                   scenario,
                                   module);
    }

    @Test
    public void testInit() throws Exception {
        testEditorService.init();

        verify(saveAndRenameService).init(testEditorService);
    }

    @Test
    public void testSaveAndRename() throws Exception {

        final Path path = mock(Path.class);
        final String newFileName = "newFileName";
        final Metadata metadata = mock(Metadata.class);
        final Scenario content = mock(Scenario.class);
        final String comment = "comment";

        testEditorService.saveAndRename(path, newFileName, metadata, content, comment);

        verify(saveAndRenameService).saveAndRename(path, newFileName, metadata, content, comment);
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

    private Path getEmptyScenarioPath() throws URISyntaxException {
        final URL scenarioResource = getClass().getResource(EMPTY_SCENARIO_FILENAME);
        return PathFactory.newPath(scenarioResource.getFile(),
                                   scenarioResource.toURI().toString());
    }
}

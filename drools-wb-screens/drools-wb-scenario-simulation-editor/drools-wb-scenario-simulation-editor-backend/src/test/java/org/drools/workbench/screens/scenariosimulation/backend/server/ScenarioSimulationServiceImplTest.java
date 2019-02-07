/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.backend.server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Named;

import org.assertj.core.api.Assertions;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioJunitActivator;
import org.drools.workbench.screens.scenariosimulation.backend.server.util.ScenarioSimulationBuilder;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.guvnor.common.services.backend.config.SafeSessionInfo;
import org.guvnor.common.services.backend.metadata.MetadataServerSideService;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.model.Dependencies;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.backend.service.KieServiceOverviewLoader;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;
import org.uberfire.ext.editor.commons.backend.version.PathResolver;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.OpenOption;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScenarioSimulationServiceImplTest {

    @Mock
    @Named("ioStrategy")
    private IOService ioService;

    @Mock
    private CommentedOptionFactory commentedOptionFactory;

    @Mock
    private SaveAndRenameServiceImpl<ScenarioSimulationModel, Metadata> saveAndRenameService;

    @Mock
    private PathResolver pathResolver;

    @Mock
    protected KieServiceOverviewLoader overviewLoader;

    @Mock
    protected MetadataServerSideService metadataService;

    @Mock
    private DeleteService deleteService;

    @Mock
    private RenameService renameService;

    @Mock
    private CopyService copyService;

    @Mock
    private User user;

    @Mock
    private ScenarioRunnerServiceImpl scenarioRunnerService;

    @Mock
    private POMService pomService;

    @Mock
    private org.uberfire.java.nio.file.Path activatorPath;

    @Mock
    private KieModuleService kieModuleService;

    @Mock
    private KieModule module;

    @Mock
    private POM projectPom;

    @Mock
    private GAV gav;

    @Mock
    private Dependencies dependencies;

    @Mock
    private Package mockedPackage;

    @InjectMocks
    private ScenarioSimulationServiceImpl service = new ScenarioSimulationServiceImpl(mock(SafeSessionInfo.class));

    @Mock
    private ScenarioSimulationBuilder scenarioSimulationBuilderMock;

    private Path path = PathFactory.newPath("contextpath", "file:///contextpath");

    @Before
    public void setup() throws Exception {
        Set<Package> packages = new HashSet<>();
        packages.add(new Package(path, path, path, path, path, "Test", "", ""));
        when(kieModuleService.resolveModule(any())).thenReturn(module);
        when(kieModuleService.resolvePackages(any(KieModule.class))).thenReturn(packages);
        when(ioService.exists(activatorPath)).thenReturn(false);

        when(kieModuleService.resolveModule(any())).thenReturn(module);
        when(module.getPom()).thenReturn(projectPom);
        when(projectPom.getGav()).thenReturn(gav);
        when(gav.getGroupId()).thenReturn("Test");
        when(projectPom.getDependencies()).thenReturn(dependencies);
        when(ioService.exists(any(org.uberfire.java.nio.file.Path.class))).thenReturn(false);
        when(mockedPackage.getPackageTestSrcPath()).thenReturn(path);
        when(scenarioSimulationBuilderMock.createSimulation(any(), any(), any())).thenReturn(new Simulation());
        service.scenarioSimulationBuilder = scenarioSimulationBuilderMock;
    }

    @Test
    public void init() throws Exception {
        service.init();

        verify(saveAndRenameService).init(service);
    }

    @Test
    public void delete() throws Exception {
        service.delete(path,
                       "Removing this");
        verify(deleteService).delete(path,
                                     "Removing this");
    }

    @Test
    public void rename() throws Exception {
        service.rename(path,
                       "newName.scesim",
                       "comment");
        verify(renameService).rename(path,
                                     "newName.scesim",
                                     "comment");
    }

    @Test
    public void copy() throws Exception {
        service.copy(path,
                     "newName.scesim",
                     "comment");
        verify(copyService).copy(path,
                                 "newName.scesim",
                                 "comment");
    }

    @Test
    public void copyToDirectory() throws Exception {
        final Path folder = mock(Path.class);
        service.copy(path,
                     "newName.scesim",
                     folder,
                     "comment");
        verify(copyService).copy(path,
                                 "newName.scesim",
                                 folder,
                                 "comment");
    }

    @Test
    public void saveAndRename() throws Exception {
        final Metadata metadata = mock(Metadata.class);
        final ScenarioSimulationModel model = new ScenarioSimulationModel();
        service.saveAndRename(path,
                              "newName.scesim",
                              metadata,
                              model,
                              "comment");
        verify(saveAndRenameService).saveAndRename(path,
                                                   "newName.scesim",
                                                   metadata,
                                                   model,
                                                   "comment");
    }

    @Test
    public void save() throws Exception {

        final Path returnPath = service.save(this.path,
                                             new ScenarioSimulationModel(),
                                             new Metadata(),
                                             "Commit comment");

        assertNotNull(returnPath);
        verify(ioService).write(any(org.uberfire.java.nio.file.Path.class),
                                anyString(),
                                anyMap(),
                                any(CommentedOption.class));
    }

    @Test
    public void createRULEScenario() throws Exception {
        doReturn(false).when(ioService).exists(any());
        ScenarioSimulationModel model = new ScenarioSimulationModel();
        assertNull(model.getSimulation());
        final Path returnPath = service.create(this.path,
                                               "test.scesim",
                                               model,
                                               "Commit comment",
                                               ScenarioSimulationModel.Type.RULE,
                                               "default");

        assertNotNull(returnPath);
        assertNotNull(model.getSimulation());
        verify(ioService, times(2)).write(any(org.uberfire.java.nio.file.Path.class),
                                          anyString(),
                                          any(CommentedOption.class));
    }

    @Test
    public void createDMNScenario() throws Exception {
        doReturn(false).when(ioService).exists(any());
        ScenarioSimulationModel model = new ScenarioSimulationModel();
        assertNull(model.getSimulation());
        final Path returnPath = service.create(this.path,
                                               "test.scesim",
                                               model,
                                               "Commit comment",
                                               ScenarioSimulationModel.Type.DMN,
                                               "test");

        assertNotNull(returnPath);
        assertNotNull(model.getSimulation());
        verify(ioService, times(2)).write(any(org.uberfire.java.nio.file.Path.class),
                                          anyString(),
                                          any(CommentedOption.class));
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void createFileExists() throws Exception {
        doReturn(true).when(ioService).exists(any());
        ScenarioSimulationModel model = new ScenarioSimulationModel();
        service.create(this.path,
                       "test.scesim",
                       model,
                       "Commit comment");
    }

    @Test
    public void runScenario() throws Exception {
        doReturn("test user").when(user).getIdentifier();

        final Path path = mock(Path.class);
        final ScenarioSimulationModel model = new ScenarioSimulationModel();

        service.runScenario(path, model);

        verify(scenarioRunnerService).runTest("test user",
                                              path,
                                              model);
    }

    @Test
    public void createActivatorIfNotExistTest() {
        service.createActivatorIfNotExist(path);

        verify(ioService, times(1))
                .write(any(org.uberfire.java.nio.file.Path.class),
                       anyString(),
                       any(OpenOption.class));

        when(kieModuleService.resolvePackages(any(KieModule.class))).thenReturn(Collections.emptySet());
        Assertions.assertThatThrownBy(() -> service.createActivatorIfNotExist(path))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Impossible to retrieve package information from path: file:///contextpath");
    }

    @Test
    public void ensureDependenciesTest() {
        service.ensureDependencies(module);

        verify(pomService, times(1)).save(any(Path.class),
                                          any(POM.class),
                                          any(Metadata.class),
                                          anyString());

        reset(pomService);
        when(dependencies.containsDependency(any())).thenReturn(true);

        service.ensureDependencies(module);

        verify(pomService, never()).save(any(Path.class),
                                         any(POM.class),
                                         any(Metadata.class),
                                         anyString());
    }

    @Test
    public void editPomIfNecessaryTest() {
        String groupId = "groupId";
        String artifactId = "artifactId";
        String version = "version";
        GAV gav = new GAV(groupId, artifactId, version);
        Dependencies dependencies = new Dependencies();

        assertTrue(service.editPomIfNecessary(dependencies, gav));

        assertFalse(service.editPomIfNecessary(dependencies, gav));
    }

    @Test
    public void getActivatorPathTest() {
        assertTrue(service.getActivatorPath(mockedPackage).endsWith(ScenarioJunitActivator.ACTIVATOR_CLASS_NAME + ".java"));
    }
}
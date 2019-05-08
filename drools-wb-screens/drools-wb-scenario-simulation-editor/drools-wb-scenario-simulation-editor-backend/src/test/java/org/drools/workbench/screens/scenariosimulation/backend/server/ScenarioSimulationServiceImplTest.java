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

import java.util.HashSet;
import java.util.Set;

import javax.inject.Named;

import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.backend.runner.ScenarioJunitActivator;
import org.drools.workbench.screens.scenariosimulation.backend.server.util.ScenarioSimulationBuilder;
import org.guvnor.common.services.backend.config.SafeSessionInfo;
import org.guvnor.common.services.backend.metadata.MetadataServerSideService;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.model.Dependencies;
import org.guvnor.common.services.project.model.Dependency;
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
    private IOService ioServiceMock;

    @Mock
    private CommentedOptionFactory commentedOptionFactoryMock;

    @Mock
    private SaveAndRenameServiceImpl<ScenarioSimulationModel, Metadata> saveAndRenameServiceMock;

    @Mock
    private PathResolver pathResolverMock;

    @Mock
    protected KieServiceOverviewLoader overviewLoaderMock;

    @Mock
    protected MetadataServerSideService metadataServiceMock;

    @Mock
    private DeleteService deleteServiceMock;

    @Mock
    private RenameService renameServiceMock;

    @Mock
    private CopyService copyServiceMock;

    @Mock
    private User userMock;

    @Mock
    private ScenarioRunnerServiceImpl scenarioRunnerServiceMock;

    @Mock
    private POMService pomServiceMock;

    @Mock
    private org.uberfire.java.nio.file.Path activatorPathMock;

    @Mock
    private KieModuleService kieModuleServiceMock;

    @Mock
    private KieModule kieModuleMock;

    @Mock
    private POM projectPomMock;

    @Mock
    private GAV gavMock;

    @Mock
    private Dependencies dependenciesMock;

    @Mock
    private Package packageMock;

    @Mock
    private ScenarioSimulationBuilder scenarioSimulationBuilderMock;

    @InjectMocks
    private ScenarioSimulationServiceImpl service = new ScenarioSimulationServiceImpl(mock(SafeSessionInfo.class));

    private Path path = PathFactory.newPath("contextPath", "file:///contextPath");

    @Before
    public void setup() throws Exception {
        Set<Package> testPackages = new HashSet<>();
        Package testPackage = new Package(path, path, path, path, path, "Test", "", "");
        testPackages.add(testPackage);
        when(kieModuleServiceMock.resolveModule(any())).thenReturn(kieModuleMock);
        when(kieModuleServiceMock.resolvePackages(any(KieModule.class))).thenReturn(testPackages);
        when(kieModuleServiceMock.newPackage(any(), anyString())).thenReturn(testPackage);
        when(kieModuleServiceMock.resolveDefaultPackage(any())).thenReturn(testPackage);
        when(ioServiceMock.exists(activatorPathMock)).thenReturn(false);

        when(kieModuleServiceMock.resolveModule(any())).thenReturn(kieModuleMock);
        when(kieModuleMock.getPom()).thenReturn(projectPomMock);
        when(projectPomMock.getGav()).thenReturn(gavMock);
        when(gavMock.getGroupId()).thenReturn("Test");
        when(projectPomMock.getDependencies()).thenReturn(dependenciesMock);
        when(dependenciesMock.iterator()).thenReturn(new Dependencies().iterator());
        when(ioServiceMock.exists(any(org.uberfire.java.nio.file.Path.class))).thenReturn(false);
        when(packageMock.getPackageTestSrcPath()).thenReturn(path);
        when(scenarioSimulationBuilderMock.createSimulation(any(), any(), any())).thenReturn(new Simulation());
        service.scenarioSimulationBuilder = scenarioSimulationBuilderMock;
    }

    @Test
    public void init() throws Exception {
        service.init();

        verify(saveAndRenameServiceMock).init(service);
    }

    @Test
    public void delete() throws Exception {
        service.delete(path,
                       "Removing this");
        verify(deleteServiceMock).delete(path,
                                         "Removing this");
    }

    @Test
    public void rename() throws Exception {
        service.rename(path,
                       "newName.scesim",
                       "comment");
        verify(renameServiceMock).rename(path,
                                         "newName.scesim",
                                         "comment");
    }

    @Test
    public void copy() throws Exception {
        service.copy(path,
                     "newName.scesim",
                     "comment");
        verify(copyServiceMock).copy(path,
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
        verify(copyServiceMock).copy(path,
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
        verify(saveAndRenameServiceMock).saveAndRename(path,
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
        verify(ioServiceMock).write(any(org.uberfire.java.nio.file.Path.class),
                                    anyString(),
                                    anyMap(),
                                    any(CommentedOption.class));
    }

    @Test
    public void createRULEScenario() throws Exception {
        doReturn(false).when(ioServiceMock).exists(any());
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
        verify(ioServiceMock, times(2)).write(any(org.uberfire.java.nio.file.Path.class),
                                              anyString(),
                                              any(CommentedOption.class));
    }

    @Test
    public void createDMNScenario() throws Exception {
        doReturn(false).when(ioServiceMock).exists(any());
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
        verify(ioServiceMock, times(2)).write(any(org.uberfire.java.nio.file.Path.class),
                                              anyString(),
                                              any(CommentedOption.class));
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void createFileExists() throws Exception {
        doReturn(true).when(ioServiceMock).exists(any());
        ScenarioSimulationModel model = new ScenarioSimulationModel();
        service.create(this.path,
                       "test.scesim",
                       model,
                       "Commit comment");
    }

    @Test
    public void runScenario() throws Exception {
        doReturn("test userMock").when(userMock).getIdentifier();

        final Path path = mock(Path.class);
        Simulation simulation = new Simulation();

        service.runScenario(path, simulation.getSimulationDescriptor(), simulation.getScenarioWithIndex());

        verify(scenarioRunnerServiceMock).runTest("test userMock",
                                                  path,
                                                  simulation.getSimulationDescriptor(),
                                                  simulation.getScenarioWithIndex());
    }

    @Test
    public void createActivatorIfNotExistTest() {
        service.createActivatorIfNotExist(path);

        verify(ioServiceMock, times(1))
                .write(any(org.uberfire.java.nio.file.Path.class),
                       anyString(),
                       any(OpenOption.class));

        reset(ioServiceMock);
        when(ioServiceMock.exists(any())).thenReturn(true);
        service.createActivatorIfNotExist(path);

        verify(ioServiceMock, never())
                .write(any(org.uberfire.java.nio.file.Path.class),
                       anyString(),
                       any(OpenOption.class));
    }

    @Test
    public void getOrCreateJunitActivatorPackageTest() {
        service.getOrCreateJunitActivatorPackage(kieModuleMock);
        verify(kieModuleServiceMock, times(1)).newPackage(any(), anyString());

        reset(kieModuleServiceMock);
        when(kieModuleServiceMock.resolveDefaultPackage(any())).thenReturn(packageMock);
        when(kieModuleServiceMock.resolvePackage(any())).thenReturn(packageMock);
        service.getOrCreateJunitActivatorPackage(kieModuleMock);
        verify(kieModuleServiceMock, never()).newPackage(any(), anyString());
    }

    @Test
    public void removeOldActivatorIfExistsTest() {
        service.removeOldActivatorIfExists(kieModuleMock);
        verify(ioServiceMock, times(1)).deleteIfExists(any());

        reset(ioServiceMock);
        when(kieModuleServiceMock.resolvePackages(any(KieModule.class))).thenReturn(new HashSet<>());
        service.removeOldActivatorIfExists(kieModuleMock);
        verify(ioServiceMock, never()).deleteIfExists(any());
    }

    @Test
    public void ensureDependenciesTest() {
        service.ensureDependencies(kieModuleMock);

        verify(pomServiceMock, times(1)).save(any(Path.class),
                                              any(POM.class),
                                              any(Metadata.class),
                                              anyString());

        reset(pomServiceMock);
        when(dependenciesMock.containsDependency(any())).thenReturn(true);

        service.ensureDependencies(kieModuleMock);

        verify(pomServiceMock, never()).save(any(Path.class),
                                             any(POM.class),
                                             any(Metadata.class),
                                             anyString());
    }

    @Test
    public void removeFromPomIfNecessaryTest() {
        String groupId = "groupId";
        String artifactId = "artifactId";
        String version = "version";
        GAV toRemove = new GAV(groupId, artifactId, version);
        Dependencies dependencies = new Dependencies();
        dependencies.add(new Dependency(toRemove));

        assertTrue(service.removeFromPomIfNecessary(dependencies, toRemove));

        assertFalse(service.removeFromPomIfNecessary(dependencies, toRemove));

        dependencies.add(new Dependency(new GAV(groupId, artifactId, null)));

        assertTrue(service.removeFromPomIfNecessary(dependencies, toRemove));

        assertFalse(service.removeFromPomIfNecessary(dependencies, toRemove));
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
        assertTrue(service.getActivatorPath(packageMock).endsWith(ScenarioJunitActivator.ACTIVATOR_CLASS_NAME + ".java"));
    }
}
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

package org.guvnor.asset.management.backend.service;

import java.util.ArrayList;
import java.util.HashMap;
import javax.inject.Inject;

import org.guvnor.asset.management.model.RepositoryStructureModel;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.structure.repositories.EnvironmentParameters;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.test.TestTempFileSystem;
import org.guvnor.test.WeldJUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(WeldJUnitRunner.class)
public class RepositoryStructureModelLoaderTest {

    @InjectMocks
    RepositoryStructureModelLoader loader;

    @Mock
    private POMService pomService;

    @Mock
    private ProjectService<? extends Project> projectService;

    @Mock
    private ManagedStatusUpdater managedStatusUpdater;

    @Mock
    private MetadataService metadataService;

    @Inject
    private TestTempFileSystem testFileSystem;

    private Path myProjectMasterBranchRoot;
    private Path myProjectPom;
    private Repository repository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        repository = mock(Repository.class);
        when(repository.getEnvironment()).thenReturn(null);
        myProjectMasterBranchRoot = testFileSystem.createTempDirectory("/myproject");

        myProjectPom = testFileSystem.createTempFile("myproject/pom.xml");
        when(repository.getBranchRoot("master")).thenReturn(myProjectMasterBranchRoot);
    }

    @After
    public void tearDown() throws Exception {
        testFileSystem.tearDown();
    }

    @Test
    public void testNullRepo() throws Exception {
        final RepositoryStructureModel structureModel = loader.load(null,
                                                                    "master",
                                                                    true);

        assertNull(structureModel);
    }

    @Test
    public void testLoadProjectInRoot() throws Exception {

        final POM pom = new POM();
        final Metadata metadata = new Metadata();
        addMyProjectToRepositoryRoot(pom,
                                     metadata);

        makeManaged(repository);

        final RepositoryStructureModel model = loader.load(repository,
                                                           "master",
                                                           false);

        assertTrue(model.isManaged());
        assertEquals(pom,
                     model.getPOM());
        assertEquals(metadata,
                     model.getPOMMetaData());
        assertEquals(myProjectPom,
                     model.getPathToPOM());
        assertTrue(model.getModulesProject().isEmpty());
    }

    @Test
    public void testNoModules() throws Exception {

        addMyProjectToRepositoryRoot(new POM(),
                                     new Metadata());

        final RepositoryStructureModel model = loader.load(repository,
                                                           "master",
                                                           true);

        assertTrue(model.getModulesProject().isEmpty());
    }

    @Test
    public void testModulesExist() throws Exception {

        addMyProjectToRepositoryRoot(new POM(),
                                     new Metadata(),
                                     "module1",
                                     "module2");

        final RepositoryStructureModel model = loader.load(repository,
                                                           "master",
                                                           true);

        assertEquals(2,
                     model.getModulesProject().size());
    }

    @Test
    public void testLoadManaged() throws Exception {

        makeManaged(repository);

        addMyProjectToRepositoryRoot(new POM(),
                                     new Metadata());

        loader.load(repository,
                    "master",
                    false);

        verify(managedStatusUpdater,
               never()).updateManagedStatus(repository,
                                            true);
    }

    @Test
    public void testUpdateToManaged() throws Exception {

        addMyProjectToRepositoryRoot(new POM(),
                                     new Metadata());

        loader.load(repository,
                    "master",
                    false);

        verify(managedStatusUpdater).updateManagedStatus(repository,
                                                         true);
    }

    @Test
    public void testLoadRepositoryStructureModelWithNoEnvironmentEntries() throws Exception {
        final RepositoryStructureModel model = loader.load(repository,
                                                           "master",
                                                           false);

        assertNull(model);
    }

    @Test
    public void testLoadRepositoryStructureModelWithRepositoryManagedStatusNotSet() throws Exception {
        final HashMap<String, Object> map = new HashMap<>();
        when(repository.getEnvironment()).thenReturn(map);

        final RepositoryStructureModel model = loader.load(repository,
                                                           "master",
                                                           false);

        assertNull(model.getPOM());
        assertNull(model.getPOMMetaData());
        assertNull(model.getPathToPOM());
        assertTrue(model.getModules().isEmpty());
        assertTrue(model.getModulesProject().isEmpty());
        assertTrue(model.getOrphanProjects().isEmpty());
        assertTrue(model.getOrphanProjectsPOM().isEmpty());
        assertFalse(model.isManaged());
    }

    private void addMyProjectToRepositoryRoot(final POM pom,
                                              final Metadata metadata,
                                              final String... moduleNames) {
        final ArrayList<String> modules = new ArrayList<>();
        for (final String moduleName : moduleNames) {
            modules.add(moduleName);
        }

        final Project project = new Project(myProjectMasterBranchRoot,
                                            myProjectPom,
                                            "myproject",
                                            modules);
        when(projectService.resolveToParentProject(myProjectMasterBranchRoot)).thenReturn(project);

        when(pomService.load(myProjectPom)).thenReturn(pom);
        when(metadataService.getMetadata(myProjectPom)).thenReturn(metadata);
    }

    private void makeManaged(final Repository repository) {
        final HashMap<String, Object> map = new HashMap<>();
        map.put(EnvironmentParameters.MANAGED,
                Boolean.TRUE);
        when(repository.getEnvironment()).thenReturn(map);
    }
}
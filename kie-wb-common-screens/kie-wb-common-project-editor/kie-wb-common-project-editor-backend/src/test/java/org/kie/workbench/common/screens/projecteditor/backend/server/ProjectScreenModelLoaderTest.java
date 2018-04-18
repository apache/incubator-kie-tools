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

package org.kie.workbench.common.screens.projecteditor.backend.server;

import javax.inject.Inject;

import org.guvnor.common.services.project.model.ModuleRepositories;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.ProjectImports;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.ModuleRepositoriesService;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.structure.repositories.impl.DefaultPublicURI;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.guvnor.test.TestTempFileSystem;
import org.guvnor.test.WeldJUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.services.shared.kmodule.KModuleModel;
import org.kie.workbench.common.services.shared.kmodule.KModuleService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.kie.workbench.common.services.shared.whitelist.PackageNameWhiteListService;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.uberfire.backend.vfs.Path;
import org.uberfire.spaces.Space;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(WeldJUnitRunner.class)
public class ProjectScreenModelLoaderTest {

    final String moduleName = "my project";

    private Path pathToPom;

    @Mock
    private KieModuleService moduleService;

    @Mock
    private Path rootPath;

    private Path kmoduleXMLPath;
    private Path importsPath;
    private Path repositoriesPath;
    private Path packageNamesWhiteListPath;

    @Mock
    private POMService pomService;

    @Mock
    private MetadataService metadataService;

    @Mock
    private KModuleService kModuleService;

    @Mock
    private ProjectImportsService projectImportsService;

    @Mock
    private ModuleRepositoriesService moduleRepositoriesService;

    @Mock
    private PackageNameWhiteListService whiteListService;

    @Mock
    private WorkspaceProjectService workspaceProjectService;

    private ProjectScreenModelLoader loader;
    private KieModule kieModule;
    private WorkspaceProject workspaceProject;

    @Inject
    private TestTempFileSystem testFileSystem;

    @BeforeClass
    public static void setupSystemProperties() {
        //These are not needed for the tests
        System.setProperty("org.uberfire.nio.git.daemon.enabled",
                           "false");
        System.setProperty("org.uberfire.nio.git.ssh.enabled",
                           "false");
        System.setProperty("org.uberfire.sys.repo.monitor.disabled",
                           "true");
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        pathToPom = testFileSystem.createTempFile("myModule/pom.xml");
        kmoduleXMLPath = testFileSystem.createTempFile("mymodule/src/main/resources/META-INF/kmodule.xml");
        importsPath = testFileSystem.createTempFile("myModule/project.imports");
        repositoriesPath = testFileSystem.createTempFile("myModule/project.repositories");
        packageNamesWhiteListPath = testFileSystem.createTempFile("myModule/package-name-white-list");

        makeKieModule();
        makeWorkspaceProject();

        when(workspaceProjectService.resolveProject(pathToPom)).thenReturn(workspaceProject);
        when(moduleService.resolveModule(pathToPom)).thenReturn(kieModule);

        loader = new ProjectScreenModelLoader(moduleService,
                                              pomService,
                                              metadataService,
                                              kModuleService,
                                              projectImportsService,
                                              moduleRepositoriesService,
                                              whiteListService,
                                              workspaceProjectService);
    }

    @After
    public void tearDown() throws Exception {
        testFileSystem.tearDown();
    }

    private void makeKieModule() {
        final POM pom = mock(POM.class);
        when(pom.getName()).thenReturn(moduleName);

        kieModule = new KieModule(rootPath,
                                  pathToPom,
                                  kmoduleXMLPath,
                                  importsPath,
                                  repositoriesPath,
                                  packageNamesWhiteListPath,
                                  pom);
    }

    private void makeWorkspaceProject() {
        final DefaultPublicURI uri = new DefaultPublicURI("git",
                                                          "git://uri:9999/space/project");

        final GitRepository repository = new GitRepository("alias",
                                                           mock(Space.class),
                                                           singletonList(uri));

        workspaceProject = spy(new WorkspaceProject());

        when(workspaceProject.getRepository()).thenReturn(repository);
    }

    @Test
    public void testLoadPom() throws Exception {
        final POM pom = new POM();
        when(pomService.load(pathToPom)).thenReturn(pom);
        final Metadata metadata = new Metadata();
        when(metadataService.getMetadata(pathToPom)).thenReturn(metadata);

        ProjectScreenModel model = loader.load(pathToPom);

        assertEquals(pathToPom,
                     model.getPathToPOM());
        assertEquals(pom,
                     model.getPOM());
        assertEquals(metadata,
                     model.getPOMMetaData());
    }

    @Test
    public void testKModule() throws Exception {
        final KModuleModel kModuleModel = new KModuleModel();
        when(kModuleService.load(kmoduleXMLPath)).thenReturn(kModuleModel);
        final Metadata metadata = new Metadata();
        when(metadataService.getMetadata(kmoduleXMLPath)).thenReturn(metadata);

        ProjectScreenModel model = loader.load(pathToPom);

        assertEquals(kmoduleXMLPath,
                     model.getPathToKModule());
        assertEquals(kModuleModel,
                     model.getKModule());
        assertEquals(metadata,
                     model.getKModuleMetaData());
    }

    @Test
    public void testImports() throws Exception {
        final ProjectImports projectImports = new ProjectImports();
        when(projectImportsService.load(importsPath)).thenReturn(projectImports);
        final Metadata metadata = new Metadata();
        when(metadataService.getMetadata(importsPath)).thenReturn(metadata);

        ProjectScreenModel model = loader.load(pathToPom);

        assertEquals(importsPath,
                     model.getPathToImports());
        assertEquals(projectImports,
                     model.getProjectImports());
        assertEquals(metadata,
                     model.getProjectImportsMetaData());
    }

    @Test
    public void testRepositories() throws Exception {
        final ModuleRepositories moduleRepositories = new ModuleRepositories();
        when(moduleRepositoriesService.load(repositoriesPath)).thenReturn(moduleRepositories);

        ProjectScreenModel model = loader.load(pathToPom);

        assertEquals(repositoriesPath,
                     model.getPathToRepositories());
        assertEquals(moduleRepositories,
                     model.getRepositories());
    }

    @Test
    public void testWhiteList() throws Exception {
        final WhiteList whiteList = new WhiteList();
        when(whiteListService.load(packageNamesWhiteListPath)).thenReturn(whiteList);
        final Metadata metadata = new Metadata();
        when(metadataService.getMetadata(packageNamesWhiteListPath)).thenReturn(metadata);

        ProjectScreenModel model = loader.load(pathToPom);

        assertEquals(packageNamesWhiteListPath,
                     model.getPathToWhiteList());
        assertEquals(whiteList,
                     model.getWhiteList());
        assertEquals(metadata,
                     model.getWhiteListMetaData());
    }

    @Test
    public void testWhiteListNoMetadata() throws Exception {
        final WhiteList whiteList = new WhiteList();

        testFileSystem.deleteFile(packageNamesWhiteListPath);

        when(whiteListService.load(packageNamesWhiteListPath)).thenReturn(whiteList);

        ProjectScreenModel model = loader.load(pathToPom);

        assertEquals(packageNamesWhiteListPath,
                     model.getPathToWhiteList());
        assertEquals(whiteList,
                     model.getWhiteList());
        assertNotNull(model.getWhiteListMetaData());
    }

    @Test
    public void testGitUrls() throws Exception {

        final ProjectScreenModel model = loader.load(pathToPom);

        assertEquals(1,
                     model.getGitUrls().size());
        assertEquals("git",
                     model.getGitUrls().get(0).getProtocol());
        assertEquals("git://uri:9999/space/project",
                     model.getGitUrls().get(0).getUrl());
    }
}
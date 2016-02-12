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

import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.ProjectImports;
import org.guvnor.common.services.project.model.ProjectRepositories;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectRepositoriesService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.test.TestFileSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.services.shared.kmodule.KModuleModel;
import org.kie.workbench.common.services.shared.kmodule.KModuleService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.kie.workbench.common.services.shared.whitelist.PackageNameWhiteListService;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectScreenModelLoaderTest {

    final String projectName = "my project";

    private Path pathToPom;

    @Mock
    private KieProjectService projectService;

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
    private ProjectRepositoriesService projectRepositoriesService;

    @Mock
    private PackageNameWhiteListService whiteListService;

    private ProjectScreenModelLoader loader;
    private KieProject kieProject;
    private TestFileSystem testFileSystem;

    @BeforeClass
    public static void setupSystemProperties() {
        //These are not needed for the tests
        System.setProperty( "org.uberfire.nio.git.daemon.enabled",
                            "false" );
        System.setProperty( "org.uberfire.nio.git.ssh.enabled",
                            "false" );
        System.setProperty( "org.uberfire.sys.repo.monitor.disabled",
                            "true" );
    }

    @Before
    public void setUp() throws Exception {

        testFileSystem = new TestFileSystem();

        pathToPom = testFileSystem.createTempFile( "myProject/pom.xml" );
        kmoduleXMLPath = testFileSystem.createTempFile( "myproject/src/main/resources/META-INF/kmodule.xml" );
        importsPath = testFileSystem.createTempFile( "myProject/project.imports" );
        repositoriesPath = testFileSystem.createTempFile( "myProject/project.repositories" );
        packageNamesWhiteListPath = testFileSystem.createTempFile( "myProject/package-name-white-list" );

        makeKieProject();

        when( projectService.resolveProject( pathToPom ) ).thenReturn( kieProject );

        loader = new ProjectScreenModelLoader( projectService,
                                               pomService,
                                               metadataService,
                                               kModuleService,
                                               projectImportsService,
                                               projectRepositoriesService,
                                               whiteListService );
    }

    @After
    public void tearDown() throws Exception {
        testFileSystem.tearDown();
    }

    private void makeKieProject() {
        kieProject = new KieProject( rootPath,
                                     pathToPom,
                                     kmoduleXMLPath,
                                     importsPath,
                                     repositoriesPath,
                                     packageNamesWhiteListPath,
                                     projectName );
    }

    @Test
    public void testLoadPom() throws Exception {
        final POM pom = new POM();
        when( pomService.load( pathToPom ) ).thenReturn( pom );
        final Metadata metadata = new Metadata();
        when( metadataService.getMetadata( pathToPom ) ).thenReturn( metadata );

        ProjectScreenModel model = loader.load( pathToPom );

        assertEquals( pathToPom,
                      model.getPathToPOM() );
        assertEquals( pom,
                      model.getPOM() );
        assertEquals( metadata,
                      model.getPOMMetaData() );
    }

    @Test
    public void testKModule() throws Exception {
        final KModuleModel kModuleModel = new KModuleModel();
        when( kModuleService.load( kmoduleXMLPath ) ).thenReturn( kModuleModel );
        final Metadata metadata = new Metadata();
        when( metadataService.getMetadata( kmoduleXMLPath ) ).thenReturn( metadata );

        ProjectScreenModel model = loader.load( pathToPom );

        assertEquals( kmoduleXMLPath,
                      model.getPathToKModule() );
        assertEquals( kModuleModel,
                      model.getKModule() );
        assertEquals( metadata,
                      model.getKModuleMetaData() );
    }

    @Test
    public void testImports() throws Exception {
        final ProjectImports projectImports = new ProjectImports();
        when( projectImportsService.load( importsPath ) ).thenReturn( projectImports );
        final Metadata metadata = new Metadata();
        when( metadataService.getMetadata( importsPath ) ).thenReturn( metadata );

        ProjectScreenModel model = loader.load( pathToPom );

        assertEquals( importsPath,
                      model.getPathToImports() );
        assertEquals( projectImports,
                      model.getProjectImports() );
        assertEquals( metadata,
                      model.getProjectImportsMetaData() );
    }

    @Test
    public void testRepositories() throws Exception {
        final ProjectRepositories projectRepositories = new ProjectRepositories();
        when( projectRepositoriesService.load( repositoriesPath ) ).thenReturn( projectRepositories );

        ProjectScreenModel model = loader.load( pathToPom );

        assertEquals( repositoriesPath,
                      model.getPathToRepositories() );
        assertEquals( projectRepositories,
                      model.getRepositories() );
    }

    @Test
    public void testWhiteList() throws Exception {
        final WhiteList whiteList = new WhiteList();
        when( whiteListService.load( packageNamesWhiteListPath ) ).thenReturn( whiteList );
        final Metadata metadata = new Metadata();
        when( metadataService.getMetadata( packageNamesWhiteListPath ) ).thenReturn( metadata );

        ProjectScreenModel model = loader.load( pathToPom );

        assertEquals( packageNamesWhiteListPath,
                      model.getPathToWhiteList() );
        assertEquals( whiteList,
                      model.getWhiteList() );
        assertEquals( metadata,
                      model.getWhiteListMetaData() );
    }

    @Test
    public void testWhiteListNoMetadata() throws Exception {
        final WhiteList whiteList = new WhiteList();

        testFileSystem.deleteFile( packageNamesWhiteListPath );

        when( whiteListService.load( packageNamesWhiteListPath ) ).thenReturn( whiteList );

        ProjectScreenModel model = loader.load( pathToPom );

        assertEquals( packageNamesWhiteListPath,
                      model.getPathToWhiteList() );
        assertEquals( whiteList,
                      model.getWhiteList() );
        assertNotNull( model.getWhiteListMetaData() );
    }

}
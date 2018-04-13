/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.service;

import java.net.URISyntaxException;

import org.guvnor.common.services.backend.metadata.MetadataServerSideService;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.version.PathResolver;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KieServiceTest {

    private SimpleFileSystemProvider fileSystemProvider;

    private KieService<TestModel> kieService;

    @Mock
    private PathResolver pathResolver;

    @Mock
    private MetadataServerSideService metadataService;

    @Mock
    private KieModuleService moduleService;

    @Mock
    private WorkspaceProjectService projectService;

    private org.uberfire.java.nio.file.Path mainFilePath;
    private org.uberfire.java.nio.file.Path dotFilePath;
    private org.uberfire.java.nio.file.Path orphanDotFilePath;

    @Before
    public void setUp() throws Exception {

        fileSystemProvider = new SimpleFileSystemProvider();

        //Ensure URLs use the default:// scheme
        fileSystemProvider.forceAsDefault();

        mainFilePath = fileSystemProvider.getPath(this.getClass().getResource("mymodel.model").toURI());
        dotFilePath = fileSystemProvider.getPath(this.getClass().getResource(".mymodel.model").toURI());
        orphanDotFilePath = fileSystemProvider.getPath(this.getClass().getResource(".mymodel").toURI());

        kieService = spy(new KieService<TestModel>() {

            {
                IOService mockIOService = mock(IOService.class);
                when(mockIOService.exists(mainFilePath)).thenReturn(true);
                when(mockIOService.exists(dotFilePath)).thenReturn(false);

                this.logger = mock(Logger.class);
                this.pathResolver = new PathResolverMock();
                this.ioService = mockIOService;
                this.metadataService = KieServiceTest.this.metadataService;
                this.moduleService = KieServiceTest.this.moduleService;
                this.projectService = KieServiceTest.this.projectService;
                this.overviewLoader = new KieServiceOverviewLoader(metadataService,
                                                                   moduleService,
                                                                   projectService);
            }

            @Override
            protected TestModel constructContent(Path path,
                                                 Overview overview) {
                if (path.getFileName().toString().equals(mainFilePath.getFileName().toString())) {
                    return new TestModel(overview);
                } else if (path.getFileName().toString().equals(orphanDotFilePath.getFileName().toString())) {
                    return new TestModel(overview);
                } else {
                    return null;
                }
            }
        });
    }

    @Test
    public void testBasic() throws Exception {
        TestModel testModel = kieService.loadContent(Paths.convert(mainFilePath));

        assertNotNull(testModel);
        assertMetadataRequestedForMainFile();
    }

    @Test
    public void testProjectName() throws Exception {
        final KieModule module = mock(KieModule.class);
        doReturn(module).when(moduleService).resolveModule(any(Path.class));

        final WorkspaceProject project = mock(WorkspaceProject.class);
        doReturn("test name").when(project).getName();
        doReturn(project).when(projectService).resolveProject(any(Path.class));

        final TestModel testModel = kieService.loadContent(Paths.convert(mainFilePath));

        assertEquals("test name", testModel.overview.getProjectName());
    }

    @Test
    public void testPathPointsToDotFile() throws Exception {
        TestModel testModel = kieService.loadContent(Paths.convert(dotFilePath));

        assertNotNull(testModel);
        assertMetadataRequestedForMainFile();
    }

    @Test
    public void testPathPointsToOrphanDotFile() throws Exception {
        TestModel testModel = kieService.loadContent(Paths.convert(orphanDotFilePath));

        assertNotNull(testModel);
        assertMetadataRequestedForOrphanFile();
    }

    private void assertMetadataRequestedForMainFile() {
        ArgumentCaptor<Path> pathArgumentCaptor = ArgumentCaptor.forClass(Path.class);
        verify(metadataService).getMetadata(pathArgumentCaptor.capture());
        assertEquals(mainFilePath.getFileName().toString(),
                     pathArgumentCaptor.getValue().getFileName());
    }

    private void assertMetadataRequestedForOrphanFile() {
        ArgumentCaptor<Path> pathArgumentCaptor = ArgumentCaptor.forClass(Path.class);
        verify(metadataService).getMetadata(pathArgumentCaptor.capture());
        assertEquals(orphanDotFilePath.getFileName().toString(),
                     pathArgumentCaptor.getValue().getFileName());
    }

    private class TestModel {

        private Overview overview;

        public TestModel(Overview overview) {
            this.overview = overview;
        }
    }

    private class PathResolverMock
            implements PathResolver {

        @Override
        public boolean isDotFile(org.uberfire.java.nio.file.Path path) {
            return path.getFileName().toString().startsWith(".");
        }

        @Override
        public org.uberfire.java.nio.file.Path resolveMainFilePath(org.uberfire.java.nio.file.Path path) throws URISyntaxException {
            if (path.getFileName().toString().equals(dotFilePath.getFileName().toString())) {
                return mainFilePath;
            } else {
                return null;
            }
        }
    }
}
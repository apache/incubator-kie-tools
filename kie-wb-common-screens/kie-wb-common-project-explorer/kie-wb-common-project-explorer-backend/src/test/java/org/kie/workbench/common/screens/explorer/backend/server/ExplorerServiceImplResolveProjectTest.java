/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.explorer.backend.server;

import java.net.URI;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOService;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class ExplorerServiceImplResolveProjectTest {

    @Mock
    IOService ioService;

    @Mock
    WorkspaceProjectService projectService;

    @InjectMocks
    ExplorerServiceImpl explorerService;

    @Test
    public void resolveProject() throws Exception {
        final WorkspaceProject project = mock(WorkspaceProject.class);

        doReturn(Paths.convert(PathFactory.newPath("testFile",
                                                   "file:///moduleName"))).when(ioService).get(any(URI.class));

        doReturn(project).when(projectService).resolveProject(any(Path.class));

        assertEquals(project, explorerService.resolveProject("path"));
    }
}
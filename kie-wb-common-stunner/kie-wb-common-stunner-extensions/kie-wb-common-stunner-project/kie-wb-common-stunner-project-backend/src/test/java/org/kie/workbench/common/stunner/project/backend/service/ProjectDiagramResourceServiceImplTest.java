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

package org.kie.workbench.common.stunner.project.backend.service;

import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.project.diagram.editor.ProjectDiagramResource;
import org.kie.workbench.common.stunner.project.diagram.editor.impl.ProjectDiagramResourceImpl;
import org.kie.workbench.common.stunner.project.diagram.impl.ProjectDiagramImpl;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;
import org.uberfire.ext.editor.commons.service.RenameService;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectDiagramResourceServiceImplTest {

    @Mock
    private ProjectDiagramService projectDiagramService;

    @Mock
    private RenameService renameService;

    @Mock
    private SaveAndRenameServiceImpl<ProjectDiagramResource, Metadata> saveAndRenameService;

    private ProjectDiagramResourceServiceImpl service;

    @Before
    public void setup() {
        service = new ProjectDiagramResourceServiceImpl(projectDiagramService, renameService, saveAndRenameService);
    }

    @Test
    public void testInit() {

        service.init();

        verify(saveAndRenameService).init(service);
    }

    @Test
    public void testSaveWhenResourceIsProjectDiagram() {

        final Path path = mock(Path.class);
        final Metadata metadata = mock(Metadata.class);
        final ProjectDiagramImpl projectDiagram = mock(ProjectDiagramImpl.class);
        final Path expectedPath = mock(Path.class);
        final ProjectDiagramResourceImpl resource = new ProjectDiagramResourceImpl(projectDiagram);
        final String comment = "comment";

        when(projectDiagramService.save(path, projectDiagram, metadata, comment)).thenReturn(expectedPath);

        final Path actualPath = service.save(path, resource, metadata, comment);

        assertSame(expectedPath, actualPath);
    }

    @Test
    public void testSaveWhenResourceIsXML() {

        final Path path = mock(Path.class);
        final Metadata metadata = mock(Metadata.class);
        final Path expectedPath = mock(Path.class);
        final String diagramXml = "<xml>";
        final ProjectDiagramResourceImpl resource = new ProjectDiagramResourceImpl(diagramXml);
        final String comment = "comment";

        when(projectDiagramService.saveAsXml(path, diagramXml, metadata, comment)).thenReturn(expectedPath);

        final Path actualPath = service.save(path, resource, metadata, comment);

        assertSame(expectedPath, actualPath);
    }

    @Test
    public void testRename() {

        final Path path = mock(Path.class);
        final String newName = "newName";
        final String comment = "comment";

        service.rename(path, newName, comment);

        verify(renameService).rename(path, newName, comment);
    }

    @Test
    public void testSaveAndRename() {

        final Path path = mock(Path.class);
        final Metadata metadata = mock(Metadata.class);
        final ProjectDiagramResourceImpl resource = mock(ProjectDiagramResourceImpl.class);
        final String newName = "newName";
        final String comment = "comment";

        service.saveAndRename(path, newName, metadata, resource, comment);

        verify(saveAndRenameService).saveAndRename(path, newName, metadata, resource, comment);
    }
}

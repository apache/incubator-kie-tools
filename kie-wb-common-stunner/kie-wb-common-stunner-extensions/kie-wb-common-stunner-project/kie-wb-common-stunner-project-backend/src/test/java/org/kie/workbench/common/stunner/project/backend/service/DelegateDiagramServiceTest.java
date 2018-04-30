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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DelegateDiagramServiceTest {

    public static final String NAME = "diagram";
    public static final String DEF_ID = "defId";
    public static final String CONTENT = "content";
    private DelegateDiagramService delegateDiagramService;

    @Mock
    private ProjectDiagramService projectDiagramService;

    @Mock
    private Path path;

    @Mock
    private Diagram diagram;

    @Mock
    private ProjectMetadata projectMetadata;

    @Mock
    private Graph graph;

    @Captor
    private ArgumentCaptor<ProjectDiagram> projectDiagramArgumentCaptor;

    @Mock
    private ProjectDiagram projectDiagram;

    @Before
    public void setUp() throws Exception {
        when(diagram.getMetadata()).thenReturn(projectMetadata);
        when(diagram.getGraph()).thenReturn(graph);
        when(diagram.getName()).thenReturn(NAME);
        when(projectDiagramService.getDiagramByPath(path)).thenReturn(projectDiagram);
        when(projectDiagram.getGraph()).thenReturn(graph);
        when(projectDiagram.getName()).thenReturn(NAME);
        when(projectDiagram.getMetadata()).thenReturn(projectMetadata);
        when(projectDiagramService.getDiagramByPath(path)).thenReturn(projectDiagram);
        when(projectDiagramService.accepts(path)).thenReturn(true);
        when(projectDiagramService.create(path, NAME, DEF_ID)).thenReturn(path);
        when(projectDiagramService.saveOrUpdate(any(ProjectDiagram.class))).thenReturn(projectMetadata);
        when(projectDiagramService.delete(any(ProjectDiagram.class))).thenReturn(true);
        when(projectDiagramService.getRawContent(any(ProjectDiagram.class))).thenReturn(CONTENT);
        delegateDiagramService = new DelegateDiagramService(projectDiagramService);
    }

    @Test
    public void getDiagramByPath() {
        Diagram<Graph, Metadata> diagram = delegateDiagramService.getDiagramByPath(path);
        verify(projectDiagramService).getDiagramByPath(path);
        assertEqualDiagram(diagram);
    }

    @Test
    public void accepts() {
        boolean accepts = delegateDiagramService.accepts(path);
        verify(projectDiagramService).accepts(path);
        assertTrue(accepts);
    }

    @Test
    public void create() {
        Path createdPath = delegateDiagramService.create(this.path, NAME, DEF_ID);
        verify(projectDiagramService).create(this.path, NAME, DEF_ID);
        assertEquals(createdPath, path);
    }

    @Test
    public void saveOrUpdate() {
        Metadata metadata = delegateDiagramService.saveOrUpdate(diagram);
        verify(projectDiagramService).saveOrUpdate(projectDiagramArgumentCaptor.capture());
        assertEqualDiagram(projectDiagramArgumentCaptor.getValue());
        assertEquals(metadata, projectMetadata);
    }

    @Test
    public void delete() {
        boolean deleted = delegateDiagramService.delete(diagram);
        verify(projectDiagramService).delete(projectDiagramArgumentCaptor.capture());
        assertEqualDiagram(projectDiagramArgumentCaptor.getValue());
        assertTrue(deleted);
    }

    private void assertEqualDiagram(ProjectDiagram projectDiagram) {
        assertEquals(projectDiagram.getName(), NAME);
        assertEquals(projectDiagram.getGraph(), graph);
        assertEquals(projectDiagram.getMetadata(), projectMetadata);
    }

    private void assertEqualDiagram(Diagram<Graph, Metadata> diagram) {
        assertEquals(diagram.getName(), NAME);
        assertEquals(diagram.getGraph(), graph);
        assertEquals(diagram.getMetadata(), projectMetadata);
    }

    @Test
    public void getRawContent() {
        String rawContent = delegateDiagramService.getRawContent(diagram);
        verify(projectDiagramService).getRawContent(projectDiagramArgumentCaptor.capture());
        assertEquals(rawContent, CONTENT);
    }
}
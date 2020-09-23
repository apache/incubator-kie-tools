/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.project.client.editor;

import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.DMNContentResource;
import org.kie.workbench.common.dmn.api.DMNContentService;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.client.marshaller.DMNMarshallerService;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramSavedEvent;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.service.DiagramLookupService;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.kie.workbench.common.stunner.project.diagram.impl.ProjectDiagramImpl;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNClientProjectDiagramServiceTest {

    @Mock
    private ShapeManager shapeManager;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private CallerMock<ProjectDiagramService> diagramServiceCaller;

    @Mock
    private CallerMock<DiagramLookupService> diagramLookupServiceCaller;

    @Mock
    private CallerMock<DMNContentService> dmnContentServiceCaller;

    @Mock
    private DMNContentService dmnContentService;

    @Mock
    private EventSourceMock<SessionDiagramSavedEvent> saveEvent;

    @Mock
    private DMNMarshallerService dmnMarshallerService;

    @Mock
    private Path path;

    @Mock
    private ServiceCallback<ProjectDiagram> projectDiagramCallback;

    @Mock
    private ServiceCallback<Diagram> diagramCallback;

    @Mock
    private RemoteCallback<DMNContentResource> resourceRemoteCallback;

    @Mock
    private ServiceCallback<String> stringCallback;

    @Captor
    private ArgumentCaptor<ProjectDiagramImpl> projectDiagramArgumentCaptor;

    @Captor
    private ArgumentCaptor<ServiceCallback<String>> serviceCallbackArgumentCaptor;

    private DMNClientProjectDiagramService service;

    @Before
    public void setup() {
        service = spy(new DMNClientProjectDiagramService(shapeManager,
                                                         sessionManager,
                                                         diagramServiceCaller,
                                                         diagramLookupServiceCaller,
                                                         saveEvent,
                                                         dmnContentServiceCaller,
                                                         dmnMarshallerService));
    }

    @Test
    public void testGetByPath() {

        final String defSetId = BindableAdapterUtils.getDefinitionSetId(DMNDefinitionSet.class);

        when(dmnContentServiceCaller.call(resourceRemoteCallback)).thenReturn(dmnContentService);
        doReturn(resourceRemoteCallback).when(service).onProjectContent(projectDiagramCallback);

        service.getByPath(path, projectDiagramCallback);

        verify(dmnContentService).getProjectContent(path, defSetId);
    }

    @Test
    public void testOnProjectContent() {

        final DMNContentResource resource = mock(DMNContentResource.class);
        final Metadata metadata = mock(Metadata.class);
        final String content = "<dmn />";

        when(resource.getMetadata()).thenReturn(metadata);
        when(resource.getContent()).thenReturn(content);
        doReturn(diagramCallback).when(service).getMarshallerCallback(resource, projectDiagramCallback);

        service.onProjectContent(projectDiagramCallback).callback(resource);

        verify(dmnMarshallerService).unmarshall(metadata, content, diagramCallback);
    }

    @Test
    public void testGetMarshallerCallback() {

        final DMNContentResource resource = mock(DMNContentResource.class);
        final Diagram diagram = mock(Diagram.class);
        final Graph graph = mock(Graph.class);
        final ProjectMetadata projectMetadata = mock(ProjectMetadata.class);
        final String name = "name";

        when(diagram.getName()).thenReturn(name);
        when(diagram.getGraph()).thenReturn(graph);
        when(resource.getMetadata()).thenReturn(projectMetadata);

        service.getMarshallerCallback(resource, projectDiagramCallback).onSuccess(diagram);

        verify(projectDiagramCallback).onSuccess(projectDiagramArgumentCaptor.capture());
        final ProjectDiagramImpl projectDiagram = projectDiagramArgumentCaptor.getValue();
        assertEquals(name, projectDiagram.getName());
        assertEquals(graph, projectDiagram.getGraph());
        assertEquals(projectMetadata, projectDiagram.getMetadata());
    }

    @Test
    public void testSaveOrUpdate() {

        final Path path = mock(Path.class);
        final ProjectDiagram diagram = mock(ProjectDiagram.class);
        final org.guvnor.common.services.shared.metadata.model.Metadata metadata = mock(org.guvnor.common.services.shared.metadata.model.Metadata.class);
        final String comment = "comment";
        final String xml = "<dmn />";

        doReturn(stringCallback).when(service).onSaveAsXmlComplete(diagram, projectDiagramCallback);
        doNothing().when(service).saveAsXml(any(), any(), any(), any(), any());

        service.saveOrUpdate(path, diagram, metadata, comment, projectDiagramCallback);

        verify(dmnMarshallerService).marshall(eq(diagram), serviceCallbackArgumentCaptor.capture());
        serviceCallbackArgumentCaptor.getValue().onSuccess(xml);

        verify(service).saveAsXml(path, xml, metadata, comment, stringCallback);
    }

    @Test
    public void testAsProjectDiagramImpl() {

        final Graph graph = mock(Graph.class);
        final Diagram diagram = mock(Diagram.class);
        final DMNContentResource resource = mock(DMNContentResource.class);
        final ProjectMetadata metadata = mock(ProjectMetadata.class);

        when(diagram.getName()).thenReturn("Traffic Violation.dmn");
        when(diagram.getGraph()).thenReturn(graph);
        when(resource.getMetadata()).thenReturn(metadata);

        final ProjectDiagramImpl projectDiagram = service.asProjectDiagramImpl(diagram, resource);

        assertEquals("Traffic Violation", projectDiagram.getName());
        assertEquals(graph, projectDiagram.getGraph());
        assertEquals(metadata, projectDiagram.getMetadata());
    }
}

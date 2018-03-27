/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.service;

import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.lookup.LookupManager;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramLookupRequest;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramRepresentation;
import org.kie.workbench.common.stunner.core.service.DiagramLookupService;
import org.kie.workbench.common.stunner.core.service.DiagramService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientDiagramServicesTest {

    @Mock
    ShapeManager shapeManager;
    @Mock
    DiagramService diagramService;
    @Mock
    DiagramLookupService diagramLookupService;
    @Mock
    Path path;
    @Mock
    Diagram diagram;
    @Mock
    Metadata metadata;

    private ClientDiagramService tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getDefinitionSetId()).thenReturn("ds1 ");
        when(metadata.getShapeSetId()).thenReturn("ss1 ");
        when(diagramService.saveOrUpdate(any(Diagram.class))).thenReturn(metadata);
        Caller<DiagramService> diagramServiceCaller = new CallerMock<>(diagramService);
        Caller<DiagramLookupService> diagramLookupServiceCaller = new CallerMock<>(diagramLookupService);
        this.tested = new ClientDiagramService(shapeManager,
                                               diagramServiceCaller,
                                               diagramLookupServiceCaller);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreate() {
        String name = "d1";
        String defSetId = "id1";
        ServiceCallback<Path> callback = mock(ServiceCallback.class);
        tested.create(path,
                      name,
                      defSetId,
                      callback);
        verify(diagramService,
               times(1)).create(eq(path),
                                eq(name),
                                eq(defSetId));
        verify(callback,
               times(1)).onSuccess(any(Path.class));
        verify(callback,
               times(0)).onError(any(ClientRuntimeError.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAdd() {
        ServiceCallback<Diagram<Graph, Metadata>> callback = mock(ServiceCallback.class);
        tested.add(diagram,
                   callback);
        verify(diagramService,
               times(1)).saveOrUpdate(eq(diagram));
        verify(callback,
               times(1)).onSuccess(any(Diagram.class));
        verify(callback,
               times(0)).onError(any(ClientRuntimeError.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSaveOrUpdate() {
        ServiceCallback<Diagram<Graph, Metadata>> callback = mock(ServiceCallback.class);
        tested.saveOrUpdate(diagram,
                            callback);
        verify(diagramService,
               times(1)).saveOrUpdate(eq(diagram));
        verify(callback,
               times(1)).onSuccess(any(Diagram.class));
        verify(callback,
               times(0)).onError(any(ClientRuntimeError.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetByPath() {
        ServiceCallback<Diagram<Graph, Metadata>> callback = mock(ServiceCallback.class);
        tested.getByPath(path,
                         callback);
        verify(diagramService,
               times(1)).getDiagramByPath(eq(path));
        verify(callback,
               times(1)).onSuccess(any(Diagram.class));
        verify(callback,
               times(0)).onError(any(ClientRuntimeError.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLookup() {
        DiagramLookupRequest request = mock(DiagramLookupRequest.class);
        ServiceCallback<LookupManager.LookupResponse<DiagramRepresentation>> callback = mock(ServiceCallback.class);
        tested.lookup(request,
                      callback);
        verify(diagramLookupService,
               times(1)).lookup(eq(request));
        verify(callback,
               times(1)).onSuccess(any(LookupManager.LookupResponse.class));
        verify(callback,
               times(0)).onError(any(ClientRuntimeError.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUpdateClientMetadata() {
        String ssid = "shapeSet1";
        ShapeSet shapeSet = mock(ShapeSet.class);
        when(shapeSet.getId()).thenReturn(ssid);
        when(shapeManager.getDefaultShapeSet(anyString())).thenReturn(shapeSet);
        when(metadata.getShapeSetId()).thenReturn(null);
        ServiceCallback<Diagram<Graph, Metadata>> callback = mock(ServiceCallback.class);
        when(diagramService.getDiagramByPath(eq(path))).thenReturn(diagram);
        tested.add(diagram,
                   callback);
        tested.saveOrUpdate(diagram,
                            callback);
        tested.getByPath(path,
                         callback);
        verify(metadata,
               times(3)).setShapeSetId(eq(ssid));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetRawContent() {
        ServiceCallback<String> callback = mock(ServiceCallback.class);
        tested.getRawContent(diagram,
                             callback);
        verify(diagramService,
               times(1)).getRawContent(eq(diagram));
        verify(callback,
               times(1)).onSuccess(any(String.class));
        verify(callback,
               times(0)).onError(any(ClientRuntimeError.class));
    }
}

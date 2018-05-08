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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.session.command.event.SaveDiagramSessionCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.lookup.LookupManager;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramLookupRequest;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramRepresentation;
import org.kie.workbench.common.stunner.core.service.BaseDiagramService;
import org.kie.workbench.common.stunner.core.service.DiagramLookupService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractClientDiagramServiceTest<M extends Metadata, D extends Diagram<Graph, M>, S extends BaseDiagramService<M, D>, CS extends AbstractClientDiagramService<M, D, S>> {

    private static final String RAW_DIAGRAM = "";
    @Mock
    protected ShapeManager shapeManager;

    @Mock
    protected DiagramLookupService diagramLookupService;

    @Mock
    protected Path path;

    protected M metadata;

    protected D diagram;

    protected S diagramService;

    protected CS tested;

    @Mock
    protected EventSourceMock<SaveDiagramSessionCommandExecutedEvent> saveDiagramSessionCommandExecutedEventEvent;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        this.metadata = makeTestMetadata();
        this.diagram = makeTestDiagram();
        this.diagramService = makeTestDiagramService();
        this.tested = makeTestClientDiagramService();

        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getDefinitionSetId()).thenReturn("ds1 ");
        when(metadata.getShapeSetId()).thenReturn("ss1 ");
        when(diagramService.saveOrUpdate(eq(diagram))).thenReturn(metadata);
        when(diagramService.getDiagramByPath(eq(path))).thenReturn(diagram);
    }

    protected abstract M makeTestMetadata();

    protected abstract D makeTestDiagram();

    protected abstract S makeTestDiagramService();

    protected abstract CS makeTestClientDiagramService();

    @Test
    @SuppressWarnings("unchecked")
    public void testCreate() {
        final String name = "d1";
        final String defSetId = "id1";
        final ServiceCallback<Path> callback = mock(ServiceCallback.class);

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
        final ServiceCallback<D> callback = mock(ServiceCallback.class);

        tested.add(diagram,
                   callback);

        verify(diagramService,
               times(1)).saveOrUpdate(eq(diagram));
        verify(callback,
               times(1)).onSuccess(eq(diagram));
        verify(callback,
               times(0)).onError(any(ClientRuntimeError.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSaveOrUpdate() {
        final ServiceCallback<D> callback = mock(ServiceCallback.class);

        tested.saveOrUpdate(diagram,
                            callback);

        verify(diagramService,
               times(1)).saveOrUpdate(eq(diagram));
        verify(callback,
               times(1)).onSuccess(eq(diagram));
        verify(callback,
               times(0)).onError(any(ClientRuntimeError.class));
    }

    @Test
    public void testSaveOrUpdateSvg() {
        final ServiceCallback<Path> callback = mock(ServiceCallback.class);

        tested.saveOrUpdateSvg(path, RAW_DIAGRAM, callback);
        verify(diagramService).saveOrUpdateSvg(path, RAW_DIAGRAM);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetByPath() {
        final ServiceCallback<D> callback = mock(ServiceCallback.class);

        tested.getByPath(path,
                         callback);

        verify(diagramService,
               times(1)).getDiagramByPath(eq(path));
        verify(callback,
               times(1)).onSuccess(eq(diagram));
        verify(callback,
               times(0)).onError(any(ClientRuntimeError.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLookup() {
        final DiagramLookupRequest request = mock(DiagramLookupRequest.class);
        final ServiceCallback<LookupManager.LookupResponse<DiagramRepresentation>> callback = mock(ServiceCallback.class);

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
        final String ssid = "shapeSet1";
        final ShapeSet shapeSet = mock(ShapeSet.class);
        final ServiceCallback<D> callback = mock(ServiceCallback.class);
        when(shapeSet.getId()).thenReturn(ssid);
        when(shapeManager.getDefaultShapeSet(anyString())).thenReturn(shapeSet);
        when(metadata.getShapeSetId()).thenReturn(null);
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
        final ServiceCallback<String> callback = mock(ServiceCallback.class);

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

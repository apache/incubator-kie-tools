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

package org.kie.workbench.common.stunner.bpmn.client.workitem;

import java.util.Collections;
import java.util.function.Consumer;

import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionCacheRegistry;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistries;
import org.kie.workbench.common.stunner.bpmn.workitem.service.WorkItemDefinitionLookupService;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkItemDefinitionClientRegistryTest {

    private static final WorkItemDefinition WID = new WorkItemDefinition().setName("testWID");

    @Mock
    private WorkItemDefinitionLookupService service;

    @Mock
    private WorkItemDefinitionRegistries<Metadata> index;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private WorkItemDefinitionCacheRegistry registry;

    @Mock
    private Consumer<Throwable> errorPresenter;

    @Mock
    private Metadata metadata;

    @Mock
    private SessionDestroyedEvent sessionDestroyedEvent;

    private WorkItemDefinitionClientRegistry tested;
    private Caller<WorkItemDefinitionLookupService> serviceCaller;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        when(index.registries()).thenReturn(m -> metadata.equals(m) ? registry : null);
        when(service.execute(eq(metadata))).thenReturn(Collections.singleton(WID));
        when(sessionDestroyedEvent.getMetadata()).thenReturn(metadata);
        when(index.remove(metadata)).thenReturn(registry);
        serviceCaller = new CallerMock<>(service);
        tested = new WorkItemDefinitionClientRegistry(serviceCaller,
                                                      sessionManager,
                                                      () -> registry,
                                                      errorPresenter,
                                                      index);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoad() {
        Command callback = mock(Command.class);
        tested.load(metadata,
                    callback);
        verify(index, times(1)).put(eq(metadata), eq(registry));
        verify(registry, times(1)).register(eq(WID));
        verify(callback, times(1)).execute();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetCurrentSessionRegistry() {
        ClientSession session = mock(ClientSession.class);
        CanvasHandler canvasHandler = mock(CanvasHandler.class);
        Diagram diagram = mock(Diagram.class);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(sessionManager.getCurrentSession()).thenReturn(session);
        Command callback = mock(Command.class);
        tested.load(metadata,
                    callback);
        verify(index, times(1)).put(eq(metadata), eq(registry));
        verify(registry, times(1)).register(eq(WID));
        verify(callback, times(1)).execute();
        WorkItemDefinitionCacheRegistry currentSessionRegistry = tested.getCurrentSessionRegistry();
        assertEquals(registry, currentSessionRegistry);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDestroy() {
        tested.destroy();
        verify(index, times(1)).clear();
    }

    @Test
    public void testOnSessionDestroyed() {
        tested.onSessionDestroyed(sessionDestroyedEvent);
        verify(index).remove(metadata);
        verify(registry).clear();
    }

    @Test
    public void testOnSessionDestroyedNullRegistry() {
        when(index.remove(metadata)).thenReturn(null);

        tested.onSessionDestroyed(sessionDestroyedEvent);

        verify(index).remove(metadata);
        verify(registry, never()).clear();
    }
}

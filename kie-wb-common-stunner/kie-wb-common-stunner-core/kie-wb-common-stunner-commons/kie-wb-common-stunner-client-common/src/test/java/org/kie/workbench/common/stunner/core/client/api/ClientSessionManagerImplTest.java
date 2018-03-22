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

package org.kie.workbench.common.stunner.core.client.api;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientSessionFactory;
import org.kie.workbench.common.stunner.core.client.session.event.OnSessionErrorEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionPausedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionResumedEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSession;
import org.kie.workbench.common.stunner.core.command.exception.CommandException;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ClientSessionManagerImplTest {

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private ManagedInstance<ClientSessionFactory> sessionFactoriesInstances;

    @Mock
    private EventSourceMock<SessionOpenedEvent> sessionOpenedEventMock;

    @Mock
    private EventSourceMock<SessionPausedEvent> sessionPausedEventMock;

    @Mock
    private EventSourceMock<SessionResumedEvent> sessionResumedEventMock;

    @Mock
    private EventSourceMock<SessionDestroyedEvent> sessionDestroyedEventMock;

    @Mock
    private EventSourceMock<OnSessionErrorEvent> sessionErrorEventMock;

    @Mock
    private AbstractClientSession session;

    @Mock
    private AbstractCanvasHandler handler;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Mock
    private Graph graph;

    @Mock
    private AbstractClientSession session1;

    private ClientSessionManagerImpl tested;

    @Before
    public void setup() throws Exception {
        when(session.getCanvasHandler()).thenReturn(handler);
        when(handler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(diagram.getGraph()).thenReturn(graph);
        this.tested = new ClientSessionManagerImpl(definitionUtils,
                                                   sessionFactoriesInstances,
                                                   sessionOpenedEventMock,
                                                   sessionDestroyedEventMock,
                                                   sessionPausedEventMock,
                                                   sessionResumedEventMock,
                                                   sessionErrorEventMock);
    }

    @Test
    public void testPostOpen() {
        tested.open(session);
        verify(sessionOpenedEventMock,
               times(1)).fire(any(SessionOpenedEvent.class));
        verify(sessionPausedEventMock,
               times(0)).fire(any(SessionPausedEvent.class));
        verify(sessionResumedEventMock,
               times(0)).fire(any(SessionResumedEvent.class));
        verify(sessionErrorEventMock,
               times(0)).fire(any(OnSessionErrorEvent.class));
        verify(sessionDestroyedEventMock,
               times(0)).fire(any(SessionDestroyedEvent.class));
    }

    @Test
    public void testPostOpenAnotherSession() {
        tested.current = session;
        tested.open(session1);
        verify(sessionOpenedEventMock,
               times(1)).fire(any(SessionOpenedEvent.class));
        verify(sessionPausedEventMock,
               times(1)).fire(any(SessionPausedEvent.class));
        verify(sessionResumedEventMock,
               times(0)).fire(any(SessionResumedEvent.class));
        verify(sessionErrorEventMock,
               times(0)).fire(any(OnSessionErrorEvent.class));
        verify(sessionDestroyedEventMock,
               times(0)).fire(any(SessionDestroyedEvent.class));
    }

    @Test
    public void testPostPause() {
        tested.current = session;
        tested.pause();
        verify(sessionOpenedEventMock,
               times(0)).fire(any(SessionOpenedEvent.class));
        verify(sessionPausedEventMock,
               times(1)).fire(any(SessionPausedEvent.class));
        verify(sessionResumedEventMock,
               times(0)).fire(any(SessionResumedEvent.class));
        verify(sessionErrorEventMock,
               times(0)).fire(any(OnSessionErrorEvent.class));
        verify(sessionDestroyedEventMock,
               times(0)).fire(any(SessionDestroyedEvent.class));
    }

    @Test
    public void testPostResume() {
        tested.current = session1;
        tested.resume(session);
        verify(sessionOpenedEventMock,
               times(0)).fire(any(SessionOpenedEvent.class));
        verify(sessionPausedEventMock,
               times(1)).fire(any(SessionPausedEvent.class));
        verify(sessionResumedEventMock,
               times(1)).fire(any(SessionResumedEvent.class));
        verify(sessionErrorEventMock,
               times(0)).fire(any(OnSessionErrorEvent.class));
        verify(sessionDestroyedEventMock,
               times(0)).fire(any(SessionDestroyedEvent.class));
    }

    @Test
    public void testPostDestroy() {
        when(session.getSessionUUID()).thenReturn("sessionUUID");
        when(diagram.getName()).thenReturn("diagramName");
        when(graph.getUUID()).thenReturn("graphUUID");
        tested.current = session;
        tested.destroy();
        verify(sessionOpenedEventMock,
               times(0)).fire(any(SessionOpenedEvent.class));
        verify(sessionPausedEventMock,
               times(0)).fire(any(SessionPausedEvent.class));
        verify(sessionResumedEventMock,
               times(0)).fire(any(SessionResumedEvent.class));
        verify(sessionErrorEventMock,
               times(0)).fire(any(OnSessionErrorEvent.class));
        ArgumentCaptor<SessionDestroyedEvent> destroyedEventArgumentCaptor =
                ArgumentCaptor.forClass(SessionDestroyedEvent.class);
        verify(sessionDestroyedEventMock,
               times(1)).fire(destroyedEventArgumentCaptor.capture());
        SessionDestroyedEvent event = destroyedEventArgumentCaptor.getValue();
        assertEquals("sessionUUID", event.getSessionUUID());
        assertEquals("diagramName", event.getDiagramName());
        assertEquals("graphUUID", event.getGraphUuid());
        assertEquals(metadata, event.getMetadata());
    }

    @Test
    public void testPostDestroyButNoDiagram() {
        when(session.getSessionUUID()).thenReturn("sessionUUID");
        when(handler.getDiagram()).thenReturn(null);
        tested.current = session;
        tested.destroy();
        verify(sessionOpenedEventMock,
               times(0)).fire(any(SessionOpenedEvent.class));
        verify(sessionPausedEventMock,
               times(0)).fire(any(SessionPausedEvent.class));
        verify(sessionResumedEventMock,
               times(0)).fire(any(SessionResumedEvent.class));
        verify(sessionErrorEventMock,
               times(0)).fire(any(OnSessionErrorEvent.class));
        ArgumentCaptor<SessionDestroyedEvent> destroyedEventArgumentCaptor =
                ArgumentCaptor.forClass(SessionDestroyedEvent.class);
        verify(sessionDestroyedEventMock,
               times(1)).fire(destroyedEventArgumentCaptor.capture());
        SessionDestroyedEvent event = destroyedEventArgumentCaptor.getValue();
        assertEquals("sessionUUID", event.getSessionUUID());
        assertNull(event.getDiagramName());
        assertNull(event.getGraphUuid());
        assertNull(event.getMetadata());
    }

    @Test
    public void testHandleClientError() {
        tested.current = session;
        ClientRuntimeError error = mock(ClientRuntimeError.class);
        tested.handleClientError(error);
        verify(sessionOpenedEventMock,
               times(0)).fire(any(SessionOpenedEvent.class));
        verify(sessionPausedEventMock,
               times(0)).fire(any(SessionPausedEvent.class));
        verify(sessionResumedEventMock,
               times(0)).fire(any(SessionResumedEvent.class));
        verify(sessionErrorEventMock,
               times(1)).fire(any(OnSessionErrorEvent.class));
        verify(sessionDestroyedEventMock,
               times(0)).fire(any(SessionDestroyedEvent.class));
    }

    @Test
    public void testHandleClientCommandError() {
        tested.current = session;
        CommandException error = mock(CommandException.class);
        tested.handleCommandError(error);
        verify(sessionOpenedEventMock,
               times(0)).fire(any(SessionOpenedEvent.class));
        verify(sessionPausedEventMock,
               times(0)).fire(any(SessionPausedEvent.class));
        verify(sessionResumedEventMock,
               times(0)).fire(any(SessionResumedEvent.class));
        verify(sessionErrorEventMock,
               times(1)).fire(any(OnSessionErrorEvent.class));
        verify(sessionDestroyedEventMock,
               times(0)).fire(any(SessionDestroyedEvent.class));
    }
}

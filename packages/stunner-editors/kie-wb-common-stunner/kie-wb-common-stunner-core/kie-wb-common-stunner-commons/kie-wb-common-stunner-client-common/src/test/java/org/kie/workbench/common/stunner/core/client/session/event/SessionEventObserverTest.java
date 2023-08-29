/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.client.session.event;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Instance;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SessionEventObserverTest {

    private static final int HANDLERS = 10;

    @Mock
    private Instance<SessionDiagramOpenedHandler> sessionDiagramOpenedHandlersInstance;

    private List<SessionDiagramOpenedHandler> allSessionDiagramOpenedHandlers = new ArrayList<>();

    private List<SessionDiagramOpenedHandler> acceptsDiagramSessionDiagramOpenedHandlers = new ArrayList<>();

    private List<SessionDiagramOpenedHandler> dontAcceptsDiagramSessionDiagramOpenedHandlers = new ArrayList<>();

    @Mock
    private Instance<SessionDiagramSavedHandler> sessionDiagramSavedHandlersInstance;

    private List<SessionDiagramSavedHandler> allSessionDiagramSavedHandlers = new ArrayList<>();

    private List<SessionDiagramSavedHandler> acceptsDiagramSessionDiagramSavedHandlers = new ArrayList<>();

    private List<SessionDiagramSavedHandler> dontAcceptsDiagramSessionDiagramSavedHandlers = new ArrayList<>();

    @Mock
    private Diagram diagram;

    @Mock
    private CanvasHandler canvasHandler;

    @Mock
    private ClientSession session;

    private SessionEventObserver observer;

    @Before
    public void setUp() {
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        for (int i = 0; i < HANDLERS; i++) {
            SessionDiagramOpenedHandler openedHandler = mock(SessionDiagramOpenedHandler.class);
            when(openedHandler.accepts(diagram)).thenReturn(true);
            acceptsDiagramSessionDiagramOpenedHandlers.add(openedHandler);

            openedHandler = mock(SessionDiagramOpenedHandler.class);
            when(openedHandler.accepts(diagram)).thenReturn(false);
            dontAcceptsDiagramSessionDiagramOpenedHandlers.add(openedHandler);

            SessionDiagramSavedHandler savedHandler = mock(SessionDiagramSavedHandler.class);
            when(savedHandler.accepts(diagram)).thenReturn(true);
            acceptsDiagramSessionDiagramSavedHandlers.add(savedHandler);

            savedHandler = mock(SessionDiagramSavedHandler.class);
            when(savedHandler.accepts(diagram)).thenReturn(false);
            dontAcceptsDiagramSessionDiagramSavedHandlers.add(savedHandler);
        }
        allSessionDiagramOpenedHandlers.addAll(acceptsDiagramSessionDiagramOpenedHandlers);
        allSessionDiagramOpenedHandlers.addAll(dontAcceptsDiagramSessionDiagramOpenedHandlers);
        allSessionDiagramSavedHandlers.addAll(acceptsDiagramSessionDiagramSavedHandlers);
        allSessionDiagramSavedHandlers.addAll(dontAcceptsDiagramSessionDiagramSavedHandlers);

        when(sessionDiagramOpenedHandlersInstance.iterator()).thenReturn(allSessionDiagramOpenedHandlers.iterator());
        when(sessionDiagramSavedHandlersInstance.iterator()).thenReturn(allSessionDiagramSavedHandlers.iterator());
        observer = new SessionEventObserver(sessionDiagramOpenedHandlersInstance, sessionDiagramSavedHandlersInstance);
    }

    @Test
    public void testOnSessionDiagramOpenedEvent() {
        observer.onSessionDiagramOpenedEvent(new SessionDiagramOpenedEvent(session));
        allSessionDiagramOpenedHandlers.forEach(handler -> verify(handler).accepts(diagram));
        acceptsDiagramSessionDiagramOpenedHandlers.forEach(handler -> verify(handler).onSessionDiagramOpened(session));
        dontAcceptsDiagramSessionDiagramOpenedHandlers.forEach(handler -> verify(handler, never()).onSessionDiagramOpened(session));
    }

    @Test
    public void testOnSessionDiagramSavedEvent() {
        observer.onSessionDiagramSavedEvent(new SessionDiagramSavedEvent(session));
        allSessionDiagramSavedHandlers.forEach(handler -> verify(handler).accepts(diagram));
        acceptsDiagramSessionDiagramSavedHandlers.forEach(handler -> verify(handler).onSessionDiagramSaved(session));
        dontAcceptsDiagramSessionDiagramSavedHandlers.forEach(handler -> verify(handler, never()).onSessionDiagramSaved(session));
    }
}

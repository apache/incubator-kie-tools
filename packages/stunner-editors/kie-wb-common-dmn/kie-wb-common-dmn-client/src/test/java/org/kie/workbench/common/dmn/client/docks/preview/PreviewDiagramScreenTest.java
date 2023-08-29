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
package org.kie.workbench.common.dmn.client.docks.preview;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPreview;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionViewer;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.stubs.ManagedInstanceStub;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class PreviewDiagramScreenTest {

    @Mock
    private SessionManager clientSessionManager;

    @Mock
    private SessionDiagramPreview<AbstractSession> sessionPreview;

    @Mock
    private IsWidget previewWidget;

    @Mock
    private AbstractSession session;

    @Mock
    private PreviewDiagramScreen.View view;

    @Mock
    private DMNDiagramsSession dmnDiagramsSession;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Captor
    private ArgumentCaptor<SessionViewer.SessionViewerCallback> sessionViewerCallbackArgumentCaptor;

    private ManagedInstance<SessionDiagramPreview<AbstractSession>> sessionPreviews;

    private PreviewDiagramScreen tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(clientSessionManager.getCurrentSession()).thenReturn(session);
        when(sessionPreview.getView()).thenReturn(previewWidget);
        when(sessionPreview.getInstance()).thenReturn(session);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);

        this.sessionPreviews = new ManagedInstanceStub<>(sessionPreview);
        this.tested = spy(new PreviewDiagramScreen(clientSessionManager,
                                                   sessionPreviews,
                                                   view,
                                                   dmnDiagramsSession));
    }

    @Test
    public void testOnOpen() {
        tested.onStartup(mock(PlaceRequest.class));
        tested.onOpen();

        verify(tested).showPreview(any());
    }

    @Test
    public void testOnClose() {
        tested.onStartup(mock(PlaceRequest.class));
        tested.onOpen();
        tested.onClose();

        verify(tested).closePreview();
    }

    @Test
    public void testOnCanvasSessionOpenedWhenItsTheSameSession() {
        final SessionOpenedEvent event = mock(SessionOpenedEvent.class);
        final String currentSessionKey = "key1";
        final String eventSessionKey = "key1";

        when(dmnDiagramsSession.getCurrentSessionKey()).thenReturn(currentSessionKey);
        when(dmnDiagramsSession.getSessionKey(metadata)).thenReturn(eventSessionKey);
        when(event.getSession()).thenReturn(session);

        tested.onCanvasSessionOpened(event);

        verify(tested).showPreview(session);
    }

    @Test
    public void testOnCanvasSessionOpenedWhenItsNotTheSameSession() {
        final SessionOpenedEvent event = mock(SessionOpenedEvent.class);
        final String currentSessionKey = "key1";
        final String eventSessionKey = "key2";

        when(dmnDiagramsSession.getCurrentSessionKey()).thenReturn(currentSessionKey);
        when(dmnDiagramsSession.getSessionKey(metadata)).thenReturn(eventSessionKey);
        when(event.getSession()).thenReturn(session);

        tested.onCanvasSessionOpened(event);

        verify(tested, never()).showPreview(session);
    }

    @Test
    public void testOnCanvasSessionDestroyedWhenItsTheSameSession() {
        final SessionDestroyedEvent event = mock(SessionDestroyedEvent.class);
        final String currentSessionKey = "key1";
        final String eventSessionKey = "key1";

        when(event.getMetadata()).thenReturn(metadata);
        when(dmnDiagramsSession.getCurrentSessionKey()).thenReturn(currentSessionKey);
        when(dmnDiagramsSession.getSessionKey(metadata)).thenReturn(eventSessionKey);

        tested.onCanvasSessionDestroyed(event);

        verify(tested).closePreview();
    }

    @Test
    public void testOnCanvasSessionDestroyedWhenItsNotTheSameSession() {
        final SessionDestroyedEvent event = mock(SessionDestroyedEvent.class);
        final String currentSessionKey = "key1";
        final String eventSessionKey = "key2";

        when(event.getMetadata()).thenReturn(metadata);
        when(dmnDiagramsSession.getCurrentSessionKey()).thenReturn(currentSessionKey);
        when(dmnDiagramsSession.getSessionKey(metadata)).thenReturn(eventSessionKey);

        tested.onCanvasSessionDestroyed(event);

        verify(tested, never()).closePreview();
    }

    @Test
    public void testOnSessionDiagramOpenedEventWhenItsTheSameSession() {
        final SessionDiagramOpenedEvent event = mock(SessionDiagramOpenedEvent.class);
        final String currentSessionKey = "key1";
        final String eventSessionKey = "key1";

        when(dmnDiagramsSession.getCurrentSessionKey()).thenReturn(currentSessionKey);
        when(dmnDiagramsSession.getSessionKey(metadata)).thenReturn(eventSessionKey);
        when(event.getSession()).thenReturn(session);

        tested.onSessionDiagramOpenedEvent(event);

        verify(tested).showPreview(session);
    }

    @Test
    public void testOnSessionDiagramOpenedEventWhenItsNotTheSameSession() {
        final SessionDiagramOpenedEvent event = mock(SessionDiagramOpenedEvent.class);
        final String currentSessionKey = "key1";
        final String eventSessionKey = "key2";

        when(dmnDiagramsSession.getCurrentSessionKey()).thenReturn(currentSessionKey);
        when(dmnDiagramsSession.getSessionKey(metadata)).thenReturn(eventSessionKey);
        when(event.getSession()).thenReturn(session);

        tested.onSessionDiagramOpenedEvent(event);

        verify(tested, never()).showPreview(session);
    }

    @Test
    public void testView() {
        assertEquals(view,
                     tested.getWidget());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testShowPreview() {
        tested.showPreview(session);

        verify(sessionPreview).open(eq(session),
                                    sessionViewerCallbackArgumentCaptor.capture());

        final SessionViewer.SessionViewerCallback sessionViewerCallback = sessionViewerCallbackArgumentCaptor.getValue();
        sessionViewerCallback.onSuccess();

        verify(sessionPreview,
               never()).clear();
        verify(view).setPreviewWidget(previewWidget);
    }

    @Test
    public void testClose() {
        tested.showPreview(session);
        tested.closePreview();

        verify(view).clearPreviewWidget();
        verify(sessionPreview).destroy();
    }
}

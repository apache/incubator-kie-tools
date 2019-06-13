/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.project.client.docks.screens;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPreview;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionViewer;
import org.kie.workbench.common.stunner.core.client.ManagedInstanceStub;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyInt;
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
    private ManagedInstance<SessionDiagramPreview<AbstractSession>> sessionPreviews;

    @Mock
    private IsWidget previewWidget;
    @Mock

    private AbstractSession session;

    @Mock
    private PreviewDiagramScreen.View view;

    @Captor
    private ArgumentCaptor<SessionViewer.SessionViewerCallback> sessionViewerCallbackArgumentCaptor;

    private PreviewDiagramScreen tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(clientSessionManager.getCurrentSession()).thenReturn(session);
        when(sessionPreview.getView()).thenReturn(previewWidget);
        when(sessionPreview.getInstance()).thenReturn(session);

        this.sessionPreviews = new ManagedInstanceStub<>(sessionPreview);
        this.tested = spy(new PreviewDiagramScreen(clientSessionManager,
                                                   sessionPreviews,
                                                   view));
    }

    @Test
    public void testOnOpen() {
        tested.onOpen();

        verify(tested).showPreview(any());
    }

    @Test
    public void testOnClose() {
        tested.onClose();

        verify(tested).closePreview();
    }

    @Test
    public void testOnCanvasSessionOpened() {
        final SessionOpenedEvent event = mock(SessionOpenedEvent.class);
        when(event.getSession()).thenReturn(session);

        tested.onCanvasSessionOpened(event);

        verify(tested).showPreview(session);
    }

    @Test
    public void testOnCanvasSessionDestroyed() {
        final SessionDestroyedEvent event = mock(SessionDestroyedEvent.class);

        tested.onCanvasSessionDestroyed(event);

        verify(tested).closePreview();
    }

    @Test
    public void testOnSessionDiagramOpenedEvent() {
        final SessionDiagramOpenedEvent event = mock(SessionDiagramOpenedEvent.class);
        when(event.getSession()).thenReturn(session);

        tested.onSessionDiagramOpenedEvent(event);

        verify(tested).showPreview(session);
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
                                    anyInt(),
                                    anyInt(),
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

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

package org.kie.workbench.common.stunner.client.widgets.presenters.session.impl;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.event.SessionDiagramOpenedEvent;
import org.kie.workbench.common.stunner.client.widgets.notification.NotificationsObserver;
import org.kie.workbench.common.stunner.client.widgets.palette.BS3PaletteWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.BS3PaletteFactory;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPreview;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.EditorToolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.EditorToolbarFactory;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.ViewerToolbarFactory;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenMaximizedEvent;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenMinimizedEvent;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenResizeEventObserver;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SessionEditorPresenterTest {

    private SessionEditorPresenter<AbstractClientFullSession, AbstractCanvasHandler> sessionEditorPresenter;

    private ScreenResizeEventObserver screenResizeEventObserver;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private CanvasCommandManager<AbstractCanvasHandler> commandManagerInstances;

    @Mock
    private ViewerToolbarFactory viewerToolbarFactoryInstances;

    @Mock
    private EditorToolbarFactory editorToolbarFactoryInstances;

    @Mock
    private SessionDiagramPreview<AbstractClientSession> sessionPreviewInstances;

    @Mock
    private WidgetWrapperView diagramViewerViewInstances;

    @Mock
    private SessionPresenter.View viewInstances;

    @Mock
    private NotificationsObserver notificationsObserverInstances;

    @Mock
    private BS3PaletteFactory paletteWidgetFactory;

    @Mock
    private Event<SessionDiagramOpenedEvent> sessionDiagramOpenedEventInstances;

    @Mock
    private BS3PaletteWidget paletteWidget;

    @Mock
    private AbstractClientFullSession clientFullSession;

    @Mock
    private EditorToolbar toolbar;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    @Before
    public void init() throws Exception {

        screenResizeEventObserver = new ScreenResizeEventObserver();

        sessionEditorPresenter = new SessionEditorPresenter<>(sessionManager,
                                                              commandManagerInstances,
                                                              sessionDiagramOpenedEventInstances,
                                                              editorToolbarFactoryInstances,
                                                              paletteWidgetFactory,
                                                              diagramViewerViewInstances,
                                                              notificationsObserverInstances,
                                                              viewInstances,
                                                              screenResizeEventObserver);

        when(paletteWidgetFactory.newPalette(anyString())).thenReturn(paletteWidget);
        when(editorToolbarFactoryInstances.build(clientFullSession)).thenReturn(toolbar);

        when(clientFullSession.getCanvasHandler()).thenReturn(canvasHandler);

        when(canvasHandler.getDiagram()).thenReturn(diagram);

        when(diagram.getMetadata()).thenReturn(Mockito.mock(Metadata.class));

        when(paletteWidgetFactory.newPalette(anyString(),
                                             any())).thenReturn(paletteWidget);

        when(viewInstances.setPaletteWidget(any())).thenReturn(viewInstances);

        sessionEditorPresenter.onSessionOpened(clientFullSession);
    }

    @Test
    public void screenResizeEventTest() throws Exception {
        screenResizeEventObserver.onEventReceived(new ScreenMaximizedEvent(true));
        screenResizeEventObserver.onEventReceived(new ScreenMaximizedEvent(false));
        screenResizeEventObserver.onEventReceived(new ScreenMinimizedEvent(true));
        screenResizeEventObserver.onEventReceived(new ScreenMinimizedEvent(false));

        InOrder inOrder = inOrder(paletteWidget);
        inOrder.verify(paletteWidget).setVisible(true);
        inOrder.verify(paletteWidget).setVisible(false);
        inOrder.verify(paletteWidget,
                       times(2)).setVisible(true);
    }
}

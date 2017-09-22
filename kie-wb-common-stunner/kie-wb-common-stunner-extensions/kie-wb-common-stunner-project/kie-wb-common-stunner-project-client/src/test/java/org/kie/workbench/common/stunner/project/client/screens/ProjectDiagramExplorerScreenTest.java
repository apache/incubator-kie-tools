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
package org.kie.workbench.common.stunner.project.client.screens;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorer;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenterFactory;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPreview;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionViewer;
import org.kie.workbench.common.stunner.core.client.api.AbstractClientSessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.Mock;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ProjectDiagramExplorerScreenTest {

    @Mock
    AbstractClientSessionManager clientSessionManager;
    @Mock
    TreeExplorer treeExplorer;
    @Mock
    SessionPresenterFactory sessionPresenterFactory;
    @Mock
    SessionPreview sessionPreview;
    @Mock
    EventSourceMock<ChangeTitleWidgetEvent> changeTitleNotificationEvent;
    @Mock
    Widget treeExplorerWidget;
    @Mock
    IsWidget previewWidget;
    @Mock
    AbstractClientSession session;
    @Mock
    AbstractCanvasHandler canvasHandler;
    @Mock
    Diagram diagram;
    @Mock
    Metadata metadata;
    @Mock
    ErrorPopupPresenter errorPopupPresenter;
    @Mock
    ProjectDiagramExplorerScreen.View view;

    private ProjectDiagramExplorerScreen tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(clientSessionManager.getCurrentSession()).thenReturn(session);
        when(sessionPresenterFactory.newPreview()).thenReturn(sessionPreview);
        when(sessionPreview.getView()).thenReturn(previewWidget);
        when(sessionPreview.getInstance()).thenReturn(session);
        when(treeExplorer.asWidget()).thenReturn(treeExplorerWidget);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getTitle()).thenReturn("Diagram title");
        this.tested = new ProjectDiagramExplorerScreen(clientSessionManager,
                                                       treeExplorer,
                                                       changeTitleNotificationEvent,
                                                       sessionPresenterFactory,
                                                       errorPopupPresenter,
                                                       view);
    }

    @Test
    public void testInit() {
        tested.init();
        verify(view,
               times(0)).setPreviewWidget(any(IsWidget.class));
        verify(view,
               times(1)).setExplorerWidget(eq(treeExplorerWidget));
    }

    @Test
    public void testView() {
        tested.init();
        assertEquals(view,
                     tested.getWidget());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testShow() {
        tested.init();
        tested.show(session);
        verify(sessionPresenterFactory,
               times(1)).newPreview();
        verify(sessionPreview,
               times(1)).open(eq(session),
                              anyInt(),
                              anyInt(),
                              any(SessionViewer.SessionViewerCallback.class));
        verify(treeExplorer,
               times(1)).show(eq(canvasHandler));
        verify(changeTitleNotificationEvent,
               times(1)).fire(any(ChangeTitleWidgetEvent.class));
        verify(treeExplorer,
               times(0)).clear();
        verify(sessionPreview,
               times(0)).clear();
        verify(errorPopupPresenter,
               times(0)).showMessage(anyString());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testClose() {
        tested.init();
        tested.close();
        verify(sessionPreview,
               times(0)).open(any(AbstractClientSession.class),
                              anyInt(),
                              anyInt(),
                              any(SessionViewer.SessionViewerCallback.class));
        verify(sessionPreview,
               times(0)).clear();
        verify(treeExplorer,
               times(1)).clear();
        verify(treeExplorer,
               times(0)).show(any(AbstractCanvasHandler.class));
        verify(errorPopupPresenter,
               times(0)).showMessage(anyString());
    }
}

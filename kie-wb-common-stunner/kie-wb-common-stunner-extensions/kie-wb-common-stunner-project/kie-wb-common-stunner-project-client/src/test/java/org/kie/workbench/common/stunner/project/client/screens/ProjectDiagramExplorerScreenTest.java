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
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorer;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPreview;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionViewer;
import org.kie.workbench.common.stunner.core.client.ManagedInstanceStub;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
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
    private SessionManager clientSessionManager;
    @Mock
    private TreeExplorer treeExplorer;
    private ManagedInstance<TreeExplorer> treeExplorers;
    @Mock
    private SessionDiagramPreview<AbstractSession> sessionPreview;
    private ManagedInstance<SessionDiagramPreview<AbstractSession>> sessionPreviews;
    @Mock
    private EventSourceMock<ChangeTitleWidgetEvent> changeTitleNotificationEvent;
    @Mock
    private Widget treeExplorerWidget;
    @Mock
    private IsWidget previewWidget;
    @Mock
    private AbstractSession session;
    @Mock
    private AbstractCanvasHandler canvasHandler;
    @Mock
    private Diagram diagram;
    @Mock
    private Metadata metadata;
    @Mock
    private ErrorPopupPresenter errorPopupPresenter;
    @Mock
    private ProjectDiagramExplorerScreen.View view;

    private ProjectDiagramExplorerScreen tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(clientSessionManager.getCurrentSession()).thenReturn(session);
        when(sessionPreview.getView()).thenReturn(previewWidget);
        when(sessionPreview.getInstance()).thenReturn(session);
        when(treeExplorer.asWidget()).thenReturn(treeExplorerWidget);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getTitle()).thenReturn("Diagram title");
        treeExplorers = new ManagedInstanceStub<>(treeExplorer);
        sessionPreviews = new ManagedInstanceStub<>(sessionPreview);
        this.tested = new ProjectDiagramExplorerScreen(clientSessionManager,
                                                       treeExplorers,
                                                       changeTitleNotificationEvent,
                                                       sessionPreviews,
                                                       errorPopupPresenter,
                                                       view);
    }

    @Test
    public void testView() {
        assertEquals(view,
                     tested.getWidget());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testShow() {
        tested.show(session);
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
        verify(view,
               times(0)).setPreviewWidget(any(IsWidget.class));
        verify(view,
               times(1)).setExplorerWidget(any(IsWidget.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testClose() {
        tested.show(session);
        tested.close();
        verify(sessionPreview,
               times(1)).destroy();
    }
}

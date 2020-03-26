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

package org.kie.workbench.common.stunner.kogito.client.editor;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerView;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerPresenter;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport;
import org.kie.workbench.common.stunner.core.client.components.layout.LayoutHelper;
import org.kie.workbench.common.stunner.core.client.components.layout.OpenDiagramLayoutExecutor;
import org.kie.workbench.common.stunner.core.client.error.DiagramClientErrorHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.documentation.DocumentationView;
import org.kie.workbench.common.stunner.kogito.client.docks.DiagramEditorPreviewAndExplorerDock;
import org.kie.workbench.common.stunner.kogito.client.docks.DiagramEditorPropertiesDock;
import org.kie.workbench.common.stunner.kogito.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.kogito.client.menus.BPMNStandaloneEditorMenuSessionItems;
import org.kie.workbench.common.stunner.kogito.client.perspectives.AuthoringPerspective;
import org.kie.workbench.common.stunner.kogito.client.service.KogitoClientDiagramService;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.promise.SyncPromises;
import org.uberfire.workbench.events.NotificationEvent;

import static org.jgroups.util.Util.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class BPMNDiagramEditorTest {

    private BPMNDiagramEditor editor;

    @Mock
    private DiagramEditorCore.View view;

    @Mock
    private FileMenuBuilder fileMenuBuilder;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private MultiPageEditorContainerView multiPageEditorContainerView;

    @Mock
    private EventSourceMock<ChangeTitleWidgetEvent> changeTitleNotificationEvent;

    @Mock
    private EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    private EventSourceMock<OnDiagramFocusEvent> onDiagramFocusEvent;

    @Mock
    private TextEditorView xmlEditorView;

    @Mock
    private ManagedInstance<SessionEditorPresenter<EditorSession>> editorSessionPresenterInstances;

    @Mock
    private ManagedInstance<SessionViewerPresenter<ViewerSession>> viewerSessionPresenterInstances;

    @Mock
    private BPMNStandaloneEditorMenuSessionItems menuSessionItems;

    @Mock
    private ErrorPopupPresenter errorPopupPresenter;

    @Mock
    private DiagramClientErrorHandler diagramClientErrorHandler;

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private DocumentationView documentationView;

    @Mock
    private DiagramEditorPreviewAndExplorerDock diagramPreviewAndExplorerDock;

    @Mock
    private DiagramEditorPropertiesDock diagramPropertiesDock;

    @Mock
    private LayoutHelper layoutHelper;

    @Mock
    private OpenDiagramLayoutExecutor openDiagramLayoutExecutor;

    @Mock
    private KogitoClientDiagramService diagramServices;

    @Mock
    private CanvasFileExport canvasFileExport;

    private Promises promises = new SyncPromises();

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        editor = new BPMNDiagramEditor(view,
                                       fileMenuBuilder,
                                       placeManager,
                                       multiPageEditorContainerView,
                                       changeTitleNotificationEvent,
                                       notificationEvent,
                                       onDiagramFocusEvent,
                                       xmlEditorView,
                                       editorSessionPresenterInstances,
                                       viewerSessionPresenterInstances,
                                       menuSessionItems,
                                       errorPopupPresenter,
                                       diagramClientErrorHandler,
                                       translationService,
                                       documentationView,
                                       diagramPreviewAndExplorerDock,
                                       diagramPropertiesDock,
                                       layoutHelper,
                                       openDiagramLayoutExecutor,
                                       diagramServices,
                                       canvasFileExport,
                                       promises);
    }

    @Test
    public void testMenuInitialized() {
        editor.menuBarInitialized = false;
        editor.makeMenuBar();
        assertEquals(editor.menuBarInitialized, true);

        editor.menuBarInitialized = true;
        editor.makeMenuBar();
        assertEquals(editor.menuBarInitialized, true);
    }

    @Test
    public void testSuperOnCloseOnSetContent() {
        //First setContent call context
        editor.setContent("", "");
        verify(menuSessionItems, times(1)).destroy();

        //Second setContent call context
        editor.setContent("", "");
        verify(menuSessionItems, times(2)).destroy();
    }

    @Test
    public void testDocksAndOrdering() {
        editor.initDocks();
        InOrder initOrder = inOrder(diagramPropertiesDock, diagramPreviewAndExplorerDock);
        initOrder.verify(diagramPropertiesDock).init(eq(AuthoringPerspective.PERSPECTIVE_ID));
        initOrder.verify(diagramPreviewAndExplorerDock).init(eq(AuthoringPerspective.PERSPECTIVE_ID));
        editor.openDocks();
        initOrder.verify(diagramPropertiesDock).open();
        initOrder.verify(diagramPreviewAndExplorerDock).open();
        editor.onClose();
        initOrder.verify(diagramPropertiesDock).close();
        initOrder.verify(diagramPreviewAndExplorerDock).close();
    }
}
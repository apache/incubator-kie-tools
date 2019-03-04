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

package org.kie.workbench.common.dmn.showcase.client.screens.editor;

import java.util.List;
import java.util.function.Consumer;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.decision.DecisionNavigatorDock;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPage;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsPageStateProviderImpl;
import org.kie.workbench.common.dmn.client.editors.types.DataTypePageTabActiveEvent;
import org.kie.workbench.common.dmn.client.editors.types.DataTypesPage;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent;
import org.kie.workbench.common.dmn.client.session.DMNEditorSession;
import org.kie.workbench.common.dmn.showcase.client.perspectives.AuthoringPerspective;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.views.session.ScreenPanelView;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.layout.LayoutHelper;
import org.kie.workbench.common.stunner.core.client.components.layout.OpenDiagramLayoutExecutor;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.client.workbench.widgets.multipage.MultiPageEditor;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static java.util.Collections.singletonList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class SessionDiagramEditorScreenTest {

    @Mock
    private DecisionNavigatorDock decisionNavigatorDock;

    @Mock
    private ScreenPanelView screenPanelView;

    @Mock
    private SessionEditorPresenter<EditorSession> presenter;

    @Mock
    private ExpressionEditorView.Presenter expressionEditor;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private DMNEditorSession session;

    @Mock
    private EventSourceMock<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    @Captor
    private ArgumentCaptor<Consumer<EditorSession>> clientFullSessionConsumer;

    @Mock
    private LayoutHelper layoutHelper;

    @Mock
    private OpenDiagramLayoutExecutor layoutExecutor;

    @Mock
    private KieEditorWrapperView kieView;

    @Mock
    private DataTypesPage dataTypesPage;

    @Mock
    private IncludedModelsPage includedModelsPage;

    @Mock
    private IncludedModelsPageStateProviderImpl importsPageProvider;

    private SessionDiagramEditorScreen editor;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        doReturn(presenter).when(presenter).withToolbar(anyBoolean());
        doReturn(presenter).when(presenter).withPalette(anyBoolean());
        doReturn(presenter).when(presenter).displayNotifications(any());
        doReturn(session).when(presenter).getInstance();
        doReturn(session).when(sessionManager).getCurrentSession();
        doReturn(expressionEditor).when(session).getExpressionEditor();
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Diagram diagram = (Diagram) invocation.getArguments()[0];
                SessionPresenter.SessionPresenterCallback callback = (SessionPresenter.SessionPresenterCallback) invocation.getArguments()[1];
                callback.onOpen(diagram);
                callback.afterCanvasInitialized();
                callback.afterSessionOpened();
                callback.onSuccess();
                return null;
            }
        }).when(presenter).open(any(Diagram.class),
                                any(SessionPresenter.SessionPresenterCallback.class));

        editor = spy(new SessionDiagramEditorScreen(null,
                                                    null,
                                                    null,
                                                    sessionManager,
                                                    null,
                                                    presenter,
                                                    null,
                                                    null,
                                                    refreshFormPropertiesEvent,
                                                    null,
                                                    screenPanelView,
                                                    null,
                                                    decisionNavigatorDock,
                                                    layoutHelper,
                                                    kieView,
                                                    dataTypesPage,
                                                    layoutExecutor,
                                                    includedModelsPage, importsPageProvider));
    }

    @Test
    public void testInit() {

        final Widget screenPanelWidget = mock(Widget.class);
        final MultiPageEditor multiPageEditor = mock(MultiPageEditor.class);

        when(kieView.getMultiPage()).thenReturn(multiPageEditor);
        when(screenPanelView.asWidget()).thenReturn(screenPanelWidget);

        editor.init();

        verify(decisionNavigatorDock).init(AuthoringPerspective.PERSPECTIVE_ID);
        verify(kieView).setPresenter(editor);
        verify(kieView).clear();
        verify(kieView).addMainEditorPage(screenPanelWidget);
        verify(multiPageEditor).addPage(dataTypesPage);
        verify(multiPageEditor).addPage(includedModelsPage);
    }

    @Test
    public void testOpenDiagram() {

        final Diagram diagram = mock(Diagram.class);
        final Command callback = mock(Command.class);
        final Metadata metadata = mock(Metadata.class);
        final AbstractCanvasHandler canvasHandler = mock(AbstractCanvasHandler.class);

        when(importsPageProvider.withDiagram(diagram)).thenReturn(importsPageProvider);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(diagram.getMetadata()).thenReturn(metadata);

        editor.open(diagram, callback);

        final InOrder inOrder = inOrder(decisionNavigatorDock);
        inOrder.verify(decisionNavigatorDock).setupCanvasHandler(canvasHandler);
        inOrder.verify(decisionNavigatorDock).open();

        verify(dataTypesPage).reload();
        verify(dataTypesPage).enableShortcuts();
        verify(includedModelsPage).setup(importsPageProvider);
    }

    @Test
    public void testOnClose() {

        doNothing().when(editor).destroyDock();
        doNothing().when(editor).destroySession();

        editor.onClose();

        verify(editor).destroyDock();
        verify(editor).destroySession();
        verify(dataTypesPage).disableShortcuts();
    }

    @Test
    public void testOpenDock() {

        final EditorSession session = mock(EditorSession.class);
        final AbstractCanvasHandler canvasHandler = mock(AbstractCanvasHandler.class);

        when(session.getCanvasHandler()).thenReturn(canvasHandler);

        editor.openDock();

        verify(decisionNavigatorDock).open();
    }

    @Test
    public void testDestroyDock() {

        editor.destroyDock();

        verify(decisionNavigatorDock).close();
        verify(decisionNavigatorDock).resetContent();
    }

    @Test
    public void testOnDataTypePageNavTabActiveEvent() {

        final MultiPageEditor multiPageEditor = mock(MultiPageEditor.class);

        when(kieView.getMultiPage()).thenReturn(multiPageEditor);

        editor.onDataTypePageNavTabActiveEvent(mock(DataTypePageTabActiveEvent.class));

        verify(multiPageEditor).selectPage(1);
    }

    @Test
    public void testOnDataTypeEditModeToggleWhenEditModeIsEnabled() {

        final DataTypeEditModeToggleEvent event = mock(DataTypeEditModeToggleEvent.class);
        final MenuItem menuItem = mock(MenuItem.class);
        final Menus menus = mock(Menus.class);
        final List<MenuItem> items = singletonList(menuItem);

        when(menus.getItems()).thenReturn(items);
        when(event.isEditModeEnabled()).thenReturn(true);
        doReturn(menus).when(editor).getMenu();

        editor.onDataTypeEditModeToggle(event);

        verify(menuItem).setEnabled(false);
    }

    @Test
    public void testOnDataTypeEditModeToggleWhenEditModeIsNotEnabled() {

        final DataTypeEditModeToggleEvent event = mock(DataTypeEditModeToggleEvent.class);
        final MenuItem menuItem = mock(MenuItem.class);
        final Menus menus = mock(Menus.class);
        final List<MenuItem> items = singletonList(menuItem);

        when(menus.getItems()).thenReturn(items);
        when(event.isEditModeEnabled()).thenReturn(false);
        doReturn(menus).when(editor).getMenu();

        editor.onDataTypeEditModeToggle(event);

        verify(menuItem).setEnabled(true);
    }
}

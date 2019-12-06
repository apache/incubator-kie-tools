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
package org.kie.workbench.common.dmn.webapp.kogito.common.client.editor;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.client.commands.general.NavigateToExpressionEditorCommand;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorDock;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.dmn.client.editors.search.DMNEditorSearchIndex;
import org.kie.workbench.common.dmn.client.editors.search.DMNSearchableElement;
import org.kie.workbench.common.dmn.client.editors.types.DataTypePageTabActiveEvent;
import org.kie.workbench.common.dmn.client.editors.types.DataTypesPage;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.session.DMNEditorSession;
import org.kie.workbench.common.dmn.webapp.common.client.docks.preview.PreviewDiagramDock;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerView;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerPresenter;
import org.kie.workbench.common.stunner.core.client.ManagedInstanceStub;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.components.layout.LayoutHelper;
import org.kie.workbench.common.stunner.core.client.components.layout.OpenDiagramLayoutExecutor;
import org.kie.workbench.common.stunner.core.client.error.DiagramClientErrorHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.documentation.DocumentationView;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.kie.workbench.common.stunner.kogito.api.editor.KogitoDiagramResource;
import org.kie.workbench.common.stunner.kogito.api.editor.impl.KogitoDiagramResourceImpl;
import org.kie.workbench.common.stunner.kogito.client.docks.DiagramEditorPropertiesDock;
import org.kie.workbench.common.stunner.kogito.client.editor.DiagramEditorCore;
import org.kie.workbench.common.stunner.kogito.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.kogito.client.service.KogitoClientDiagramService;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.search.component.SearchBarComponent;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorSelectedPageEvent;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.client.workbench.widgets.multipage.MultiPageEditor;
import org.uberfire.ext.editor.commons.client.menu.MenuItems;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.AbstractDMNDiagramEditor.DATA_TYPES_PAGE_INDEX;
import static org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.AbstractDMNDiagramEditor.PERSPECTIVE_ID;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public abstract class AbstractDMNDiagramEditorTest {

    protected static final String CONTENT = "content";

    protected static final String ROOT = "default://master@system/stunner/diagrams";

    @Mock
    protected AbstractCanvasHandler canvasHandler;

    @Mock
    protected DiagramEditorCore.View view;

    @Mock
    protected FileMenuBuilder fileMenuBuilder;

    @Mock
    protected PlaceManager placeManager;

    @Mock
    protected MultiPageEditorContainerView multiPageEditorContainerView;

    @Mock
    protected MultiPageEditor multiPageEditor;

    @Mock
    protected EventSourceMock<ChangeTitleWidgetEvent> changeTitleWidgetEventSourceMock;

    @Mock
    protected EventSourceMock<NotificationEvent> notificationEventSourceMock;

    @Mock
    protected EventSourceMock<OnDiagramFocusEvent> onDiagramFocusEventSourceMock;

    @Mock
    protected TextEditorView xmlEditorView;

    protected ManagedInstanceStub<SessionEditorPresenter<EditorSession>> sessionEditorPresenters;

    protected ManagedInstanceStub<SessionViewerPresenter<ViewerSession>> sessionViewerPresenters;

    @Mock
    protected DMNEditorMenuSessionItems dmnEditorMenuSessionItems;

    @Mock
    protected ErrorPopupPresenter errorPopupPresenter;

    @Mock
    protected DiagramClientErrorHandler diagramClientErrorHandler;

    @Mock
    protected ClientTranslationService clientTranslationService;

    @Mock
    protected DocumentationView<Diagram> documentationView;

    @Mock
    protected DMNEditorSearchIndex editorSearchIndex;

    @Mock
    protected SearchBarComponent<DMNSearchableElement> searchBarComponent;

    @Mock
    protected SessionManager sessionManager;

    @Mock
    protected SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    protected EventSourceMock<RefreshFormPropertiesEvent> refreshFormPropertiesEventSourceMock;

    @Mock
    protected DecisionNavigatorDock decisionNavigatorDock;

    @Mock
    protected DiagramEditorPropertiesDock diagramPropertiesDock;

    @Mock
    protected PreviewDiagramDock diagramPreviewDock;

    @Mock
    protected LayoutHelper layoutHelper;

    @Mock
    protected OpenDiagramLayoutExecutor layoutExecutor;

    @Mock
    protected DataTypesPage dataTypesPage;

    @Mock
    protected KogitoClientDiagramService clientDiagramService;

    @Mock
    protected SessionEditorPresenter<EditorSession> editorPresenter;

    @Mock
    protected SessionViewerPresenter<ViewerSession> viewerPresenter;

    @Mock
    protected ExpressionEditorView.Presenter expressionEditor;

    @Mock
    protected DMNEditorSession session;

    @Mock
    protected SearchBarComponent.View searchBarComponentView;

    @Mock
    protected HTMLElement searchBarComponentViewElement;

    @Mock
    protected ElementWrapperWidget searchBarComponentWidget;

    @Mock
    protected DataTypePageTabActiveEvent dataTypePageTabActiveEvent;

    @Mock
    protected DataTypeEditModeToggleEvent dataTypeEditModeToggleEvent;

    @Mock
    protected EditExpressionEvent editExpressionEvent;

    @Mock
    protected MultiPageEditorSelectedPageEvent multiPageEditorSelectedPageEvent;

    @Mock
    protected RefreshFormPropertiesEvent refreshFormPropertiesEvent;

    @Mock
    protected Path root;

    @Mock
    protected ClientRuntimeError clientRuntimeError;

    @Captor
    protected ArgumentCaptor<KogitoDiagramResourceImpl> kogitoDiagramResourceArgumentCaptor;

    @Captor
    protected ArgumentCaptor<ServiceCallback<Diagram>> serviceCallbackArgumentCaptor;

    protected Diagram diagram;

    protected Metadata metadata;

    protected AbstractDMNDiagramEditor editor;

    protected PlaceRequest place = new DefaultPlaceRequest();

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        metadata = new MetadataImpl.MetadataImplBuilder(DMNDefinitionSet.class.getName()).setTitle("dmn").setRoot(root).build();
        diagram = new DiagramImpl("dmn", metadata);

        sessionEditorPresenters = new ManagedInstanceStub<>(editorPresenter);
        sessionViewerPresenters = new ManagedInstanceStub<>(viewerPresenter);
        when(searchBarComponent.getView()).thenReturn(searchBarComponentView);
        when(searchBarComponentView.getElement()).thenReturn(searchBarComponentViewElement);
        when(multiPageEditorContainerView.getMultiPage()).thenReturn(multiPageEditor);

        when(editorPresenter.withToolbar(anyBoolean())).thenReturn(editorPresenter);
        when(editorPresenter.withPalette(anyBoolean())).thenReturn(editorPresenter);
        when(editorPresenter.displayNotifications(any())).thenReturn(editorPresenter);
        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getExpressionEditor()).thenReturn(expressionEditor);

        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(editorPresenter.getInstance()).thenReturn(session);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(root.toURI()).thenReturn(ROOT);

        doAnswer((invocation) -> {
            final Diagram diagram = (Diagram) invocation.getArguments()[0];
            final SessionPresenter.SessionPresenterCallback callback = (SessionPresenter.SessionPresenterCallback) invocation.getArguments()[1];
            callback.onOpen(diagram);
            callback.afterCanvasInitialized();
            callback.afterSessionOpened();
            callback.onSuccess();
            return null;
        }).when(editorPresenter).open(any(Diagram.class),
                                      any(SessionPresenter.SessionPresenterCallback.class));

        editor = spy(getEditor());
    }

    protected abstract AbstractDMNDiagramEditor getEditor();

    @Test
    public void testOnStartup() {
        editor.onStartup(place);

        verify(decisionNavigatorDock).init(PERSPECTIVE_ID);
        verify(diagramPreviewDock).init(PERSPECTIVE_ID);
        verify(diagramPropertiesDock).init(PERSPECTIVE_ID);

        verify(dmnEditorMenuSessionItems).populateMenu(fileMenuBuilder);
        verify(fileMenuBuilder).build();

        verify(multiPageEditorContainerView).init(eq(editor));
    }

    @Test
    public void testOnDataTypePageNavTabActiveEvent() {
        editor.onDataTypePageNavTabActiveEvent(dataTypePageTabActiveEvent);

        verify(multiPageEditor).selectPage(DATA_TYPES_PAGE_INDEX);
    }

    @Test
    public void testOnDataTypeEditModeToggleEnabled() {
        openDiagram();

        when(dataTypeEditModeToggleEvent.isEditModeEnabled()).thenReturn(true);

        editor.onDataTypeEditModeToggle(dataTypeEditModeToggleEvent);

        verify(editor).disableMenuItem(eq(MenuItems.SAVE));
    }

    @Test
    public void testOnDataTypeEditModeToggleDisabled() {
        openDiagram();

        when(dataTypeEditModeToggleEvent.isEditModeEnabled()).thenReturn(false);

        editor.onDataTypeEditModeToggle(dataTypeEditModeToggleEvent);

        verify(editor).enableMenuItem(eq(MenuItems.SAVE));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnEditExpressionEventWhenSameSession() {
        when(editExpressionEvent.getSession()).thenReturn(session);

        openDiagram();

        when(editorPresenter.getInstance()).thenReturn(session);

        editor.onEditExpressionEvent(editExpressionEvent);

        verify(searchBarComponent).disableSearch();
        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              any(NavigateToExpressionEditorCommand.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnEditExpressionEventWhenDifferentSession() {
        editor.onEditExpressionEvent(editExpressionEvent);

        verify(searchBarComponent).disableSearch();
        verify(sessionCommandManager, never()).execute(any(AbstractCanvasHandler.class),
                                                       any(Command.class));
    }

    @Test
    public void testOnMultiPageEditorSelectedPageEvent() {
        editor.onMultiPageEditorSelectedPageEvent(multiPageEditorSelectedPageEvent);

        verify(searchBarComponent).disableSearch();
    }

    @Test
    public void testOnRefreshFormPropertiesEvent() {
        editor.onRefreshFormPropertiesEvent(refreshFormPropertiesEvent);

        verify(searchBarComponent).disableSearch();
    }

    @Test
    public void testOnClose() {
        openDiagram();

        editor.onClose();

        verify(dmnEditorMenuSessionItems).destroy();
        verify(editorPresenter).destroy();

        verify(decisionNavigatorDock).destroy();
        verify(decisionNavigatorDock).resetContent();

        verify(diagramPropertiesDock).destroy();
        verify(diagramPreviewDock).destroy();

        verify(dataTypesPage).disableShortcuts();
    }

    @Test
    public void testOnFocus() {
        openDiagram();

        //Setting focus activates the diagram identically to opening a diagram; so reset applicable mocks.
        reset(decisionNavigatorDock, diagramPropertiesDock, diagramPreviewDock, dataTypesPage);

        editor.onFocus();

        verify(editorPresenter).focus();
        verify(dataTypesPage).onFocus();
        verify(dataTypesPage).enableShortcuts();

        assertOnDiagramLoad();
    }

    @Test
    public void testOnLostFocus() {
        openDiagram();

        editor.onLostFocus();

        verify(editorPresenter).lostFocus();
        verify(dataTypesPage).onLostFocus();
    }

    @Test
    public void testGetContent() {
        openDiagram();

        editor.getContent();

        verify(clientDiagramService).transform(kogitoDiagramResourceArgumentCaptor.capture());

        final KogitoDiagramResource resource = kogitoDiagramResourceArgumentCaptor.getValue();
        assertThat(resource).isNotNull();
        assertThat(resource.projectDiagram().isPresent()).isTrue();
        assertThat(resource.projectDiagram().get()).isEqualTo(diagram);
    }

    @Test
    public void testSetContentSuccess() {
        editor.setContent(CONTENT);

        verify(clientDiagramService).transform(eq(CONTENT), serviceCallbackArgumentCaptor.capture());

        final ServiceCallback<Diagram> serviceCallback = serviceCallbackArgumentCaptor.getValue();
        assertThat(serviceCallback).isNotNull();
        serviceCallback.onSuccess(diagram);

        assertOnDiagramLoad();
    }

    @Test
    public void testSetContentFailure() {
        editor.setContent(CONTENT);

        verify(clientDiagramService).transform(eq(CONTENT), serviceCallbackArgumentCaptor.capture());

        final ServiceCallback<Diagram> serviceCallback = serviceCallbackArgumentCaptor.getValue();
        assertThat(serviceCallback).isNotNull();
        serviceCallback.onError(clientRuntimeError);

        verify(diagramClientErrorHandler).handleError(eq(clientRuntimeError), any());
    }

    @Test
    public void testResetContentHash() {
        openDiagram();

        editor.setOriginalContentHash(diagram.hashCode() + 1);

        assertThat(editor.isDirty()).isTrue();

        editor.resetContentHash();

        assertThat(editor.isDirty()).isFalse();
    }

    protected void openDiagram() {
        editor.onStartup(place);

        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(editorPresenter.getInstance()).thenReturn(session);
        when(canvasHandler.getDiagram()).thenReturn(diagram);

        editor.open(diagram);

        assertOnDiagramLoad();
    }

    protected void assertOnDiagramLoad() {
        verify(decisionNavigatorDock).setupCanvasHandler(canvasHandler);
        verify(dataTypesPage).reload();
    }
}

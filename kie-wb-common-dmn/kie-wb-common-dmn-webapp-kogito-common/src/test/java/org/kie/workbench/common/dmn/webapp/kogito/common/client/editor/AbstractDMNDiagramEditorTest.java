/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import org.kie.workbench.common.dmn.client.docks.navigator.common.LazyCanvasFocusUtils;
import org.kie.workbench.common.dmn.client.editors.drd.DRDNameChanger;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPage;
import org.kie.workbench.common.dmn.client.editors.included.common.IncludedModelsContext;
import org.kie.workbench.common.dmn.client.editors.search.DMNEditorSearchIndex;
import org.kie.workbench.common.dmn.client.editors.search.DMNSearchableElement;
import org.kie.workbench.common.dmn.client.editors.types.DataTypePageTabActiveEvent;
import org.kie.workbench.common.dmn.client.editors.types.DataTypesPage;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.session.DMNEditorSession;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoFEELInitializer;
import org.kie.workbench.common.dmn.webapp.common.client.docks.preview.PreviewDiagramDock;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.session.DMNEditorSessionCommands;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.GuidedTourBridgeInitializer;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerView;
import org.kie.workbench.common.stunner.client.widgets.editor.StunnerEditor;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.components.layout.LayoutHelper;
import org.kie.workbench.common.stunner.core.client.components.layout.OpenDiagramLayoutExecutor;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.documentation.DocumentationView;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.kie.workbench.common.stunner.kogito.client.docks.DiagramEditorPropertiesDock;
import org.kie.workbench.common.stunner.kogito.client.service.KogitoClientDiagramService;
import org.kie.workbench.common.widgets.client.search.component.SearchBarComponent;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorSelectedPageEvent;
import org.uberfire.client.workbench.widgets.multipage.MultiPageEditor;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.AbstractDMNDiagramEditor.DATA_TYPES_PAGE_INDEX;
import static org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.AbstractDMNDiagramEditor.PERSPECTIVE_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public abstract class AbstractDMNDiagramEditorTest {

    protected static final String CONTENT = "content";

    protected static final String ROOT = "default://master@system/stunner/diagrams";

    @Mock
    protected AbstractCanvasHandler canvasHandler;

    @Mock
    protected PlaceManager placeManager;

    @Mock
    protected MultiPageEditorContainerView multiPageEditorContainerView;

    @Mock
    protected MultiPageEditor multiPageEditor;

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
    protected DMNEditorSessionCommands sessionCommands;

    @Mock
    protected LayoutHelper layoutHelper;

    @Mock
    protected OpenDiagramLayoutExecutor layoutExecutor;

    @Mock
    protected DataTypesPage dataTypesPage;

    @Mock
    protected KogitoClientDiagramService clientDiagramService;

    @Mock
    protected StunnerEditor stunnerEditor;

    @Mock
    protected AbstractDMNDiagramEditor.View view;

    @Mock
    protected SessionDiagramPresenter presenter;

    @Mock
    protected SessionPresenter.View presenterView;

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

    @Mock
    protected MonacoFEELInitializer feelInitializer;

    @Mock
    protected GuidedTourBridgeInitializer guidedTourBridgeInitializer;

    @Mock
    protected CanvasFileExport canvasFileExport;

    @Mock
    protected IncludedModelsPage includedModelsPage;

    @Mock
    protected IncludedModelsContext includedModelContext;

    @Mock
    protected ReadOnlyProvider readonlyProvider;

    @Mock
    protected DRDNameChanger drdNameChanger;

    @Mock
    protected LazyCanvasFocusUtils lazyCanvasFocusUtils;

    @Mock
    private HTMLElement drdNameChangerElement;

    @Mock
    private ElementWrapperWidget drdNameWidget;

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

        when(searchBarComponent.getView()).thenReturn(searchBarComponentView);
        when(searchBarComponentView.getElement()).thenReturn(searchBarComponentViewElement);
        when(multiPageEditorContainerView.getMultiPage()).thenReturn(multiPageEditor);

        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getExpressionEditor()).thenReturn(expressionEditor);

        when(presenter.getView()).thenReturn(presenterView);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(stunnerEditor.getSession()).thenReturn(session);
        when(stunnerEditor.getPresenter()).thenReturn(presenter);
        when(stunnerEditor.getView()).thenReturn(view);
        when(stunnerEditor.getCanvasHandler()).thenReturn(canvasHandler);
        when(stunnerEditor.getDiagram()).thenReturn(diagram);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(root.toURI()).thenReturn(ROOT);

        when(drdNameChanger.getElement()).thenReturn(drdNameChangerElement);

        doAnswer((invocation) -> {
            final Diagram diagram = (Diagram) invocation.getArguments()[0];
            final SessionPresenter.SessionPresenterCallback callback = (SessionPresenter.SessionPresenterCallback) invocation.getArguments()[1];
            callback.onOpen(diagram);
            callback.afterCanvasInitialized();
            callback.afterSessionOpened();
            callback.onSuccess();
            return null;
        }).when(stunnerEditor).open(any(Diagram.class),
                                    any(SessionPresenter.SessionPresenterCallback.class));

        editor = spy(getEditor());

        doReturn(drdNameWidget).when(editor).getWidget(drdNameChangerElement);
    }

    protected abstract AbstractDMNDiagramEditor getEditor();

    @Test
    public void testOnStartup() {
        editor.onStartup(place);
        verify(stunnerEditor).setReadOnly(eq(false));
        verify(decisionNavigatorDock).init(PERSPECTIVE_ID);
        verify(diagramPreviewDock).init(PERSPECTIVE_ID);
        verify(diagramPropertiesDock).init(PERSPECTIVE_ID);
        verify(multiPageEditorContainerView).init(eq(editor));
        verify(guidedTourBridgeInitializer).init();
    }

    @Test
    public void testOnDataTypePageNavTabActiveEvent() {
        editor.onDataTypePageNavTabActiveEvent(dataTypePageTabActiveEvent);
        verify(multiPageEditor).selectPage(DATA_TYPES_PAGE_INDEX);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnEditExpressionEventWhenSameSession() {
        when(editExpressionEvent.getSession()).thenReturn(session);
        openDiagram();
        when(stunnerEditor.getSession()).thenReturn(session);
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
        verify(stunnerEditor, atLeastOnce()).close();
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
        verify(stunnerEditor).focus();
        verify(dataTypesPage).onFocus();
        verify(dataTypesPage).enableShortcuts();
    }

    @Test
    public void testOnLostFocus() {
        openDiagram();
        editor.onLostFocus();
        verify(stunnerEditor).lostFocus();
        verify(dataTypesPage).onLostFocus();
    }

    @Test
    public void testGetContent() {
        openDiagram();
        editor.getContent();
        verify(clientDiagramService).transform(eq(diagram));
    }

    @Test
    public void testSetContentSuccess() {
        final String path = "path";
        editor.setContent(path, CONTENT);

        verify(clientDiagramService).transform(eq(path), eq(CONTENT), serviceCallbackArgumentCaptor.capture());

        final ServiceCallback<Diagram> serviceCallback = serviceCallbackArgumentCaptor.getValue();
        assertThat(serviceCallback).isNotNull();
        serviceCallback.onSuccess(diagram);

        assertOnDiagramLoad();
    }

    @Test
    public void testSetContentFailure() {
        final String path = "path";
        editor.setContent(path, CONTENT);
        verify(clientDiagramService).transform(eq(path), eq(CONTENT), serviceCallbackArgumentCaptor.capture());
        final ServiceCallback<Diagram> serviceCallback = serviceCallbackArgumentCaptor.getValue();
        assertThat(serviceCallback).isNotNull();
        serviceCallback.onError(clientRuntimeError);
        verify(feelInitializer, never()).initializeFEELEditor();
        verify(multiPageEditorContainerView, times(1)).clear();
        verify(multiPageEditorContainerView, times(1)).setEditorWidget(eq(view));
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
        when(stunnerEditor.getSession()).thenReturn(session);
        when(canvasHandler.getDiagram()).thenReturn(diagram);

        editor.open(diagram, mock(SessionPresenter.SessionPresenterCallback.class));

        assertOnDiagramLoad();
    }

    protected void assertOnDiagramLoad() {
        verify(decisionNavigatorDock).reload();
        verify(layoutHelper).applyLayout(eq(diagram), eq(layoutExecutor));
        verify(feelInitializer).initializeFEELEditor();
        verify(dataTypesPage).reload();
        verify(lazyCanvasFocusUtils, atLeastOnce()).releaseFocus();
    }

    @Test
    public void testReleaseOnSetContent() {
        editor.setContent("", "");
        verify(stunnerEditor, times(1)).close();
    }
}

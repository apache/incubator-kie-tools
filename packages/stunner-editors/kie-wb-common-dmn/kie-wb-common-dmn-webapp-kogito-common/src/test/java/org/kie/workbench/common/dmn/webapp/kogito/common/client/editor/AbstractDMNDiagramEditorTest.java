/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.common.KogitoChannelHelper;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorDock;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorPresenter;
import org.kie.workbench.common.dmn.client.editors.drd.DRDNameChanger;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPage;
import org.kie.workbench.common.dmn.client.editors.search.DMNEditorSearchIndex;
import org.kie.workbench.common.dmn.client.editors.search.DMNSearchableElement;
import org.kie.workbench.common.dmn.client.editors.types.DataTypesPage;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoFEELInitializer;
import org.kie.workbench.common.dmn.webapp.common.client.docks.preview.PreviewDiagramDock;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.GuidedTourBridgeInitializer;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerView;
import org.kie.workbench.common.stunner.client.widgets.editor.StunnerEditor;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.ConfirmationDialog;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.components.layout.LayoutHelper;
import org.kie.workbench.common.stunner.core.client.components.layout.OpenDiagramLayoutExecutor;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.documentation.DocumentationView;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.kie.workbench.common.stunner.kogito.client.docks.DiagramEditorPropertiesDock;
import org.kie.workbench.common.stunner.kogito.client.service.KogitoClientDiagramService;
import org.kie.workbench.common.widgets.client.search.component.SearchBarComponent;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.client.promise.Promises;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractDMNDiagramEditorTest {

    @Mock
    private AbstractDMNDiagramEditor.View view;

    @Mock
    private MultiPageEditorContainerView containerView;

    @Mock
    private StunnerEditor stunnerEditor;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private DocumentationView documentationView;

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private EventSourceMock<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    @Mock
    private DecisionNavigatorDock decisionNavigatorDock;

    @Mock
    private DiagramEditorPropertiesDock diagramPropertiesDock;

    @Mock
    private PreviewDiagramDock diagramPreviewAndExplorerDock;

    @Mock
    private LayoutHelper layoutHelper;

    @Mock
    private OpenDiagramLayoutExecutor openDiagramLayoutExecutor;

    @Mock
    private DataTypesPage dataTypesPage;

    @Mock
    private DMNEditorSearchIndex editorSearchIndex;

    @Mock
    private SearchBarComponent<DMNSearchableElement> searchBarComponent;

    @Mock
    private KogitoClientDiagramService diagramServices;

    @Mock
    private MonacoFEELInitializer feelInitializer;

    @Mock
    private CanvasFileExport canvasFileExport;

    @Mock
    private Promises promises;

    @Mock
    private IncludedModelsPage includedModelsPage;

    @Mock
    private KogitoChannelHelper kogitoChannelHelper;

    @Mock
    private GuidedTourBridgeInitializer guidedTourBridgeInitializer;

    @Mock
    private DRDNameChanger drdNameChanger;

    @Mock
    private ConfirmationDialog confirmationDialog;

    @Mock
    private DecisionNavigatorPresenter decisionNavigatorPresenter;

    private AbstractDMNDiagramEditor editor;

    @Before
    public void setup() {
        editor = spy(new AbstractDMNDiagramEditorMock(view,
                                                      containerView,
                                                      stunnerEditor,
                                                      editorSearchIndex,
                                                      searchBarComponent,
                                                      sessionManager,
                                                      sessionCommandManager,
                                                      documentationView,
                                                      translationService,
                                                      refreshFormPropertiesEvent,
                                                      decisionNavigatorDock,
                                                      diagramPropertiesDock,
                                                      diagramPreviewAndExplorerDock,
                                                      layoutHelper,
                                                      openDiagramLayoutExecutor,
                                                      dataTypesPage,
                                                      diagramServices,
                                                      feelInitializer,
                                                      canvasFileExport,
                                                      promises,
                                                      includedModelsPage,
                                                      kogitoChannelHelper,
                                                      guidedTourBridgeInitializer,
                                                      drdNameChanger,
                                                      confirmationDialog,
                                                      decisionNavigatorPresenter));
    }

    @Test
    public void testOpen_WhenItHaveLayoutInformation() {

        final Diagram diagram = mock(Diagram.class);
        final SessionPresenter.SessionPresenterCallback callback = mock(SessionPresenter.SessionPresenterCallback.class);

        when(layoutHelper.hasLayoutInformation(diagram)).thenReturn(true);
        doNothing().when(editor).executeOpen(diagram, callback);

        editor.open(diagram, callback);

        verify(feelInitializer).initializeFEELEditor();
        verify(editor, never()).showAutomaticLayoutDialog(diagram, callback);
        verify(editor).executeOpen(diagram, callback);
    }

    @Test
    public void testOpen_WhenItDoesNotHaveLayoutInformation() {

        final Diagram diagram = mock(Diagram.class);
        final SessionPresenter.SessionPresenterCallback callback = mock(SessionPresenter.SessionPresenterCallback.class);

        when(layoutHelper.hasLayoutInformation(diagram)).thenReturn(false);
        doNothing().when(editor).executeOpen(diagram, callback);

        editor.open(diagram, callback);

        verify(feelInitializer).initializeFEELEditor();
        verify(editor).showAutomaticLayoutDialog(diagram, callback);
        verify(editor, never()).executeOpen(diagram, callback);
    }

    @Test
    public void testExecuteOpen_WhenEditorIsNotClosed() {

        final Diagram diagram = mock(Diagram.class);
        final SessionPresenter.SessionPresenterCallback callback = mock(SessionPresenter.SessionPresenterCallback.class);
        final AbstractSession currentSession = mock(AbstractSession.class);
        final SessionPresenter.SessionPresenterCallback expectedSessionPresenterCallback = mock(SessionPresenter.SessionPresenterCallback.class);

        doReturn(expectedSessionPresenterCallback).when(editor).getSessionPresenterCallback(diagram,
                                                                                            callback,
                                                                                            currentSession);
        when(stunnerEditor.isClosed()).thenReturn(false);
        when(stunnerEditor.getSession()).thenReturn(currentSession);

        editor.executeOpen(diagram, callback);

        verify(decisionNavigatorPresenter).setIsRefreshComponentsViewSuspended(true);
        verify(stunnerEditor).open(diagram,
                                   expectedSessionPresenterCallback);
    }

    @Test
    public void testExecuteOpen_WhenEditorIsClosed() {

        final Diagram diagram = mock(Diagram.class);
        final SessionPresenter.SessionPresenterCallback callback = mock(SessionPresenter.SessionPresenterCallback.class);
        final SessionPresenter.SessionPresenterCallback expectedSessionPresenterCallback = mock(SessionPresenter.SessionPresenterCallback.class);

        doReturn(expectedSessionPresenterCallback).when(editor).getSessionPresenterCallback(diagram,
                                                                                            callback,
                                                                                            null);
        when(stunnerEditor.isClosed()).thenReturn(true);

        editor.executeOpen(diagram, callback);

        verify(stunnerEditor).open(diagram,
                                   expectedSessionPresenterCallback);
    }

    @Test
    public void testShowAutomaticLayoutDialog() {

        final Diagram diagram = mock(Diagram.class);
        final SessionPresenter.SessionPresenterCallback callback = mock(SessionPresenter.SessionPresenterCallback.class);
        final ArgumentCaptor<Command> captor = ArgumentCaptor.forClass(Command.class);
        final String automaticLayoutLabel = "LABEL";
        final String text = "TEXT";

        when(translationService.getValue(DMNEditorConstants.AutomaticLayout_Label)).thenReturn(automaticLayoutLabel);
        when(translationService.getValue(DMNEditorConstants.AutomaticLayout_DiagramDoesNotHaveLayout)).thenReturn(text);

        editor.showAutomaticLayoutDialog(diagram, callback);

        verify(confirmationDialog).show(eq(automaticLayoutLabel),
                                        isNull(),
                                        eq(text),
                                        captor.capture(),
                                        captor.capture());

        final List<Command> commands = captor.getAllValues();

        assertEquals(2, commands.size());

        final Command yesCommand = commands.get(0);
        final Command noCommand = commands.get(1);

        yesCommand.execute();

        verify(layoutHelper).applyLayout(diagram, openDiagramLayoutExecutor);
        verify(editor).executeOpen(diagram, callback);

        reset(layoutHelper, editor);

        noCommand.execute();

        verify(layoutHelper, never()).applyLayout(diagram, openDiagramLayoutExecutor);
        verify(editor).executeOpen(diagram, callback);
    }

    @Test
    public void testGetSessionPresenterCallback() {

        final Diagram diagram = mock(Diagram.class);
        final SessionPresenter.SessionPresenterCallback callback = mock(SessionPresenter.SessionPresenterCallback.class);
        final AbstractSession currentSession = mock(AbstractSession.class);
        final ClientRuntimeError clientRuntimeError = mock(ClientRuntimeError.class);

        final SessionPresenter.SessionPresenterCallback actualCallback = editor.getSessionPresenterCallback(diagram,
                                                                                                            callback,
                                                                                                            currentSession);

        final InOrder inOrder = Mockito.inOrder(decisionNavigatorPresenter);
        actualCallback.afterCanvasInitialized();
        verifyAfterCanvasInitialized(callback, diagram, currentSession);
        reset(editor, callback, currentSession);

        actualCallback.onOpen(diagram);
        verifyOnOpen(callback, diagram, currentSession);
        reset(editor, callback, currentSession);

        actualCallback.afterSessionOpened();
        verifyAfterSessionOpened(callback, diagram, currentSession);
        reset(editor, callback, currentSession);

        doNothing().when(editor).initialiseKieEditorForSession(diagram);
        doNothing().when(editor).setupSessionHeaderContainer();
        actualCallback.onSuccess();
        verifyOnSuccess(callback, diagram, currentSession, inOrder);
        reset(editor, callback, currentSession, decisionNavigatorPresenter);

        actualCallback.onError(clientRuntimeError);
        verifyOnError(callback, diagram, currentSession, clientRuntimeError);
    }

    private void verifyAfterCanvasInitialized(final SessionPresenter.SessionPresenterCallback callback,
                                              final Diagram diagram,
                                              final AbstractSession currentSession) {

        verify(editor, never()).initialiseKieEditorForSession(diagram);
        verify(editor, never()).setupSessionHeaderContainer();
        verify(callback, never()).onSuccess();
        verify(currentSession, never()).close();
        verify(callback).afterCanvasInitialized();
        verify(callback, never()).onOpen(diagram);
        verify(callback, never()).afterSessionOpened();
        verify(callback, never()).onError(any());
    }

    private void verifyOnOpen(final SessionPresenter.SessionPresenterCallback callback,
                              final Diagram diagram,
                              final AbstractSession currentSession) {

        verify(editor, never()).initialiseKieEditorForSession(diagram);
        verify(editor, never()).setupSessionHeaderContainer();
        verify(callback, never()).onSuccess();
        verify(currentSession, never()).close();
        verify(callback, never()).afterCanvasInitialized();
        verify(callback).onOpen(diagram);
        verify(callback, never()).afterSessionOpened();
        verify(callback, never()).onError(any());
    }

    private void verifyAfterSessionOpened(final SessionPresenter.SessionPresenterCallback callback,
                                          final Diagram diagram,
                                          final AbstractSession currentSession) {

        verify(editor, never()).initialiseKieEditorForSession(diagram);
        verify(editor, never()).setupSessionHeaderContainer();
        verify(callback, never()).onSuccess();
        verify(currentSession, never()).close();
        verify(callback, never()).afterCanvasInitialized();
        verify(callback, never()).onOpen(diagram);
        verify(callback).afterSessionOpened();
        verify(callback, never()).onError(any());
    }

    private void verifyOnError(final SessionPresenter.SessionPresenterCallback callback,
                               final Diagram diagram,
                               final AbstractSession currentSession,
                               final ClientRuntimeError error) {

        verify(editor, never()).initialiseKieEditorForSession(diagram);
        verify(editor, never()).setupSessionHeaderContainer();
        verify(callback, never()).onSuccess();
        verify(currentSession, never()).close();
        verify(callback, never()).afterCanvasInitialized();
        verify(callback, never()).onOpen(diagram);
        verify(callback, never()).afterSessionOpened();
        verify(callback).onError(error);
        verify(decisionNavigatorPresenter).setIsRefreshComponentsViewSuspended(false);
    }

    private void verifyOnSuccess(final SessionPresenter.SessionPresenterCallback callback,
                                 final Diagram diagram,
                                 final AbstractSession currentSession, InOrder inOrder) {

        verify(editor).initialiseKieEditorForSession(diagram);
        verify(editor).setupSessionHeaderContainer();
        verify(callback).onSuccess();
        verify(currentSession).close();
        verify(callback, never()).afterCanvasInitialized();
        verify(callback, never()).onOpen(diagram);
        verify(callback, never()).afterSessionOpened();
        verify(callback, never()).onError(any());
        inOrder.verify(decisionNavigatorPresenter).setIsRefreshComponentsViewSuspended(false);
        inOrder.verify(decisionNavigatorPresenter).refreshComponentsView();
    }

    private class AbstractDMNDiagramEditorMock extends AbstractDMNDiagramEditor {

        protected AbstractDMNDiagramEditorMock(final View view,
                                               final MultiPageEditorContainerView containerView,
                                               final StunnerEditor stunnerEditor,
                                               final DMNEditorSearchIndex editorSearchIndex,
                                               final SearchBarComponent<DMNSearchableElement> searchBarComponent,
                                               final SessionManager sessionManager,
                                               final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                               final DocumentationView documentationView,
                                               final ClientTranslationService translationService,
                                               final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent,
                                               final DecisionNavigatorDock decisionNavigatorDock,
                                               final DiagramEditorPropertiesDock diagramPropertiesDock,
                                               final PreviewDiagramDock diagramPreviewAndExplorerDock,
                                               final LayoutHelper layoutHelper,
                                               final OpenDiagramLayoutExecutor openDiagramLayoutExecutor,
                                               final DataTypesPage dataTypesPage,
                                               final KogitoClientDiagramService diagramServices,
                                               final MonacoFEELInitializer feelInitializer,
                                               final CanvasFileExport canvasFileExport,
                                               final Promises promises,
                                               final IncludedModelsPage includedModelsPage,
                                               final KogitoChannelHelper kogitoChannelHelper,
                                               final GuidedTourBridgeInitializer guidedTourBridgeInitializer,
                                               final DRDNameChanger drdNameChanger,
                                               final ConfirmationDialog confirmationDialog,
                                               final DecisionNavigatorPresenter decisionNavigatorPresenter) {
            super(view,
                  containerView,
                  stunnerEditor,
                  editorSearchIndex,
                  searchBarComponent,
                  sessionManager,
                  sessionCommandManager,
                  documentationView,
                  translationService,
                  refreshFormPropertiesEvent,
                  decisionNavigatorDock,
                  diagramPropertiesDock,
                  diagramPreviewAndExplorerDock,
                  layoutHelper,
                  openDiagramLayoutExecutor,
                  dataTypesPage,
                  diagramServices,
                  feelInitializer,
                  canvasFileExport,
                  promises,
                  includedModelsPage,
                  kogitoChannelHelper,
                  guidedTourBridgeInitializer,
                  drdNameChanger,
                  confirmationDialog,
                  decisionNavigatorPresenter);
        }
    }
}

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

package org.kie.workbench.common.dmn.project.client.editor;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.enterprise.event.Event;

import com.google.gwt.user.client.Command;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import elemental2.dom.HTMLElement;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.commands.general.NavigateToExpressionEditorCommand;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorDock;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPage;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsPageStateProviderImpl;
import org.kie.workbench.common.dmn.client.editors.search.DMNEditorSearchIndex;
import org.kie.workbench.common.dmn.client.editors.search.DMNSearchableElement;
import org.kie.workbench.common.dmn.client.editors.types.DataTypePageTabActiveEvent;
import org.kie.workbench.common.dmn.client.editors.types.DataTypesPage;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.session.DMNEditorSession;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoFEELInitializer;
import org.kie.workbench.common.dmn.project.client.resources.i18n.DMNProjectClientConstants;
import org.kie.workbench.common.dmn.project.client.type.DMNDiagramResourceType;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerPresenter;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.components.layout.LayoutHelper;
import org.kie.workbench.common.stunner.core.client.components.layout.OpenDiagramLayoutExecutor;
import org.kie.workbench.common.stunner.core.client.error.DiagramClientErrorHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.documentation.DocumentationPage;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditorMenuSessionItems;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectDiagramEditor;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectDiagramEditorCore;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectDiagramEditorTest;
import org.kie.workbench.common.stunner.project.client.editor.ProjectDiagramEditorProxy;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.kie.workbench.common.stunner.project.diagram.editor.ProjectDiagramResource;
import org.kie.workbench.common.widgets.client.docks.DefaultEditorDock;
import org.kie.workbench.common.widgets.client.search.component.SearchBarComponent;
import org.kie.workbench.common.workbench.client.PerspectiveIds;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorSelectedPageEvent;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.client.workbench.widgets.multipage.MultiPageEditor;
import org.uberfire.ext.editor.commons.client.menu.MenuItems;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.project.client.editor.DMNDiagramEditor.DATA_TYPES_PAGE_INDEX;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(PathPlaceRequest.class)
public class DMNDiagramEditorTest extends AbstractProjectDiagramEditorTest {

    @Mock
    private PlaceRequest currentPlace;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private DMNEditorSession dmnEditorSession;

    @Mock
    private DMNEditorSession defaultEditorSession;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private EventSourceMock<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    @Mock
    private ExpressionEditorView.Presenter expressionEditor;

    @Mock
    private DecisionNavigatorDock decisionNavigatorDock;

    @Mock
    private EditExpressionEvent editExpressionEvent;

    @Mock
    private DMNEditorMenuSessionItems dmnProjectMenuSessionItems;

    @Mock
    private LayoutHelper layoutHelper;

    @Mock
    private OpenDiagramLayoutExecutor layoutExecutor;

    @Mock
    private DataTypesPage dataTypesPage;

    @Mock
    private IncludedModelsPage includedModelsPage;

    @Mock
    private IncludedModelsPageStateProviderImpl importsPageProvider;

    @Mock
    private MultiPageEditor multiPage;

    private DMNDiagramEditor diagramEditor;

    @Mock
    private DefaultEditorDock docks;

    @Mock
    private DMNEditorSearchIndex editorSearchIndex;

    @Mock
    private SearchBarComponent<DMNSearchableElement> searchBarComponent;

    @Mock
    private SearchBarComponent.View searchBarComponentView;

    @Mock
    private HTMLElement searchBarComponentViewElement;

    @Mock
    private MonacoFEELInitializer feelInitializer;

    @Mock
    private ElementWrapperWidget searchBarComponentWidget;

    @Captor
    private ArgumentCaptor<Consumer<String>> errorConsumerCaptor;

    @Before
    public void before() {
        when(kieView.getMultiPage()).thenReturn(multiPage);
    }

    @Override
    public void setUp() {
        super.setUp();
        when(sessionManager.getCurrentSession()).thenReturn(dmnEditorSession);
        when(sessionEditorPresenter.getInstance()).thenReturn(dmnEditorSession);
        when(dmnEditorSession.getExpressionEditor()).thenReturn(expressionEditor);
        when(dmnEditorSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(searchBarComponent.getView()).thenReturn(searchBarComponentView);
        when(searchBarComponentView.getElement()).thenReturn(searchBarComponentViewElement);
    }

    @Override
    protected DMNDiagramResourceType mockResourceType() {
        final DMNDiagramResourceType resourceType = mock(DMNDiagramResourceType.class);
        when(resourceType.getSuffix()).thenReturn("dmn");
        when(resourceType.getShortName()).thenReturn("DMN");
        return resourceType;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected AbstractProjectDiagramEditor createDiagramEditor() {
        diagramEditor = spy(new DMNDiagramEditor(view,
                                                 xmlEditorView,
                                                 sessionEditorPresenters,
                                                 sessionViewerPresenters,
                                                 onDiagramFocusEvent,
                                                 onDiagramLostFocusEvent,
                                                 notificationEvent,
                                                 errorPopupPresenter,
                                                 diagramClientErrorHandler,
                                                 documentationView,
                                                 (DMNDiagramResourceType) getResourceType(),
                                                 (DMNEditorMenuSessionItems) getMenuSessionItems(),
                                                 projectMessagesListener,
                                                 translationService,
                                                 clientProjectDiagramService,
                                                 projectDiagramResourceServiceCaller,
                                                 sessionManager,
                                                 sessionCommandManager,
                                                 refreshFormPropertiesEvent,
                                                 decisionNavigatorDock,
                                                 layoutHelper,
                                                 layoutExecutor,
                                                 dataTypesPage,
                                                 includedModelsPage,
                                                 importsPageProvider,
                                                 editorSearchIndex,
                                                 searchBarComponent,
                                                 feelInitializer) {
            {
                docks = DMNDiagramEditorTest.this.docks;
                fileMenuBuilder = DMNDiagramEditorTest.this.fileMenuBuilder;
                workbenchContext = DMNDiagramEditorTest.this.workbenchContext;
                projectController = DMNDiagramEditorTest.this.projectController;
                versionRecordManager = DMNDiagramEditorTest.this.versionRecordManager;
                sessionEditorPresenters = DMNDiagramEditorTest.this.sessionEditorPresenters;
                alertsButtonMenuItemBuilder = DMNDiagramEditorTest.this.alertsButtonMenuItemBuilder;
                kieView = DMNDiagramEditorTest.this.kieView;
                overviewWidget = DMNDiagramEditorTest.this.overviewWidget;
                notification = DMNDiagramEditorTest.this.notificationEvent;
                placeManager = DMNDiagramEditorTest.this.placeManager;
                changeTitleNotification = DMNDiagramEditorTest.this.changeTitleNotificationEvent;
                savePopUpPresenter = DMNDiagramEditorTest.this.savePopUpPresenter;
            }

            @Override
            protected AbstractProjectDiagramEditorCore<ProjectMetadata, ProjectDiagram, ProjectDiagramResource, ProjectDiagramEditorProxy<ProjectDiagramResource>> makeCore(final AbstractProjectDiagramEditor.View view,
                                                                                                                                                                            final TextEditorView xmlEditorView,
                                                                                                                                                                            final Event<NotificationEvent> notificationEvent,
                                                                                                                                                                            final ManagedInstance<SessionEditorPresenter<EditorSession>> editorSessionPresenterInstances,
                                                                                                                                                                            final ManagedInstance<SessionViewerPresenter<ViewerSession>> viewerSessionPresenterInstances,
                                                                                                                                                                            final AbstractDiagramEditorMenuSessionItems<?> menuSessionItems,
                                                                                                                                                                            final ErrorPopupPresenter errorPopupPresenter,
                                                                                                                                                                            final DiagramClientErrorHandler diagramClientErrorHandler,
                                                                                                                                                                            final ClientTranslationService translationService) {
                presenterCore = spy(super.makeCore(view,
                                                   xmlEditorView,
                                                   notificationEvent,
                                                   editorSessionPresenterInstances,
                                                   viewerSessionPresenterInstances,
                                                   menuSessionItems,
                                                   errorPopupPresenter,
                                                   diagramClientErrorHandler,
                                                   translationService));
                return presenterCore;
            }

            @Override
            protected boolean isReadOnly() {
                return DMNDiagramEditorTest.this.isReadOnly;
            }

            @Override
            ElementWrapperWidget<?> getWidget(final HTMLElement element) {
                return searchBarComponentWidget;
            }
        });

        return diagramEditor;
    }

    @Override
    protected AbstractDiagramEditorMenuSessionItems getMenuSessionItems() {
        return dmnProjectMenuSessionItems;
    }

    @Test
    public void testInit() {

        final Supplier<Boolean> isDataTypesTabActiveSupplier = () -> true;
        final Supplier<Integer> currentContentHashSupplier = () -> 123;

        doReturn(isDataTypesTabActiveSupplier).when(diagramEditor).getIsDataTypesTabActiveSupplier();
        doReturn(currentContentHashSupplier).when(diagramEditor).getGetCurrentContentHashSupplier();

        diagramEditor.init();

        //DMNProjectEditorMenuSessionItems.setErrorConsumer(..) is called several times so just check the last invocation
        verify(dmnProjectMenuSessionItems, atLeast(1)).setErrorConsumer(errorConsumerCaptor.capture());

        errorConsumerCaptor.getValue().accept("ERROR");

        verify(view).hideBusyIndicator();
        verify(errorPopupPresenter, never()).showMessage(anyString());
        verify(editorSearchIndex).setIsDataTypesTabActiveSupplier(isDataTypesTabActiveSupplier);
        verify(editorSearchIndex).setCurrentAssetHashcodeSupplier(currentContentHashSupplier);
    }

    @Test
    public void testGetIsDataTypesTabActiveSupplierWhenDataTypesTabIsActive() {
        when(multiPage.selectedPage()).thenReturn(DATA_TYPES_PAGE_INDEX);
        assertTrue(diagramEditor.getIsDataTypesTabActiveSupplier().get());
    }

    @Test
    public void testGetIsDataTypesTabActiveSupplierWhenDataTypesTabIsNotActive() {
        when(multiPage.selectedPage()).thenReturn(DATA_TYPES_PAGE_INDEX + 1);
        assertFalse(diagramEditor.getIsDataTypesTabActiveSupplier().get());
    }

    @Test
    public void testOnStartup() {
        doNothing().when(diagramEditor).superDoStartUp(filePath, currentPlace);

        diagramEditor.onStartup(filePath, currentPlace);

        verify(diagramEditor).superDoStartUp(filePath, currentPlace);
        verify(decisionNavigatorDock).init(PerspectiveIds.LIBRARY);
    }

    @Test
    public void testOnClose() {
        doNothing().when(diagramEditor).superDoClose();

        diagramEditor.onClose();

        verify(diagramEditor).superDoClose();
        verify(dataTypesPage).disableShortcuts();
        verify(kieView).clear();
    }

    @Test
    public void testInitialiseKieEditorForSession() {
        doNothing().when(diagramEditor).superInitialiseKieEditorForSession(any());

        diagramEditor.initialiseKieEditorForSession(diagram);

        verify(multiPage).addPage(dataTypesPage);
        verify(multiPage).addPage(includedModelsPage);
        verify(diagramEditor).setupSearchComponent();
    }

    @Test
    public void testSetupSearchComponent() {

        diagramEditor.setupSearchComponent();

        verify(searchBarComponent).init(editorSearchIndex);
        verify(multiPage).addTabBarWidget(searchBarComponentWidget);
    }

    @Test
    public void testOnDiagramLoadWhenCanvasHandlerIsNotNull() {
        when(sessionManager.getCurrentSession()).thenReturn(dmnEditorSession);
        when(dmnEditorSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(importsPageProvider.withDiagram(diagram)).thenReturn(importsPageProvider);

        open();

        final InOrder inOrder = inOrder(decisionNavigatorDock);
        inOrder.verify(decisionNavigatorDock).setupCanvasHandler(eq(canvasHandler));

        verify(expressionEditor).setToolbarStateHandler(any(DMNProjectToolbarStateHandler.class));
        verify(dataTypesPage).reload();
        verify(layoutHelper).applyLayout(diagram, layoutExecutor);
        verify(includedModelsPage).setup(importsPageProvider);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnDiagramLoadWhenCanvasHandlerIsNull() {
        diagramEditor.onDiagramLoad();

        verify(expressionEditor, never()).setToolbarStateHandler(any(DMNProjectToolbarStateHandler.class));
        verify(decisionNavigatorDock, never()).setupCanvasHandler(any());
        verify(decisionNavigatorDock, never()).open();
        verify(dataTypesPage, never()).reload();
        verify(includedModelsPage, never()).setup(any());
    }

    @Test
    public void testOnFocus() {
        doNothing().when(diagramEditor).superDoFocus();

        diagramEditor.onFocus();

        verify(diagramEditor).superDoFocus();
        verify(diagramEditor).onDiagramLoad();
        verify(dataTypesPage).onFocus();
        verify(dataTypesPage).enableShortcuts();
    }

    @Test
    public void testOnLostFocus() {
        diagramEditor.onLostFocus();

        verify(dataTypesPage).onLostFocus();
    }

    @Test
    public void testOnEditExpressionEvent() {
        when(editExpressionEvent.getSession()).thenReturn(dmnEditorSession);
        when(sessionManager.getCurrentSession()).thenReturn(dmnEditorSession);
        when(dmnEditorSession.getCanvasHandler()).thenReturn(canvasHandler);

        open();

        diagramEditor.onEditExpressionEvent(editExpressionEvent);

        verify(searchBarComponent).disableSearch();
        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              any(NavigateToExpressionEditorCommand.class));
    }

    @Test
    public void testOnDataTypePageNavTabActiveEvent() {
        diagramEditor.onDataTypePageNavTabActiveEvent(mock(DataTypePageTabActiveEvent.class));

        verify(multiPage).selectPage(DATA_TYPES_PAGE_INDEX);
    }

    @Test
    public void testOnDataTypeEditModeToggleWhenEditModeIsEnabled() {
        final DataTypeEditModeToggleEvent editModeToggleEvent = mock(DataTypeEditModeToggleEvent.class);

        doNothing().when(diagramEditor).disableMenuItem(any());
        when(editModeToggleEvent.isEditModeEnabled()).thenReturn(true);

        diagramEditor.getOnDataTypeEditModeToggleCallback(editModeToggleEvent).onInvoke();

        verify(diagramEditor).disableMenuItem(MenuItems.SAVE);
    }

    @Test
    public void testOnDataTypeEditModeToggleWhenEditModeIsNotEnabled() {
        final DataTypeEditModeToggleEvent editModeToggleEvent = mock(DataTypeEditModeToggleEvent.class);

        doNothing().when(diagramEditor).enableMenuItem(any());
        when(editModeToggleEvent.isEditModeEnabled()).thenReturn(false);

        diagramEditor.getOnDataTypeEditModeToggleCallback(editModeToggleEvent).onInvoke();

        verify(diagramEditor).enableMenuItem(MenuItems.SAVE);
    }

    @Test
    public void testShowDocks() {
        diagramEditor.showDocks();

        verify(decisionNavigatorDock).open();
        verify(docks).show();
    }

    @Test
    public void testHideDocks() {
        diagramEditor.hideDocks();

        verify(decisionNavigatorDock).close();
        verify(decisionNavigatorDock).resetContent();
        verify(docks).hide();
    }

    @Override
    public void testDocksQualifiers() {
        final Annotation[] qualifiers = presenter.getDockQualifiers();
        assertEquals(1, qualifiers.length);
        assertEquals(DMNEditor.class, qualifiers[0].annotationType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testParsingErrorMessage() {
        doAnswer(i -> i.getArguments()[0]).when(translationService).getValue(anyString());

        final String xml = "xml";

        openInvalidFile(xml);

        final ArgumentCaptor<NotificationEvent> notificationEventCaptor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(notificationEvent).fire(notificationEventCaptor.capture());

        final NotificationEvent notificationEvent = notificationEventCaptor.getValue();
        assertEquals(DMNProjectClientConstants.DMNDiagramParsingErrorMessage,
                     notificationEvent.getNotification());
    }

    @Test
    public void testStunnerSave_ValidationUnsuccessful() {
        final Overview overview = assertBasicStunnerSaveOperation(false);
        assertSaveOperation(overview);
    }

    @Test
    public void testOnMultiPageEditorSelectedPageEvent() {

        final SessionPresenter sessionPresenter = mock(SessionPresenter.class);

        doReturn(sessionPresenter).when(diagramEditor).getSessionPresenter();
        when(sessionPresenter.getInstance()).thenReturn(dmnEditorSession);
        when(sessionManager.getCurrentSession()).thenReturn(dmnEditorSession);

        open();

        diagramEditor.onMultiPageEditorSelectedPageEvent(mock(MultiPageEditorSelectedPageEvent.class));

        verify(searchBarComponent).disableSearch();
    }

    @Test
    public void testOnMultiPageEditorSelectedPageEventWhenEditorIsNotInTheSameSession() {

        final SessionPresenter sessionPresenter = mock(SessionPresenter.class);

        doReturn(sessionPresenter).when(diagramEditor).getSessionPresenter();
        when(sessionPresenter.getInstance()).thenReturn(dmnEditorSession);
        when(sessionManager.getCurrentSession()).thenReturn(defaultEditorSession);
        when(defaultEditorSession.getExpressionEditor()).thenReturn(expressionEditor);

        open();

        diagramEditor.onMultiPageEditorSelectedPageEvent(mock(MultiPageEditorSelectedPageEvent.class));

        verify(searchBarComponent, never()).disableSearch();
    }

    @Test
    public void testOnRefreshFormPropertiesEvent() {
        diagramEditor.onRefreshFormPropertiesEvent(mock(RefreshFormPropertiesEvent.class));
        verify(searchBarComponent).disableSearch();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTabContentOrdering() {
        when(documentationView.isEnabled()).thenReturn(Boolean.TRUE);
        when(documentationView.initialize(diagram)).thenReturn(documentationView);
        when(kieView.getMultiPage()).thenReturn(multiPage);

        open();

        final InOrder inOrder = inOrder(kieView, multiPage);
        inOrder.verify(kieView).addMainEditorPage(view);
        inOrder.verify(kieView).addPage(any(DocumentationPage.class));
        inOrder.verify(multiPage).addPage(dataTypesPage);
        inOrder.verify(multiPage).addPage(includedModelsPage);
        inOrder.verify(kieView).addOverviewPage(eq(overviewWidget), any(Command.class));
    }

    @Test
    @Override
    public void testLoadContentWithValidFile() {
        super.testLoadContentWithValidFile();

        verify(layoutHelper).applyLayout(eq(diagram), eq(layoutExecutor));
        verify(feelInitializer).initializeFEELEditor();
    }

    @Test
    @Override
    public void testLoadContentWithInvalidFile() {
        super.testLoadContentWithInvalidFile();

        verify(feelInitializer, never()).initializeFEELEditor();
    }
}

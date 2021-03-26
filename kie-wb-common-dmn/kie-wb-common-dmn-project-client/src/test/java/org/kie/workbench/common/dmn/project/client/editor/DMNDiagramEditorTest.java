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

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import elemental2.dom.HTMLElement;
import org.guvnor.messageconsole.client.console.widget.button.AlertsButtonMenuItemBuilder;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.commands.general.NavigateToExpressionEditorCommand;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorDock;
import org.kie.workbench.common.dmn.client.docks.navigator.common.LazyCanvasFocusUtils;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.editors.drd.DRDNameChanger;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPage;
import org.kie.workbench.common.dmn.client.editors.search.DMNEditorSearchIndex;
import org.kie.workbench.common.dmn.client.editors.search.DMNSearchableElement;
import org.kie.workbench.common.dmn.client.editors.types.DataTypePageTabActiveEvent;
import org.kie.workbench.common.dmn.client.editors.types.DataTypesPage;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.session.DMNEditorSession;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoFEELInitializer;
import org.kie.workbench.common.dmn.project.client.type.DMNDiagramResourceType;
import org.kie.workbench.common.services.shared.resources.PerspectiveIds;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.components.layout.LayoutHelper;
import org.kie.workbench.common.stunner.core.client.components.layout.OpenDiagramLayoutExecutor;
import org.kie.workbench.common.stunner.core.documentation.DocumentationPage;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectDiagramEditorTest;
import org.kie.workbench.common.widgets.client.docks.DefaultEditorDock;
import org.kie.workbench.common.widgets.client.search.component.SearchBarComponent;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorSelectedPageEvent;
import org.uberfire.client.workbench.widgets.multipage.MultiPageEditor;
import org.uberfire.ext.editor.commons.client.menu.MenuItems;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.project.client.editor.DMNDiagramEditor.DATA_TYPES_PAGE_INDEX;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
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
    private SessionManager sessionManager;

    @Mock
    private DMNEditorSession dmnEditorSession;

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
    private MultiPageEditor multiPage;

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

    @Mock
    private HTMLElement drdNameChangerElement;

    @Mock
    private DRDNameChanger drdNameChanger;

    @Mock
    private LazyCanvasFocusUtils lazyCanvasFocusUtils;

    @Mock
    private ElementWrapperWidget drdNameWidget;

    @Mock
    private DMNDiagramsSession dmnDiagramsSession;

    @Captor
    private ArgumentCaptor<Consumer<String>> errorConsumerCaptor;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private AlertsButtonMenuItemBuilder alertsButtonMenuItemBuilder;

    @Mock
    private SessionPresenter.View presenterView;

    @Mock
    private SessionDiagramPresenter presenter;

    private DMNDiagramEditor diagramEditor;

    @Before
    public void before() {
        when(kieView.getMultiPage()).thenReturn(multiPage);
    }

    @Override
    public void setUp() {
        super.setUp();

        when(sessionManager.getCurrentSession()).thenReturn(dmnEditorSession);
        when(stunnerEditor.getSession()).thenReturn(dmnEditorSession);
        when(dmnEditorSession.getExpressionEditor()).thenReturn(expressionEditor);
        when(dmnEditorSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(searchBarComponent.getView()).thenReturn(searchBarComponentView);
        when(searchBarComponentView.getElement()).thenReturn(searchBarComponentViewElement);
        when(drdNameChanger.getElement()).thenReturn(drdNameChangerElement);
        when(dmnProjectMenuSessionItems.setErrorConsumer(Mockito.<Consumer>any())).thenReturn(dmnProjectMenuSessionItems);
        when(dmnProjectMenuSessionItems.setLoadingCompleted(Mockito.<org.uberfire.mvp.Command>any())).thenReturn(dmnProjectMenuSessionItems);
        when(dmnProjectMenuSessionItems.setLoadingStarts(Mockito.<Command>any())).thenReturn(dmnProjectMenuSessionItems);

        final DMNDiagramResourceType resourceType = mock(DMNDiagramResourceType.class);
        when(resourceType.getSuffix()).thenReturn("dmn");
        when(resourceType.getShortName()).thenReturn("DMN");

        diagramEditor = spy(new DMNDiagramEditor(view,
                                                 onDiagramFocusEvent,
                                                 onDiagramLostFocusEvent,
                                                 documentationView,
                                                 resourceType,
                                                 dmnProjectMenuSessionItems,
                                                 projectMessagesListener,
                                                 translationService,
                                                 projectDiagramServices,
                                                 projectDiagramResourceServiceCaller,
                                                 stunnerEditor,
                                                 sessionManager,
                                                 sessionCommandManager,
                                                 refreshFormPropertiesEvent,
                                                 decisionNavigatorDock,
                                                 layoutHelper,
                                                 layoutExecutor,
                                                 dataTypesPage,
                                                 includedModelsPage,
                                                 editorSearchIndex,
                                                 searchBarComponent,
                                                 feelInitializer,
                                                 drdNameChanger,
                                                 lazyCanvasFocusUtils,
                                                 dmnDiagramsSession) {
            {
                docks = DMNDiagramEditorTest.this.docks;
                fileMenuBuilder = DMNDiagramEditorTest.this.fileMenuBuilder;
                workbenchContext = DMNDiagramEditorTest.this.workbenchContext;
                projectController = DMNDiagramEditorTest.this.projectController;
                versionRecordManager = DMNDiagramEditorTest.this.versionRecordManager;
                alertsButtonMenuItemBuilder = DMNDiagramEditorTest.this.alertsButtonMenuItemBuilder;
                kieView = DMNDiagramEditorTest.this.kieView;
                overviewWidget = DMNDiagramEditorTest.this.overviewWidget;
                notification = DMNDiagramEditorTest.this.notification;
                placeManager = DMNDiagramEditorTest.this.placeManager;
                changeTitleNotification = DMNDiagramEditorTest.this.changeTitleNotification;
                savePopUpPresenter = DMNDiagramEditorTest.this.savePopUpPresenter;
                saveAndRenameCommandBuilder = DMNDiagramEditorTest.this.saveAndRenameCommandBuilder;
            }
        });
        doReturn(searchBarComponentWidget).when(diagramEditor).getWidget(any());
        doReturn(drdNameWidget).when(diagramEditor).getWidget(eq(drdNameChangerElement));
        doNothing().when(stunnerEditor).focus();
        doNothing().when(stunnerEditor).lostFocus();
        when(presenter.getView()).thenReturn(presenterView);
        when(stunnerEditor.getPresenter()).thenReturn(presenter);
        when(stunnerEditor.getCanvasHandler()).thenReturn(canvasHandler);
        when(stunnerEditor.getView()).thenReturn(view);
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
        verify(stunnerEditor, never()).showMessage(Mockito.<String>any());
        verify(stunnerEditor, never()).showError(Mockito.<String>any());
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
        diagramEditor.onStartup(filePath, currentPlace);
        verify(decisionNavigatorDock).init(eq(PerspectiveIds.LIBRARY));
    }

    @Test
    public void testOnClose() {
        diagramEditor.onClose();
        verify(dataTypesPage).disableShortcuts();
        verify(kieView).clear();
    }

    @Test
    public void testInitialiseKieEditorForSession() {

        diagramEditor.initialiseKieEditorForSession(diagram);

        verify(multiPage).addPage(dataTypesPage);
        verify(multiPage).addPage(includedModelsPage);
        verify(diagramEditor).setupSearchComponent();
    }

    @Test
    public void testInitialiseKieEditorForSessionWhenInitializingKieEditorForSessionThenDiagramAlreadyLoaded() {
        final InOrder inOrder = inOrder(diagramEditor);
        diagramEditor.initialiseKieEditorForSession(diagram);
        inOrder.verify(diagramEditor).onDiagramLoad();
        inOrder.verify(diagramEditor).superInitialiseKieEditorForSession(any());
    }

    @Test
    public void testSetupSearchComponent() {
        diagramEditor.setupSearchComponent();
        verify(searchBarComponent).init(editorSearchIndex);
        verify(multiPage).addTabBarWidget(searchBarComponentWidget);
    }

    @Test
    public void testOnFocus() {
        when(stunnerEditor.isClosed()).thenReturn(false);
        diagramEditor.onFocus();
        verify(stunnerEditor).focus();
        verify(stunnerEditor, never()).lostFocus();
        verify(diagramEditor).onDiagramLoad();
        verify(dataTypesPage).onFocus();
        verify(dataTypesPage, never()).onLostFocus();
        verify(dataTypesPage).enableShortcuts();
        verify(dataTypesPage, never()).disableShortcuts();
    }

    @Test
    public void testOnLostFocus() {
        when(stunnerEditor.isClosed()).thenReturn(false);
        diagramEditor.onLostFocus();
        verify(stunnerEditor).lostFocus();
        verify(stunnerEditor, never()).focus();
        verify(dataTypesPage).onLostFocus();
        verify(dataTypesPage, never()).onFocus();
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
        final Annotation[] qualifiers = diagramEditor.getDockQualifiers();
        assertEquals(1, qualifiers.length);
        assertEquals(DMNEditor.class, qualifiers[0].annotationType());
    }

    @Test
    public void testOnEditExpressionEvent() {
        when(editExpressionEvent.getSession()).thenReturn(dmnEditorSession);
        when(sessionManager.getCurrentSession()).thenReturn(dmnEditorSession);
        when(dmnEditorSession.getCanvasHandler()).thenReturn(canvasHandler);

        diagramEditor.open(diagram);

        diagramEditor.onEditExpressionEvent(editExpressionEvent);

        verify(searchBarComponent).disableSearch();
        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              Mockito.<NavigateToExpressionEditorCommand>any());
    }

    @Test
    public void testOnDiagramLoadWhenCanvasHandlerIsNotNull() {
        when(sessionManager.getCurrentSession()).thenReturn(dmnEditorSession);
        when(dmnEditorSession.getCanvasHandler()).thenReturn(canvasHandler);

        diagramEditor.open(diagram);

        verify(decisionNavigatorDock, atLeast(1)).reload();
        verify(expressionEditor, atLeast(1)).setToolbarStateHandler(Mockito.<DMNProjectToolbarStateHandler>any());
        verify(dataTypesPage, atLeast(1)).reload();
        verify(layoutHelper).applyLayout(diagram, layoutExecutor);
        verify(includedModelsPage, atLeast(1)).reload();
        verify(lazyCanvasFocusUtils, atLeast(1)).releaseFocus();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnDiagramLoadWhenCanvasHandlerIsNull() {
        when(stunnerEditor.getCanvasHandler()).thenReturn(null);
        diagramEditor.onDiagramLoad();

        verify(expressionEditor, never()).setToolbarStateHandler(Mockito.<DMNProjectToolbarStateHandler>any());
        verify(decisionNavigatorDock, never()).reload();
        verify(decisionNavigatorDock, never()).open();
        verify(dataTypesPage, never()).reload();
        verify(includedModelsPage, never()).reload();
        verify(lazyCanvasFocusUtils, never()).releaseFocus();
    }

    @Test
    public void testOnMultiPageEditorSelectedPageEvent() {
        diagramEditor.open(diagram);
        diagramEditor.onMultiPageEditorSelectedPageEvent(mock(MultiPageEditorSelectedPageEvent.class));
        verify(searchBarComponent).disableSearch();
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

        diagramEditor.open(diagram);

        final InOrder inOrder = inOrder(kieView, multiPage);
        inOrder.verify(kieView).addMainEditorPage(view);
        inOrder.verify(kieView).addPage(Mockito.<DocumentationPage>any());
        inOrder.verify(multiPage).addPage(dataTypesPage);
        inOrder.verify(multiPage).addPage(includedModelsPage);
        inOrder.verify(kieView).addOverviewPage(eq(overviewWidget), Mockito.<com.google.gwt.user.client.Command>any());
    }

    @Test
    @Override
    public void testLoadContentWithInvalidFile() {
        super.testLoadContentWithInvalidFile();

        verify(feelInitializer, never()).initializeFEELEditor();
    }

    @Test
    public void testSaveEvenIfValidationNotSuccessful() {
        assertTrue(diagramEditor.isSaveAllowedAfterValidationFailed(Violation.Type.INFO));
        assertTrue(diagramEditor.isSaveAllowedAfterValidationFailed(Violation.Type.WARNING));
        assertTrue(diagramEditor.isSaveAllowedAfterValidationFailed(Violation.Type.ERROR));
    }
}

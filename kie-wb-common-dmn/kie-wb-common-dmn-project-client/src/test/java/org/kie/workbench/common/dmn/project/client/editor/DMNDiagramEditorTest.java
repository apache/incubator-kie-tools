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

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.commands.general.NavigateToExpressionEditorCommand;
import org.kie.workbench.common.dmn.client.decision.DecisionNavigatorDock;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.session.DMNEditorSession;
import org.kie.workbench.common.dmn.project.client.type.DMNDiagramResourceType;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectDiagramEditor;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectDiagramEditorTest;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectEditorMenuSessionItems;
import org.kie.workbench.common.workbench.client.PerspectiveIds;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
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
    private DMNProjectEditorMenuSessionItems dmnProjectMenuSessionItems;

    private DMNDiagramEditor diagramEditor;

    @Override
    public void setUp() {
        super.setUp();
        when(sessionEditorPresenter.getInstance()).thenReturn(dmnEditorSession);
        when(dmnEditorSession.getExpressionEditor()).thenReturn(expressionEditor);
        when(resourceType.getSuffix()).thenReturn("dmn");
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
                                                 placeManager,
                                                 errorPopupPresenter,
                                                 changeTitleNotificationEvent,
                                                 savePopUpPresenter,
                                                 (DMNDiagramResourceType) getResourceType(),
                                                 clientProjectDiagramService,
                                                 sessionEditorPresenters,
                                                 sessionViewerPresenters,
                                                 (DMNProjectEditorMenuSessionItems) getMenuSessionItems(),
                                                 onDiagramFocusEvent,
                                                 onDiagramLostFocusEvent,
                                                 refreshFormPropertiesEvent,
                                                 projectMessagesListener,
                                                 diagramClientErrorHandler,
                                                 translationService,
                                                 xmlEditorView,
                                                 projectDiagramResourceServiceCaller,
                                                 sessionManager,
                                                 sessionCommandManager,
                                                 decisionNavigatorDock) {
            {
                fileMenuBuilder = DMNDiagramEditorTest.this.fileMenuBuilder;
                workbenchContext = DMNDiagramEditorTest.this.workbenchContext;
                projectController = DMNDiagramEditorTest.this.projectController;
                versionRecordManager = DMNDiagramEditorTest.this.versionRecordManager;
                sessionEditorPresenters = DMNDiagramEditorTest.this.sessionEditorPresenters;
                alertsButtonMenuItemBuilder = DMNDiagramEditorTest.this.alertsButtonMenuItemBuilder;
                kieView = DMNDiagramEditorTest.this.kieView;
                overviewWidget = DMNDiagramEditorTest.this.overviewWidget;
                notification = DMNDiagramEditorTest.this.notification;
            }
        });

        return diagramEditor;
    }

    @Override
    protected AbstractProjectEditorMenuSessionItems getMenuSessionItems() {
        return dmnProjectMenuSessionItems;
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
        doNothing().when(diagramEditor).superOnClose();

        diagramEditor.onClose();

        verify(diagramEditor).superOnClose();
        verify(decisionNavigatorDock).close();
        verify(decisionNavigatorDock).resetContent();
    }

    @Test
    public void testOnDiagramLoadWhenCanvasHandlerIsNotNull() {
        when(sessionManager.getCurrentSession()).thenReturn(dmnEditorSession);
        when(dmnEditorSession.getCanvasHandler()).thenReturn(canvasHandler);

        open();

        verify(expressionEditor).setToolbarStateHandler(any(ProjectToolbarStateHandler.class));
        verify(decisionNavigatorDock).setupContent(eq(canvasHandler));
        verify(decisionNavigatorDock).open();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnDiagramLoadWhenCanvasHandlerIsNull() {
        diagramEditor.onDiagramLoad();

        verify(expressionEditor, never()).setToolbarStateHandler(any(ProjectToolbarStateHandler.class));
        verify(decisionNavigatorDock, never()).setupContent(any());
        verify(decisionNavigatorDock, never()).open();
    }

    @Test
    public void testOnFocus() {
        doNothing().when(diagramEditor).superDoFocus();

        diagramEditor.onFocus();

        verify(diagramEditor).superDoFocus();
        verify(diagramEditor).onDiagramLoad();
    }

    @Test
    public void testOnEditExpressionEvent() {
        when(editExpressionEvent.getSession()).thenReturn(dmnEditorSession);
        when(sessionManager.getCurrentSession()).thenReturn(dmnEditorSession);
        when(dmnEditorSession.getCanvasHandler()).thenReturn(canvasHandler);

        open();

        diagramEditor.onEditExpressionEvent(editExpressionEvent);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              any(NavigateToExpressionEditorCommand.class));
    }

    @Override
    public void testOpen() {
    }

    @Override
    public void testFormatTitle() {
    }
}

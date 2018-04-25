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

import java.util.logging.Level;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.decision.DecisionNavigatorDock;
import org.kie.workbench.common.dmn.project.client.type.DMNDiagramResourceType;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectDiagramEditor;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectDiagramEditorTest;
import org.kie.workbench.common.workbench.client.PerspectiveIds;
import org.mockito.Mock;
import org.uberfire.mvp.PlaceRequest;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNDiagramEditorTest extends AbstractProjectDiagramEditorTest {

    @Mock
    private PlaceRequest currentPlace;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private DecisionNavigatorDock decisionNavigatorDock;

    private DMNDiagramEditor diagramEditor;

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
                                                 sessionManager,
                                                 sessionPresenterFactory,
                                                 sessionCommandFactory,
                                                 projectMenuItemsBuilder,
                                                 onDiagramFocusEvent,
                                                 onDiagramLostFocusEvent,
                                                 projectMessagesListener,
                                                 diagramClientErrorHandler,
                                                 translationService,
                                                 decisionNavigatorDock) {
            {
                fileMenuBuilder = DMNDiagramEditorTest.this.fileMenuBuilder;
                workbenchContext = DMNDiagramEditorTest.this.workbenchContext;
                projectController = DMNDiagramEditorTest.this.projectController;
                versionRecordManager = DMNDiagramEditorTest.this.versionRecordManager;
                alertsButtonMenuItemBuilder = DMNDiagramEditorTest.this.alertsButtonMenuItemBuilder;
                kieView = DMNDiagramEditorTest.this.kieView;
                overviewWidget = DMNDiagramEditorTest.this.overviewWidget;
            }

            @Override
            protected void log(Level level,
                               String message) {
                //avoid GWT log initialization.
            }
        });

        return diagramEditor;
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
        when(sessionManager.getCurrentSession()).thenReturn(clientFullSession);
        when(clientFullSession.getCanvasHandler()).thenReturn(canvasHandler);

        diagramEditor.onDiagramLoad();

        verify(decisionNavigatorDock).setupContent(canvasHandler);
        verify(decisionNavigatorDock).open();
    }

    @Test
    public void testOnDiagramLoadWhenCanvasHandlerIsNull() {
        diagramEditor.onDiagramLoad();

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
}

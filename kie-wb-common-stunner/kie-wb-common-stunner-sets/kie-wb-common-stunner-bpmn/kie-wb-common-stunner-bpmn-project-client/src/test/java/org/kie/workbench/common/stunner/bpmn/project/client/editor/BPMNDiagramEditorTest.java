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

package org.kie.workbench.common.stunner.bpmn.project.client.editor;

import java.util.Optional;
import java.util.logging.Level;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.integration.client.IntegrationHandler;
import org.kie.workbench.common.stunner.bpmn.integration.client.IntegrationHandlerProvider;
import org.kie.workbench.common.stunner.bpmn.project.client.type.BPMNDiagramResourceType;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectDiagramEditor;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectDiagramEditorTest;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectEditorMenuSessionItems;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class BPMNDiagramEditorTest extends AbstractProjectDiagramEditorTest {

    @Mock
    private PlaceRequest currentPlace;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private BPMNProjectEditorMenuSessionItems bpmnMenuSessionItems;

    @Mock
    private IntegrationHandlerProvider integrationHandlerProvider;

    @Mock
    private IntegrationHandler integrationHandler;

    private ArgumentCaptor<Command> commandCaptor;

    private BPMNDiagramEditor diagramEditor;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        super.setUp();
        commandCaptor = ArgumentCaptor.forClass(Command.class);
        when(canvasHandler.getDiagram()).thenReturn(diagram);

        super.setUp();
    }

    @Override
    protected AbstractProjectEditorMenuSessionItems getMenuSessionItems() {
        return bpmnMenuSessionItems;
    }

    @Override
    protected BPMNDiagramResourceType mockResourceType() {
        final BPMNDiagramResourceType resourceType = mock(BPMNDiagramResourceType.class);
        when(resourceType.getSuffix()).thenReturn("bpmn");
        when(resourceType.getShortName()).thenReturn("Business Process");
        return resourceType;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected AbstractProjectDiagramEditor createDiagramEditor() {
        when(integrationHandlerProvider.getIntegrationHandler()).thenReturn(Optional.empty());
        diagramEditor = spy(new BPMNDiagramEditor(view,
                                                  documentationView,
                                                  placeManager,
                                                  errorPopupPresenter,
                                                  changeTitleNotificationEvent,
                                                  savePopUpPresenter,
                                                  (BPMNDiagramResourceType) getResourceType(),
                                                  clientProjectDiagramService,
                                                  sessionEditorPresenters,
                                                  sessionViewerPresenters,
                                                  bpmnMenuSessionItems,
                                                  onDiagramFocusEvent,
                                                  onDiagramLostFocusEvent,
                                                  projectMessagesListener,
                                                  diagramClientErrorHandler,
                                                  translationService,
                                                  projectDiagramResourceServiceCaller,
                                                  integrationHandlerProvider,
                                                  xmlEditorView) {
            {
                docks = defaultEditorDock;
                perspectiveManager = perspectiveManagerMock;
                fileMenuBuilder = BPMNDiagramEditorTest.this.fileMenuBuilder;
                workbenchContext = BPMNDiagramEditorTest.this.workbenchContext;
                projectController = BPMNDiagramEditorTest.this.projectController;
                versionRecordManager = BPMNDiagramEditorTest.this.versionRecordManager;
                alertsButtonMenuItemBuilder = BPMNDiagramEditorTest.this.alertsButtonMenuItemBuilder;
                place = BPMNDiagramEditorTest.this.currentPlace;
                kieView = BPMNDiagramEditorTest.this.kieView;
                overviewWidget = BPMNDiagramEditorTest.this.overviewWidget;
                notification = BPMNDiagramEditorTest.this.notification;
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
    public void testInitWhenIntegrationIsPresent() {
        Optional<IntegrationHandler> optional = Optional.of(integrationHandler);
        when(integrationHandlerProvider.getIntegrationHandler()).thenReturn(optional);
        diagramEditor.init();
        verify(bpmnMenuSessionItems).setOnMigrate(any(Command.class));
    }

    @Test
    public void testInitWhenIntegrationIsNotPresent() {
        Optional<IntegrationHandler> optional = Optional.empty();
        when(integrationHandlerProvider.getIntegrationHandler()).thenReturn(optional);
        diagramEditor.init();
        verify(bpmnMenuSessionItems, never()).setOnMigrate(any(Command.class));
    }

    @Test
    public void testMigrateWhenNotDirty() {
        testMigrate(false);
    }

    @Test
    public void testMigrateWhenDirty() {
        testMigrate(true);
    }

    private void testMigrate(boolean isDirty) {
        ObservablePath currentPath = mock(ObservablePath.class);
        when(versionRecordManager.getCurrentPath()).thenReturn(currentPath);
        doReturn(isDirty).when(diagramEditor).isDirty(any(Integer.class));

        Optional<IntegrationHandler> optional = Optional.of(integrationHandler);
        when(integrationHandlerProvider.getIntegrationHandler()).thenReturn(optional);
        diagramEditor.init();
        verify(bpmnMenuSessionItems).setOnMigrate(commandCaptor.capture());
        commandCaptor.getValue().execute();
        verify(integrationHandler).migrateFromStunnerToJBPMDesigner(eq(currentPath), eq(currentPlace), eq(isDirty), any(ParameterizedCommand.class));
    }
}

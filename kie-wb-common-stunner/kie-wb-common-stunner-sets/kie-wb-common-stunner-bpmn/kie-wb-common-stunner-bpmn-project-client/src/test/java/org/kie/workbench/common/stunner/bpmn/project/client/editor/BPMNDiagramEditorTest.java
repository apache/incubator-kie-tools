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

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.project.client.type.BPMNDiagramResourceType;
import org.kie.workbench.common.stunner.client.widgets.presenters.Viewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.resources.i18n.StunnerWidgetsConstants;
import org.kie.workbench.common.stunner.client.widgets.screens.DiagramEditorExplorerScreen;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenMaximizedEvent;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenMinimizedEvent;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenPreMaximizedStateEvent;
import org.kie.workbench.common.stunner.core.documentation.DocumentationPage;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.kie.workbench.common.stunner.forms.client.screens.DiagramEditorPropertiesScreen;
import org.kie.workbench.common.stunner.project.client.docks.StunnerDocksHandler;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectDiagramEditorTest;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class BPMNDiagramEditorTest extends AbstractProjectDiagramEditorTest {

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private BPMNProjectEditorMenuSessionItems bpmnMenuSessionItems;

    @Mock
    private UberfireDocks uberfireDocks;

    @Mock
    private UberfireDock propertiesDock;

    @Mock
    private PlaceRequest propertiesPlace;

    @Mock
    private UberfireDock explorerDock;

    @Mock
    private PlaceRequest explorerPlace;

    @Mock
    private StunnerDocksHandler stunnerDocksHandler;

    @Mock
    private PerspectiveActivity currentPerspective;

    @Mock
    private ScreenMaximizedEvent maximizedEvent;

    @Mock
    private ScreenMinimizedEvent minimizedEvent;

    @Mock
    private ScreenPreMaximizedStateEvent preMaximizedStateEvent;

    private BPMNDiagramEditor diagramEditor;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        super.setUp();
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        BPMNDiagramResourceType bpmnDiagramResourceType = mock(BPMNDiagramResourceType.class);
        when(bpmnDiagramResourceType.getSuffix()).thenReturn("bpmn");
        when(bpmnDiagramResourceType.getShortName()).thenReturn("bpmn");
        when(stunnerEditor.close()).thenReturn(stunnerEditor);
        doAnswer(invocation -> {
            diagramEditor.initialiseKieEditorForSession(diagram);
            ((Viewer.Callback) invocation.getArguments()[1]).onSuccess();
            return null;
        }).when(stunnerEditor).open(eq(diagram), any(SessionPresenter.SessionPresenterCallback.class));
        diagramEditor = spy(new BPMNDiagramEditor(view,
                                                  onDiagramFocusEvent,
                                                  onDiagramLostFocusEvent,
                                                  documentationView,
                                                  bpmnDiagramResourceType,
                                                  mock(BPMNProjectEditorMenuSessionItems.class),
                                                  projectMessagesListener,
                                                  translationService,
                                                  projectDiagramServices,
                                                  projectDiagramResourceServiceCaller,
                                                  stunnerEditor,
                                                  uberfireDocks,
                                                  stunnerDocksHandler) {
            {
                fileMenuBuilder = BPMNDiagramEditorTest.this.fileMenuBuilder;
                projectController = BPMNDiagramEditorTest.this.projectController;
                workbenchContext = BPMNDiagramEditorTest.this.workbenchContext;
                versionRecordManager = BPMNDiagramEditorTest.this.versionRecordManager;
                kieView = BPMNDiagramEditorTest.this.kieView;
                overviewWidget = BPMNDiagramEditorTest.this.overviewWidget;
                saveAndRenameCommandBuilder = BPMNDiagramEditorTest.this.saveAndRenameCommandBuilder;
                changeTitleNotification = BPMNDiagramEditorTest.this.changeTitleNotification;
                notification = BPMNDiagramEditorTest.this.notification;
                savePopUpPresenter = BPMNDiagramEditorTest.this.savePopUpPresenter;
                docks = BPMNDiagramEditorTest.this.docks;
                perspectiveManager = BPMNDiagramEditorTest.this.perspectiveManager;
            }
        });
    }

    @Override
    public void testOpen() {
        tested.open(diagram);
        verify(diagramEditor).addDocumentationPage(eq(diagram));
    }

    @Test
    public void testFocus() {
        diagramEditor.onFocus();
        verify(stunnerEditor, times(1)).focus();
        verify(stunnerEditor, never()).lostFocus();
    }

    @Test
    public void testLostFocus() {
        diagramEditor.onLostFocus();
        verify(stunnerEditor, times(1)).lostFocus();
        verify(stunnerEditor, never()).focus();
    }

    @Test
    public void testOnOpen() {
        Collection<UberfireDock> stunnerDocks = new ArrayList<>();
        stunnerDocks.add(propertiesDock);
        stunnerDocks.add(explorerDock);

        String perspectiveIdentifier = "Test Perspective ID";

        when(perspectiveManager.getCurrentPerspective()).thenReturn(currentPerspective);
        when(currentPerspective.getIdentifier()).thenReturn(perspectiveIdentifier);

        when(stunnerDocksHandler.provideDocks(perspectiveIdentifier)).thenReturn(stunnerDocks);

        when(propertiesDock.getPlaceRequest()).thenReturn(propertiesPlace);
        when(propertiesPlace.getIdentifier()).thenReturn(DiagramEditorPropertiesScreen.SCREEN_ID);

        when(explorerDock.getPlaceRequest()).thenReturn(explorerPlace);
        when(explorerPlace.getIdentifier()).thenReturn(DiagramEditorExplorerScreen.SCREEN_ID);

        diagramEditor.onOpen();
        verify(uberfireDocks, times(1)).open(propertiesDock);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddDocumentationPage() {
        when(documentationView.isEnabled()).thenReturn(Boolean.TRUE);
        when(translationService.getValue(StunnerWidgetsConstants.Documentation)).thenReturn(DOC_LABEL);
        when(documentationView.initialize(diagram)).thenReturn(documentationView);
        ArgumentCaptor<DocumentationPage> documentationPageCaptor = ArgumentCaptor.forClass(DocumentationPage.class);
        diagramEditor.addDocumentationPage(diagram);
        verify(translationService).getValue(StunnerWidgetsConstants.Documentation);
        verify(kieView).addPage(documentationPageCaptor.capture());
        DocumentationPage documentationPage = documentationPageCaptor.getValue();
        assertEquals(documentationPage.getDocumentationView(), documentationView);
        assertEquals(documentationPage.getLabel(), DOC_LABEL);
    }

    @Test
    public void testMaximizedState() {
        Collection<UberfireDock> stunnerDocks = new ArrayList<>();
        stunnerDocks.add(propertiesDock);
        stunnerDocks.add(explorerDock);

        String perspectiveIdentifier = "Test Perspective ID";

        when(perspectiveManager.getCurrentPerspective()).thenReturn(currentPerspective);
        when(currentPerspective.getIdentifier()).thenReturn(perspectiveIdentifier);

        when(stunnerDocksHandler.provideDocks(perspectiveIdentifier)).thenReturn(stunnerDocks);

        when(propertiesDock.getPlaceRequest()).thenReturn(propertiesPlace);
        when(propertiesPlace.getIdentifier()).thenReturn(DiagramEditorPropertiesScreen.SCREEN_ID);

        when(explorerDock.getPlaceRequest()).thenReturn(explorerPlace);
        when(explorerPlace.getIdentifier()).thenReturn(DiagramEditorExplorerScreen.SCREEN_ID);

        diagramEditor.onOpen();
        verify(uberfireDocks, times(1)).open(propertiesDock);

        diagramEditor.openPropertiesDocks();
        verify(uberfireDocks, times(2)).open(propertiesDock);

        diagramEditor.onScreenMaximizedEvent(maximizedEvent);
        diagramEditor.onScreenPreMaximizedStateEvent(preMaximizedStateEvent);
        diagramEditor.onScreenMinimizedEvent(minimizedEvent);

        // properties should be opened since it was opened before maximized
        verify(uberfireDocks, times(3)).open(propertiesDock);

        diagramEditor.onOpen();
        verify(uberfireDocks, times(4)).open(propertiesDock);

        diagramEditor.openExplorerDocks();
        verify(uberfireDocks, times(1)).open(explorerDock);

        diagramEditor.onScreenMaximizedEvent(maximizedEvent);
        when(preMaximizedStateEvent.isExplorerScreen()).thenReturn(true);
        diagramEditor.onScreenPreMaximizedStateEvent(preMaximizedStateEvent);
        diagramEditor.onScreenMinimizedEvent(minimizedEvent);

        // explore should be opened since it was opened before maximized
        verify(uberfireDocks, times(2)).open(explorerDock);
    }

    @Test
    public void testDontSaveIfValidationErrors() {
        assertTrue(diagramEditor.isSaveAllowedAfterValidationFailed(Violation.Type.INFO));
        assertTrue(diagramEditor.isSaveAllowedAfterValidationFailed(Violation.Type.WARNING));
        assertFalse(diagramEditor.isSaveAllowedAfterValidationFailed(Violation.Type.ERROR));
    }
}

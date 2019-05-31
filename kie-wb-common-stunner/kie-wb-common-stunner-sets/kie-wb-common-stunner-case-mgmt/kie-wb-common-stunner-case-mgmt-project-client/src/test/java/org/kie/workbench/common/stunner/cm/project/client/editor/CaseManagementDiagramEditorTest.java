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

package org.kie.workbench.common.stunner.cm.project.client.editor;

import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.cm.project.client.type.CaseManagementDiagramResourceType;
import org.kie.workbench.common.stunner.cm.project.service.CaseManagementSwitchViewService;
import org.kie.workbench.common.stunner.core.client.session.command.ManagedClientSessionCommands;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectDiagramEditor;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectDiagramEditorTest;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectEditorMenuSessionItems;
import org.kie.workbench.common.stunner.project.client.session.EditorSessionCommands;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.mockito.Mock;
import org.uberfire.mvp.PlaceRequest;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Ignore
@RunWith(GwtMockitoTestRunner.class)
public class CaseManagementDiagramEditorTest extends AbstractProjectDiagramEditorTest {

    @Mock
    private CaseManagementProjectEditorMenuSessionItems cmMenuSessionItems;

    @Mock
    private PlaceRequest currentPlace;

    @Mock
    private Caller<CaseManagementSwitchViewService> caseManagementSwitchViewServiceCaller;

    @Mock
    private CaseManagementSwitchViewService caseManagementSwitchViewService;

    private CaseManagementDiagramEditor tested;

    @Before
    @Override
    public void setUp() {
        super.setUp();

        final EditorSessionCommands editorSessionCommands = mock(EditorSessionCommands.class);
        final ManagedClientSessionCommands managedClientSessionCommands = mock(ManagedClientSessionCommands.class);
        when(editorSessionCommands.getCommands()).thenReturn(managedClientSessionCommands);
        when(cmMenuSessionItems.getCommands()).thenReturn(editorSessionCommands);

        RemoteCallback[] remoteCallback = new RemoteCallback[1];

        when(caseManagementSwitchViewServiceCaller.call(any(RemoteCallback.class))).thenAnswer(invocation -> {
            remoteCallback[0] = invocation.getArgumentAt(0, RemoteCallback.class);
            return caseManagementSwitchViewService;
        });

        when(caseManagementSwitchViewService.switchView(any(Diagram.class), any(String.class), any(String.class))).thenAnswer(invocation -> {
            if (remoteCallback.length > 0 && remoteCallback[0] != null) {
                final ProjectDiagram projectDiagram = mock(ProjectDiagram.class);
                remoteCallback[0].callback(Optional.of(projectDiagram));
                return Optional.of(projectDiagram);
            }
            return null;
        });
    }

    @Override
    protected AbstractProjectDiagramEditor createDiagramEditor() {
        tested = spy(new CaseManagementDiagramEditor(view,
                                                     documentationView,
                                                     placeManager,
                                                     errorPopupPresenter,
                                                     changeTitleNotificationEvent,
                                                     savePopUpPresenter,
                                                     (CaseManagementDiagramResourceType) getResourceType(),
                                                     clientProjectDiagramService,
                                                     sessionEditorPresenters,
                                                     sessionViewerPresenters,
                                                     cmMenuSessionItems,
                                                     onDiagramFocusEvent,
                                                     onDiagramLostFocusEvent,
                                                     projectMessagesListener,
                                                     diagramClientErrorHandler,
                                                     translationService,
                                                     xmlEditorView,
                                                     projectDiagramResourceServiceCaller,
                                                     caseManagementSwitchViewServiceCaller) {
            {
                {
                    docks = defaultEditorDock;
                    perspectiveManager = perspectiveManagerMock;
                    fileMenuBuilder = CaseManagementDiagramEditorTest.this.fileMenuBuilder;
                    workbenchContext = CaseManagementDiagramEditorTest.this.workbenchContext;
                    projectController = CaseManagementDiagramEditorTest.this.projectController;
                    versionRecordManager = CaseManagementDiagramEditorTest.this.versionRecordManager;
                    alertsButtonMenuItemBuilder = CaseManagementDiagramEditorTest.this.alertsButtonMenuItemBuilder;
                    place = CaseManagementDiagramEditorTest.this.currentPlace;
                    kieView = CaseManagementDiagramEditorTest.this.kieView;
                    overviewWidget = CaseManagementDiagramEditorTest.this.overviewWidget;
                    notification = CaseManagementDiagramEditorTest.this.notification;
                }
            }
        });

        tested.open(mock(ProjectDiagram.class));
        when(sessionEditorPresenters.get()).thenReturn(sessionEditorPresenter);

        return tested;
    }

    @Override
    protected AbstractProjectEditorMenuSessionItems getMenuSessionItems() {
        return cmMenuSessionItems;
    }

    @Override
    protected CaseManagementDiagramResourceType mockResourceType() {
        final CaseManagementDiagramResourceType resourceType = mock(CaseManagementDiagramResourceType.class);
        when(resourceType.getSuffix()).thenReturn("bpmn-cm");
        when(resourceType.getShortName()).thenReturn("Case Management");
        return resourceType;
    }

    @Test
    public void testReopenSession() {
        final ProjectDiagram projectDiagram = mock(ProjectDiagram.class);

        tested.reopenSession(projectDiagram);

        verify(sessionEditorPresenter, times(1)).destroy();
        verify(sessionEditorPresenter, times(1)).open(eq(projectDiagram), any(SessionPresenter.SessionPresenterCallback.class));
    }

    @Test
    public void testOnSwitch() {
        final Diagram diagram = mock(Diagram.class);
        final String defSetId = "defSetId";
        final String shapeDefId = "shapeDefId";

        tested.onSwitch(diagram, defSetId, shapeDefId);

        verify(view, times(1)).showLoading();
        verify(view, times(1)).hideBusyIndicator();
        verify(sessionEditorPresenter, times(1)).destroy();
        verify(sessionEditorPresenter, times(1)).open(any(ProjectDiagram.class), any(SessionPresenter.SessionPresenterCallback.class));
    }
}
/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.project.client.editor;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenterFactory;
import org.kie.workbench.common.stunner.core.client.api.AbstractClientSessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.ClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.ClientSessionFactory;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ClearSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ClearStatesSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.DeleteSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.RedoSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.SessionCommandFactory;
import org.kie.workbench.common.stunner.core.client.session.command.impl.SwitchGridSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.UndoSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ValidateSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.VisitGraphSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientReadOnlySession;
import org.kie.workbench.common.stunner.core.client.session.impl.ClientFullSessionImpl;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramLoseFocusEvent;
import org.kie.workbench.common.stunner.project.client.screens.ProjectMessagesListener;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.events.PlaceHiddenEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ProjectDiagramEditorTest {

    @Mock
    EventSourceMock<ChangeTitleWidgetEvent> changeTitleNotification;
    @Mock
    EventSourceMock<NotificationEvent> notification;
    @Mock
    VersionRecordManager versionRecordManager;
    @Mock
    BasicFileMenuBuilder menuBuilder;
    @Mock
    DefaultFileNameValidator fileNameValidator;
    @Mock
    PlaceRequest placeRequest;
    @Mock
    AbstractProjectDiagramEditor.View view;
    @Mock
    PlaceManager placeManager;
    @Mock
    ErrorPopupPresenter errorPopupPresenter;
    @Mock
    EventSourceMock<ChangeTitleWidgetEvent> changeTitleNotificationEvent;
    @Mock
    SavePopUpPresenter savePopUpPresenter;
    @Mock
    ClientResourceType resourceType;
    @Mock
    ClientProjectDiagramService projectDiagramServices;
    @Mock
    AbstractClientSessionManager clientSessionManager;
    @Mock
    SessionPresenterFactory<Diagram, AbstractClientReadOnlySession, AbstractClientFullSession> presenterFactory;
    @Mock
    SessionPresenter presenter;
    @Mock
    SessionCommandFactory sessionCommandFactory;
    @Mock
    ProjectDiagramEditorMenuItemsBuilder menuItemsBuilder;
    @Mock
    ClearStatesSessionCommand sessionClearStatesCommand;
    @Mock
    VisitGraphSessionCommand sessionVisitGraphCommand;
    @Mock
    SwitchGridSessionCommand sessionSwitchGridCommand;
    @Mock
    ClearSessionCommand sessionClearCommand;
    @Mock
    DeleteSelectionSessionCommand sessionDeleteSelectionCommand;
    @Mock
    UndoSessionCommand sessionUndoCommand;
    @Mock
    RedoSessionCommand sessionRedoCommand;
    @Mock
    ValidateSessionCommand sessionValidateCommand;
    @Mock
    ClientFullSessionImpl fullSession;
    @Mock
    ObservablePath path;
    @Mock
    ProjectDiagram diagram;
    @Mock
    ProjectMetadata metadata;
    @Mock
    OverviewWidgetPresenter overviewWidgetMock;
    @Mock
    ClientSessionFactory<ClientFullSession> clientSessionFactory;
    @Mock
    AbstractCanvasHandler canvasHandler;
    @Mock
    EventSourceMock<OnDiagramFocusEvent> onDiagramFocusEvent;
    @Mock
    EventSourceMock<OnDiagramLoseFocusEvent> onDiagramLostFocusEven;
    @Mock
    ProjectMessagesListener projectMessagesListener;

    private ProjectDiagramEditorStub tested;

    @Before
    public void setup() throws Exception {
        VersionRecordManager versionRecordManagerMock = versionRecordManager;
        when(versionRecordManager.getCurrentPath()).thenReturn(path);
        when(sessionCommandFactory.newClearCommand()).thenReturn(sessionClearCommand);
        when(sessionCommandFactory.newClearStatesCommand()).thenReturn(sessionClearStatesCommand);
        when(sessionCommandFactory.newVisitGraphCommand()).thenReturn(sessionVisitGraphCommand);
        when(sessionCommandFactory.newSwitchGridCommand()).thenReturn(sessionSwitchGridCommand);
        when(sessionCommandFactory.newDeleteSelectedElementsCommand()).thenReturn(sessionDeleteSelectionCommand);
        when(sessionCommandFactory.newUndoCommand()).thenReturn(sessionUndoCommand);
        when(sessionCommandFactory.newRedoCommand()).thenReturn(sessionRedoCommand);
        when(sessionCommandFactory.newValidateCommand()).thenReturn(sessionValidateCommand);
        when(presenterFactory.newPresenterEditor()).thenReturn(presenter);
        when(clientSessionManager.getCurrentSession()).thenReturn(fullSession);
        when(clientSessionManager.getSessionFactory(diagram,
                                                    ClientFullSession.class)).thenReturn(clientSessionFactory);
        when(clientSessionFactory.newSession()).thenReturn((fullSession));
        when(presenter.getInstance()).thenReturn(fullSession);
        when(presenter.withToolbar(anyBoolean())).thenReturn(presenter);
        when(presenter.withPalette(anyBoolean())).thenReturn(presenter);
        when(presenter.displayNotifications(any())).thenReturn(presenter);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getTitle()).thenReturn("Title");
        when(fullSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);

        when(placeRequest.getIdentifier()).thenReturn(ProjectDiagramEditorStub.EDITOR_ID);

        ClientProjectDiagramService mock = Mockito.mock(ClientProjectDiagramService.class);

        this.tested = new ProjectDiagramEditorStub(view,
                                                   placeManager,
                                                   errorPopupPresenter,
                                                   changeTitleNotificationEvent,
                                                   savePopUpPresenter,
                                                   resourceType,
                                                   projectDiagramServices,
                                                   clientSessionManager,
                                                   presenterFactory,
                                                   sessionCommandFactory,
                                                   menuItemsBuilder,
                                                   onDiagramFocusEvent,
                                                   onDiagramLostFocusEven,
                                                   projectMessagesListener
        ) {
            {
                overviewWidget = overviewWidgetMock;
                versionRecordManager = versionRecordManagerMock;
                place = placeRequest;
            }
        };
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInit() {
        tested.init();
        verify(view,
               times(1)).init(eq(tested));
        verify(sessionClearStatesCommand,
               times(0)).bind(eq(fullSession));
        verify(sessionVisitGraphCommand,
               times(0)).bind(eq(fullSession));
        verify(sessionSwitchGridCommand,
               times(0)).bind(eq(fullSession));
        verify(sessionClearCommand,
               times(0)).bind(eq(fullSession));
        verify(sessionDeleteSelectionCommand,
               times(0)).bind(eq(fullSession));
        verify(sessionUndoCommand,
               times(0)).bind(eq(fullSession));
        verify(sessionRedoCommand,
               times(0)).bind(eq(fullSession));
        verify(sessionValidateCommand,
               times(0)).bind(eq(fullSession));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testValidateBeforeSave() {
        tested.save();
        verify(sessionValidateCommand,
               times(1)).execute(any(ClientSessionCommand.Callback.class));
    }

    // TODO: @Test - versionRecordManager is not being set.
    @SuppressWarnings("unchecked")
    public void testLoadContent() {
        tested.loadContent();
        verify(projectDiagramServices,
               times(1)).getByPath(eq(path),
                                   any(ServiceCallback.class));
    }

    @Test
    public void testIsDirty() {
        tested.init();
        tested.open(diagram);
        assertFalse(tested.isDirty(tested.getCurrentDiagramHash()));
        tested.setOriginalHash(~~(tested.getCurrentDiagramHash() + 1));
        assertTrue(tested.isDirty(tested.getCurrentDiagramHash()));
    }

    @Test
    public void testHasChanges() {
        tested.init();
        tested.open(diagram);
        assertFalse(tested.hasUnsavedChanges());
        tested.setOriginalHash(~~(tested.getCurrentDiagramHash() + 1));
        assertTrue(tested.hasUnsavedChanges());
        tested.setOriginalHash(~~(tested.getCurrentDiagramHash()));
        assertFalse(tested.hasUnsavedChanges());
    }

    @Test
    public void testOnPlaceHiddenEvent() {
        PlaceHiddenEvent event = new PlaceHiddenEvent(placeRequest);

        tested.hideDiagramEditorDocks(event);

        verify(onDiagramLostFocusEven).fire(any(OnDiagramLoseFocusEvent.class));
    }

    @Test
    public void testNotValidOnPlaceHiddenEvent() {

        PlaceRequest anotherRequest = mock(PlaceRequest.class);

        when(anotherRequest.getIdentifier()).thenReturn("");

        PlaceHiddenEvent event = new PlaceHiddenEvent(anotherRequest);

        tested.hideDiagramEditorDocks(event);

        verify(onDiagramLostFocusEven,
               never()).fire(any(OnDiagramLoseFocusEvent.class));
    }

    @Test
    public void testOnPlaceGainFocusEvent() {
        PlaceGainFocusEvent event = new PlaceGainFocusEvent(placeRequest);

        tested.showDiagramEditorDocks(event);

        verify(onDiagramFocusEvent).fire(any(OnDiagramFocusEvent.class));
    }

    @Test
    public void testNotValidOnPlaceGainFocusEvent() {

        PlaceRequest anotherRequest = mock(PlaceRequest.class);

        when(anotherRequest.getIdentifier()).thenReturn("");

        PlaceGainFocusEvent event = new PlaceGainFocusEvent(anotherRequest);

        tested.showDiagramEditorDocks(event);

        verify(onDiagramFocusEvent,
               never()).fire(any(OnDiagramFocusEvent.class));
    }
}

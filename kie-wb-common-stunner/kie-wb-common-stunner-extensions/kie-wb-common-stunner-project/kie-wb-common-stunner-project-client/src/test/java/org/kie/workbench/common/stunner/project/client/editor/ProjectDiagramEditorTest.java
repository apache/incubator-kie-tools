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

import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenterFactory;
import org.kie.workbench.common.stunner.core.client.api.AbstractClientSessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.error.DiagramClientErrorHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.ClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.ClientSessionFactory;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ClearSessionCommand;
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
import org.kie.workbench.common.stunner.core.definition.exception.DefinitionNotFoundException;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramLoseFocusEvent;
import org.kie.workbench.common.stunner.project.client.screens.ProjectMessagesListener;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.events.PlaceHiddenEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(PathPlaceRequest.class)
public class ProjectDiagramEditorTest {

    @Mock
    private VersionRecordManager versionRecordManager;
    @Mock
    private PlaceRequest placeRequest;
    @Mock
    private AbstractProjectDiagramEditor.View view;
    @Mock
    private PlaceManager placeManager;
    @Mock
    private ErrorPopupPresenter errorPopupPresenter;
    @Mock
    private EventSourceMock<ChangeTitleWidgetEvent> changeTitleNotificationEvent;
    @Mock
    private SavePopUpPresenter savePopUpPresenter;
    @Mock
    private ClientResourceType resourceType;
    @Mock
    private ClientProjectDiagramService projectDiagramServices;
    @Mock
    private AbstractClientSessionManager clientSessionManager;
    @Mock
    private SessionPresenterFactory<Diagram, AbstractClientReadOnlySession, AbstractClientFullSession> presenterFactory;
    @Mock
    private SessionPresenter presenter;
    @Mock
    private SessionCommandFactory sessionCommandFactory;
    @Mock
    private ProjectDiagramEditorMenuItemsBuilder menuItemsBuilder;
    @Mock
    private VisitGraphSessionCommand sessionVisitGraphCommand;
    @Mock
    private SwitchGridSessionCommand sessionSwitchGridCommand;
    @Mock
    private ClearSessionCommand sessionClearCommand;
    @Mock
    private DeleteSelectionSessionCommand sessionDeleteSelectionCommand;
    @Mock
    private UndoSessionCommand sessionUndoCommand;
    @Mock
    private RedoSessionCommand sessionRedoCommand;
    @Mock
    private ValidateSessionCommand sessionValidateCommand;
    @Mock
    private ClientFullSessionImpl fullSession;
    @Mock
    private ObservablePath path;
    @Mock
    private ProjectDiagram diagram;
    @Mock
    private ProjectMetadata metadata;
    @Mock
    private  OverviewWidgetPresenter overviewWidgetMock;
    @Mock
    private ClientSessionFactory<ClientFullSession> clientSessionFactory;
    @Mock
    private AbstractCanvasHandler canvasHandler;
    @Mock
    private EventSourceMock<OnDiagramFocusEvent> onDiagramFocusEvent;
    @Mock
    private EventSourceMock<OnDiagramLoseFocusEvent> onDiagramLostFocusEven;
    @Mock
    private ProjectMessagesListener projectMessagesListener;
    @Mock
    private DiagramClientErrorHandler diagramClientErrorHandler;
    @Mock
    private ClientTranslationService translationService;

    private ProjectDiagramEditorStub tested;

    @Before
    public void setup() {
        VersionRecordManager versionRecordManagerMock = versionRecordManager;
        when(versionRecordManager.getCurrentPath()).thenReturn(path);
        when(sessionCommandFactory.newClearCommand()).thenReturn(sessionClearCommand);
        when(sessionCommandFactory.newVisitGraphCommand()).thenReturn(sessionVisitGraphCommand);
        when(sessionCommandFactory.newSwitchGridCommand()).thenReturn(sessionSwitchGridCommand);
        when(sessionCommandFactory.newDeleteSelectedElementsCommand()).thenReturn(sessionDeleteSelectionCommand);
        when(sessionCommandFactory.newUndoCommand()).thenReturn(sessionUndoCommand);
        when(sessionCommandFactory.newRedoCommand()).thenReturn(sessionRedoCommand);
        when(sessionCommandFactory.newValidateCommand()).thenReturn(sessionValidateCommand);
        when(presenterFactory.newPresenterEditor()).thenReturn(presenter);
        when(clientSessionManager.getCurrentSession()).thenReturn(fullSession);
        when(clientSessionManager.getSessionFactory(metadata,
                                                    ClientFullSession.class)).thenReturn(clientSessionFactory);
        doAnswer(invocationOnMock -> {
            final Consumer<ClientFullSessionImpl> sessionConsumer =
                    (Consumer<ClientFullSessionImpl>) invocationOnMock.getArguments()[1];
            sessionConsumer.accept(fullSession);
            return null;
        }).when(clientSessionFactory).newSession(eq(metadata),
                                                     any(Consumer.class));
        when(presenter.getInstance()).thenReturn(fullSession);
        when(presenter.withToolbar(anyBoolean())).thenReturn(presenter);
        when(presenter.withPalette(anyBoolean())).thenReturn(presenter);
        when(presenter.displayNotifications(any())).thenReturn(presenter);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getTitle()).thenReturn("Title");
        when(fullSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);

        when(placeRequest.getIdentifier()).thenReturn(ProjectDiagramEditorStub.EDITOR_ID);

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
                                                   projectMessagesListener,
                                                   diagramClientErrorHandler,
                                                   translationService
        ) {
            {
                overviewWidget = overviewWidgetMock;
                versionRecordManager = versionRecordManagerMock;
                place = placeRequest;
            }
        };
        tested.init();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInit() {
        verify(view,
               times(1)).init(eq(tested));
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
    public void testLoadContentError() {
        ArgumentCaptor<ServiceCallback> callbackArgumentCaptor = forClass(ServiceCallback.class);

        tested.loadContent();

        verify(projectDiagramServices,
               times(1)).getByPath(eq(path),
                                   callbackArgumentCaptor.capture());

        callbackArgumentCaptor.getValue().onError(new ClientRuntimeError(new DefinitionNotFoundException()));

        verify(placeManager,
               times(1)).forceClosePlace(any(PathPlaceRequest.class));

        ArgumentCaptor<Consumer> consumerArgumentCaptor = forClass(Consumer.class);
        verify(diagramClientErrorHandler,
               times(1)).handleError(any(ClientRuntimeError.class),
                                     consumerArgumentCaptor.capture());
        consumerArgumentCaptor.getValue().accept("error message");
        verify(errorPopupPresenter,
               times(1)).showMessage("error message");
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

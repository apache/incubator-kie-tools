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

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerPresenter;
import org.kie.workbench.common.stunner.core.client.ManagedInstanceStub;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.error.DiagramClientErrorHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistry;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ValidateSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.definition.exception.DefinitionNotFoundException;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.documentation.DocumentationView;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.preferences.StunnerDiagramEditorPreferences;
import org.kie.workbench.common.stunner.core.preferences.StunnerPreferences;
import org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditorMenuSessionItems;
import org.kie.workbench.common.stunner.kogito.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.kogito.client.editor.event.OnDiagramLoseFocusEvent;
import org.kie.workbench.common.stunner.kogito.client.session.EditorSessionCommands;
import org.kie.workbench.common.stunner.project.client.resources.i18n.StunnerProjectClientConstants;
import org.kie.workbench.common.stunner.project.client.screens.ProjectMessagesListener;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.kie.workbench.common.stunner.project.diagram.editor.ProjectDiagramResource;
import org.kie.workbench.common.stunner.project.diagram.impl.ProjectDiagramImpl;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramResourceService;
import org.kie.workbench.common.widgets.client.docks.DefaultEditorDock;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(PathPlaceRequest.class)
public class ProjectDiagramEditorTest {

    private static final String SAVE_MESSAGE = "save";

    private static final String ERROR_MESSAGE = "error";

    @Mock
    private PerspectiveManager perspectiveManager;

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
    private SessionManager clientSessionManager;

    @Mock
    private SessionEditorPresenter<EditorSession> sessionEditorPresenter;
    private ManagedInstance<SessionEditorPresenter<EditorSession>> sessionEditorPresenters;

    @Mock
    private SessionViewerPresenter<ViewerSession> sessionViewerPresenter;
    private ManagedInstance<SessionViewerPresenter<ViewerSession>> sessionViewerPresenters;

    @Mock
    private SessionPresenter.View presenterView;

    @Mock
    private AbstractDiagramEditorMenuSessionItems sessionItems;

    @Mock
    private EditorSessionCommands editorSessionCommands;

    @Mock
    private ValidateSessionCommand sessionValidateCommand;

    @Mock
    private EditorSession fullSession;

    @Mock
    private ObservablePath path;

    @Mock
    private ProjectDiagramImpl diagram;

    @Mock
    private Graph graph;

    @Mock
    private DefinitionSet definitionSetContent;

    @Mock
    private ProjectMetadata metadata;

    @Mock
    private Overview overview;

    @Mock
    private Metadata kieMetadata;

    @Mock
    private OverviewWidgetPresenter overviewWidgetMock;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private EventSourceMock<OnDiagramFocusEvent> onDiagramFocusEvent;

    @Mock
    private EventSourceMock<OnDiagramLoseFocusEvent> onDiagramLostFocusEvent;

    @Mock
    private EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    private ProjectMessagesListener projectMessagesListener;

    @Mock
    private DiagramClientErrorHandler diagramClientErrorHandler;

    @Mock
    private ClientTranslationService translationService;

    @Captor
    private ArgumentCaptor<ServiceCallback<ProjectDiagram>> serviceCallbackCaptor;

    @Captor
    private ArgumentCaptor<Consumer<String>> consumerCaptor;

    @Captor
    private ArgumentCaptor<ParameterizedCommand<String>> savePopupCommandCaptor;

    @Mock
    private TextEditorView xmlEditorView;

    @Mock
    private StunnerPreferences preferences;

    @Mock
    private StunnerDiagramEditorPreferences diagramEditorPreferences;

    @Mock
    private StunnerPreferencesRegistry stunnerPreferencesRegistr;

    @Mock
    private Caller<ProjectDiagramResourceService> projectDiagramResourceServiceCaller;

    private ProjectDiagramEditorStub presenter;

    private AbstractProjectDiagramEditorCore<ProjectMetadata, ProjectDiagram, ProjectDiagramResource, ProjectDiagramEditorProxy<ProjectDiagramResource>> presenterCore;

    @Mock
    private DocumentationView documentationView;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        sessionEditorPresenters = new ManagedInstanceStub<>(sessionEditorPresenter);
        sessionViewerPresenters = new ManagedInstanceStub<>(sessionViewerPresenter);
        VersionRecordManager versionRecordManagerMock = versionRecordManager;
        when(versionRecordManager.getCurrentPath()).thenReturn(path);
        when(sessionItems.setErrorConsumer(any(Consumer.class))).thenReturn(sessionItems);
        when(sessionItems.setLoadingStarts(any(Command.class))).thenReturn(sessionItems);
        when(sessionItems.setLoadingCompleted(any(Command.class))).thenReturn(sessionItems);
        when(sessionItems.getCommands()).thenReturn(editorSessionCommands);
        when(editorSessionCommands.getValidateSessionCommand()).thenReturn(sessionValidateCommand);
        when(sessionEditorPresenter.getInstance()).thenReturn(fullSession);
        when(sessionEditorPresenter.withToolbar(anyBoolean())).thenReturn(sessionEditorPresenter);
        when(sessionEditorPresenter.withPalette(anyBoolean())).thenReturn(sessionEditorPresenter);
        when(sessionEditorPresenter.displayNotifications(any())).thenReturn(sessionEditorPresenter);
        when(sessionEditorPresenter.getView()).thenReturn(presenterView);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Diagram diagram = (Diagram) invocationOnMock.getArguments()[0];
                SessionPresenter.SessionPresenterCallback callback = (SessionPresenter.SessionPresenterCallback) invocationOnMock.getArguments()[1];
                callback.onOpen(diagram);
                callback.afterCanvasInitialized();
                callback.afterSessionOpened();
                callback.onSuccess();
                return null;
            }
        }).when(sessionEditorPresenter).open(any(Diagram.class),
                                             any(SessionPresenter.SessionPresenterCallback.class));
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getTitle()).thenReturn("Title");
        when(metadata.getOverview()).thenReturn(overview);
        when(overview.getMetadata()).thenReturn(kieMetadata);
        when(fullSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);

        when(resourceType.getSuffix()).thenReturn("bpmn");
        when(resourceType.getShortName()).thenReturn("bpmn");
        when(placeRequest.getIdentifier()).thenReturn(ProjectDiagramEditorStub.EDITOR_ID);
        when(diagram.getGraph()).thenReturn(graph);
        when(graph.getContent()).thenReturn(definitionSetContent);
        when(stunnerPreferencesRegistr.get(StunnerPreferences.class)).thenReturn(preferences);
        when(preferences.getDiagramEditorPreferences()).thenReturn(diagramEditorPreferences);

        this.presenter = new ProjectDiagramEditorStub(view,
                                                      xmlEditorView,
                                                      sessionEditorPresenters,
                                                      sessionViewerPresenters,
                                                      onDiagramFocusEvent,
                                                      onDiagramLostFocusEvent,
                                                      notificationEvent,
                                                      errorPopupPresenter,
                                                      diagramClientErrorHandler,
                                                      documentationView,
                                                      resourceType,
                                                      sessionItems,
                                                      projectMessagesListener,
                                                      translationService,
                                                      projectDiagramServices,
                                                      projectDiagramResourceServiceCaller,
                                                      placeManager,
                                                      changeTitleNotificationEvent,
                                                      savePopUpPresenter) {
            {
                docks = mock(DefaultEditorDock.class);
                perspectiveManager = ProjectDiagramEditorTest.this.perspectiveManager;
                overviewWidget = overviewWidgetMock;
                versionRecordManager = versionRecordManagerMock;
                place = placeRequest;
                kieView = mock(KieEditorWrapperView.class);
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
        };
        presenter.init();
        presenterCore.setEditorSessionPresenter(sessionEditorPresenter);

        when(translationService.getValue(anyString())).thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInit() {
        verify(sessionItems,
               times(0)).bind(eq(fullSession));
    }

    // TODO @Test
    @SuppressWarnings("unchecked")
    public void testValidateBeforeSave() {
        presenter.save();
        verify(sessionValidateCommand,
               times(1)).execute(any(ClientSessionCommand.Callback.class));
    }

    // TODO: @Test - versionRecordManager is not being set.
    @SuppressWarnings("unchecked")
    public void testLoadContent() {
        presenter.loadContent();
        verify(projectDiagramServices,
               times(1)).getByPath(eq(path),
                                   any(ServiceCallback.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoadContentError() {
        ArgumentCaptor<ServiceCallback> callbackArgumentCaptor = forClass(ServiceCallback.class);

        presenter.loadContent();

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
        presenter.init();
        presenter.open(diagram);
        assertFalse(presenter.isDirty(presenter.getCurrentDiagramHash()));
        presenter.setOriginalHash(~~(presenter.getCurrentDiagramHash() + 1));
        assertTrue(presenter.isDirty(presenter.getCurrentDiagramHash()));
    }

    @Test
    public void testHasChanges() {
        presenter.init();
        presenter.open(diagram);
        assertFalse(presenter.hasUnsavedChanges());
        presenter.setOriginalHash(~~(presenter.getCurrentDiagramHash() + 1));
        assertTrue(presenter.hasUnsavedChanges());
        presenter.setOriginalHash(~~(presenter.getCurrentDiagramHash()));
        assertFalse(presenter.hasUnsavedChanges());
    }

    @Test
    public void testOnSaveWithoutChanges() {
        presenter.open(diagram);
        when(versionRecordManager.isCurrentLatest()).thenReturn(true);

        presenter.onSave();

        verify(presenterView).showMessage(CommonConstants.INSTANCE.NoChangesSinceLastSave());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnSaveWithChanges() {
        presenter.open(diagram);
        presenter.setOriginalHash(diagram.hashCode() + 1);
        doAnswer(i -> {
            ((ClientSessionCommand.Callback) i.getArguments()[0]).onSuccess();
            return null;
        }).when(sessionValidateCommand).execute(any(ClientSessionCommand.Callback.class));

        presenter.onSave();

        assertOnSaveSavedDiagram();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnSaveRestore() {
        presenter.open(diagram);
        doAnswer(i -> {
            ((ClientSessionCommand.Callback) i.getArguments()[0]).onSuccess();
            return null;
        }).when(sessionValidateCommand).execute(any(ClientSessionCommand.Callback.class));

        presenter.onSave();

        assertOnSaveSavedDiagram();
    }

    private void assertOnSaveSavedDiagram() {
        verify(savePopUpPresenter).show(eq(path),
                                        savePopupCommandCaptor.capture());

        final ParameterizedCommand<String> savePopupCommand = savePopupCommandCaptor.getValue();
        savePopupCommand.execute(SAVE_MESSAGE);

        verify(projectDiagramServices).saveOrUpdate(eq(path),
                                                    eq(diagram),
                                                    any(Metadata.class),
                                                    eq(SAVE_MESSAGE),
                                                    serviceCallbackCaptor.capture());
    }

    // TODO @Test
    public void testSaveWithCommitMessageOnSuccess() {
        presenter.save(SAVE_MESSAGE);

        verify(view).showSaving();

        verify(projectDiagramServices).saveOrUpdate(eq(path),
                                                    any(ProjectDiagramImpl.class),
                                                    any(Metadata.class),
                                                    eq(SAVE_MESSAGE),
                                                    serviceCallbackCaptor.capture());

        final ServiceCallback<ProjectDiagram> serviceCallback = serviceCallbackCaptor.getValue();
        final ProjectDiagramImpl diagram = mock(ProjectDiagramImpl.class);
        serviceCallback.onSuccess(diagram);

        verify(view).hideBusyIndicator();
        verify(versionRecordManager).reloadVersions(eq(path));
        verify(presenterView).showMessage(StunnerProjectClientConstants.DIAGRAM_SAVE_SUCCESSFUL);
    }

    // TODO @Test
    public void testSaveWithCommitMessageOnError() {
        presenter.save(SAVE_MESSAGE);

        verify(view).showSaving();

        verify(projectDiagramServices).saveOrUpdate(eq(path),
                                                    any(ProjectDiagramImpl.class),
                                                    any(Metadata.class),
                                                    eq(SAVE_MESSAGE),
                                                    serviceCallbackCaptor.capture());

        final ServiceCallback<ProjectDiagram> serviceCallback = serviceCallbackCaptor.getValue();
        final ClientRuntimeError error = mock(ClientRuntimeError.class);
        serviceCallback.onError(error);

        verify(diagramClientErrorHandler).handleError(eq(error),
                                                      consumerCaptor.capture());

        final Consumer<String> consumer = consumerCaptor.getValue();
        consumer.accept(ERROR_MESSAGE);

        verify(errorPopupPresenter).showMessage(ERROR_MESSAGE);
    }

    @Test
    public void testShowLoadingViews() {
        presenter.showLoadingViews();

        verify(view).showLoading();
    }

    @Test
    public void testShowSavingViews() {
        presenter.showSavingViews();

        verify(view).showSaving();
    }

    @Test
    public void testHideLoadingViews() {
        presenter.hideLoadingViews();

        verify(view).hideBusyIndicator();
    }
}

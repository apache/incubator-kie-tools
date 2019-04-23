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

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.messageconsole.client.console.widget.button.AlertsButtonMenuItemBuilder;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerPresenter;
import org.kie.workbench.common.stunner.core.client.ManagedInstanceStub;
import org.kie.workbench.common.stunner.core.client.error.DiagramClientErrorHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ValidateSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramParsingException;
import org.kie.workbench.common.stunner.core.documentation.DocumentationPage;
import org.kie.workbench.common.stunner.core.documentation.DocumentationView;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.preferences.StunnerDiagramEditorPreferences;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramLoseFocusEvent;
import org.kie.workbench.common.stunner.project.client.resources.i18n.StunnerProjectClientConstants;
import org.kie.workbench.common.stunner.project.client.screens.ProjectMessagesListener;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.kie.workbench.common.stunner.project.client.session.EditorSessionCommands;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.kie.workbench.common.stunner.project.editor.ProjectDiagramResource;
import org.kie.workbench.common.stunner.project.editor.impl.ProjectDiagramResourceImpl;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramResourceService;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramService;
import org.kie.workbench.common.widgets.client.docks.DefaultEditorDock;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.validation.AssetUpdateValidator;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
import org.uberfire.ext.widgets.common.client.ace.AceEditorMode;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.project.editor.ProjectDiagramResource.Type.PROJECT_DIAGRAM;
import static org.kie.workbench.common.stunner.project.editor.ProjectDiagramResource.Type.XML_DIAGRAM;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractProjectDiagramEditorTest {

    private static final String TITLE = "title";
    public static final String DOC_LABEL = "doc";

    @Mock
    protected PerspectiveManager perspectiveManagerMock;

    @Mock
    protected ProjectDiagram diagram;

    @Mock
    protected AbstractProjectDiagramEditor.View view;

    @Mock
    protected PlaceManager placeManager;

    @Mock
    protected ErrorPopupPresenter errorPopupPresenter;

    @Mock
    protected EventSourceMock<ChangeTitleWidgetEvent> changeTitleNotificationEvent;

    @Mock
    protected SavePopUpPresenter savePopUpPresenter;

    @Mock
    protected EventSourceMock<NotificationEvent> notification;

    @Mock
    protected ClientProjectDiagramService clientProjectDiagramService;

    @Mock
    protected SessionEditorPresenter<EditorSession> sessionEditorPresenter;
    protected ManagedInstance<SessionEditorPresenter<EditorSession>> sessionEditorPresenters;

    @Mock
    protected SessionViewerPresenter<ViewerSession> sessionViewerPresenter;
    protected ManagedInstance<SessionViewerPresenter<ViewerSession>> sessionViewerPresenters;

    @Mock
    protected AbstractProjectEditorMenuSessionItems projectMenuSessionItems;

    @Mock
    protected EventSourceMock<OnDiagramFocusEvent> onDiagramFocusEvent;

    @Mock
    protected EventSourceMock<OnDiagramLoseFocusEvent> onDiagramLostFocusEvent;

    @Mock
    protected BasicFileMenuBuilder menuBuilder;

    @Mock
    protected VersionRecordManager versionRecordManager;

    @Spy
    @InjectMocks
    protected FileMenuBuilderImpl fileMenuBuilder;

    @Mock
    protected ProjectController projectController;

    @Mock
    protected WorkspaceProjectContext workbenchContext;
    @Mock
    protected ProjectMessagesListener projectMessagesListener;
    protected ClientResourceType resourceType;
    @Mock
    protected DiagramClientErrorHandler diagramClientErrorHandler;
    @Mock
    protected ClientTranslationService translationService;
    @Mock
    protected AlertsButtonMenuItemBuilder alertsButtonMenuItemBuilder;
    @Mock
    protected SessionPresenter.View sessionPresenterView;
    @Mock
    protected EditorSession editorSession;
    @Mock
    protected ViewerSession viewerSession;
    @Mock
    protected ObservablePath filePath;
    @Mock
    protected KieEditorWrapperView kieView;
    @Mock
    protected OverviewWidgetPresenter overviewWidget;
    @Mock
    protected TextEditorView xmlEditorView;
    @Mock
    protected Caller<ProjectDiagramResourceService> projectDiagramResourceServiceCaller;

    @Mock
    protected StunnerDiagramEditorPreferences diagramEditorPreferences;
    @Mock
    protected Caller<ProjectDiagramService> diagramServiceCaller;
    protected AbstractProjectDiagramEditor<ClientResourceTypeMock> presenter;
    @Mock
    protected DefaultEditorDock defaultEditorDock;
    @Captor
    private ArgumentCaptor<Consumer<EditorSession>> clientSessionConsumerCaptor;
    @Captor
    protected ArgumentCaptor<DocumentationPage> documentationPageCaptor;

    private ArgumentCaptor<SessionPresenter.SessionPresenterCallback> clientSessionPresenterCallbackCaptor;
    @Mock
    protected DocumentationView documentationView;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        sessionEditorPresenters = new ManagedInstanceStub<>(sessionEditorPresenter);
        sessionViewerPresenters = new ManagedInstanceStub<>(sessionViewerPresenter);
        when(sessionEditorPresenter.getInstance()).thenReturn(editorSession);
        when(sessionEditorPresenter.withToolbar(anyBoolean())).thenReturn(sessionEditorPresenter);
        when(sessionEditorPresenter.withPalette(anyBoolean())).thenReturn(sessionEditorPresenter);
        when(sessionEditorPresenter.displayNotifications(any(Predicate.class))).thenReturn(sessionEditorPresenter);
        when(sessionEditorPresenter.getView()).thenReturn(sessionPresenterView);
        doAnswer(invocation -> {
            Diagram diagram1 = (Diagram) invocation.getArguments()[0];
            SessionPresenter.SessionPresenterCallback callback = (SessionPresenter.SessionPresenterCallback) invocation.getArguments()[1];
            callback.onOpen(diagram1);
            callback.afterSessionOpened();
            callback.onSuccess();
            return null;
        }).when(sessionEditorPresenter).open(any(Diagram.class),
                                             any(SessionPresenter.SessionPresenterCallback.class));
        when(sessionViewerPresenter.getInstance()).thenReturn(viewerSession);
        when(sessionViewerPresenter.withToolbar(anyBoolean())).thenReturn(sessionViewerPresenter);
        when(sessionViewerPresenter.withPalette(anyBoolean())).thenReturn(sessionViewerPresenter);
        when(sessionViewerPresenter.displayNotifications(any(Predicate.class))).thenReturn(sessionViewerPresenter);
        when(sessionViewerPresenter.getView()).thenReturn(sessionPresenterView);
        doAnswer(invocation -> {
            Diagram diagram1 = (Diagram) invocation.getArguments()[0];
            SessionPresenter.SessionPresenterCallback callback = (SessionPresenter.SessionPresenterCallback) invocation.getArguments()[1];
            callback.onOpen(diagram1);
            callback.afterSessionOpened();
            callback.onSuccess();
            return null;
        }).when(sessionViewerPresenter).open(any(Diagram.class),
                                             any(SessionPresenter.SessionPresenterCallback.class));
        when(versionRecordManager.getPathToLatest()).thenReturn(filePath);
        when(getMenuSessionItems().setErrorConsumer(any(Consumer.class))).thenReturn(getMenuSessionItems());
        when(getMenuSessionItems().setLoadingCompleted(any(Command.class))).thenReturn(getMenuSessionItems());
        when(getMenuSessionItems().setLoadingStarts(any(Command.class))).thenReturn(getMenuSessionItems());
        resourceType = mockResourceType();
        presenter = createDiagramEditor();
        presenter.init();
    }

    protected ClientResourceType mockResourceType() {
        final ClientResourceType resourceType = mock(ClientResourceTypeMock.class);
        when(resourceType.getSuffix()).thenReturn("bpmn");
        when(resourceType.getShortName()).thenReturn("Business Process");
        return resourceType;
    }

    protected ClientResourceType getResourceType() {
        return resourceType;
    }

    protected AbstractProjectEditorMenuSessionItems getMenuSessionItems() {
        return projectMenuSessionItems;
    }

    @SuppressWarnings("unchecked")
    protected AbstractProjectDiagramEditor createDiagramEditor() {
        return spy(new AbstractProjectDiagramEditor<ClientResourceTypeMock>(view,
                                                                            documentationView,
                                                                            placeManager,
                                                                            errorPopupPresenter,
                                                                            changeTitleNotificationEvent,
                                                                            savePopUpPresenter,
                                                                            (ClientResourceTypeMock) getResourceType(),
                                                                            clientProjectDiagramService,
                                                                            sessionEditorPresenters,
                                                                            sessionViewerPresenters,
                                                                            getMenuSessionItems(),
                                                                            onDiagramFocusEvent,
                                                                            onDiagramLostFocusEvent,
                                                                            projectMessagesListener,
                                                                            diagramClientErrorHandler,
                                                                            translationService,
                                                                            xmlEditorView,
                                                                            projectDiagramResourceServiceCaller) {
            {
                docks = AbstractProjectDiagramEditorTest.this.defaultEditorDock;
                perspectiveManager = AbstractProjectDiagramEditorTest.this.perspectiveManagerMock;
                fileMenuBuilder = AbstractProjectDiagramEditorTest.this.fileMenuBuilder;
                workbenchContext = AbstractProjectDiagramEditorTest.this.workbenchContext;
                projectController = AbstractProjectDiagramEditorTest.this.projectController;
                versionRecordManager = AbstractProjectDiagramEditorTest.this.versionRecordManager;
                alertsButtonMenuItemBuilder = AbstractProjectDiagramEditorTest.this.alertsButtonMenuItemBuilder;
                kieView = AbstractProjectDiagramEditorTest.this.kieView;
                overviewWidget = AbstractProjectDiagramEditorTest.this.overviewWidget;
                notification = AbstractProjectDiagramEditorTest.this.notification;
            }

            @Override
            protected String getEditorIdentifier() {
                return null;
            }
        });
    }

    @Test
    public void testMakeMenuBar() {

        final Command saveAndRenameCommand = mock(Command.class);

        doNothing().when(presenter).addDownloadMenuItem(any());
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(true).when(projectController).canUpdateProject(any());
        doReturn(saveAndRenameCommand).when(presenter).getSaveAndRename();

        presenter.makeMenuBar();

        verify(getMenuSessionItems()).populateMenu(eq(fileMenuBuilder));
        verify(fileMenuBuilder).addSave(any(MenuItem.class));
        verify(fileMenuBuilder).addCopy(any(Path.class),
                                        any(AssetUpdateValidator.class));
        verify(fileMenuBuilder).addRename(saveAndRenameCommand);
        verify(fileMenuBuilder).addDelete(any(Path.class),
                                          any(AssetUpdateValidator.class));
        verify(presenter).addDownloadMenuItem(fileMenuBuilder);
    }

    @Test
    public void testMakeMenuBarWithoutUpdateProjectPermission() {
        doNothing().when(presenter).addDownloadMenuItem(any());
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(false).when(projectController).canUpdateProject(any());

        presenter.makeMenuBar();

        verify(getMenuSessionItems()).populateMenu(eq(fileMenuBuilder));
        verify(fileMenuBuilder,
               never()).addSave(any(MenuItem.class));
        verify(fileMenuBuilder,
               never()).addCopy(any(Path.class),
                                any(AssetUpdateValidator.class));
        verify(fileMenuBuilder,
               never()).addRename(any(Path.class),
                                  any(AssetUpdateValidator.class));
        verify(fileMenuBuilder,
               never()).addDelete(any(Path.class),
                                  any(AssetUpdateValidator.class));
    }

    @Test
    public void testFormatTitle() {
        String title = "testDiagram";

        String formattedTitle = presenter.formatTitle(title);
        assertEquals(formattedTitle,
                     "testDiagram." + resourceType.getSuffix() + " - " + resourceType.getShortName());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOpen() {
        Overview overview = open();

        verify(view).showLoading();
        verify(view).setWidget(eq(sessionPresenterView));
        verify(sessionEditorPresenter).withToolbar(eq(false));
        verify(sessionEditorPresenter).withPalette(eq(true));
        verify(sessionEditorPresenter).open(eq(diagram),
                                            any(SessionPresenter.SessionPresenterCallback.class));

        verify(presenter).setOriginalHash(anyInt());
        verify(view).hideBusyIndicator();

        //Verify Overview widget was setup. It'd be nice to just verify(presenter).resetEditorPages(..) but it is protected
        verify(overviewWidget).setContent(eq(overview),
                                          eq(filePath));
        verify(kieView).clear();
        verify(kieView).addMainEditorPage(eq(view));
        verify(kieView).addOverviewPage(eq(overviewWidget),
                                        any(com.google.gwt.user.client.Command.class));

        verify(presenter).addDocumentationPage(diagram);
        verify(presenter).onDiagramLoad();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCloseEditor() {
        SessionPresenter sessionPresenter = mock(SessionPresenter.class);
        presenter.setEditorSessionPresenter(sessionEditorPresenter);
        presenter.doClose();
        verify(getMenuSessionItems(), times(1)).destroy();
        verify(sessionEditorPresenter, times(1)).destroy();
        verify(sessionPresenter, never()).clear();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOpenReadOnly() {
        when(presenter.isReadOnly()).thenReturn(true);
        Overview overview = open();

        verify(view).showLoading();
        verify(presenter).setOriginalHash(anyInt());

        verify(view).setWidget(eq(sessionPresenterView));
        verify(sessionViewerPresenter).withToolbar(eq(false));
        verify(sessionViewerPresenter).withPalette(eq(false));
        verify(view).hideBusyIndicator();

        //Verify Overview widget was setup. It'd be nice to just verify(presenter).resetEditorPages(..) but it is protected
        verify(overviewWidget).setContent(eq(overview),
                                          eq(filePath));
        verify(kieView).clear();
        verify(kieView).addMainEditorPage(eq(view));
        verify(kieView).addOverviewPage(eq(overviewWidget),
                                        any(com.google.gwt.user.client.Command.class));

        verify(presenter).onDiagramLoad();

        assertEquals(sessionViewerPresenter,
                     presenter.getSessionPresenter());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOpenWithInvalidBPMNFile() {
        final String xml = "xml";
        final Overview overview = openInvalidBPMNFile(xml);

        verify(presenter).setOriginalHash(eq(xml.hashCode()));
        verify(view).setWidget(any(IsWidget.class));
        verify(view).hideBusyIndicator();

        //Verify Overview widget was setup. It'd be nice to just verify(presenter).resetEditorPages(..) but it is protected
        verify(overviewWidget).setContent(eq(overview),
                                          eq(filePath));
        verify(kieView).clear();
        verify(kieView).addMainEditorPage(eq(view));
        verify(kieView).addOverviewPage(eq(overviewWidget),
                                        any(com.google.gwt.user.client.Command.class));

        verify(getMenuSessionItems()).setEnabled(eq(false));
        verify(xmlEditorView).setReadOnly(eq(false));
        verify(xmlEditorView).setContent(eq(xml), eq(AceEditorMode.XML));
        verify(presenter).makeXmlEditorProxy();
    }

    @SuppressWarnings("unchecked")
    protected Overview openInvalidBPMNFile(final String xml) {
        final ClientRuntimeError clientRuntimeError = mock(ClientRuntimeError.class);
        final DiagramParsingException dpe = mock(DiagramParsingException.class);
        final ProjectMetadata metadata = mock(ProjectMetadata.class);
        final Overview overview = mock(Overview.class);

        when(metadata.getTitle()).thenReturn(TITLE);
        when(metadata.getOverview()).thenReturn(overview);
        when(clientRuntimeError.getThrowable()).thenReturn(dpe);
        when(dpe.getMetadata()).thenReturn(metadata);
        when(dpe.getXml()).thenReturn(xml);
        when(diagram.getMetadata()).thenReturn(metadata);

        doAnswer(i -> {
            final ServiceCallback serviceCallback = (ServiceCallback) i.getArguments()[1];
            serviceCallback.onError(clientRuntimeError);
            return null;
        }).when(clientProjectDiagramService).getByPath(any(Path.class),
                                                       any(ServiceCallback.class));

        presenter.loadContent();

        verify(presenter).destroySession();

        return overview;
    }

    protected Overview open() {
        final ProjectMetadata metadata = mock(ProjectMetadata.class);
        final Overview overview = mock(Overview.class);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getTitle()).thenReturn(TITLE);
        when(metadata.getOverview()).thenReturn(overview);
        final Graph graph = mock(Graph.class);
        final DefinitionSet definitionSet = mock(DefinitionSet.class);
        when(diagram.getGraph()).thenReturn(graph);
        when(graph.getContent()).thenReturn(definitionSet);
        presenter.open(diagram);
        return overview;
    }

    @Test
    public void testIsDirty() {
        open();

        assertFalse(presenter.isDirty(presenter.getCurrentDiagramHash()));
        presenter.setOriginalHash(~~(presenter.getCurrentDiagramHash() + 1));
        assertTrue(presenter.isDirty(presenter.getCurrentDiagramHash()));
    }

    @Test
    public void testHasChanges() {
        open();

        assertFalse(presenter.hasUnsavedChanges());
        presenter.setOriginalHash(~~(presenter.getCurrentDiagramHash() + 1));
        assertTrue(presenter.hasUnsavedChanges());
        presenter.setOriginalHash(~~(presenter.getCurrentDiagramHash()));
        assertFalse(presenter.hasUnsavedChanges());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStunnerSave_SaveFailed() {
        final String errorMessage = "Something went wrong";
        final ClientRuntimeError cre = new ClientRuntimeError(errorMessage);
        final ServiceCallback<ProjectDiagram> serviceCallback = assertBasicStunnerSaveOperation(true);

        serviceCallback.onError(cre);

        verify(presenter).onSaveError(eq(cre));
        final ArgumentCaptor<Consumer> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(diagramClientErrorHandler).handleError(eq(cre), consumerCaptor.capture());

        final Consumer consumer = consumerCaptor.getValue();
        consumer.accept(errorMessage);

        verify(errorPopupPresenter).showMessage(eq(errorMessage));
    }

    @SuppressWarnings("unchecked")
    private ServiceCallback<ProjectDiagram> assertBasicStunnerSaveOperation(final boolean validateSuccess) {
        final String commitMessage = "message";
        final Overview overview = open();
        final Metadata metadata = overview.getMetadata();
        doReturn(diagram).when(presenter).getDiagram();
        EditorSessionCommands editorSessionCommands = mock(EditorSessionCommands.class);
        when(getMenuSessionItems().getCommands()).thenReturn(editorSessionCommands);
        ValidateSessionCommand validateSessionCommand = mock(ValidateSessionCommand.class);
        when(editorSessionCommands.getValidateSessionCommand()).thenReturn(validateSessionCommand);
        doAnswer(invocation -> {
            ClientSessionCommand.Callback callback = (ClientSessionCommand.Callback) invocation.getArguments()[0];
            if (validateSuccess) {
                callback.onSuccess();
            } else {
                DiagramElementViolation<RuleViolation> violation = mock(DiagramElementViolation.class);
                when(violation.getViolationType()).thenReturn(Violation.Type.ERROR);
                callback.onError(Collections.singletonList(violation));
            }
            return null;
        }).when(validateSessionCommand).execute(any(ClientSessionCommand.Callback.class));
        presenter.save();

        if (validateSuccess) {
            ArgumentCaptor<ParameterizedCommand> savePopupCommandCaptor = ArgumentCaptor.forClass(ParameterizedCommand.class);
            verify(savePopUpPresenter).show(eq(versionRecordManager.getCurrentPath()),
                                            savePopupCommandCaptor.capture());

            final ParameterizedCommand<String> savePopupCommand = savePopupCommandCaptor.getValue();
            savePopupCommand.execute(commitMessage);

            verify(view).showSaving();
            ArgumentCaptor<ServiceCallback> serviceCallbackCaptor = ArgumentCaptor.forClass(ServiceCallback.class);
            verify(clientProjectDiagramService).saveOrUpdate(eq(versionRecordManager.getCurrentPath()),
                                                             eq(diagram),
                                                             eq(metadata),
                                                             eq(commitMessage),
                                                             serviceCallbackCaptor.capture());

            return serviceCallbackCaptor.getValue();
        }
        return null;
    }

    @Test
    public void testStunnerSave_ValidationSuccessful() {
        when(translationService.getValue(eq(StunnerProjectClientConstants.DIAGRAM_SAVE_SUCCESSFUL))).thenReturn("okk");
        final ServiceCallback<ProjectDiagram> serviceCallback = assertBasicStunnerSaveOperation(true);
        serviceCallback.onSuccess(diagram);

        final Path path = versionRecordManager.getCurrentPath();
        verify(versionRecordManager).reloadVersions(eq(path));
        verify(sessionPresenterView).showMessage(eq("okk"));
        verify(view, atLeastOnce()).hideBusyIndicator();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStunnerSave_ValidationUnsuccessful() {
        assertBasicStunnerSaveOperation(false);
        verify(presenter).onValidationFailed(any(Collection.class));
        verify(view, atLeastOnce()).hideBusyIndicator();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testXMLSave_ValidationNotRequired() {
        final String xml = "xml";
        final ServiceCallback<String> serviceCallback = assertBasicXMLSaveOperation(xml);

        serviceCallback.onSuccess(xml);

        final Path path = versionRecordManager.getCurrentPath();
        verify(versionRecordManager).reloadVersions(eq(path));
        ArgumentCaptor<NotificationEvent> notificationEventCaptor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(notification, times(2)).fire(notificationEventCaptor.capture());

        final NotificationEvent notificationEvent = notificationEventCaptor.getValue();
        assertEquals("ItemSavedSuccessfully",
                     notificationEvent.getNotification());

        verify(view, atLeastOnce()).hideBusyIndicator();
    }

    @Test
    public void testProxyContentSupplierWhenXmlEditorIsMade() {

        final ProjectDiagramEditorProxy editorProxy = presenter.makeXmlEditorProxy();
        final Supplier<ProjectDiagramResource> contentSupplier = editorProxy.getContentSupplier();
        final String content = "<xml>";

        when(xmlEditorView.getContent()).thenReturn(content);

        final ProjectDiagramResource resource = contentSupplier.get();

        assertEquals(Optional.empty(), resource.projectDiagram());
        assertEquals(Optional.of(content), resource.xmlDiagram());
        assertEquals(XML_DIAGRAM, resource.getType());
    }

    @Test
    public void testProxyContentSupplierWhenStunnerEditorIsMade() {

        final ProjectDiagramEditorProxy editorProxy = presenter.makeStunnerEditorProxy();
        final Supplier<ProjectDiagramResource> contentSupplier = editorProxy.getContentSupplier();
        final ProjectDiagram diagram = mock(ProjectDiagram.class);

        doReturn(diagram).when(presenter).getDiagram();

        final ProjectDiagramResource resource = contentSupplier.get();

        assertEquals(Optional.of(diagram), resource.projectDiagram());
        assertEquals(Optional.empty(), resource.xmlDiagram());
        assertEquals(PROJECT_DIAGRAM, resource.getType());
    }

    @Test
    public void testProxyContentSupplierWhenNoEditorIsMade() {

        final Supplier<ProjectDiagramResource> contentSupplier = presenter.editorProxy.getContentSupplier();
        final ProjectDiagramResource resource = contentSupplier.get();

        assertNotNull(contentSupplier);
        assertNull(resource);
    }

    @Test
    public void testGetSaveAndRenameServiceCaller() {

        final Caller<ProjectDiagramResourceService> expectedCaller = this.projectDiagramResourceServiceCaller;

        final Caller<? extends SupportsSaveAndRename<ProjectDiagramResource, Metadata>> actualCaller = presenter.getSaveAndRenameServiceCaller();

        assertEquals(expectedCaller, actualCaller);
    }

    @Test
    public void testGetContentSupplier() {

        final ProjectDiagram expectedProjectDiagram = mock(ProjectDiagram.class);
        final ProjectDiagramEditorProxy editorProxy = mock(ProjectDiagramEditorProxy.class);
        final ProjectDiagramResource expectedResource = new ProjectDiagramResourceImpl(expectedProjectDiagram);

        doReturn(editorProxy).when(presenter).getEditorProxy();

        when(editorProxy.getContentSupplier()).thenReturn(() -> expectedResource);

        final ProjectDiagramResource actualResource = presenter.getContentSupplier().get();

        assertEquals(expectedResource, actualResource);
    }

    @Test
    public void testGetCurrentContentHash() {

        final Integer expectedContentHash = 42;

        doReturn(expectedContentHash).when(presenter).getCurrentDiagramHash();

        final Integer actualContentHash = presenter.getCurrentContentHash();

        assertEquals(expectedContentHash, actualContentHash);
    }

    @SuppressWarnings("unchecked")
    private ServiceCallback<String> assertBasicXMLSaveOperation(final String xml) {
        final String commitMessage = "message";
        final Overview overview = openInvalidBPMNFile(xml);
        final Metadata metadata = overview.getMetadata();
        EditorSessionCommands editorSessionCommands = mock(EditorSessionCommands.class);
        when(getMenuSessionItems().getCommands()).thenReturn(editorSessionCommands);
        ValidateSessionCommand validateSessionCommand = mock(ValidateSessionCommand.class);
        when(editorSessionCommands.getValidateSessionCommand()).thenReturn(validateSessionCommand);
        doAnswer(invocation -> {
            ClientSessionCommand.Callback callback = (ClientSessionCommand.Callback) invocation.getArguments()[0];
            callback.onSuccess();
            return null;
        }).when(validateSessionCommand).execute(any(ClientSessionCommand.Callback.class));
        doReturn(xml).when(xmlEditorView).getContent();

        presenter.save();

        ArgumentCaptor<ParameterizedCommand> savePopupCommandCaptor = ArgumentCaptor.forClass(ParameterizedCommand.class);
        verify(savePopUpPresenter).show(eq(versionRecordManager.getCurrentPath()),
                                        savePopupCommandCaptor.capture());

        final ParameterizedCommand<String> savePopupCommand = savePopupCommandCaptor.getValue();
        savePopupCommand.execute(commitMessage);

        verify(view).showSaving();
        ArgumentCaptor<ServiceCallback> serviceCallbackCaptor = ArgumentCaptor.forClass(ServiceCallback.class);
        verify(clientProjectDiagramService).saveAsXml(eq(versionRecordManager.getCurrentPath()),
                                                      eq(xml),
                                                      eq(metadata),
                                                      eq(commitMessage),
                                                      serviceCallbackCaptor.capture());

        return serviceCallbackCaptor.getValue();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testXMLSave_SaveFailed() {
        final String xml = "xml";
        final String errorMessage = "Something went wrong";
        final ClientRuntimeError cre = new ClientRuntimeError(errorMessage);
        final ServiceCallback<String> serviceCallback = assertBasicXMLSaveOperation(xml);

        serviceCallback.onError(cre);

        verify(presenter).onSaveError(eq(cre));
        final ArgumentCaptor<Consumer> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(diagramClientErrorHandler).handleError(eq(cre), consumerCaptor.capture());

        final Consumer consumer = consumerCaptor.getValue();
        consumer.accept(errorMessage);

        verify(errorPopupPresenter).showMessage(eq(errorMessage));
    }

    @Test
    public void testDiagramHashCodeWithInvalidBPMNFile() {
        final String xml = "xml";
        when(xmlEditorView.getContent()).thenReturn(xml);

        openInvalidBPMNFile(xml);

        assertEquals(xml.hashCode(),
                     presenter.getCurrentDiagramHash());
    }

    @Test
    public void testHideDocks() {
        presenter.hideDocks();

        verify(onDiagramLostFocusEvent).fire(any());
        verify(defaultEditorDock).hide();
    }

    @Test
    public void testShowDocks() {
        PerspectiveActivity perspectiveActivity = mock(PerspectiveActivity.class);
        when(perspectiveActivity.getIdentifier()).thenReturn("perspectiveId");
        when(perspectiveManagerMock.getCurrentPerspective()).thenReturn(perspectiveActivity);

        presenter.showDocks();

        verify(onDiagramFocusEvent).fire(any());
        verify(defaultEditorDock).show();
    }

    @Test
    public void testAddDocumentationPage() {
        when(documentationView.isEnabled()).thenReturn(Boolean.TRUE);
        when(translationService.getValue(StunnerProjectClientConstants.DOCUMENTATION)).thenReturn(DOC_LABEL);
        when(documentationView.initialize(diagram)).thenReturn(documentationView);

        presenter.addDocumentationPage(diagram);
        verify(translationService).getValue(StunnerProjectClientConstants.DOCUMENTATION);
        verify(kieView).addPage(documentationPageCaptor.capture());
        DocumentationPage documentationPage = documentationPageCaptor.getValue();
        assertEquals(documentationPage.getDocumentationView(), documentationView);
        assertEquals(documentationPage.getLabel(), DOC_LABEL);
    }

    abstract class ClientResourceTypeMock implements ClientResourceType {

    }
}
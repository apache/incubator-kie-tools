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

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.editor.EditorSessionCommands;
import org.kie.workbench.common.stunner.client.widgets.editor.StunnerEditor;
import org.kie.workbench.common.stunner.client.widgets.editor.StunnerEditorView;
import org.kie.workbench.common.stunner.client.widgets.presenters.Viewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.resources.i18n.StunnerWidgetsConstants;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ValidateSessionCommand;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramParsingException;
import org.kie.workbench.common.stunner.core.documentation.DocumentationPage;
import org.kie.workbench.common.stunner.core.documentation.DocumentationView;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramLoseFocusEvent;
import org.kie.workbench.common.stunner.project.client.resources.i18n.StunnerProjectClientConstants;
import org.kie.workbench.common.stunner.project.client.screens.ProjectMessagesListener;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramResourceService;
import org.kie.workbench.common.widgets.client.docks.DefaultEditorDock;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.validation.AssetUpdateValidator;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.menu.common.SaveAndRenameCommandBuilder;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.promise.SyncPromises;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    protected static final String TITLE = "title";
    protected static final String DOC_LABEL = "doc";

    @Mock
    protected AbstractProjectDiagramEditor.View view;
    @Mock
    protected StunnerEditorView stunnerEditorView;
    @Mock
    protected AbstractDiagramEditorMenuSessionItems menuSessionItems;
    @Mock
    protected Event<OnDiagramFocusEvent> onDiagramFocusEvent;
    @Mock
    protected Event<OnDiagramLoseFocusEvent> onDiagramLostFocusEvent;
    @Mock
    protected ClientTranslationService translationService;
    @Mock
    protected DocumentationView documentationView;
    @Mock
    protected ClientResourceType resourceType;
    @Mock
    protected ProjectMessagesListener projectMessagesListener;
    @Mock
    protected ClientProjectDiagramService projectDiagramServices;
    @Mock
    protected Caller<ProjectDiagramResourceService> projectDiagramResourceServiceCaller;
    @Mock
    @SuppressWarnings("unused")
    //This is injected into FileMenuBuilderImpl by the @InjectMocks annotation
    protected BasicFileMenuBuilder menuBuilder;
    @Spy
    @InjectMocks
    protected FileMenuBuilderImpl fileMenuBuilder;
    @Mock
    protected ProjectController projectController;
    @Mock
    protected WorkspaceProjectContext workbenchContext;
    @Mock
    protected VersionRecordManager versionRecordManager;
    @Mock
    protected ObservablePath filePath;
    @Mock
    protected KieEditorWrapperView kieView;
    @Mock
    protected OverviewWidgetPresenter overviewWidget;
    @Mock
    protected EventSourceMock<ChangeTitleWidgetEvent> changeTitleNotification;
    @Mock
    protected EventSourceMock<NotificationEvent> notification;
    @Mock
    protected SavePopUpPresenter savePopUpPresenter;
    @Mock
    protected SaveAndRenameCommandBuilder saveAndRenameCommandBuilder;
    @Mock
    protected DefaultEditorDock docks;
    @Mock
    protected PerspectiveManager perspectiveManager;
    @Mock
    protected ClientSession session;
    @Mock
    protected AbstractCanvasHandler canvasHandler;
    @Mock
    protected ProjectDiagram diagram;
    @Mock
    protected ProjectMetadata metadata;
    @Mock
    protected Overview overview;
    @Mock
    protected Graph graph;

    protected AbstractProjectDiagramEditor tested;
    protected StunnerEditor stunnerEditor;
    protected Consumer<DiagramParsingException> parsingExceptionProcessor;
    protected Consumer<Throwable> exceptionProcessor;
    protected Promises promises;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {

        promises = new SyncPromises();
        when(versionRecordManager.getPathToLatest()).thenReturn(filePath);
        when(menuSessionItems.setErrorConsumer(Mockito.<Consumer>any())).thenReturn(menuSessionItems);
        when(menuSessionItems.setLoadingCompleted(Mockito.<Command>any())).thenReturn(menuSessionItems);
        when(menuSessionItems.setLoadingStarts(Mockito.<Command>any())).thenReturn(menuSessionItems);
        when(resourceType.getSuffix()).thenReturn("bpmn");
        when(resourceType.getShortName()).thenReturn("Business Process");
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(diagram.getName()).thenReturn(TITLE);
        when(metadata.getOverview()).thenReturn(overview);
        when(diagram.getGraph()).thenReturn(graph);
        when(graph.getContent()).thenReturn(mock(DefinitionSet.class));
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        when(projectController.canUpdateProject(any())).thenReturn(promises.resolve(true));

        stunnerEditor = spy(new StunnerEditor(null, null, translationService, null, mock(ErrorPopupPresenter.class), stunnerEditorView) {
            @Override
            public ClientSession getSession() {
                return session;
            }

            @Override
            public CanvasHandler getCanvasHandler() {
                return canvasHandler;
            }

            @Override
            public Diagram getDiagram() {
                return diagram;
            }

            @Override
            public void setParsingExceptionProcessor(Consumer<DiagramParsingException> parsingExceptionProcessor) {
                AbstractProjectDiagramEditorTest.this.parsingExceptionProcessor = parsingExceptionProcessor;
                super.setParsingExceptionProcessor(parsingExceptionProcessor);
            }

            @Override
            public void setExceptionProcessor(Consumer<Throwable> exceptionProcessor) {
                AbstractProjectDiagramEditorTest.this.exceptionProcessor = exceptionProcessor;
                super.setExceptionProcessor(exceptionProcessor);
            }
        });
        doReturn(stunnerEditor).when(stunnerEditor).close();
        doNothing().when(stunnerEditor).showMessage(Mockito.anyString());
        doAnswer(invocation -> {
            ((Viewer.Callback) invocation.getArguments()[1]).onSuccess();
            return null;
        }).when(stunnerEditor).open(eq(diagram), Mockito.<SessionPresenter.SessionPresenterCallback>any());

        tested = spy(new AbstractProjectDiagramEditor(view,
                                                      onDiagramFocusEvent,
                                                      onDiagramLostFocusEvent,
                                                      documentationView,
                                                      resourceType,
                                                      menuSessionItems,
                                                      projectMessagesListener,
                                                      translationService,
                                                      projectDiagramServices,
                                                      projectDiagramResourceServiceCaller,
                                                      stunnerEditor) {
            {
                fileMenuBuilder = AbstractProjectDiagramEditorTest.this.fileMenuBuilder;
                projectController = AbstractProjectDiagramEditorTest.this.projectController;
                workbenchContext = AbstractProjectDiagramEditorTest.this.workbenchContext;
                versionRecordManager = AbstractProjectDiagramEditorTest.this.versionRecordManager;
                kieView = AbstractProjectDiagramEditorTest.this.kieView;
                overviewWidget = AbstractProjectDiagramEditorTest.this.overviewWidget;
                saveAndRenameCommandBuilder = AbstractProjectDiagramEditorTest.this.saveAndRenameCommandBuilder;
                changeTitleNotification = AbstractProjectDiagramEditorTest.this.changeTitleNotification;
                notification = AbstractProjectDiagramEditorTest.this.notification;
                savePopUpPresenter = AbstractProjectDiagramEditorTest.this.savePopUpPresenter;
                docks = AbstractProjectDiagramEditorTest.this.docks;
                perspectiveManager = AbstractProjectDiagramEditorTest.this.perspectiveManager;
            }

            @Override
            public String getEditorIdentifier() {
                return "testAbstractEditor";
            }
        });
    }

    @Test
    public void testInit() {
        tested.init();
        verify(projectMessagesListener, times(1)).enable();
        verify(view, times(1)).setWidget(eq(stunnerEditorView));
    }

    @Test
    public void testMakeMenuBar() {

        final Command saveAndRenameCommand = mock(Command.class);

        doNothing().when(tested).addDownloadMenuItem(any());
        doReturn(saveAndRenameCommand).when(tested).getSaveAndRename();

        tested.makeMenuBar();

        verify(menuSessionItems).populateMenu(eq(fileMenuBuilder));
        verify(fileMenuBuilder).addSave(Mockito.<MenuItem>any());
        verify(fileMenuBuilder).addCopy(Mockito.<Path>any(),
                                        Mockito.<AssetUpdateValidator>any());
        verify(fileMenuBuilder).addRename(saveAndRenameCommand);
        verify(fileMenuBuilder).addDelete(Mockito.<Path>any(),
                                          Mockito.<AssetUpdateValidator>any());
        verify(tested).addDownloadMenuItem(fileMenuBuilder);
    }

    @Test
    public void testMakeMenuBarWithoutUpdateProjectPermission() {
        doNothing().when(tested).addDownloadMenuItem(any());
        when(projectController.canUpdateProject(any())).thenReturn(promises.resolve(false));

        tested.makeMenuBar();

        verify(menuSessionItems).populateMenu(eq(fileMenuBuilder));
        verify(fileMenuBuilder,
               never()).addSave(Mockito.<MenuItem>any());
        verify(fileMenuBuilder,
               never()).addCopy(Mockito.<Path>any(),
                                Mockito.<AssetUpdateValidator>any());
        verify(fileMenuBuilder,
               never()).addRename(Mockito.<Path>any(),
                                  Mockito.<AssetUpdateValidator>any());
        verify(fileMenuBuilder,
               never()).addDelete(Mockito.<Path>any(),
                                  Mockito.<AssetUpdateValidator>any());
    }

    @Test
    public void testFormatTitle() {
        String title = "testDiagram";

        String formattedTitle = tested.formatTitle(title);
        assertEquals(formattedTitle,
                     "testDiagram." + resourceType.getSuffix() + " - " + resourceType.getShortName());
    }

    @Test
    public void testSetOriginalHash() {
        int hash = 123;
        tested.setOriginalHash(hash);
        verify(stunnerEditor, times(1)).resetContentHash();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOpen() {
        tested.open(diagram);
        verify(view).showLoading();
        verify(stunnerEditor).setReadOnly(eq(false));
        verify(stunnerEditor).open(eq(diagram), Mockito.<SessionPresenter.SessionPresenterCallback>any());
        verify(view).hideBusyIndicator();
        verify(overviewWidget).setContent(eq(overview), eq(filePath));
        verify(kieView).clear();
        verify(kieView).addMainEditorPage(eq(view));
        verify(kieView).addOverviewPage(eq(overviewWidget), Mockito.<com.google.gwt.user.client.Command>any());
        verify(saveAndRenameCommandBuilder).addContentSupplier(any());
        verify(changeTitleNotification).fire(any());
    }

    @Test
    public void testCloseEditor() {
        tested.doClose();
        verify(menuSessionItems, times(1)).destroy();
        verify(stunnerEditor, times(1)).close();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoadContentWithInvalidFile() {
        tested.doStartUp(mock(ObservablePath.class), mock(PlaceRequest.class));
        doAnswer(invocation -> {
            ProjectMetadata metadata = mock(ProjectMetadata.class);
            when(metadata.getTitle()).thenReturn("someXmlTitle");
            when(metadata.getOverview()).thenReturn(overview);
            DiagramParsingException dpe = new DiagramParsingException(metadata, "someXml");
            parsingExceptionProcessor.accept(dpe);
            ((Viewer.Callback) invocation.getArguments()[1]).onError(new ClientRuntimeError(dpe));
            return null;
        }).when(stunnerEditor).open(eq(diagram), Mockito.<SessionPresenter.SessionPresenterCallback>any());
        tested.open(diagram);
        verify(view).hideBusyIndicator();
        verify(overviewWidget).setContent(eq(overview), eq(filePath));
        verify(changeTitleNotification).fire(any());
        verify(notification).fire(any());
        verify(kieView).clear();
        verify(kieView).addMainEditorPage(eq(view));
        verify(kieView).addOverviewPage(eq(overviewWidget), Mockito.<com.google.gwt.user.client.Command>any());
        verify(menuSessionItems).setEnabled(eq(false));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoadValidContent() {
        doAnswer(i -> {
            ((ServiceCallback) i.getArguments()[1]).onSuccess(diagram);
            return null;
        }).when(projectDiagramServices).getByPath(Mockito.<Path>any(), Mockito.<ServiceCallback>any());
        tested.loadContent();
        verify(stunnerEditor).open(eq(diagram), any());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoadInvalidContent() {
        ClientRuntimeError e = mock(ClientRuntimeError.class);
        doAnswer(i -> {
            ((ServiceCallback) i.getArguments()[1]).onError(e);
            return null;
        }).when(projectDiagramServices).getByPath(Mockito.<Path>any(), Mockito.<ServiceCallback>any());
        tested.loadContent();
        verify(stunnerEditor).handleError(eq(e));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStunnerSave_SaveFailed() {
        final String errorMessage = "Something went wrong";
        final ClientRuntimeError cre = new ClientRuntimeError(errorMessage);
        final Overview overview = assertBasicStunnerSaveOperation(true);
        final ServiceCallback<ProjectDiagram> serviceCallback = assertSaveOperation(overview);
        serviceCallback.onError(cre);
        verify(stunnerEditor).handleError(eq(cre));
        verify(stunnerEditor).showError(eq(errorMessage));
    }

    @SuppressWarnings("unchecked")
    protected Overview assertBasicStunnerSaveOperation(final boolean validateSuccess) {
        tested.open(diagram);
        EditorSessionCommands editorSessionCommands = mock(EditorSessionCommands.class);
        when(menuSessionItems.getCommands()).thenReturn(editorSessionCommands);
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
        }).when(validateSessionCommand).execute(Mockito.<ClientSessionCommand.Callback>any());
        tested.onSave();

        return overview;
    }

    @SuppressWarnings("unchecked")
    protected ServiceCallback<ProjectDiagram> assertSaveOperation(final Overview overview) {
        final String commitMessage = "message";
        final Metadata metadata = overview.getMetadata();

        final ArgumentCaptor<ParameterizedCommand> savePopupCommandCaptor = ArgumentCaptor.forClass(ParameterizedCommand.class);
        verify(savePopUpPresenter).show(eq(versionRecordManager.getCurrentPath()),
                                        savePopupCommandCaptor.capture());

        final ParameterizedCommand<String> savePopupCommand = savePopupCommandCaptor.getValue();
        savePopupCommand.execute(commitMessage);

        verify(view).showSaving();
        final ArgumentCaptor<ServiceCallback> serviceCallbackCaptor = ArgumentCaptor.forClass(ServiceCallback.class);
        verify(projectDiagramServices).saveOrUpdate(eq(versionRecordManager.getCurrentPath()),
                                                    eq(diagram),
                                                    eq(metadata),
                                                    eq(commitMessage),
                                                    serviceCallbackCaptor.capture());

        return serviceCallbackCaptor.getValue();
    }

    @Test
    public void testStunnerSave_ValidationSuccessful() {
        when(translationService.getValue(eq(StunnerProjectClientConstants.DIAGRAM_SAVE_SUCCESSFUL))).thenReturn("okk");
        final Overview overview = assertBasicStunnerSaveOperation(true);
        final ServiceCallback<ProjectDiagram> serviceCallback = assertSaveOperation(overview);
        serviceCallback.onSuccess(diagram);

        final Path path = versionRecordManager.getCurrentPath();
        verify(versionRecordManager).reloadVersions(eq(path));
        verify(stunnerEditor).showMessage(eq("okk"));
        verify(view, atLeastOnce()).hideBusyIndicator();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStunnerSave_ValidationUnsuccessful() {
        assertBasicStunnerSaveOperation(false);
        verify(view, atLeastOnce()).hideBusyIndicator();
    }

    @Test
    public void testGetSaveAndRenameServiceCaller() {
        final Caller<ProjectDiagramResourceService> expectedCaller = this.projectDiagramResourceServiceCaller;
        final Caller<? extends SupportsSaveAndRename<ProjectDiagram, Metadata>> actualCaller = tested.getSaveAndRenameServiceCaller();

        assertEquals(expectedCaller, actualCaller);
    }

    @Test
    public void testGetContentSupplier() {
        assertEquals(diagram, tested.getContentSupplier().get());
    }

    @Test
    public void testGetCurrentContentHash() {
        final Integer expectedContentHash = 42;
        doReturn(expectedContentHash).when(stunnerEditor).getCurrentContentHash();
        final Integer actualContentHash = tested.getCurrentContentHash();
        assertEquals(expectedContentHash, actualContentHash);
    }

    @Test
    public void testHideDocks() {
        tested.hideDocks();
        verify(onDiagramLostFocusEvent).fire(any());
        verify(docks).hide();
    }

    @Test
    public void testShowDocks() {
        tested.showDocks();
        verify(onDiagramFocusEvent).fire(any());
        verify(docks).show();
    }

    @Test
    public void testDocksQualifiers() {
        final Annotation[] qualifiers = tested.getDockQualifiers();
        assertEquals(1, qualifiers.length);
        assertEquals(DefinitionManager.DEFAULT_QUALIFIER, qualifiers[0]);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddDocumentationPage() {
        when(documentationView.isEnabled()).thenReturn(Boolean.TRUE);
        when(translationService.getValue(StunnerWidgetsConstants.Documentation)).thenReturn(DOC_LABEL);
        when(documentationView.initialize(diagram)).thenReturn(documentationView);
        tested.addDocumentationPage(diagram);
        verify(translationService).getValue(StunnerWidgetsConstants.Documentation);
        ArgumentCaptor<DocumentationPage> documentationPageCaptor = ArgumentCaptor.forClass(DocumentationPage.class);
        verify(kieView).addPage(documentationPageCaptor.capture());
        final DocumentationPage documentationPage = documentationPageCaptor.getValue();
        assertEquals(documentationPage.getDocumentationView(), documentationView);
        assertEquals(documentationPage.getLabel(), DOC_LABEL);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSuccessCallbackStateReady() {
        when(stunnerEditor.isClosed()).thenReturn(false);
        tested.onSuccess().execute(mock(Path.class));
        verify(tested).getContentSupplier();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSuccessCallbackStateClosed() {
        when(stunnerEditor.isClosed()).thenReturn(true);
        tested.onSuccess().execute(mock(Path.class));
        verify(tested, never()).getContentSupplier();
    }
}
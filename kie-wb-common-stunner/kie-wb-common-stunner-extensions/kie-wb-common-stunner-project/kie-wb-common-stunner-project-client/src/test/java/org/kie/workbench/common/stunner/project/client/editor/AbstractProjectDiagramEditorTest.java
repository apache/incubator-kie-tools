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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.messageconsole.client.console.widget.button.AlertsButtonMenuItemBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenterFactory;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.error.DiagramClientErrorHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistry;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.ClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.ClientReadOnlySession;
import org.kie.workbench.common.stunner.core.client.session.ClientSessionFactory;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ClearSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ClearStatesSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.CopySelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.CutSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.DeleteSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToBpmnSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToJpgSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToPdfSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToPngSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToSvgSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.PasteSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.RedoSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.SessionCommandFactory;
import org.kie.workbench.common.stunner.core.client.session.command.impl.SwitchGridSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.UndoSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ValidateSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.VisitGraphSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientReadOnlySession;
import org.kie.workbench.common.stunner.core.definition.exception.DefinitionNotFoundException;
import org.kie.workbench.common.stunner.core.diagram.DiagramParsingException;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.preferences.StunnerPreferences;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.violations.BoundsExceededViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.impl.ElementViolationImpl;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramLoseFocusEvent;
import org.kie.workbench.common.stunner.project.client.resources.i18n.StunnerProjectClientConstants;
import org.kie.workbench.common.stunner.project.client.screens.ProjectMessagesListener;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
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
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.events.PlaceHiddenEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.widgets.common.client.ace.AceEditorMode;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(PathPlaceRequest.class)
public class AbstractProjectDiagramEditorTest {

    protected static final String EDITOR_ID = "AbstractProjectDiagramEditor";

    protected static final String TITLE = "title";

    @Mock
    protected ProjectDiagram diagram;

    @Mock
    protected AbstractProjectDiagramEditor.View view;

    @Mock
    protected PlaceManager placeManager;

    @Mock
    protected PlaceRequest placeRequest;

    @Mock
    protected ErrorPopupPresenter errorPopupPresenter;

    @Mock
    protected EventSourceMock<ChangeTitleWidgetEvent> changeTitleNotificationEvent;

    @Mock
    protected SavePopUpPresenter savePopUpPresenter;

    @Mock
    protected ClientProjectDiagramService clientProjectDiagramService;

    @Mock
    protected SessionManager sessionManager;

    @Mock
    protected SessionPresenterFactory sessionPresenterFactory;

    @Mock
    protected EventSourceMock<OnDiagramFocusEvent> onDiagramFocusEvent;

    @Mock
    protected EventSourceMock<OnDiagramLoseFocusEvent> onDiagramLostFocusEvent;

    @Mock
    protected EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    protected BasicFileMenuBuilder menuBuilder;

    @Mock
    protected VersionRecordManager versionRecordManager;

    @Spy
    @InjectMocks
    protected FileMenuBuilderImpl fileMenuBuilder;

    @Mock
    protected ProjectDiagramEditorMenuItemsBuilder projectMenuItemsBuilder;

    @Mock
    protected ProjectController projectController;

    @Mock
    protected WorkspaceProjectContext workbenchContext;

    @Mock
    protected SessionCommandFactory sessionCommandFactory;

    @Mock
    protected ProjectMessagesListener projectMessagesListener;

    protected ClientResourceType resourceType;

    @Mock
    protected DiagramClientErrorHandler diagramClientErrorHandler;

    @Mock
    protected ClientTranslationService translationService;

    @Mock
    protected TextEditorView xmlEditorView;

    @Mock
    protected Widget xmlEditorWidget;

    @Mock
    protected StunnerPreferencesRegistry stunnerPreferencesRegistry;

    @Mock
    protected AlertsButtonMenuItemBuilder alertsButtonMenuItemBuilder;

    @Mock
    protected MenuItem alertsButtonMenuItem;

    @Mock
    private ClearStatesSessionCommand clearStatesSessionCommand;

    @Mock
    protected SwitchGridSessionCommand switchGridSessionCommand;

    @Mock
    protected VisitGraphSessionCommand visitGraphSessionCommand;

    @Mock
    protected ClearSessionCommand clearSessionCommand;

    @Mock
    protected DeleteSelectionSessionCommand deleteSelectionSessionCommand;

    @Mock
    protected UndoSessionCommand undoSessionCommand;

    @Mock
    protected RedoSessionCommand redoSessionCommand;

    @Mock
    protected ValidateSessionCommand validateSessionCommand;

    @Mock
    protected ExportToPngSessionCommand exportToPngSessionCommand;

    @Mock
    protected ExportToJpgSessionCommand exportToJpgSessionCommand;

    @Mock
    protected ExportToPdfSessionCommand exportToPdfSessionCommand;

    @Mock
    protected ExportToBpmnSessionCommand exportToBpmnSessionCommand;

    @Mock
    protected CopySelectionSessionCommand copySelectionSessionCommand;

    @Mock
    protected PasteSelectionSessionCommand pasteSelectionSessionCommand;

    @Mock
    protected CutSelectionSessionCommand cutSelectionSessionCommand;

    @Mock
    protected ExportToSvgSessionCommand exportToSvgSessionCommand;

    @Mock
    protected SessionPresenter fullSessionPresenter;

    @Mock
    protected SessionPresenter readOnlySessionPresenter;

    @Mock
    protected SessionPresenter.View sessionPresenterView;

    @Mock
    protected AbstractClientFullSession clientFullSession;

    @Mock
    protected AbstractClientReadOnlySession clientReadOnlySession;

    @Mock
    protected ObservablePath filePath;

    @Mock
    protected KieEditorWrapperView kieView;

    @Mock
    protected OverviewWidgetPresenter overviewWidget;

    @Mock
    protected MenuItem clearItem;

    @Mock
    protected MenuItem visitGraphItem;

    @Mock
    protected MenuItem switchGridItem;

    @Mock
    protected MenuItem deleteSelectionItem;

    @Mock
    protected MenuItem undoItem;

    @Mock
    protected MenuItem redoItem;

    @Mock
    protected MenuItem validateItem;

    @Mock
    protected MenuItem exportsItem;

    @Mock
    protected MenuItem pasteItem;

    @Mock
    protected MenuItem copyItem;

    @Mock
    protected MenuItem cutItem;

    @Captor
    protected ArgumentCaptor<Consumer<ClientFullSession>> clientFullSessionConsumerCaptor;

    @Captor
    protected ArgumentCaptor<Consumer<ClientReadOnlySession>> clientReadOnlySessionConsumerCaptor;

    @Captor
    protected ArgumentCaptor<SessionPresenter.SessionPresenterCallback> clientSessionPresenterCallbackCaptor;

    @Captor
    protected ArgumentCaptor<ServiceCallback> serviceCallbackCaptor;

    @Captor
    protected ArgumentCaptor<ClientSessionCommand.Callback> validationCallbackCaptor;

    @Captor
    protected ArgumentCaptor<ParameterizedCommand<String>> savePopupCommandCaptor;

    @Captor
    protected ArgumentCaptor<NotificationEvent> notificationEventCaptor;

    @Mock
    private StunnerPreferences stunnerPreferences;

    abstract class ClientResourceTypeMock implements ClientResourceType {

    }

    protected AbstractProjectDiagramEditor<ClientResourceTypeMock> presenter;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        doReturn(clearStatesSessionCommand).when(sessionCommandFactory).newClearStatesCommand();
        doReturn(switchGridSessionCommand).when(sessionCommandFactory).newSwitchGridCommand();
        doReturn(visitGraphSessionCommand).when(sessionCommandFactory).newVisitGraphCommand();
        doReturn(clearSessionCommand).when(sessionCommandFactory).newClearCommand();
        doReturn(deleteSelectionSessionCommand).when(sessionCommandFactory).newDeleteSelectedElementsCommand();
        doReturn(undoSessionCommand).when(sessionCommandFactory).newUndoCommand();
        doReturn(redoSessionCommand).when(sessionCommandFactory).newRedoCommand();
        doReturn(validateSessionCommand).when(sessionCommandFactory).newValidateCommand();
        doReturn(exportToPngSessionCommand).when(sessionCommandFactory).newExportToPngSessionCommand();
        doReturn(exportToJpgSessionCommand).when(sessionCommandFactory).newExportToJpgSessionCommand();
        doReturn(exportToSvgSessionCommand).when(sessionCommandFactory).newExportToSvgSessionCommand();
        doReturn(exportToPdfSessionCommand).when(sessionCommandFactory).newExportToPdfSessionCommand();
        doReturn(exportToBpmnSessionCommand).when(sessionCommandFactory).newExportToBpmnSessionCommand();
        doReturn(copySelectionSessionCommand).when(sessionCommandFactory).newCopySelectionCommand();
        doReturn(pasteSelectionSessionCommand).when(sessionCommandFactory).newPasteSelectionCommand();
        doReturn(cutSelectionSessionCommand).when(sessionCommandFactory).newCutSelectionCommand();

        when(sessionPresenterFactory.newPresenterEditor()).thenReturn(fullSessionPresenter);
        when(fullSessionPresenter.getInstance()).thenReturn(clientFullSession);
        when(fullSessionPresenter.withToolbar(anyBoolean())).thenReturn(fullSessionPresenter);
        when(fullSessionPresenter.withPalette(anyBoolean())).thenReturn(fullSessionPresenter);
        when(fullSessionPresenter.withPreferences(stunnerPreferences)).thenReturn(fullSessionPresenter);
        when(fullSessionPresenter.displayNotifications(any(Predicate.class))).thenReturn(fullSessionPresenter);
        when(fullSessionPresenter.getView()).thenReturn(sessionPresenterView);

        when(sessionPresenterFactory.newPresenterViewer()).thenReturn(readOnlySessionPresenter);
        when(readOnlySessionPresenter.getInstance()).thenReturn(clientReadOnlySession);
        when(readOnlySessionPresenter.withToolbar(anyBoolean())).thenReturn(readOnlySessionPresenter);
        when(readOnlySessionPresenter.withPalette(anyBoolean())).thenReturn(readOnlySessionPresenter);
        when(readOnlySessionPresenter.displayNotifications(any(Predicate.class))).thenReturn(readOnlySessionPresenter);
        when(readOnlySessionPresenter.getView()).thenReturn(sessionPresenterView);

        when(projectMenuItemsBuilder.newClearItem(any(Command.class))).thenReturn(clearItem);
        when(projectMenuItemsBuilder.newVisitGraphItem(any(Command.class))).thenReturn(visitGraphItem);
        when(projectMenuItemsBuilder.newSwitchGridItem(any(Command.class))).thenReturn(switchGridItem);
        when(projectMenuItemsBuilder.newDeleteSelectionItem(any(Command.class))).thenReturn(deleteSelectionItem);
        when(projectMenuItemsBuilder.newUndoItem(any(Command.class))).thenReturn(undoItem);
        when(projectMenuItemsBuilder.newRedoItem(any(Command.class))).thenReturn(redoItem);
        when(projectMenuItemsBuilder.newValidateItem(any(Command.class))).thenReturn(validateItem);
        when(projectMenuItemsBuilder.newExportsItem(any(Command.class),
                                                    any(Command.class),
                                                    any(Command.class),
                                                    any(Command.class),
                                                    any(Command.class))).thenReturn(exportsItem);
        when(projectMenuItemsBuilder.newPasteItem(any(Command.class))).thenReturn(pasteItem);
        when(projectMenuItemsBuilder.newCopyItem(any(Command.class))).thenReturn(copyItem);
        when(projectMenuItemsBuilder.newCutItem(any(Command.class))).thenReturn(cutItem);

        when(alertsButtonMenuItemBuilder.build()).thenReturn(alertsButtonMenuItem);
        when(versionRecordManager.getPathToLatest()).thenReturn(filePath);
        when(stunnerPreferencesRegistry.get()).thenReturn(stunnerPreferences);

        when(xmlEditorView.asWidget()).thenReturn(xmlEditorWidget);

        doAnswer(i -> i.getArguments()[0]).when(translationService).getValue(anyString());

        resourceType = mockResourceType();
        presenter = createDiagramEditor();
        presenter.init();

        final String editorIdentifier = presenter.getEditorIdentifier();
        when(placeRequest.getIdentifier()).thenReturn(editorIdentifier);
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

    @SuppressWarnings("unchecked")
    protected AbstractProjectDiagramEditor createDiagramEditor() {
        return spy(new AbstractProjectDiagramEditor<ClientResourceTypeMock>(view,
                                                                            placeManager,
                                                                            errorPopupPresenter,
                                                                            changeTitleNotificationEvent,
                                                                            savePopUpPresenter,
                                                                            (ClientResourceTypeMock) getResourceType(),
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
                                                                            xmlEditorView,
                                                                            stunnerPreferencesRegistry) {
            {
                place = AbstractProjectDiagramEditorTest.this.placeRequest;
                fileMenuBuilder = AbstractProjectDiagramEditorTest.this.fileMenuBuilder;
                workbenchContext = AbstractProjectDiagramEditorTest.this.workbenchContext;
                projectController = AbstractProjectDiagramEditorTest.this.projectController;
                versionRecordManager = AbstractProjectDiagramEditorTest.this.versionRecordManager;
                alertsButtonMenuItemBuilder = AbstractProjectDiagramEditorTest.this.alertsButtonMenuItemBuilder;
                kieView = AbstractProjectDiagramEditorTest.this.kieView;
                overviewWidget = AbstractProjectDiagramEditorTest.this.overviewWidget;
                notification = AbstractProjectDiagramEditorTest.this.notificationEvent;
            }

            @Override
            protected int getCanvasWidth() {
                return 0;
            }

            @Override
            protected int getCanvasHeight() {
                return 0;
            }

            @Override
            protected String getEditorIdentifier() {
                return EDITOR_ID;
            }
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInit() {
        verify(view,
               times(1)).init(eq(presenter));
        verify(visitGraphSessionCommand,
               times(0)).bind(eq(clientFullSession));
        verify(switchGridSessionCommand,
               times(0)).bind(eq(clientFullSession));
        verify(clearSessionCommand,
               times(0)).bind(eq(clientFullSession));
        verify(deleteSelectionSessionCommand,
               times(0)).bind(eq(clientFullSession));
        verify(undoSessionCommand,
               times(0)).bind(eq(clientFullSession));
        verify(redoSessionCommand,
               times(0)).bind(eq(clientFullSession));
        verify(validateSessionCommand,
               times(0)).bind(eq(clientFullSession));
    }

    @Test
    public void testMakeMenuBar() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(true).when(projectController).canUpdateProject(any());

        presenter.makeMenuBar();

        verify(fileMenuBuilder).addSave(any(MenuItem.class));
        verify(fileMenuBuilder).addCopy(any(Path.class),
                                        any(AssetUpdateValidator.class));
        verify(fileMenuBuilder).addRename(any(Path.class),
                                          any(AssetUpdateValidator.class));
        verify(fileMenuBuilder).addDelete(any(Path.class),
                                          any(AssetUpdateValidator.class));
        verify(fileMenuBuilder).addNewTopLevelMenu(alertsButtonMenuItem);
    }

    @Test
    public void testMakeMenuBarWithoutUpdateProjectPermission() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(false).when(projectController).canUpdateProject(any());

        presenter.makeMenuBar();

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
        verify(fileMenuBuilder).addNewTopLevelMenu(alertsButtonMenuItem);
    }

    @Test
    public void testFormatTitle() {
        final String title = "testDiagram";
        final String formattedTitle = presenter.formatTitle(title);
        final String suffix = getResourceType().getSuffix();
        final String shortName = getResourceType().getShortName();
        assertEquals(formattedTitle,
                     "testDiagram." + suffix + " - " + shortName);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOpenWithUnhandledException() {
        openDiagram();

        verify(clientProjectDiagramService,
               times(1)).getByPath(eq(versionRecordManager.getCurrentPath()),
                                   serviceCallbackCaptor.capture());

        serviceCallbackCaptor.getValue().onError(new ClientRuntimeError(new DefinitionNotFoundException()));

        verify(placeManager,
               times(1)).forceClosePlace(any(PathPlaceRequest.class));

        final ArgumentCaptor<Consumer> consumerArgumentCaptor = forClass(Consumer.class);
        verify(diagramClientErrorHandler,
               times(1)).handleError(any(ClientRuntimeError.class),
                                     consumerArgumentCaptor.capture());
        consumerArgumentCaptor.getValue().accept("error message");
        verify(errorPopupPresenter,
               times(1)).showMessage("error message");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOpen() {
        final Overview overview = openDiagram();

        verify(view).showLoading();
        verify(presenter).setOriginalHash(anyInt());

        verify(view).setWidget(eq(sessionPresenterView));
        verify(fullSessionPresenter).withToolbar(eq(false));
        verify(fullSessionPresenter).withPalette(eq(true));
        verify(view).hideBusyIndicator();

        //Verify Overview widget was setup. It'd be nice to just verify(presenter).resetEditorPages(..) but it is protected
        verify(overviewWidget).setContent(eq(overview),
                                          eq(filePath));
        verify(kieView).clear();
        verify(kieView).addMainEditorPage(eq(view));
        verify(kieView).addOverviewPage(eq(overviewWidget),
                                        any(com.google.gwt.user.client.Command.class));

        verify(presenter).onDiagramLoad();

        assertEquals(fullSessionPresenter,
                     presenter.getSessionPresenter());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOpenReadOnly() {
        final Overview overview = openReadOnlyDiagram();

        verify(view).showLoading();
        verify(presenter).setOriginalHash(anyInt());

        verify(view).setWidget(eq(sessionPresenterView));
        verify(readOnlySessionPresenter).withToolbar(eq(false));
        verify(readOnlySessionPresenter).withPalette(eq(false));
        verify(view).hideBusyIndicator();

        //Verify Overview widget was setup. It'd be nice to just verify(presenter).resetEditorPages(..) but it is protected
        verify(overviewWidget).setContent(eq(overview),
                                          eq(filePath));
        verify(kieView).clear();
        verify(kieView).addMainEditorPage(eq(view));
        verify(kieView).addOverviewPage(eq(overviewWidget),
                                        any(com.google.gwt.user.client.Command.class));

        verify(presenter).onDiagramLoad();

        assertEquals(readOnlySessionPresenter,
                     presenter.getSessionPresenter());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOpenWithInvalidBPMNFile() {
        final String xml = "xml";
        final Overview overview = openInvalidBPMNFile(xml);

        verify(view).showLoading();
        verify(presenter).setOriginalHash(eq(xml.hashCode()));
        verify(view).setWidget(eq(xmlEditorWidget));
        verify(view).hideBusyIndicator();

        //Verify Overview widget was setup. It'd be nice to just verify(presenter).resetEditorPages(..) but it is protected
        verify(overviewWidget).setContent(eq(overview),
                                          eq(filePath));
        verify(kieView).clear();
        verify(kieView).addMainEditorPage(eq(view));
        verify(kieView).addOverviewPage(eq(overviewWidget),
                                        any(com.google.gwt.user.client.Command.class));

        verify(presenter).initialiseMenuBarStateForSession(eq(false));
        verify(xmlEditorView).setReadOnly(eq(false));
        verify(xmlEditorView).setContent(eq(xml), eq(AceEditorMode.XML));
        verify(presenter).makeXmlEditorProxy();
    }

    @Test
    public void testIsDirty() {
        openDiagram();

        assertFalse(presenter.isDirty(presenter.getCurrentDiagramHash()));
        presenter.setOriginalHash(~~(presenter.getCurrentDiagramHash() + 1));
        assertTrue(presenter.isDirty(presenter.getCurrentDiagramHash()));
    }

    @Test
    public void testHasChanges() {
        openDiagram();

        assertFalse(presenter.hasUnsavedChanges());
        presenter.setOriginalHash(~~(presenter.getCurrentDiagramHash() + 1));
        assertTrue(presenter.hasUnsavedChanges());
        presenter.setOriginalHash(~~(presenter.getCurrentDiagramHash()));
        assertFalse(presenter.hasUnsavedChanges());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testValidateBeforeSave() {
        //Need to open a diagram in order to setup Session and Presenter
        openDiagram();

        presenter.save();

        verify(validateSessionCommand,
               times(1)).execute(any(ClientSessionCommand.Callback.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStunnerSave_SaveFailed() {
        final String errorMessage = "Something went wrong";
        final ClientRuntimeError cre = new ClientRuntimeError(errorMessage);
        final ServiceCallback<ProjectDiagram> serviceCallback = assertBasicStunnerSaveOperation();

        serviceCallback.onError(cre);

        verify(presenter).onSaveError(eq(cre));
        final ArgumentCaptor<Consumer> consumerCaptor = forClass(Consumer.class);
        verify(diagramClientErrorHandler).handleError(eq(cre), consumerCaptor.capture());

        final Consumer consumer = consumerCaptor.getValue();
        consumer.accept(errorMessage);

        verify(errorPopupPresenter).showMessage(eq(errorMessage));
    }

    @Test
    public void testStunnerSave_ValidationSuccessful() {
        final ServiceCallback<ProjectDiagram> serviceCallback = assertBasicStunnerSaveOperation();

        serviceCallback.onSuccess(diagram);

        final Path path = versionRecordManager.getCurrentPath();
        verify(versionRecordManager).reloadVersions(eq(path));
        verify(sessionPresenterView).showMessage(eq(StunnerProjectClientConstants.DIAGRAM_SAVE_SUCCESSFUL));
        verify(view).hideBusyIndicator();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStunnerSave_ValidationUnsuccessful() {
        openDiagram();

        reset(presenter, view);

        doReturn(diagram).when(presenter).getDiagram();
        presenter.save();

        verify(validateSessionCommand).execute(validationCallbackCaptor.capture());

        final Collection<DiagramElementViolation<RuleViolation>> violations = new ArrayList<>();
        violations.add(new ElementViolationImpl.Builder().setUuid("UUID").setGraphViolations(Collections.singletonList(new BoundsExceededViolation(mock(Bounds.class)))).build());
        final ClientSessionCommand.Callback validationCallback = validationCallbackCaptor.getValue();
        validationCallback.onError(violations);

        verify(presenter).onValidationFailed(eq(violations));
        verify(view).hideBusyIndicator();
    }

    @SuppressWarnings("unchecked")
    private ServiceCallback<ProjectDiagram> assertBasicStunnerSaveOperation() {
        final String commitMessage = "message";
        final Overview overview = openDiagram();
        final Metadata metadata = overview.getMetadata();

        reset(presenter, view);

        doReturn(diagram).when(presenter).getDiagram();
        presenter.save();

        verify(validateSessionCommand).execute(validationCallbackCaptor.capture());

        final ClientSessionCommand.Callback validationCallback = validationCallbackCaptor.getValue();
        validationCallback.onSuccess();

        verify(savePopUpPresenter).show(eq(versionRecordManager.getCurrentPath()),
                                        savePopupCommandCaptor.capture());

        final ParameterizedCommand<String> savePopupCommand = savePopupCommandCaptor.getValue();
        savePopupCommand.execute(commitMessage);

        verify(view).showSaving();
        verify(clientProjectDiagramService).saveOrUpdate(eq(versionRecordManager.getCurrentPath()),
                                                         eq(diagram),
                                                         eq(metadata),
                                                         eq(commitMessage),
                                                         serviceCallbackCaptor.capture());

        return serviceCallbackCaptor.getValue();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testXMLSave_ValidationNotRequired() {
        final String xml = "xml";
        final ServiceCallback<String> serviceCallback = assertBasicXMLSaveOperation(xml);

        serviceCallback.onSuccess(xml);

        final Path path = versionRecordManager.getCurrentPath();
        verify(versionRecordManager).reloadVersions(eq(path));
        verify(notificationEvent).fire(notificationEventCaptor.capture());

        final NotificationEvent notificationEvent = notificationEventCaptor.getValue();
        assertEquals("ItemSavedSuccessfully",
                     notificationEvent.getNotification());

        verify(view).hideBusyIndicator();
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
        final ArgumentCaptor<Consumer> consumerCaptor = forClass(Consumer.class);
        verify(diagramClientErrorHandler).handleError(eq(cre), consumerCaptor.capture());

        final Consumer consumer = consumerCaptor.getValue();
        consumer.accept(errorMessage);

        verify(errorPopupPresenter).showMessage(eq(errorMessage));
    }

    @SuppressWarnings("unchecked")
    private ServiceCallback<String> assertBasicXMLSaveOperation(final String xml) {
        final String commitMessage = "message";
        final Overview overview = openInvalidBPMNFile(xml);
        final Metadata metadata = overview.getMetadata();

        reset(presenter, view);

        doReturn(xml).when(xmlEditorView).getContent();
        presenter.save();

        verify(savePopUpPresenter).show(eq(versionRecordManager.getCurrentPath()),
                                        savePopupCommandCaptor.capture());

        final ParameterizedCommand<String> savePopupCommand = savePopupCommandCaptor.getValue();
        savePopupCommand.execute(commitMessage);

        verify(view).showSaving();
        verify(clientProjectDiagramService).saveAsXml(eq(versionRecordManager.getCurrentPath()),
                                                      eq(xml),
                                                      eq(metadata),
                                                      eq(commitMessage),
                                                      serviceCallbackCaptor.capture());

        return serviceCallbackCaptor.getValue();
    }

    @Test
    public void testDiagramHashCodeWithInvalidBPMNFile() {
        final String xml = "xml";
        when(xmlEditorView.getContent()).thenReturn(xml);

        openInvalidBPMNFile(xml);

        assertEquals(xml.hashCode(),
                     presenter.getCurrentDiagramHash());
    }

    @SuppressWarnings("unchecked")
    protected Overview openDiagram() {
        final ProjectMetadata metadata = mock(ProjectMetadata.class);
        final Metadata overviewMetadata = mock(Metadata.class);
        final Overview overview = mock(Overview.class);
        final ClientSessionFactory clientSessionFactory = mock(ClientSessionFactory.class);

        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getTitle()).thenReturn(TITLE);
        when(metadata.getOverview()).thenReturn(overview);
        when(sessionManager.getSessionFactory(eq(metadata), eq(ClientFullSession.class))).thenReturn(clientSessionFactory);
        when(overview.getMetadata()).thenReturn(overviewMetadata);

        doAnswer(i -> {
            final ServiceCallback serviceCallback = (ServiceCallback) i.getArguments()[1];
            serviceCallback.onSuccess(diagram);
            return null;
        }).when(clientProjectDiagramService).getByPath(any(Path.class),
                                                       any(ServiceCallback.class));

        presenter.loadContent();

        verify(presenter).destroySession();

        verify(clientSessionFactory).newSession(eq(metadata),
                                                clientFullSessionConsumerCaptor.capture());

        final Consumer<ClientFullSession> clientFullSessionConsumer = clientFullSessionConsumerCaptor.getValue();
        clientFullSessionConsumer.accept(clientFullSession);

        verify(fullSessionPresenter).open(eq(diagram),
                                          eq(clientFullSession),
                                          clientSessionPresenterCallbackCaptor.capture());

        final SessionPresenter.SessionPresenterCallback clientSessionPresenterCallback = clientSessionPresenterCallbackCaptor.getValue();
        clientSessionPresenterCallback.onSuccess();

        verify(presenter).makeStunnerEditorProxy();

        return overview;
    }

    @SuppressWarnings("unchecked")
    protected Overview openReadOnlyDiagram() {
        final ProjectMetadata metadata = mock(ProjectMetadata.class);
        final Overview overview = mock(Overview.class);
        final ClientSessionFactory clientSessionFactory = mock(ClientSessionFactory.class);

        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getTitle()).thenReturn(TITLE);
        when(metadata.getOverview()).thenReturn(overview);
        when(sessionManager.getSessionFactory(eq(metadata), eq(ClientReadOnlySession.class))).thenReturn(clientSessionFactory);

        doReturn(true).when(presenter).isReadOnly();

        doAnswer(i -> {
            final ServiceCallback serviceCallback = (ServiceCallback) i.getArguments()[1];
            serviceCallback.onSuccess(diagram);
            return null;
        }).when(clientProjectDiagramService).getByPath(any(Path.class),
                                                       any(ServiceCallback.class));

        presenter.loadContent();

        verify(presenter).destroySession();

        verify(clientSessionFactory).newSession(eq(metadata),
                                                clientReadOnlySessionConsumerCaptor.capture());

        final Consumer<ClientReadOnlySession> clientReadOnlySessionConsumer = clientReadOnlySessionConsumerCaptor.getValue();
        clientReadOnlySessionConsumer.accept(clientReadOnlySession);

        verify(readOnlySessionPresenter).open(eq(diagram),
                                              eq(clientReadOnlySession),
                                              clientSessionPresenterCallbackCaptor.capture());

        final SessionPresenter.SessionPresenterCallback clientSessionPresenterCallback = clientSessionPresenterCallbackCaptor.getValue();
        clientSessionPresenterCallback.onSuccess();

        verify(presenter).makeStunnerEditorProxy();

        return overview;
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

    @Test
    @SuppressWarnings("unchecked")
    public void testCloseEditor() {
        presenter.setFullSessionPresenter(fullSessionPresenter);
        presenter.doClose();

        verify(clearStatesSessionCommand, times(1)).unbind();
        verify(switchGridSessionCommand, times(1)).unbind();
        verify(visitGraphSessionCommand, times(1)).unbind();
        verify(clearSessionCommand, times(1)).unbind();
        verify(deleteSelectionSessionCommand, times(1)).unbind();
        verify(undoSessionCommand, times(1)).unbind();
        verify(redoSessionCommand, times(1)).unbind();
        verify(validateSessionCommand, times(1)).unbind();
        verify(exportToPngSessionCommand, times(1)).unbind();
        verify(exportToJpgSessionCommand, times(1)).unbind();
        verify(exportToPdfSessionCommand, times(1)).unbind();
        verify(exportToSvgSessionCommand, times(1)).unbind();
        verify(copySelectionSessionCommand, times(1)).unbind();
        verify(pasteSelectionSessionCommand, times(1)).unbind();
        verify(cutSelectionSessionCommand, times(1)).unbind();
        verify(fullSessionPresenter, never()).clear();
        verify(fullSessionPresenter, times(1)).destroy();
    }

    @Test
    public void testInitialiseMenuBarStateForSession_Enabled() {
        presenter.initialiseMenuBarStateForSession(true);

        verify(clearItem).setEnabled(true);
        verify(visitGraphItem).setEnabled(true);
        verify(switchGridItem).setEnabled(true);
        verify(validateItem).setEnabled(true);
        verify(exportsItem).setEnabled(true);

        verify(deleteSelectionItem).setEnabled(false);
        verify(undoItem).setEnabled(false);
        verify(redoItem).setEnabled(false);
        verify(copyItem).setEnabled(false);
        verify(cutItem).setEnabled(false);
        verify(pasteItem).setEnabled(false);
    }

    @Test
    public void testInitialiseMenuBarStateForSession_Disabled() {
        presenter.initialiseMenuBarStateForSession(false);

        verify(clearItem).setEnabled(false);
        verify(visitGraphItem).setEnabled(false);
        verify(switchGridItem).setEnabled(false);
        verify(validateItem).setEnabled(false);
        verify(exportsItem).setEnabled(false);

        verify(deleteSelectionItem).setEnabled(false);
        verify(undoItem).setEnabled(false);
        verify(redoItem).setEnabled(false);
        verify(copyItem).setEnabled(false);
        verify(cutItem).setEnabled(false);
        verify(pasteItem).setEnabled(false);
    }

    @Test
    public void testOnPlaceHiddenEvent() {
        final PlaceHiddenEvent event = new PlaceHiddenEvent(placeRequest);

        presenter.hideDiagramEditorDocks(event);

        verify(onDiagramLostFocusEvent).fire(any(OnDiagramLoseFocusEvent.class));
    }

    @Test
    public void testNotValidOnPlaceHiddenEvent() {
        final PlaceRequest anotherRequest = mock(PlaceRequest.class);

        when(anotherRequest.getIdentifier()).thenReturn("");

        final PlaceHiddenEvent event = new PlaceHiddenEvent(anotherRequest);

        presenter.hideDiagramEditorDocks(event);

        verify(onDiagramLostFocusEvent,
               never()).fire(any(OnDiagramLoseFocusEvent.class));
    }

    @Test
    public void testOnPlaceGainFocusEvent() {
        final PlaceGainFocusEvent event = new PlaceGainFocusEvent(placeRequest);

        presenter.showDiagramEditorDocks(event);

        verify(onDiagramFocusEvent).fire(any(OnDiagramFocusEvent.class));
    }

    @Test
    public void testNotValidOnPlaceGainFocusEvent() {
        final PlaceRequest anotherRequest = mock(PlaceRequest.class);

        when(anotherRequest.getIdentifier()).thenReturn("");

        final PlaceGainFocusEvent event = new PlaceGainFocusEvent(anotherRequest);

        presenter.showDiagramEditorDocks(event);

        verify(onDiagramFocusEvent,
               never()).fire(any(OnDiagramFocusEvent.class));
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

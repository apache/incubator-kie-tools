/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.metadata.client;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.messageconsole.client.console.widget.button.AlertsButtonMenuItemBuilder;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.docks.DefaultEditorDock;
import org.kie.workbench.common.widgets.client.docks.DockPlaceHolderView;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl;
import org.kie.workbench.common.widgets.metadata.client.validation.AssetUpdateValidator;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.mvp.AbstractWorkbenchActivity;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.events.PlaceHiddenEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.editor.commons.client.BaseEditorView;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.menu.common.SaveAndRenameCommandBuilder;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class KieEditorTest {

    @Mock
    protected PlaceManager placeManager;
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
    protected EventSourceMock<NotificationEvent> notification;
    @Mock
    protected SaveAndRenameCommandBuilder<String, Metadata> saveAndRenameCommandBuilder;
    @Mock
    protected Metadata metadata;
    @Mock
    protected KieEditorWrapperView kieView;
    @Mock
    protected AlertsButtonMenuItemBuilder alertsButtonMenuItemBuilder;
    @Mock
    protected MenuItem alertsButtonMenuItem;
    @Spy
    @InjectMocks
    protected AssetUpdateValidator assetUpdateValidator;
    protected KieEditorFake presenter;
    @Captor
    ArgumentCaptor<PlaceRequest> placeRequestArgumentCaptor;
    @Captor
    ArgumentCaptor<Command> commandArgumentCaptor;
    @Mock
    private DefaultEditorDock docks;
    @Mock
    private PerspectiveManager perspectiveManager;

    @Before
    public void setup() {
        when(alertsButtonMenuItemBuilder.build()).thenReturn(alertsButtonMenuItem);
        presenter = spy(new KieEditorFake());
    }

    @Test
    public void testMakeMenuBar() {

        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(true).when(projectController).canUpdateProject(any());

        presenter.makeMenuBar();

        verify(presenter).addSave(fileMenuBuilder);
        verify(presenter).addCopy(fileMenuBuilder);
        verify(presenter).addRename(fileMenuBuilder);
        verify(presenter).addDelete(fileMenuBuilder);
        verify(presenter).addDownloadMenuItem(fileMenuBuilder);
        verify(presenter).addCommonActions(fileMenuBuilder);
    }

    @Test
    public void testMakeMenuBarWithoutUpdateProjectPermission() {

        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(false).when(projectController).canUpdateProject(any());

        presenter.makeMenuBar();

        verify(presenter, never()).addSave(fileMenuBuilder);
        verify(presenter, never()).addCopy(fileMenuBuilder);
        verify(presenter, never()).addRename(fileMenuBuilder);
        verify(presenter, never()).addDelete(fileMenuBuilder);
        verify(presenter).addCommonActions(fileMenuBuilder);
    }

    @Test
    public void testAddSave() {

        final MenuItem menuItem = mock(MenuItem.class);
        final Command command = mock(Command.class);

        doReturn(command).when(presenter).getSaveActionCommand();
        doReturn(menuItem).when(versionRecordManager).newSaveMenuItem(command);

        presenter.addSave(fileMenuBuilder);

        verify(fileMenuBuilder).addSave(menuItem);
    }

    @Test
    public void testAddCopy() {

        final ObservablePath observablePath = mock(ObservablePath.class);

        doReturn(observablePath).when(versionRecordManager).getCurrentPath();

        presenter.addCopy(fileMenuBuilder);

        verify(fileMenuBuilder).addCopy(observablePath, assetUpdateValidator);
    }

    @Test
    public void testAddRenameWhenSaveAndRenameIsEnabled() {

        final Caller saveAndRenameCaller = mock(Caller.class);
        final Command command = mock(Command.class);

        doReturn(saveAndRenameCaller).when(presenter).getSaveAndRenameServiceCaller();
        doReturn(command).when(presenter).getSaveAndRename();

        presenter.addRename(fileMenuBuilder);

        verify(fileMenuBuilder).addRename(command);
    }

    @Test
    public void testAddRenameWhenSaveAndRenameIsNotEnabled() {

        final ObservablePath observablePath = mock(ObservablePath.class);

        doReturn(observablePath).when(versionRecordManager).getPathToLatest();
        doReturn(null).when(presenter).getSaveAndRenameServiceCaller();

        presenter.addRename(fileMenuBuilder);

        verify(fileMenuBuilder).addRename(observablePath, assetUpdateValidator);
    }

    @Test
    public void testAddDelete() {

        final ObservablePath observablePath = mock(ObservablePath.class);

        doReturn(observablePath).when(versionRecordManager).getPathToLatest();

        presenter.addDelete(fileMenuBuilder);

        verify(fileMenuBuilder).addDelete(observablePath, assetUpdateValidator);
    }

    @Test
    public void testAddDownloadMenuItem() {

        final MenuItem menuItem = mock(MenuItem.class);

        doReturn(menuItem).when(presenter).downloadMenuItem();

        presenter.addDownloadMenuItem(fileMenuBuilder);

        verify(fileMenuBuilder).addNewTopLevelMenu(menuItem);
    }

    @Test
    public void testAddCommonActions() {

        final Command onValidate = mock(Command.class);
        final MenuItem buildMenu = mock(MenuItem.class);
        final MenuItem build = mock(MenuItem.class);

        doReturn(onValidate).when(presenter).getValidateCommand();
        doReturn(buildMenu).when(versionRecordManager).buildMenu();
        doReturn(build).when(alertsButtonMenuItemBuilder).build();

        presenter.addCommonActions(fileMenuBuilder);

        verify(fileMenuBuilder).addValidate(onValidate);
        verify(fileMenuBuilder).addNewTopLevelMenu(buildMenu);
        verify(fileMenuBuilder).addNewTopLevelMenu(build);
    }

    @Test
    public void testValidSaveAction() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(true).when(projectController).canUpdateProject(any());

        presenter.saveAction();

        verify(presenter).onSave();
    }

    @Test
    public void testNotAllowedSaveAction() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(false).when(projectController).canUpdateProject(any());
        doReturn("not-allowed").when(kieView).getNotAllowedSavingMessage();

        presenter.saveAction();

        verify(notification).fire(new NotificationEvent("not-allowed",
                                                        NotificationEvent.NotificationType.ERROR));
        verify(presenter,
               never()).onSave();
    }

    @Test
    public void testGetMetadataSupplier() {

        final Supplier<Metadata> metadataSupplier = presenter.getMetadataSupplier();

        assertEquals(metadata, metadataSupplier.get());
    }

    @Test
    public void testShowDiagramEditorDocks() {

        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();

        PlaceRequest placeRequest = mock(PlaceRequest.class);
        when(placeRequest.getIdentifier()).thenReturn(KieEditorFake.EDITOR_ID);

        presenter.init(mock(ObservablePath.class),
                       placeRequest,
                       mock(ClientResourceType.class));

        when(docks.isSetup()).thenReturn(false);
        PerspectiveActivity perspectiveActivity = mock(PerspectiveActivity.class);
        when(perspectiveActivity.getIdentifier()).thenReturn("perspectiveId");
        when(perspectiveManager.getCurrentPerspective()).thenReturn(perspectiveActivity);

        presenter.onShowDiagramEditorDocks(new PlaceGainFocusEvent(placeRequest));

        verify(docks).setup(eq("perspectiveId"),
                            placeRequestArgumentCaptor.capture());
        assertEquals("org.kie.guvnor.explorer", placeRequestArgumentCaptor.getValue().getIdentifier());
        verify(docks).show();
    }

    @Test
    public void testShowDiagramEditorDocksInitDone() {

        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();

        PlaceRequest placeRequest = mock(PlaceRequest.class);
        when(placeRequest.getIdentifier()).thenReturn(KieEditorFake.EDITOR_ID);

        presenter.init(mock(ObservablePath.class),
                       placeRequest,
                       mock(ClientResourceType.class));

        when(docks.isSetup()).thenReturn(true);
        PerspectiveActivity perspectiveActivity = mock(PerspectiveActivity.class);
        when(perspectiveActivity.getIdentifier()).thenReturn("perspectiveId");
        when(perspectiveManager.getCurrentPerspective()).thenReturn(perspectiveActivity);

        presenter.onShowDiagramEditorDocks(new PlaceGainFocusEvent(placeRequest));

        verify(docks, never()).setup(anyString(),
                                     any());
        verify(docks).show();
    }

    @Test
    public void testShowDiagramEditorDocksPlaceDoesNotMatch() {

        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();

        PlaceRequest placeRequest = mock(PlaceRequest.class);
        when(placeRequest.getIdentifier()).thenReturn(KieEditorFake.EDITOR_ID);

        presenter.init(mock(ObservablePath.class),
                       mock(PlaceRequest.class),
                       mock(ClientResourceType.class));

        presenter.onShowDiagramEditorDocks(new PlaceGainFocusEvent(placeRequest));

        verify(docks, never()).show();
    }

    @Test
    public void testHideDiagramEditorDocks() {

        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();

        PlaceRequest placeRequest = mock(PlaceRequest.class);
        when(placeRequest.getIdentifier()).thenReturn(KieEditorFake.EDITOR_ID);

        presenter.init(mock(ObservablePath.class),
                       placeRequest,
                       mock(ClientResourceType.class));

        presenter.onHideDocks(new PlaceHiddenEvent(placeRequest));

        verify(docks).hide();
    }

    @Test
    public void testHideDiagramEditorDocksDifferentPlace() {

        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();

        PlaceRequest placeRequest = mock(PlaceRequest.class);
        when(placeRequest.getIdentifier()).thenReturn(KieEditorFake.EDITOR_ID);

        presenter.init(mock(ObservablePath.class),
                       mock(PlaceRequest.class),
                       mock(ClientResourceType.class));

        presenter.onHideDocks(new PlaceHiddenEvent(placeRequest));

        verify(docks, never()).hide();
    }

    @Test
    public void testGetRenameValidator() {

        final Validator renameValidator = presenter.getRenameValidator();

        assertEquals(assetUpdateValidator, renameValidator);
    }

    @Test
    public void registerDock() {
        presenter.registerDock("test", mock(IsWidget.class));

        verify(placeManager).registerOnOpenCallback(placeRequestArgumentCaptor.capture(),
                                                    any(Command.class));
        final PlaceRequest placeRequest = placeRequestArgumentCaptor.getValue();
        assertEquals("org.docks.PlaceHolder", placeRequest.getIdentifier());
        final Map<String, String> parameters = placeRequest.getParameters();
        assertEquals(1, parameters.size());
        assertEquals("test", parameters.get("name"));
    }

    @Test
    public void registerDockWhenItExists() {
        final IsWidget widget = mock(IsWidget.class);
        presenter.registerDock("test", widget);

        doReturn(PlaceStatus.OPEN).when(placeManager).getStatus(any(PlaceRequest.class));
        final AbstractWorkbenchActivity workbenchActivity = mock(AbstractWorkbenchActivity.class);
        doReturn(workbenchActivity).when(placeManager).getActivity(any());
        final DockPlaceHolderView placeHolderView = mock(DockPlaceHolderView.class);
        doReturn(placeHolderView).when(workbenchActivity).getWidget();
        verify(placeManager).registerOnOpenCallback(any(),
                                                    commandArgumentCaptor.capture());

        commandArgumentCaptor.getValue().execute();

        verify(placeHolderView).setWidget(widget);
    }

    @Test
    public void registerDockWhenDockDoesNotExist() {
        final IsWidget widget = mock(IsWidget.class);
        presenter.registerDock("test", widget);

        doReturn(PlaceStatus.CLOSE).when(placeManager).getStatus(any(PlaceRequest.class));
        final AbstractWorkbenchActivity workbenchActivity = mock(AbstractWorkbenchActivity.class);
        doReturn(workbenchActivity).when(placeManager).getActivity(any());
        final DockPlaceHolderView placeHolderView = mock(DockPlaceHolderView.class);
        doReturn(placeHolderView).when(workbenchActivity).getWidget();
        verify(placeManager).registerOnOpenCallback(any(),
                                                    commandArgumentCaptor.capture());

        commandArgumentCaptor.getValue().execute();

        verify(placeHolderView, never()).setWidget(widget);
    }

    class KieEditorFake extends KieEditor<String> {

        public static final String EDITOR_ID = "KieEditorFake";

        public KieEditorFake() {
            projectController = mock(ProjectController.class);
            baseView = mock(BaseEditorView.class);
            docks = KieEditorTest.this.docks;
            fileMenuBuilder = KieEditorTest.this.fileMenuBuilder;
            projectController = KieEditorTest.this.projectController;
            workbenchContext = KieEditorTest.this.workbenchContext;
            versionRecordManager = KieEditorTest.this.versionRecordManager;
            assetUpdateValidator = KieEditorTest.this.assetUpdateValidator;
            notification = KieEditorTest.this.notification;
            kieView = KieEditorTest.this.kieView;
            saveAndRenameCommandBuilder = KieEditorTest.this.saveAndRenameCommandBuilder;
            metadata = KieEditorTest.this.metadata;
            alertsButtonMenuItemBuilder = KieEditorTest.this.alertsButtonMenuItemBuilder;
            perspectiveManager = KieEditorTest.this.perspectiveManager;
            placeManager = KieEditorTest.this.placeManager;
        }

        @Override
        protected String getEditorIdentifier() {
            return EDITOR_ID;
        }

        @Override
        protected Command getSaveAndRename() {
            return mock(Command.class);
        }

        @Override
        protected void loadContent() {
        }

        @Override
        protected Supplier<String> getContentSupplier() {
            return null;
        }

        @Override
        protected void onSave() {
        }

        @Override
        protected Caller<? extends SupportsSaveAndRename<String, Metadata>> getSaveAndRenameServiceCaller() {
            return super.getSaveAndRenameServiceCaller();
        }

        @Override
        protected MenuItem downloadMenuItem() {
            return mock(MenuItem.class);
        }
    }
}

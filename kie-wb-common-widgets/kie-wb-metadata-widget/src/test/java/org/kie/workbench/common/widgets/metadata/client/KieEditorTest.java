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

import java.util.Optional;
import java.util.function.Supplier;

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
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl;
import org.kie.workbench.common.widgets.metadata.client.validation.AssetUpdateValidator;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.menu.common.SaveAndRenameCommandBuilder;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class KieEditorTest {

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
    public void testGetRenameValidator() {

        final Validator renameValidator = presenter.getRenameValidator();

        assertEquals(assetUpdateValidator, renameValidator);
    }

    class KieEditorFake extends KieEditor<String> {

        public KieEditorFake() {
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

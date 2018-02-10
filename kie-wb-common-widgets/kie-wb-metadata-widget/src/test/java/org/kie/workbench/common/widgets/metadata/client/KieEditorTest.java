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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl;
import org.kie.workbench.common.widgets.metadata.client.validation.AssetUpdateValidator;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.menu.common.SaveAndRenameCommandBuilder;
import org.uberfire.ext.editor.commons.client.validation.Validator;
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

    @Spy
    @InjectMocks
    protected AssetUpdateValidator assetUpdateValidator;

    protected KieEditor<String> presenter;

    @Before
    public void setup() {
        presenter = spy(new KieEditor<String>() {
            {
                fileMenuBuilder = KieEditorTest.this.fileMenuBuilder;
                projectController = KieEditorTest.this.projectController;
                workbenchContext = KieEditorTest.this.workbenchContext;
                versionRecordManager = KieEditorTest.this.versionRecordManager;
                assetUpdateValidator = KieEditorTest.this.assetUpdateValidator;
                notification = KieEditorTest.this.notification;
                kieView = KieEditorTest.this.kieView;
                saveAndRenameCommandBuilder = KieEditorTest.this.saveAndRenameCommandBuilder;
                metadata = KieEditorTest.this.metadata;
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
        });
    }

    @Test
    public void testMakeMenuBar() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(true).when(projectController).canUpdateProject(any());

        presenter.makeMenuBar();

        verify(fileMenuBuilder).addSave(any(MenuItem.class));
        verify(fileMenuBuilder).addCopy(any(Path.class), any(AssetUpdateValidator.class));
        verify(fileMenuBuilder).addRename(any(Command.class));
        verify(fileMenuBuilder).addDelete(any(Path.class), any(AssetUpdateValidator.class));
    }

    @Test
    public void testMakeMenuBarWithoutUpdateProjectPermission() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(false).when(projectController).canUpdateProject(any());

        presenter.makeMenuBar();

        verify(fileMenuBuilder, never()).addSave(any(MenuItem.class));
        verify(fileMenuBuilder, never()).addCopy(any(Path.class), any(AssetUpdateValidator.class));
        verify(fileMenuBuilder, never()).addRename(any(Command.class));
        verify(fileMenuBuilder, never()).addDelete(any(Path.class), any(AssetUpdateValidator.class));
    }

    @Test
    public void validSaveActionTest() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(true).when(projectController).canUpdateProject(any());

        presenter.saveAction();

        verify(presenter).onSave();
    }

    @Test
    public void notAllowedSaveActionTest() {
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
}

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

package org.kie.workbench.common.screens.defaulteditor.client.editor;

import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.messageconsole.client.console.widget.button.AlertsButtonMenuItemBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.defaulteditor.service.DefaultEditorService;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl;
import org.kie.workbench.common.widgets.metadata.client.validation.AssetUpdateValidator;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.promise.Promises;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.promise.SyncPromises;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class GuvnorDefaultEditorPresenterTest {

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
    protected AlertsButtonMenuItemBuilder alertsButtonMenuItemBuilder;

    @Mock
    protected MenuItem alertsButtonMenuItem;

    @Mock
    protected DefaultEditorService defaultEditorServiceMock;

    @Mock
    protected MetadataService metadataServiceMock;

    @Mock
    protected EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    protected OverviewWidgetPresenter overviewWidgetMock;

    @Mock
    private Metadata metadataMock;

    protected Promises promises;

    protected GuvnorDefaultEditorPresenter presenter;

    @Before
    public void setup() {
        promises = new SyncPromises();
        doReturn(alertsButtonMenuItem).when(alertsButtonMenuItemBuilder).build();
        presenter = spy(new GuvnorDefaultEditorPresenter(mock(GuvnorDefaultEditorView.class)) {
            {
                fileMenuBuilder = GuvnorDefaultEditorPresenterTest.this.fileMenuBuilder;
                projectController = GuvnorDefaultEditorPresenterTest.this.projectController;
                workbenchContext = GuvnorDefaultEditorPresenterTest.this.workbenchContext;
                versionRecordManager = GuvnorDefaultEditorPresenterTest.this.versionRecordManager;
                alertsButtonMenuItemBuilder = GuvnorDefaultEditorPresenterTest.this.alertsButtonMenuItemBuilder;
                promises = GuvnorDefaultEditorPresenterTest.this.promises;
                defaultEditorService = new CallerMock<>(defaultEditorServiceMock);
                metadataService = new CallerMock<>(metadataServiceMock);
                metadata = metadataMock;
                notification = notificationEvent;
                overviewWidget = overviewWidgetMock;
            }
        });

        doNothing().when(presenter).addDownloadMenuItem(any());
    }

    @Test
    public void testMakeMenuBar() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(promises.resolve(true)).when(projectController).canUpdateProject(any());

        presenter.makeMenuBar();

        verify(fileMenuBuilder).addSave(any(MenuItem.class));
        verify(fileMenuBuilder).addCopy(any(Path.class),
                                        any(AssetUpdateValidator.class));
        verify(fileMenuBuilder).addRename(any(Path.class),
                                          any(AssetUpdateValidator.class));
        verify(fileMenuBuilder).addDelete(any(Path.class),
                                          any(AssetUpdateValidator.class));
        verify(fileMenuBuilder).addNewTopLevelMenu(alertsButtonMenuItem);
        verify(presenter).addDownloadMenuItem(fileMenuBuilder);
    }

    @Test
    public void testSave() {
        final ObservablePath currentPathMock = mock(ObservablePath.class);
        doReturn(currentPathMock).when(versionRecordManager).getCurrentPath();

        presenter.save("save");

        verify(metadataServiceMock).saveMetadata(eq(currentPathMock),
                                                 eq(metadataMock),
                                                 eq("save"));

        verify(presenter).getSaveSuccessCallback(eq(metadataMock.hashCode()));
    }

    @Test
    public void testMakeMenuBarWithoutUpdateProjectPermission() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(promises.resolve(false)).when(projectController).canUpdateProject(any());

        presenter.makeMenuBar();

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
        verify(presenter).addDownloadMenuItem(fileMenuBuilder);
    }
}

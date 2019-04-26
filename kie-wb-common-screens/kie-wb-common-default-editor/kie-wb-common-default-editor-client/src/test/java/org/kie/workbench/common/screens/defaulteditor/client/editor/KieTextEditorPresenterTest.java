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
import org.kie.workbench.common.screens.defaulteditor.service.DefaultEditorService;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl;
import org.kie.workbench.common.widgets.metadata.client.validation.AssetUpdateValidator;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.promise.Promises;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
import org.uberfire.mvp.Command;
import org.uberfire.promise.SyncPromises;
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
public class KieTextEditorPresenterTest {

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
    protected Caller<DefaultEditorService> defaultEditorService;

    @Mock
    protected AlertsButtonMenuItemBuilder alertsButtonMenuItemBuilder;

    @Mock
    protected MenuItem alertsButtonMenuItem;

    @Mock
    protected KieTextEditorView view;

    protected Promises promises;

    protected KieTextEditorPresenter presenter;

    @Before
    public void setup() {
        promises = new SyncPromises();
        when(alertsButtonMenuItemBuilder.build()).thenReturn(alertsButtonMenuItem);
        presenter = new KieTextEditorPresenter(view) {
            {
                fileMenuBuilder = KieTextEditorPresenterTest.this.fileMenuBuilder;
                projectController = KieTextEditorPresenterTest.this.projectController;
                workbenchContext = KieTextEditorPresenterTest.this.workbenchContext;
                versionRecordManager = KieTextEditorPresenterTest.this.versionRecordManager;
                defaultEditorService = KieTextEditorPresenterTest.this.defaultEditorService;
                alertsButtonMenuItemBuilder = KieTextEditorPresenterTest.this.alertsButtonMenuItemBuilder;
            }

            @Override
            protected String getEditorIdentifier() {
                return "KieTextEditorPresenter";
            }

            @Override
            public void addDownloadMenuItem(final FileMenuBuilder fileMenuBuilder) {
                // Do nothing.
            }

            @Override
            protected Command getSaveAndRename() {
                return mock(Command.class);
            }
        };
    }

    @Test
    public void testMakeMenuBar() {

        final KieTextEditorPresenter presenter = spy(this.presenter);

        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(promises.resolve(true)).when(projectController).canUpdateProject(any());

        presenter.makeMenuBar();

        verify(fileMenuBuilder).addSave(any(MenuItem.class));
        verify(fileMenuBuilder).addCopy(any(Path.class), any(AssetUpdateValidator.class));
        verify(fileMenuBuilder).addRename(any(Command.class));
        verify(fileMenuBuilder).addDelete(any(Path.class), any(AssetUpdateValidator.class));
        verify(fileMenuBuilder).addNewTopLevelMenu(alertsButtonMenuItem);
        verify(presenter).addDownloadMenuItem(fileMenuBuilder);
    }

    @Test
    public void testMakeMenuBarWithoutUpdateProjectPermission() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(promises.resolve(false)).when(projectController).canUpdateProject(any());

        presenter.makeMenuBar();

        verify(fileMenuBuilder, never()).addSave(any(MenuItem.class));
        verify(fileMenuBuilder, never()).addCopy(any(Path.class), any(AssetUpdateValidator.class));
        verify(fileMenuBuilder, never()).addRename(any(Command.class));
        verify(fileMenuBuilder, never()).addDelete(any(Path.class), any(AssetUpdateValidator.class));
        verify(fileMenuBuilder).addNewTopLevelMenu(alertsButtonMenuItem);
    }

    @Test
    public void testGetSaveAndRenameServiceCaller() {

        final Caller<? extends SupportsSaveAndRename<String, Metadata>> serviceCaller = presenter.getSaveAndRenameServiceCaller();

        assertEquals(defaultEditorService, serviceCaller);
    }

    @Test
    public void testGetContentSupplier() {

        final String content = "content";

        doReturn(content).when(view).getContent();

        final Supplier<String> contentSupplier = presenter.getContentSupplier();

        assertEquals(content, contentSupplier.get());
    }
}

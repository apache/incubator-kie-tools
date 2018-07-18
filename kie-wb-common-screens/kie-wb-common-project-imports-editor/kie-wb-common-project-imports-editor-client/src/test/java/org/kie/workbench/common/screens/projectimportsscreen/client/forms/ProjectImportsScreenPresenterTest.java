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

package org.kie.workbench.common.screens.projectimportsscreen.client.forms;

import java.util.Optional;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import java.util.function.Supplier;

import org.guvnor.common.services.project.model.ProjectImports;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.messageconsole.client.console.widget.button.AlertsButtonMenuItemBuilder;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.projectimportsscreen.client.forms.answer.LoadContentAnswer;
import org.kie.workbench.common.services.shared.project.ProjectImportsContent;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.validation.AssetUpdateValidator;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.common.SaveAndRenameCommandBuilder;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.category.Others;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectImportsScreenPresenterTest {

    @Mock
    protected ProjectImportsScreenView view;

    @Mock
    protected ObservablePath path;

    @Mock
    protected PlaceRequest placeRequest;

    @Mock
    protected KieEditorWrapperView wrapperView;

    @Mock
    protected VersionRecordManager recordManager;

    @Mock
    protected FileMenuBuilder menuBuilder;

    @Mock
    protected ProjectImportsService importsService;

    @Mock
    protected ProjectImportsContent importsContent;

    @Mock
    protected OverviewWidgetPresenter overviewWidgetPresenter;

    @Mock
    protected Overview overview;

    @Mock
    protected ProjectController projectController;

    @Mock
    protected SaveAndRenameCommandBuilder<ProjectImports, Metadata> saveAndRenameCommandBuilder;

    @Mock
    private Message message;

    @Mock
    private Throwable cause;

    @Mock
    private WorkspaceProjectContext workbenchContext;

    @Mock
    private HasBusyIndicatorDefaultErrorCallback errorCallback;

    @Mock
    private Caller<ProjectImportsService> serviceCaller;

    @Mock
    private AlertsButtonMenuItemBuilder alertsButtonMenuItemBuilder;

    @Mock
    protected MenuItem alertsButtonMenuItem;

    @InjectMocks
    protected ProjectImportsScreenPresenter presenter = makeProjectImportsScreen();

    @Before
    public void initTest() {
        when(importsContent.getOverview()).thenReturn(overview);
        when(recordManager.getCurrentPath()).thenReturn(path);

        when(menuBuilder.addSave(any(MenuItem.class))).thenReturn(menuBuilder);
        when(menuBuilder.addCopy(any(Path.class),
                                 any(Validator.class))).thenReturn(menuBuilder);
        when(menuBuilder.addRename(any(Command.class))).thenReturn(menuBuilder);
        when(menuBuilder.addDelete(any(Path.class))).thenReturn(menuBuilder);
        when(menuBuilder.addNewTopLevelMenu(any(MenuItem.class))).thenReturn(menuBuilder);

        when(workbenchContext.getActiveOrganizationalUnit()).thenReturn(Optional.empty());
        when(workbenchContext.getActiveWorkspaceProject()).thenReturn(Optional.empty());
        when(workbenchContext.getActiveModule()).thenReturn(Optional.empty());
        when(workbenchContext.getActiveRepositoryRoot()).thenReturn(Optional.empty());
        when(workbenchContext.getActivePackage()).thenReturn(Optional.empty());

        when(alertsButtonMenuItemBuilder.build()).thenReturn(alertsButtonMenuItem);
    }

    @Test
    public void testHideAfterLoadPositive() {
        verifyShowHide(true);
    }

    @Test
    public void testHideAfterLoadNegative() {
        verifyShowHide(false);
    }

    private void verifyShowHide(boolean positive) {
        when(serviceCaller.call(any(RemoteCallback.class),
                                any(ErrorCallback.class)))
                .thenAnswer(new LoadContentAnswer(importsService,
                                                  importsContent,
                                                  positive ? null : errorCallback));

        presenter.init(path,
                       placeRequest);
        verify(view).showLoading();

        if (positive) {
            verify(view).hideBusyIndicator();
        } else {
            verify(errorCallback).error(any(Message.class),
                                        any(Throwable.class));
        }
    }

    @Test
    public void testMakeMenuBar() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(true).when(projectController).canUpdateProject(any());

        presenter.makeMenuBar();

        verify(menuBuilder).addSave(any(MenuItem.class));
        verify(menuBuilder).addCopy(any(Path.class), any(AssetUpdateValidator.class));
        verify(menuBuilder).addRename(any(Command.class));
        verify(menuBuilder).addDelete(any(Path.class), any(AssetUpdateValidator.class));
        verify(menuBuilder).addNewTopLevelMenu(alertsButtonMenuItem);
        verify(presenter).addDownloadMenuItem(menuBuilder);
    }

    @Test
    public void testMakeMenuBarWithoutUpdateProjectPermission() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(false).when(projectController).canUpdateProject(any());

        presenter.makeMenuBar();

        verify(menuBuilder, never()).addSave(any(MenuItem.class));
        verify(menuBuilder, never()).addCopy(any(Path.class), any(AssetUpdateValidator.class));
        verify(menuBuilder, never()).addRename(any(Command.class));
        verify(menuBuilder, never()).addDelete(any(Path.class), any(AssetUpdateValidator.class));
        verify(menuBuilder).addNewTopLevelMenu(alertsButtonMenuItem);
    }

    @Test
    public void testGetContentSupplier() {

        final ProjectImports content = mock(ProjectImports.class);

        presenter.setModel(content);

        final Supplier<ProjectImports> contentSupplier = presenter.getContentSupplier();

        assertEquals(content, contentSupplier.get());
    }

    @Test
    public void testGetSaveAndRenameServiceCaller() {

        final Caller<? extends SupportsSaveAndRename<ProjectImports, Metadata>> serviceCaller = presenter.getSaveAndRenameServiceCaller();

        assertEquals(this.serviceCaller, serviceCaller);
    }

    private ProjectImportsScreenPresenter makeProjectImportsScreen() {

        return spy(new ProjectImportsScreenPresenter(view, serviceCaller, new Others()) {

            @Override
            public void addDownloadMenuItem(final FileMenuBuilder fileMenuBuilder) {
                // Do nothing.
            }

            @Override
            protected Command getSaveAndRename() {
                return mock(Command.class);
            }
        });
    }
}

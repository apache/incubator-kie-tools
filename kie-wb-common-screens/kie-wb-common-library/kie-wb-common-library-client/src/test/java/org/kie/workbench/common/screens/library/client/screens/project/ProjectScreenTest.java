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
 *
 */

package org.kie.workbench.common.screens.library.client.screens.project;

import javax.enterprise.event.Event;

import elemental2.dom.HTMLElement;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.messageconsole.client.console.widget.button.ViewHideAlertsButtonPresenter;
import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.defaulteditor.client.editor.NewFileUploader;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.screens.ProjectScreenTestBase;
import org.kie.workbench.common.screens.library.client.screens.assets.AssetsScreen;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.edit.EditContributorsPopUpPresenter;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.tab.ContributorsListPresenter;
import org.kie.workbench.common.screens.library.client.screens.project.delete.DeleteProjectPopUpScreen;
import org.kie.workbench.common.screens.library.client.screens.project.rename.RenameProjectPopUpScreen;
import org.kie.workbench.common.screens.library.client.settings.SettingsPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.projecteditor.client.build.BuildExecutor;
import org.kie.workbench.common.screens.projecteditor.client.validation.ProjectNameValidator;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.FileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.promise.SyncPromises;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectScreenTest extends ProjectScreenTestBase {

    private ProjectScreen presenter;

    @Mock
    private ProjectScreen.View view;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private AssetsScreen assetsScreen;

    @Mock
    private AssetsScreen.View assetsView;

    @Mock
    private ContributorsListPresenter contributorsListScreen;

    @Mock
    private ProjectMetricsScreen projectMetrictsScreen;

    @Mock
    private ProjectController projectController;

    @Mock
    private OrganizationalUnitController organizationalUnitController;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private NewFileUploader newFileUploader;

    @Mock
    private NewResourcePresenter newResourcePresenter;

    @Mock
    private BuildExecutor buildExecutor;

    @Mock
    private ManagedInstance<EditContributorsPopUpPresenter> editContributorsPopUpPresenterInstance;

    @Mock
    private EditContributorsPopUpPresenter editContributorsPopUpPresenter;

    @Mock
    private ManagedInstance<DeleteProjectPopUpScreen> deleteProjectPopUpScreenInstance;

    @Mock
    private DeleteProjectPopUpScreen deleteProjectPopUpScreen;

    @Mock
    private ManagedInstance<RenameProjectPopUpScreen> renameProjectPopUpScreenInstance;

    @Mock
    private RenameProjectPopUpScreen renameProjectPopUpScreen;

    @Mock
    private LibraryService libraryService;

    @Mock
    private SettingsPresenter settingsPresenter;

    @Mock
    private ProjectScreenService projectScreenService;
    private Caller<ProjectScreenService> projectScreenServiceCaller;

    @Mock
    private CopyPopUpPresenter copyPopUpPresenter;

    @Mock
    private ProjectNameValidator projectNameValidator;

    @Mock
    private Event<NotificationEvent> notificationEvent;

    @Mock
    private ViewHideAlertsButtonPresenter viewHideAlertsButtonPresenter;

    @Mock
    private ViewHideAlertsButtonPresenter.View viewHideAlertsButtonView;

    @Mock
    private WorkspaceProject workspaceProject;

    private SyncPromises promises;

    @Before
    public void setUp() {
        projectScreenServiceCaller = new CallerMock<>(projectScreenService);
        promises = spy(new SyncPromises());

        when(assetsScreen.getView()).thenReturn(assetsView);
        when(viewHideAlertsButtonPresenter.getView()).thenReturn(viewHideAlertsButtonView);

        when(editContributorsPopUpPresenterInstance.get()).thenReturn(editContributorsPopUpPresenter);
        when(deleteProjectPopUpScreenInstance.get()).thenReturn(deleteProjectPopUpScreen);
        when(renameProjectPopUpScreenInstance.get()).thenReturn(renameProjectPopUpScreen);

        final Module module = mock(Module.class);
        when(workspaceProject.getName()).thenReturn("module-name");
        when(workspaceProject.getMainModule()).thenReturn(module);
        when(libraryPlaces.getActiveWorkspaceContext()).thenReturn(workspaceProject);

        this.presenter = spy(new ProjectScreen(this.view,
                                               this.libraryPlaces,
                                               this.assetsScreen,
                                               this.contributorsListScreen,
                                               this.projectMetrictsScreen,
                                               this.projectController,
                                               this.settingsPresenter,
                                               this.organizationalUnitController,
                                               this.newFileUploader,
                                               this.newResourcePresenter,
                                               this.buildExecutor,
                                               this.editContributorsPopUpPresenterInstance,
                                               this.deleteProjectPopUpScreenInstance,
                                               this.renameProjectPopUpScreenInstance,
                                               new CallerMock<>(this.libraryService),
                                               projectScreenServiceCaller,
                                               copyPopUpPresenter,
                                               projectNameValidator,
                                               promises,
                                               notificationEvent,
                                               viewHideAlertsButtonPresenter));

        this.presenter.workspaceProject = createProject();
    }

    @Test
    public void testInitialize() {
        presenter.initialize();

        verify(view).init(presenter);
        verify(buildExecutor).init(view);
        verify(view).setTitle("module-name");
        verify(view).addMainAction(viewHideAlertsButtonView);
    }

    @Test
    public void testAddAsset() {
        {
            doReturn(false).when(this.presenter).userCanUpdateProject();
            this.presenter.addAsset();
            verify(this.libraryPlaces,
                   never()).goToAddAsset();
        }
        {
            doReturn(true).when(this.presenter).userCanUpdateProject();
            this.presenter.addAsset();
            verify(this.libraryPlaces,
                   times(1)).goToAddAsset();
        }
    }

    @Test
    public void testImportAsset() {
        {
            doReturn(false).when(this.presenter).userCanUpdateProject();
            this.presenter.importAsset();
            verify(this.newFileUploader,
                   never()).getCommand(any());
        }
        {
            doReturn(true).when(this.presenter).userCanUpdateProject();
            this.presenter.importAsset();
            verify(this.newFileUploader,
                   times(1)).getCommand(any());
        }
    }

    @Test
    public void testShowSettings() {

        SettingsPresenter.View settingsView = mock(SettingsPresenter.View.class);
        when(settingsView.getElement()).thenReturn(new HTMLElement());
        when(this.settingsPresenter.getView()).thenReturn(settingsView);

        {
            doReturn(false).when(this.presenter).userCanUpdateProject();
            this.presenter.showSettings();
            verify(this.settingsPresenter,
                   never()).onOpen();
        }
        {
            doReturn(true).when(this.presenter).userCanUpdateProject();
            this.presenter.showSettings();
            verify(this.settingsPresenter,
                   times(1)).onOpen();
        }
    }

    @Test
    public void testRename() {
        {
            doReturn(false).when(this.presenter).userCanUpdateProject();
            this.presenter.rename();
            verify(this.renameProjectPopUpScreen,
                   never()).show(any());
        }
        {
            doReturn(true).when(this.presenter).userCanUpdateProject();
            this.presenter.rename();
            verify(this.renameProjectPopUpScreen,
                   times(1)).show(any());
        }
    }

    @Test
    public void testEditContributors() {
        {
            doReturn(false).when(this.presenter).canEditContributors();
            this.presenter.editContributors();
            verify(this.editContributorsPopUpPresenter,
                   never()).show(any());
        }
        {
            doReturn(true).when(this.presenter).canEditContributors();
            this.presenter.editContributors();
            verify(this.editContributorsPopUpPresenter,
                   times(1)).show(any());
        }
    }

    @Test
    public void testDeploy() {
        {
            doReturn(false).when(this.presenter).userCanBuildProject();
            this.presenter.deploy();
            verify(this.buildExecutor,
                   never()).triggerBuildAndDeploy();
        }
        {
            doReturn(true).when(this.presenter).userCanBuildProject();
            this.presenter.deploy();
            verify(this.buildExecutor,
                   times(1)).triggerBuildAndDeploy();
        }
    }

    @Test
    public void testBuild() {
        {
            doReturn(false).when(this.presenter).userCanBuildProject();
            this.presenter.build();
            verify(this.buildExecutor,
                   never()).triggerBuild();
        }
        {
            doReturn(true).when(this.presenter).userCanBuildProject();
            this.presenter.build();
            verify(this.buildExecutor,
                   times(1)).triggerBuild();
        }
    }

    @Test
    public void testDuplicate() {
        {
            doReturn(false).when(this.presenter).userCanCreateProjects();
            this.presenter.duplicate();
            verify(this.copyPopUpPresenter,
                   never()).show(any(),
                                 any(),
                                 any());
        }
        {
            doReturn(true).when(this.presenter).userCanCreateProjects();
            CommandWithFileNameAndCommitMessage duplicateCommand = mock(CommandWithFileNameAndCommitMessage.class);
            doReturn(duplicateCommand).when(presenter).getDuplicateCommand();
            this.presenter.duplicate();
            verify(this.copyPopUpPresenter).show(presenter.workspaceProject.getMainModule().getPomXMLPath(),
                                                 projectNameValidator,
                                                 duplicateCommand);
        }
    }

    @Test
    public void testDuplicateCommand() {
        doNothing().when(projectScreenService).copy(any(),
                                                    any());
        final CopyPopUpView copyPopUpView = mock(CopyPopUpView.class);
        doReturn(copyPopUpView).when(copyPopUpPresenter).getView();

        this.presenter.getDuplicateCommand().execute(new FileNameAndCommitMessage("newFileName",
                                                                                  "commitMessage"));

        verify(copyPopUpView).hide();
        verify(view).showBusyIndicator(anyString());
        verify(projectScreenService).copy(presenter.workspaceProject,
                                          "newFileName");
        verify(view).hideBusyIndicator();
        verify(notificationEvent).fire(any());
        verify(promises).resolve();
    }

    @Test
    public void testReimport() {
        {
            doReturn(false).when(this.presenter).userCanUpdateProject();
            this.presenter.reimport();
            verify(this.copyPopUpPresenter,
                   never()).show(any(),
                                 any(),
                                 any());
        }
        {
            doNothing().when(projectScreenService).reImport(any());
            doReturn(true).when(this.presenter).userCanUpdateProject();
            CommandWithFileNameAndCommitMessage duplicateCommand = mock(CommandWithFileNameAndCommitMessage.class);
            doReturn(duplicateCommand).when(presenter).getDuplicateCommand();
            this.presenter.reimport();
            verify(view).showBusyIndicator(anyString());
            verify(projectScreenService).reImport(presenter.workspaceProject.getMainModule().getPomXMLPath());
            verify(view).hideBusyIndicator();
            verify(notificationEvent).fire(any());
            verify(promises).resolve();
        }
    }

    @Test
    public void canBuild() throws Exception {
        doReturn(true).when(projectController).canBuildProject(any(WorkspaceProject.class));
        assertTrue(presenter.userCanBuildProject());
    }

    @Test
    public void notAllowedToBuild() throws Exception {
        doReturn(false).when(projectController).canBuildProject(any(WorkspaceProject.class));
        assertFalse(presenter.userCanBuildProject());
    }

    @Test
    public void allowedToBuildButNoModule() throws Exception {
        doReturn(null).when(workspaceProject).getMainModule();
        presenter.initialize();
        doReturn(true).when(projectController).canBuildProject(workspaceProject);

        assertFalse(presenter.userCanBuildProject());
    }
}
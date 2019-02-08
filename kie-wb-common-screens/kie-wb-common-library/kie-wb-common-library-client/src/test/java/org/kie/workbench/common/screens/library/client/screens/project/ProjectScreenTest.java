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
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.Branch;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.defaulteditor.client.editor.NewFileUploader;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.screens.ProjectScreenTestBase;
import org.kie.workbench.common.screens.library.client.screens.assets.AssetsScreen;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.tab.ContributorsListPresenter;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.tab.ProjectContributorsListServiceImpl;
import org.kie.workbench.common.screens.library.client.screens.project.actions.ProjectMainActions;
import org.kie.workbench.common.screens.library.client.screens.project.branch.delete.DeleteBranchPopUpScreen;
import org.kie.workbench.common.screens.library.client.screens.project.delete.DeleteProjectPopUpScreen;
import org.kie.workbench.common.screens.library.client.screens.project.rename.RenameProjectPopUpScreen;
import org.kie.workbench.common.screens.library.client.settings.SettingsPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPermissions;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.projecteditor.client.validation.ProjectNameValidator;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.FileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.promise.SyncPromises;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
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
    private LibraryPermissions libraryPermissions;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private NewFileUploader newFileUploader;

    @Mock
    private NewResourcePresenter newResourcePresenter;

    @Mock
    private ManagedInstance<DeleteProjectPopUpScreen> deleteProjectPopUpScreenInstance;

    @Mock
    private DeleteProjectPopUpScreen deleteProjectPopUpScreen;

    @Mock
    private ManagedInstance<DeleteBranchPopUpScreen> deleteBranchPopUpScreenInstance;

    @Mock
    private DeleteBranchPopUpScreen deleteBranchPopUpScreen;

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
    private ProjectContributorsListServiceImpl projectContributorsListService;

    @Mock
    private ProjectMainActions projectMainActions;

    private SyncPromises promises;

    @Before
    public void setUp() {
        projectScreenServiceCaller = new CallerMock<>(projectScreenService);
        promises = spy(new SyncPromises());

        when(assetsScreen.getView()).thenReturn(assetsView);

        when(deleteProjectPopUpScreenInstance.get()).thenReturn(deleteProjectPopUpScreen);
        when(deleteBranchPopUpScreenInstance.get()).thenReturn(deleteBranchPopUpScreen);
        when(renameProjectPopUpScreenInstance.get()).thenReturn(renameProjectPopUpScreen);

        final WorkspaceProjectContext projectContext = mock(WorkspaceProjectContext.class);
        when(libraryPlaces.getWorkbenchContext()).thenReturn(projectContext);

        final ProjectScreen projectScreen = new ProjectScreen(this.view,
                                                              this.libraryPlaces,
                                                              this.assetsScreen,
                                                              this.contributorsListScreen,
                                                              this.projectMetrictsScreen,
                                                              this.libraryPermissions,
                                                              this.settingsPresenter,
                                                              this.newFileUploader,
                                                              this.newResourcePresenter,
                                                              this.deleteProjectPopUpScreenInstance,
                                                              this.deleteBranchPopUpScreenInstance,
                                                              this.renameProjectPopUpScreenInstance,
                                                              new CallerMock<>(this.libraryService),
                                                              projectScreenServiceCaller,
                                                              copyPopUpPresenter,
                                                              projectNameValidator,
                                                              promises,
                                                              notificationEvent,
                                                              projectContributorsListService,
                                                              projectMainActions);
        this.presenter = spy(projectScreen);

        this.presenter.workspaceProject = spy(createProject());
        when(libraryPlaces.getActiveWorkspace()).thenReturn(this.presenter.workspaceProject);
    }

    @Test
    public void testInitialize() {
        presenter.initialize();

        verify(view).init(presenter);
        verify(view).setTitle("mainModuleName");
        verify(projectMainActions).setBuildEnabled(eq(false));
        verify(projectMainActions).setDeployEnabled(eq(false));
        verify(projectMainActions).setRedeployEnabled(eq(false));
    }

    @Test
    public void testActionsVisibilityWithPermissionToUpdateProjectOnly() {
        doReturn(true).when(this.presenter).userCanUpdateProject();

        presenter.initialize();

        verify(view).setAddAssetVisible(true);
        verify(view).setImportAssetVisible(true);
        verify(view).setDuplicateVisible(false);
        verify(view).setReimportVisible(true);
        verify(view).setDeleteProjectVisible(false);
        verify(view).setDeleteBranchVisible(false);
        verify(view).setActionsVisible(true);
        verify(projectMainActions).setBuildEnabled(eq(false));
        verify(projectMainActions).setDeployEnabled(eq(false));
        verify(projectMainActions).setRedeployEnabled(eq(false));
    }

    @Test
    public void testActionsVisibilityWithPermissionToDeleteProjectOnly() {
        doReturn(true).when(this.presenter).userCanDeleteProject();

        presenter.initialize();

        verify(view).setAddAssetVisible(false);
        verify(view).setImportAssetVisible(false);
        verify(view).setDuplicateVisible(false);
        verify(view).setReimportVisible(false);
        verify(view).setDeleteProjectVisible(true);
        verify(view).setDeleteBranchVisible(false);
        verify(view).setActionsVisible(true);
        verify(projectMainActions).setBuildEnabled(eq(false));
        verify(projectMainActions).setDeployEnabled(eq(false));
        verify(projectMainActions).setRedeployEnabled(eq(false));
    }

    @Test
    public void testActionsVisibilityWithPermissionToBuildProjectOnly() {
        doReturn(true).when(this.presenter).userCanBuildProject();

        presenter.initialize();

        verify(view).setAddAssetVisible(false);
        verify(view).setImportAssetVisible(false);
        verify(view).setDuplicateVisible(false);
        verify(view).setReimportVisible(false);
        verify(view).setDeleteProjectVisible(false);
        verify(view).setDeleteBranchVisible(false);
        verify(view).setActionsVisible(true);
        verify(projectMainActions).setBuildEnabled(eq(true));
        verify(projectMainActions).setDeployEnabled(eq(false));
        verify(projectMainActions).setRedeployEnabled(eq(false));
    }

    @Test
    public void testActionsVisibilityWithPermissionToDeployProjectOnly() {
        doReturn(true).when(this.presenter).userCanDeployProject();

        presenter.initialize();

        verify(view).setAddAssetVisible(false);
        verify(view).setImportAssetVisible(false);
        verify(view).setDuplicateVisible(false);
        verify(view).setReimportVisible(false);
        verify(view).setDeleteProjectVisible(false);
        verify(view).setDeleteBranchVisible(false);
        verify(view).setActionsVisible(true);
        verify(projectMainActions).setBuildEnabled(eq(false));
        verify(projectMainActions).setDeployEnabled(eq(true));
        verify(projectMainActions).setRedeployEnabled(eq(false));
    }

    @Test
    public void testActionsVisibilityWithPermissionToCreateProjectsOnly() {
        doReturn(true).when(this.presenter).userCanCreateProjects();

        presenter.initialize();

        verify(view).setAddAssetVisible(false);
        verify(view).setImportAssetVisible(false);
        verify(view).setDuplicateVisible(true);
        verify(view).setReimportVisible(false);
        verify(view).setDeleteProjectVisible(false);
        verify(view).setDeleteBranchVisible(false);
        verify(view).setActionsVisible(true);
        verify(projectMainActions).setBuildEnabled(eq(false));
        verify(projectMainActions).setDeployEnabled(eq(false));
        verify(projectMainActions).setRedeployEnabled(eq(false));
    }

    @Test
    public void testActionsVisibilityWithoutAllPermissions() {
        presenter.initialize();

        verify(view).setAddAssetVisible(false);
        verify(view).setImportAssetVisible(false);
        verify(view).setDuplicateVisible(false);
        verify(view).setReimportVisible(false);
        verify(view).setDeleteProjectVisible(false);
        verify(view).setDeleteBranchVisible(false);
        verify(view).setActionsVisible(false);
        verify(projectMainActions).setBuildEnabled(eq(false));
        verify(projectMainActions).setDeployEnabled(eq(false));
        verify(projectMainActions).setRedeployEnabled(eq(false));
    }

    @Test
    public void testActionsVisibilityWithPermissionToDeleteProjectInCreatedBranch() {
        doReturn(true).when(this.presenter).userCanDeleteProject();
        doReturn(new Branch("other-branch", mock(Path.class))).when(presenter.workspaceProject).getBranch();

        presenter.initialize();

        verify(view).setAddAssetVisible(false);
        verify(view).setImportAssetVisible(false);
        verify(view).setDuplicateVisible(false);
        verify(view).setReimportVisible(false);
        verify(view).setDeleteProjectVisible(true);
        verify(view).setDeleteBranchVisible(true);
        verify(view).setActionsVisible(true);
        verify(projectMainActions).setBuildEnabled(eq(false));
        verify(projectMainActions).setDeployEnabled(eq(false));
        verify(projectMainActions).setRedeployEnabled(eq(false));
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
        doReturn(promises.resolve()).when(settingsPresenter).setupUsingCurrentSection();

        this.presenter.showSettings();

        verify(view).setContent(any());
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
            verify(this.copyPopUpPresenter).show(presenter.workspaceProject.getRootPath(),
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
    public void canBuild() {
        doReturn(true).when(libraryPermissions).userCanBuildProject(any(WorkspaceProject.class));
        assertTrue(presenter.userCanBuildProject());
    }

    @Test
    public void notAllowedToBuild() {
        doReturn(false).when(libraryPermissions).userCanBuildProject(any(WorkspaceProject.class));
        assertFalse(presenter.userCanBuildProject());
    }

    @Test
    public void testContextModuleIsUpdated() {
        final WorkspaceProject workspaceProject = mock(WorkspaceProject.class);
        doReturn("module name").when(workspaceProject).getName();

        final Module module = mock(Module.class);
        when(module.getPom()).thenReturn(new POM(new GAV(GROUP_ID, ARTIFACT, VERSION)));

        when(workspaceProject.getMainModule()).thenReturn(module);

        presenter.changeProjectAndTitleWhenContextChange(new WorkspaceProjectContextChangeEvent(workspaceProject));

        verify(view).setTitle("module name");

        verify(projectMainActions).setBuildEnabled(eq(false));
        verify(projectMainActions).setDeployEnabled(eq(false));
        verify(projectMainActions).setRedeployEnabled(eq(false));
    }

    @Test
    public void testContextModuleIsUpdatedSapshot() {
        final WorkspaceProject workspaceProject = mock(WorkspaceProject.class);
        doReturn("module name").when(workspaceProject).getName();

        final Module module = mock(Module.class);
        when(module.getPom()).thenReturn(new POM(new GAV(GROUP_ID, ARTIFACT, VERSION + "-SNAPSHOT")));

        when(workspaceProject.getMainModule()).thenReturn(module);

        presenter.changeProjectAndTitleWhenContextChange(new WorkspaceProjectContextChangeEvent(workspaceProject));

        verify(view).setTitle("module name");

        verify(projectMainActions).setBuildEnabled(eq(false));
        verify(projectMainActions).setDeployEnabled(eq(false));
        verify(projectMainActions).setRedeployEnabled(eq(true));
    }

    @Test
    public void shouldNotChangeProjectAndTitleWhenContextChange() {

        presenter.changeProjectAndTitleWhenContextChange(new WorkspaceProjectContextChangeEvent() {
            @Override
            public WorkspaceProject getWorkspaceProject() {
                return null;
            }
        });

        verify(view, never()).setTitle(any());
    }
}
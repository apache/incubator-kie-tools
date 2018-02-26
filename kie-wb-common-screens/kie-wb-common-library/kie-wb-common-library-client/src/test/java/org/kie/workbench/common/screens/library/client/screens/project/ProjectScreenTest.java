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

import elemental2.dom.HTMLElement;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.defaulteditor.client.editor.NewFileUploader;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.screens.ProjectScreenTestBase;
import org.kie.workbench.common.screens.library.client.screens.assets.AssetsScreen;
import org.kie.workbench.common.screens.library.client.screens.assets.EmptyAssetsScreen;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.edit.EditContributorsPopUpPresenter;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.tab.ContributorsListPresenter;
import org.kie.workbench.common.screens.library.client.screens.project.delete.DeleteProjectPopUpScreen;
import org.kie.workbench.common.screens.library.client.screens.project.rename.RenameProjectPopUpScreen;
import org.kie.workbench.common.screens.library.client.settings.SettingsPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.projecteditor.client.build.BuildExecutor;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectScreenTest extends ProjectScreenTestBase {

    private ProjectScreen presenter;

    @Mock
    private ProjectScreen.View view;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private EmptyAssetsScreen emptyAssetsScree;

    @Mock
    private AssetsScreen assetsScreen;

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

    @Before
    public void setUp() {

        when(editContributorsPopUpPresenterInstance.get()).thenReturn(editContributorsPopUpPresenter);
        when(deleteProjectPopUpScreenInstance.get()).thenReturn(deleteProjectPopUpScreen);
        when(renameProjectPopUpScreenInstance.get()).thenReturn(renameProjectPopUpScreen);

        this.presenter = spy(new ProjectScreen(this.view,
                                               this.libraryPlaces,
                                               this.emptyAssetsScree,
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
                                               new CallerMock<>(this.libraryService)));

        this.presenter.workspaceProject = createProject();
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
}
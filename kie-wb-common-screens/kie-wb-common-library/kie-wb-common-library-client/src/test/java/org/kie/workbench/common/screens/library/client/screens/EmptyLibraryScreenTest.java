/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.library.client.screens;

import org.guvnor.common.services.project.client.security.ProjectController;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.screens.importrepository.ImportRepositoryPopUpPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPermissions;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.widgets.library.AddProjectButtonPresenter;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EmptyLibraryScreenTest {

    @Mock
    private EmptyLibraryScreen.View view;

    @Mock
    private AddProjectButtonPresenter addProjectButtonPresenter;

    @Mock
    private LibraryPermissions libraryPermissions;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private ManagedInstance<ImportRepositoryPopUpPresenter> importRepositoryPopUpPresenters;

    private EmptyLibraryScreen emptyLibraryScreen;

    @Before
    public void setup() {
        doReturn(mock(AddProjectButtonPresenter.View.class)).when(addProjectButtonPresenter).getView();
        doReturn(true).when(libraryPermissions).userCanCreateProject(any());

        emptyLibraryScreen = new EmptyLibraryScreen(view,
                                                    addProjectButtonPresenter,
                                                    libraryPermissions,
                                                    libraryPlaces);
    }

    @Test
    public void setupTest() {
        emptyLibraryScreen.setup();

        verify(view).init(emptyLibraryScreen);
        verify(addProjectButtonPresenter).getView();
        verify(view).addAction(any());
    }

    @Test
    public void setupWithoutProjectCreationPermissionTest() {
        doReturn(false).when(libraryPermissions).userCanCreateProject(any());

        emptyLibraryScreen.setup();

        verify(view).init(emptyLibraryScreen);
        verify(addProjectButtonPresenter,
               never()).getView();
        verify(view,
               never()).addAction(any());
    }

    @Test
    public void trySamplesWithPermissionTest() {
        emptyLibraryScreen.trySamples();

        verify(libraryPlaces).goToTrySamples();
    }

    @Test
    public void trySamplesWithoutPermissionTest() {
        doReturn(false).when(libraryPermissions).userCanCreateProject(any());

        emptyLibraryScreen.trySamples();

        verify(libraryPlaces,
               never()).goToTrySamples();
    }

    @Test
    public void importProjectWithPermissionTest() {
        emptyLibraryScreen.importProject();

        verify(libraryPlaces).goToImportRepositoryPopUp();
    }

    @Test
    public void importProjectWithoutPermissionTest() {
        doReturn(false).when(libraryPermissions).userCanCreateProject(any());

        emptyLibraryScreen.importProject();

        verify(libraryPlaces,
               never()).goToImportRepositoryPopUp();
    }
}
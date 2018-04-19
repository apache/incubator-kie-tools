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

package org.kie.workbench.common.screens.library.client.screens.importrepository;

import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.model.ExampleProject;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ImportRepositoryPopUpPresenterTest {

    @Mock
    private ImportRepositoryPopUpPresenter.View view;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private LibraryService libraryService;

    private Caller<LibraryService> libraryServiceCaller;

    private ImportRepositoryPopUpPresenter presenter;

    @Before
    public void setup() {
        libraryServiceCaller = new CallerMock<>(libraryService);
        presenter = new ImportRepositoryPopUpPresenter(view,
                                                       libraryPlaces,
                                                       libraryServiceCaller);
    }

    @Test
    public void setupTest() {
        presenter.setup();

        verify(view).init(presenter);
    }

    @Test
    public void showTest() {
        presenter.show();

        verify(view).show();
    }

    @Test
    public void importRepositoryTest() {
        String repoUrl = "repoUrl";
        doReturn(repoUrl).when(view).getRepositoryURL();
        when(libraryService.getProjects(eq(repoUrl), any(), any())).thenReturn(singleton(mock(ExampleProject.class)));

        presenter.importRepository();

        verify(view).hideBusyIndicator();
        verify(view).hide();
        verify(libraryPlaces).goToExternalImportPresenter(any());
    }

    @Test
    public void importInvalidRepositoryTest() {
        doThrow(new RuntimeException()).when(libraryService).getProjects(any(), any(), any());
        doReturn("repoUrl").when(view).getRepositoryURL();

        presenter.importRepository();

        verify(view).hideBusyIndicator();
        verify(view).getNoProjectsToImportMessage();
        verify(view).showError(anyString());
    }

    @Test
    public void importEmptyRepositoryTest() {
        when(libraryService.getProjects(any(), any(), any())).thenReturn(emptySet());
        doReturn("repoUrl").when(view).getRepositoryURL();

        presenter.importRepository();

        verify(view).hideBusyIndicator();
        verify(view).getNoProjectsToImportMessage();
        verify(view).showError(anyString());
    }

    @Test
    public void cancelTest() {
        presenter.cancel();

        verify(view).hide();
    }
}

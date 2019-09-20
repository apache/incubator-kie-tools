/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
 */

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.list;

import org.guvnor.common.services.project.client.security.ProjectController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.promise.SyncPromises;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EmptyChangeRequestListPresenterTest {

    private EmptyChangeRequestListPresenter presenter;

    @Mock
    private EmptyChangeRequestListPresenter.View view;

    @Mock
    private ProjectController projectController;

    @Mock
    private LibraryPlaces libraryPlaces;

    private static final SyncPromises promises = new SyncPromises();

    @Before
    public void setUp() {
        this.presenter = spy(new EmptyChangeRequestListPresenter(view,
                                                                 projectController,
                                                                 libraryPlaces,
                                                                 promises));
    }

    @Test
    public void postConstructWithPermissionsTest() {
        doReturn(promises.resolve(true)).when(this.projectController).canSubmitChangeRequest(any());

        presenter.postConstruct();

        verify(view).init(presenter);
        verify(view).enableSubmitChangeRequestButton(true);
    }

    @Test
    public void postConstructWithoutPermissionsTest() {
        doReturn(promises.resolve(false)).when(this.projectController).canSubmitChangeRequest(any());

        presenter.postConstruct();

        verify(view).init(presenter);
        verify(view).enableSubmitChangeRequestButton(false);
    }

    @Test
    public void goToSubmitChangeRequestWithPermissionsTest() {
        doReturn(promises.resolve(true)).when(this.projectController).canSubmitChangeRequest(any());

        presenter.goToSubmitChangeRequest();

        verify(libraryPlaces).goToSubmitChangeRequestScreen();
    }

    @Test
    public void goToSubmitChangeRequestWithoutPermissionsTest() {
        doReturn(promises.resolve(false)).when(this.projectController).canSubmitChangeRequest(any());

        presenter.goToSubmitChangeRequest();

        verify(libraryPlaces, never()).goToSubmitChangeRequestScreen();
    }
}

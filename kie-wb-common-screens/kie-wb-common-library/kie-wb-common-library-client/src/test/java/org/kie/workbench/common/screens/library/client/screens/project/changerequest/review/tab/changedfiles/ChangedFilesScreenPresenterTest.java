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

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.review.tab.changedfiles;

import java.util.Collections;
import java.util.List;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.changerequest.ChangeRequestService;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequest;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestDiff;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.ChangeRequestUtils;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.diff.DiffItemPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.spaces.Space;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ChangedFilesScreenPresenterTest {

    private ChangedFilesScreenPresenter presenter;

    @Mock
    private ChangedFilesScreenPresenter.View view;

    @Mock
    private ManagedInstance<DiffItemPresenter> diffItemPresenterInstances;

    @Mock
    private ChangeRequestUtils changeRequestUtils;

    @Mock
    private ChangeRequestService changeRequestService;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private WorkspaceProject workspaceProject;

    @Mock
    private ChangeRequest changeRequest;

    @Before
    public void setUp() {
        doReturn(workspaceProject).when(libraryPlaces).getActiveWorkspace();
        doReturn(mock(KieModule.class)).when(workspaceProject).getMainModule();
        doReturn(mock(Repository.class)).when(workspaceProject).getRepository();
        doReturn(mock(Space.class)).when(workspaceProject).getSpace();
        doReturn(mock(DiffItemPresenter.class)).when(diffItemPresenterInstances).get();

        this.presenter = spy(new ChangedFilesScreenPresenter(view,
                                                             diffItemPresenterInstances,
                                                             changeRequestUtils,
                                                             new CallerMock<>(changeRequestService),
                                                             libraryPlaces));
    }

    @Test
    public void postConstructTest() {
        presenter.postConstruct();

        verify(view).init(presenter);
    }

    @Test
    public void resetTest() {
        presenter.reset();

        verify(view).resetAll();
    }

    @Test
    public void setupWhenEmptyDiffListTest() throws NoSuchFieldException {
        new FieldSetter(presenter, ChangedFilesScreenPresenter.class.getDeclaredField("workspaceProject"))
                .set(workspaceProject);

        doReturn(Collections.emptyList()).when(changeRequestService).getDiff(anyString(),
                                                                             anyString(),
                                                                             anyString(),
                                                                             anyString());

        presenter.setup(changeRequest,
                        b -> {
                        },
                        i -> {
                        });

        verify(view).setFilesSummary(anyString());
        verify(view, never()).addDiffItem(any(), any());
    }

    @Test
    public void setupWhenPopulatedDiffListTest() throws NoSuchFieldException {
        new FieldSetter(presenter, ChangedFilesScreenPresenter.class.getDeclaredField("workspaceProject"))
                .set(workspaceProject);

        List<ChangeRequestDiff> diffs = Collections.nCopies(5, mock(ChangeRequestDiff.class));
        doReturn(diffs).when(changeRequestService).getDiff(anyString(),
                                                           anyString(),
                                                           anyLong());

        presenter.setup(changeRequest,
                        b -> {
                        },
                        i -> {
                        });

        verify(view).setFilesSummary(anyString());
        verify(view, times(5)).addDiffItem(any(), any());
    }
}

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

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.submit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.changerequest.ChangeRequestService;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequest;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestDiff;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.ChangeRequestUtils;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.diff.DiffItemPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.promise.SyncPromises;
import org.uberfire.spaces.Space;
import org.uberfire.workbench.events.NotificationEvent;

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
public class SubmitChangeRequestScreenPresenterTest {

    private SubmitChangeRequestScreenPresenter presenter;

    @Mock
    private SubmitChangeRequestScreenPresenter.View view;

    @Mock
    private TranslationService ts;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private ManagedInstance<DiffItemPresenter> diffItemPresenterInstances;

    @Mock
    private ChangeRequestService changeRequestService;

    @Mock
    private ProjectController projectController;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    @Mock
    private ChangeRequestUtils changeRequestUtils;

    @Mock
    private Event<NotificationEvent> notificationEvent;

    @Mock
    private WorkspaceProject workspaceProject;

    private Promises promises;

    @Before
    public void setUp() {
        Repository repository = mock(Repository.class);
        Branch branch = mock(Branch.class);

        promises = new SyncPromises();

        doReturn(promises.resolve(Arrays.asList(new Branch("master", mock(Path.class)),
                                                new Branch("myBranch", mock(Path.class)))))
                .when(projectController).getReadableBranches(any());

        doReturn(mock(KieModule.class)).when(workspaceProject).getMainModule();
        doReturn(workspaceProject).when(libraryPlaces).getActiveWorkspace();
        doReturn(branch).when(workspaceProject).getBranch();
        doReturn(Optional.of(new Branch("defaultBranch", mock(Path.class)))).when(repository).getDefaultBranch();
        doReturn(repository).when(workspaceProject).getRepository();
        doReturn(mock(Space.class)).when(workspaceProject).getSpace();
        doReturn(LibraryConstants.Loading).when(ts).getTranslation(LibraryConstants.Loading);
        doReturn(mock(DiffItemPresenter.class)).when(diffItemPresenterInstances).get();

        this.presenter = spy(new SubmitChangeRequestScreenPresenter(view,
                                                                    ts,
                                                                    libraryPlaces,
                                                                    diffItemPresenterInstances,
                                                                    new CallerMock<>(changeRequestService),
                                                                    projectController,
                                                                    promises,
                                                                    busyIndicatorView,
                                                                    changeRequestUtils,
                                                                    notificationEvent));
    }

    @Test
    public void postConstructTest() {
        presenter.postConstruct();

        verify(view).init(presenter);
        verify(view).setTitle(anyString());
    }

    @Test
    public void refreshOnFocusTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        SubmitChangeRequestScreenPresenter.class.getDeclaredField("workspaceProject"))
                .set(workspaceProject);

        PlaceRequest place = mock(PlaceRequest.class);
        doReturn(LibraryPlaces.SUBMIT_CHANGE_REQUEST).when(place).getIdentifier();

        presenter.refreshOnFocus(new SelectPlaceEvent(place));

        verify(projectController).getReadableBranches(workspaceProject);
        verify(view).resetAll();
        verify(busyIndicatorView).showBusyIndicator(anyString());
        verify(view).clearDiffList();
        verify(changeRequestService).getDiff(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void refreshOnFocusWhenOtherPlaceTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        SubmitChangeRequestScreenPresenter.class.getDeclaredField("workspaceProject"))
                .set(workspaceProject);

        PlaceRequest place = mock(PlaceRequest.class);
        doReturn(LibraryPlaces.PROJECT_SCREEN).when(place).getIdentifier();

        presenter.refreshOnFocus(new SelectPlaceEvent(place));

        verify(projectController, never()).getReadableBranches(workspaceProject);
    }

    @Test
    public void cancelTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        SubmitChangeRequestScreenPresenter.class.getDeclaredField("workspaceProject"))
                .set(workspaceProject);

        presenter.cancel();

        verify(libraryPlaces).goToProject(workspaceProject);
    }

    @Test
    public void submitWhenInvalidFieldsTest() {
        doReturn("").when(view).getSummary();
        doReturn("").when(view).getDescription();

        presenter.submit();

        verify(view).clearErrors();
        verify(view).setSummaryError();
        verify(view).setDescriptionError();
        verify(changeRequestService, never()).createChangeRequest(anyString(),
                                                                  anyString(),
                                                                  anyString(),
                                                                  anyString(),
                                                                  anyString(),
                                                                  anyString());
    }

    @Test
    public void submitWhenInvalidDescriptionTest() {
        doReturn("summary").when(view).getSummary();
        doReturn("").when(view).getDescription();

        presenter.submit();

        verify(view).clearErrors();
        verify(view, never()).setSummaryError();
        verify(view).setDescriptionError();
        verify(changeRequestService, never()).createChangeRequest(anyString(),
                                                                  anyString(),
                                                                  anyString(),
                                                                  anyString(),
                                                                  anyString(),
                                                                  anyString());
    }

    @Test
    public void submitWhenInvalidSummaryTest() {
        doReturn("").when(view).getSummary();
        doReturn("description").when(view).getDescription();

        presenter.submit();

        verify(view).clearErrors();
        verify(view).setSummaryError();
        verify(view, never()).setDescriptionError();
        verify(changeRequestService, never()).createChangeRequest(anyString(),
                                                                  anyString(),
                                                                  anyString(),
                                                                  anyString(),
                                                                  anyString(),
                                                                  anyString());
    }

    @Test
    public void submitWhenCannotUpdateBranchTest() throws NoSuchFieldException {
        final String destinationBranch = "destinationBranch";

        new FieldSetter(presenter,
                        SubmitChangeRequestScreenPresenter.class.getDeclaredField("workspaceProject"))
                .set(workspaceProject);
        new FieldSetter(presenter,
                        SubmitChangeRequestScreenPresenter.class.getDeclaredField("selectedBranch"))
                .set(destinationBranch);

        doReturn("summary").when(view).getSummary();
        doReturn("description").when(view).getDescription();

        doReturn(promises.resolve(false)).when(projectController)
                .canSubmitChangeRequest(workspaceProject,
                                        destinationBranch);
        presenter.submit();

        verify(changeRequestService, never()).createChangeRequest(anyString(),
                                                                  anyString(),
                                                                  anyString(),
                                                                  anyString(),
                                                                  anyString(),
                                                                  anyString());
    }

    @Test
    public void submitSuccessTest() throws NoSuchFieldException {
        final String destinationBranch = "destinationBranch";

        new FieldSetter(presenter,
                        SubmitChangeRequestScreenPresenter.class.getDeclaredField("workspaceProject"))
                .set(workspaceProject);
        new FieldSetter(presenter,
                        SubmitChangeRequestScreenPresenter.class.getDeclaredField("selectedBranch"))
                .set(destinationBranch);

        doReturn("summary").when(view).getSummary();
        doReturn("description").when(view).getDescription();

        doReturn(promises.resolve(true)).when(projectController)
                .canSubmitChangeRequest(workspaceProject,
                                        destinationBranch);

        ChangeRequest cr = mock(ChangeRequest.class);
        doReturn(cr).when(changeRequestService).createChangeRequest(anyString(),
                                                                    anyString(),
                                                                    anyString(),
                                                                    anyString(),
                                                                    anyString(),
                                                                    anyString());
        presenter.submit();

        verify(view).clearErrors();
        verify(view, never()).setSummaryError();
        verify(view, never()).setDescriptionError();
        verify(changeRequestService).createChangeRequest(anyString(),
                                                         anyString(),
                                                         anyString(),
                                                         anyString(),
                                                         anyString(),
                                                         anyString());
        verify(libraryPlaces).goToChangeRequestReviewScreen(anyLong());
    }

    @Test
    public void updateDiffListWhenEmptyTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        SubmitChangeRequestScreenPresenter.class.getDeclaredField("workspaceProject"))
                .set(workspaceProject);

        doReturn(Collections.emptyList()).when(changeRequestService).getDiff(anyString(),
                                                                             anyString(),
                                                                             anyString(),
                                                                             anyString());

        presenter.updateDiffContainer();

        verify(view).clearDiffList();
        verify(view, never()).addDiffItem(any(),
                                          any());
        verify(changeRequestService).getDiff(anyString(),
                                             anyString(),
                                             anyString(),
                                             anyString());
        verify(view).enableSubmitButton(false);
    }

    @Test
    public void updateDiffListWhenPopulatedTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        SubmitChangeRequestScreenPresenter.class.getDeclaredField("workspaceProject"))
                .set(workspaceProject);

        ChangeRequestDiff crDiff = mock(ChangeRequestDiff.class);
        doReturn(10).when(crDiff).getDeletedLinesCount();
        doReturn(10).when(crDiff).getAddedLinesCount();

        List<ChangeRequestDiff> diffList = Collections.nCopies(5, crDiff);

        doReturn(diffList).when(changeRequestService).getDiff(anyString(),
                                                              anyString(),
                                                              anyString(),
                                                              anyString());

        presenter.updateDiffContainer();

        verify(view).clearDiffList();
        verify(view, times(diffList.size())).addDiffItem(any(),
                                                         any());
        verify(changeRequestService).getDiff(anyString(),
                                             anyString(),
                                             anyString(),
                                             anyString());
        verify(view).enableSubmitButton(true);
        verify(view).showDiff(true);
    }
}
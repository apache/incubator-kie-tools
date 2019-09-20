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

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.changerequest.ChangeRequestService;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequest;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestStatus;
import org.guvnor.structure.repositories.changerequest.portable.PaginatedChangeRequestList;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.screens.EmptyState;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.list.listitem.ChangeRequestListItemView;
import org.kie.workbench.common.screens.library.client.util.DateUtils;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;
import org.uberfire.client.promise.Promises;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.common.client.select.SelectOption;
import org.uberfire.mocks.CallerMock;
import org.uberfire.promise.SyncPromises;
import org.uberfire.spaces.Space;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class PopulatedChangeRequestListPresenterTest {

    private PopulatedChangeRequestListPresenter presenter;

    @Mock
    private PopulatedChangeRequestListPresenter.View view;

    @Mock
    private ProjectController projectController;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private EmptyState emptyState;

    @Mock
    private TranslationService ts;

    @Mock
    private ManagedInstance<ChangeRequestListItemView> changeRequestListItemViewInstances;

    @Mock
    private ChangeRequestService changeRequestService;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    @Mock
    private DateUtils dateUtils;

    @Mock
    private WorkspaceProject workspaceProject;

    private Promises promises;

    @Before
    public void setUp() throws NoSuchFieldException {
        promises = new SyncPromises();

        doReturn(mock(KieModule.class)).when(workspaceProject).getMainModule();
        doReturn(workspaceProject).when(libraryPlaces).getActiveWorkspace();
        doReturn(mock(Repository.class)).when(workspaceProject).getRepository();
        doReturn(mock(Space.class)).when(workspaceProject).getSpace();

        PaginatedChangeRequestList paginatedList = new PaginatedChangeRequestList(Collections.emptyList(),
                                                                                  0,
                                                                                  0,
                                                                                  0);

        doReturn(paginatedList).when(changeRequestService)
                .getChangeRequests(anyString(),
                                   anyString(),
                                   anyInt(),
                                   anyInt(),
                                   anyListOf(ChangeRequestStatus.class),
                                   anyString());

        doReturn(paginatedList).when(changeRequestService)
                .getChangeRequests(anyString(),
                                   anyString(),
                                   anyInt(),
                                   anyInt(),
                                   anyString());

        this.presenter = spy(new PopulatedChangeRequestListPresenter(view,
                                                                     projectController,
                                                                     libraryPlaces,
                                                                     promises,
                                                                     emptyState,
                                                                     ts,
                                                                     changeRequestListItemViewInstances,
                                                                     new CallerMock<>(changeRequestService),
                                                                     busyIndicatorView,
                                                                     dateUtils));

        new FieldSetter(presenter,
                        PopulatedChangeRequestListPresenter.class.getDeclaredField("workspaceProject"))
                .set(workspaceProject);
    }

    @Test
    public void postConstructTest() {
        doReturn(promises.resolve(true))
                .when(projectController).canSubmitChangeRequest(workspaceProject);

        presenter.postConstruct();

        verify(view).init(presenter);
        verify(view).enableSubmitChangeRequestButton(true);
        verify(view).setFilterTypes(anyListOf(SelectOption.class));
        verify(view).clearList();
    }

    @Test
    public void postConstructUserCannotSubmitChangeRequestTest() {
        doReturn(promises.resolve(false))
                .when(projectController).canSubmitChangeRequest(workspaceProject);
        doReturn(mock(PaginatedChangeRequestList.class)).when(changeRequestService)
                .getChangeRequests(anyString(),
                                   anyString(),
                                   anyInt(),
                                   anyInt(),
                                   anyListOf(ChangeRequestStatus.class),
                                   anyString());

        presenter.postConstruct();

        verify(view).enableSubmitChangeRequestButton(false);
    }

    @Test
    public void refreshListTest() {
        doReturn(LibraryConstants.ChangeRequestFilesSummaryManyFiles).when(ts).format(anyString(), anyInt());

        doReturn(promises.resolve(true))
                .when(projectController).canSubmitChangeRequest(workspaceProject);

        doReturn(mock(ChangeRequestListItemView.class)).when(changeRequestListItemViewInstances).get();

        ChangeRequest cr = mock(ChangeRequest.class);
        doReturn(ChangeRequestStatus.OPEN).when(cr).getStatus();
        doReturn(0).when(cr).getCommentsCount();
        doReturn(new Date()).when(cr).getCreatedDate();
        List<ChangeRequest> crList = Collections.nCopies(10, cr);
        PaginatedChangeRequestList paginatedList = new PaginatedChangeRequestList(crList,
                                                                                  0,
                                                                                  10,
                                                                                  10);

        doReturn(paginatedList).when(changeRequestService).getChangeRequests(anyString(),
                                                                             anyString(),
                                                                             anyInt(),
                                                                             anyInt(),
                                                                             anyListOf(ChangeRequestStatus.class),
                                                                             anyString());

        presenter.postConstruct();

        verify(changeRequestListItemViewInstances, times(10)).get();
        verify(view, times(10)).addChangeRequestItem(any());
    }

    @Test
    public void nextPageTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        PopulatedChangeRequestListPresenter.class.getDeclaredField("currentPage"))
                .set(1);
        new FieldSetter(presenter,
                        PopulatedChangeRequestListPresenter.class.getDeclaredField("totalPages"))
                .set(10);
        new FieldSetter(presenter,
                        PopulatedChangeRequestListPresenter.class.getDeclaredField("filterType"))
                .set("ALL");

        presenter.nextPage();

        verify(view).setCurrentPage(2);
    }

    @Test
    public void nextPageDoNothingTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        PopulatedChangeRequestListPresenter.class.getDeclaredField("currentPage"))
                .set(10);
        new FieldSetter(presenter,
                        PopulatedChangeRequestListPresenter.class.getDeclaredField("totalPages"))
                .set(10);
        new FieldSetter(presenter,
                        PopulatedChangeRequestListPresenter.class.getDeclaredField("filterType"))
                .set("ALL");

        presenter.nextPage();

        verify(view, never()).setCurrentPage(anyInt());
    }

    @Test
    public void prevPageTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        PopulatedChangeRequestListPresenter.class.getDeclaredField("currentPage"))
                .set(5);
        new FieldSetter(presenter,
                        PopulatedChangeRequestListPresenter.class.getDeclaredField("totalPages"))
                .set(10);
        new FieldSetter(presenter,
                        PopulatedChangeRequestListPresenter.class.getDeclaredField("filterType"))
                .set("ALL");

        presenter.prevPage();

        verify(view).setCurrentPage(4);
    }

    @Test
    public void prevPageDoNothingTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        PopulatedChangeRequestListPresenter.class.getDeclaredField("currentPage"))
                .set(1);
        new FieldSetter(presenter,
                        PopulatedChangeRequestListPresenter.class.getDeclaredField("totalPages"))
                .set(10);
        new FieldSetter(presenter,
                        PopulatedChangeRequestListPresenter.class.getDeclaredField("filterType"))
                .set("ALL");

        presenter.prevPage();

        verify(view, never()).setCurrentPage(anyInt());
    }

    @Test
    public void setCurrentPageTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        PopulatedChangeRequestListPresenter.class.getDeclaredField("totalPages"))
                .set(10);
        new FieldSetter(presenter,
                        PopulatedChangeRequestListPresenter.class.getDeclaredField("filterType"))
                .set("ALL");

        presenter.setCurrentPage(5);

        verify(view).enablePreviousButton(anyBoolean());
        verify(view).enableNextButton(anyBoolean());
    }

    @Test
    public void setCurrentPageOutRangeTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        PopulatedChangeRequestListPresenter.class.getDeclaredField("currentPage"))
                .set(10);
        new FieldSetter(presenter,
                        PopulatedChangeRequestListPresenter.class.getDeclaredField("totalPages"))
                .set(10);

        presenter.setCurrentPage(50);

        verify(view).setCurrentPage(10);
        verify(view, never()).enablePreviousButton(anyBoolean());
        verify(view, never()).enableNextButton(anyBoolean());
    }

    @Test
    public void setFilterTypeAllTest() {
        presenter.setFilterType("ALL");

        verify(view).clearSearch();
        verify(changeRequestService).getChangeRequests(anyString(),
                                                       anyString(),
                                                       anyInt(),
                                                       anyInt(),
                                                       anyString());
    }

    @Test
    public void submitChangeRequestTest() {
        presenter.setFilterType("OPEN");

        verify(view).clearSearch();
        verify(changeRequestService).getChangeRequests(anyString(),
                                                       anyString(),
                                                       anyInt(),
                                                       anyInt(),
                                                       anyListOf(ChangeRequestStatus.class),
                                                       anyString());
    }

    @Test
    public void searchTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        PopulatedChangeRequestListPresenter.class.getDeclaredField("filterType"))
                .set("ALL");

        presenter.search("value");

        verify(changeRequestService).getChangeRequests(anyString(),
                                                       anyString(),
                                                       anyInt(),
                                                       anyInt(),
                                                       eq("value"));
    }

    @Test
    public void showSearchHitNothingTest() {
        presenter.showSearchHitNothing();

        verify(emptyState).clear();
        verify(emptyState).setMessage(anyString(), anyString());
        verify(view).showEmptyState(emptyState);
    }
}

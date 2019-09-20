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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.changerequest.ChangeRequestService;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestListUpdatedEvent;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestStatus;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestStatusUpdatedEvent;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestUpdatedEvent;
import org.guvnor.structure.repositories.changerequest.portable.PaginatedChangeRequestList;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.api.RepositoryFileListUpdatedEvent;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.resources.images.LibraryImages;
import org.kie.workbench.common.screens.library.client.screens.EmptyState;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.list.listitem.ChangeRequestListItemView;
import org.kie.workbench.common.screens.library.client.util.DateUtils;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.client.promise.Promises;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.common.client.select.SelectOption;
import org.uberfire.ext.widgets.common.client.select.SelectOptionImpl;
import org.uberfire.mvp.Command;

@Dependent
public class PopulatedChangeRequestListPresenter {

    private static final int PAGE_SIZE = 10;
    private static final String FILTER_OPEN = "OPEN";
    private static final String FILTER_CLOSED = "CLOSED";
    private static final String FILTER_ALL = "ALL";
    private final View view;
    private final ProjectController projectController;
    private final LibraryPlaces libraryPlaces;
    private final Promises promises;
    private final EmptyState emptyState;
    private final TranslationService ts;
    private final ManagedInstance<ChangeRequestListItemView> changeRequestListItemViewInstances;
    private final Caller<ChangeRequestService> changeRequestService;
    private final BusyIndicatorView busyIndicatorView;
    private final DateUtils dateUtils;
    private WorkspaceProject workspaceProject;
    private int currentPage;
    private String searchFilter;
    private int totalPages;
    private String filterType;

    @Inject
    public PopulatedChangeRequestListPresenter(final View view,
                                               final ProjectController projectController,
                                               final LibraryPlaces libraryPlaces,
                                               final Promises promises,
                                               final EmptyState emptyState,
                                               final TranslationService ts,
                                               final ManagedInstance<ChangeRequestListItemView> changeRequestItemViews,
                                               final Caller<ChangeRequestService> changeRequestService,
                                               final BusyIndicatorView busyIndicatorView,
                                               final DateUtils dateUtils) {
        this.view = view;
        this.projectController = projectController;
        this.libraryPlaces = libraryPlaces;
        this.promises = promises;
        this.emptyState = emptyState;
        this.ts = ts;
        this.changeRequestListItemViewInstances = changeRequestItemViews;
        this.changeRequestService = changeRequestService;
        this.busyIndicatorView = busyIndicatorView;
        this.dateUtils = dateUtils;
    }

    @PostConstruct
    public void postConstruct() {
        this.workspaceProject = this.libraryPlaces.getActiveWorkspace();

        this.view.init(this);
        this.setupFilter();

        projectController.canSubmitChangeRequest(workspaceProject).then(userCanSubmitChangeRequest -> {
            view.enableSubmitChangeRequestButton(userCanSubmitChangeRequest);
            return promises.resolve();
        });

        this.refreshList();
    }

    public View getView() {
        return view;
    }

    public void onChangeRequestListUpdated(@Observes final ChangeRequestListUpdatedEvent event) {
        if (event.getRepositoryId().equals(workspaceProject.getRepository().getIdentifier())) {
            this.refreshList();
        }
    }

    public void onChangeRequestUpdated(@Observes final ChangeRequestUpdatedEvent event) {
        if (event.getRepositoryId().equals(workspaceProject.getRepository().getIdentifier())) {
            this.refreshList();
        }
    }

    public void onChangeRequestStatusUpdated(@Observes final ChangeRequestStatusUpdatedEvent event) {
        if (event.getRepositoryId().equals(workspaceProject.getRepository().getIdentifier()) &&
                (event.getOldStatus() == ChangeRequestStatus.OPEN ||
                        event.getNewStatus() == ChangeRequestStatus.OPEN)) {
            this.refreshList();
        }
    }

    public void onRepositoryFileListUpdated(@Observes final RepositoryFileListUpdatedEvent event) {
        if (event.getRepositoryId().equals(this.workspaceProject.getRepository().getIdentifier())) {
            this.refreshList();
        }
    }

    public void nextPage() {
        if (this.currentPage + 1 <= this.totalPages) {
            this.currentPage++;
            this.refreshList();
        }
    }

    public void prevPage() {
        if (this.currentPage - 1 >= 1) {
            this.currentPage--;
            this.refreshList();
        }
    }

    public void setCurrentPage(final int currentPage) {
        if (currentPage <= totalPages && currentPage > 0) {
            this.currentPage = currentPage;
            this.refreshList();
        } else {
            this.view.setCurrentPage(this.currentPage);
        }
    }

    public void setFilterType(final String filterType) {
        this.filterType = filterType;
        this.searchFilter = "";
        this.view.clearSearch();
        this.currentPage = 1;
        this.refreshList();
    }

    public void submitChangeRequest() {
        projectController.canSubmitChangeRequest(workspaceProject).then(userCanSubmitChangeRequest -> {
            if (Boolean.TRUE.equals(userCanSubmitChangeRequest)) {
                this.libraryPlaces.goToSubmitChangeRequestScreen();
            }

            return promises.resolve();
        });
    }

    public void search(final String searchText) {
        this.searchFilter = searchText;
        this.currentPage = 1;
        this.refreshList();
    }

    public void showSearchHitNothing() {
        this.showEmptyState(ts.getTranslation(LibraryConstants.EmptySearch),
                            ts.getTranslation(LibraryConstants.NoChangeRequestsFound));
    }

    private String formatCreatedTime(final Date createdDate) {
        return ts.format(LibraryConstants.Submitted) + " " + dateUtils.format(createdDate);
    }

    private String formatChangedFiles(final int changedFilesCount) {
        if (changedFilesCount == 1) {
            return ts.getTranslation(LibraryConstants.ChangeRequestFilesSummaryOneFile);
        } else {
            return ts.format(LibraryConstants.ChangeRequestFilesSummaryManyFiles, changedFilesCount);
        }
    }

    private void setupFilter() {
        final List<SelectOption> filterTypes = createFilterTypes();
        this.view.setFilterTypes(filterTypes);
        this.filterType = filterTypes.get(0).getSelector();

        this.searchFilter = "";
        this.currentPage = 1;
        this.view.setFilterTextPlaceHolder(ts.getTranslation(LibraryConstants.SearchByIdOrSummary));
    }

    private void showEmptyState(final String title,
                                final String message) {
        this.emptyState.clear();
        this.emptyState.setMessage(title, message);
        this.view.showEmptyState(emptyState);
    }

    private void hideEmptyState() {
        this.emptyState.clear();
        this.view.hideEmptyState(emptyState);
    }

    private Command selectCommand(final long changeRequestId) {
        return () -> libraryPlaces.goToChangeRequestReviewScreen(changeRequestId);
    }

    private void refreshList() {
        busyIndicatorView.showBusyIndicator(ts.getTranslation(LibraryConstants.Loading));

        if (filterType.equals(FILTER_ALL)) {
            changeRequestService.call(getChangeRequestsCallback(),
                                      new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView))
                    .getChangeRequests(workspaceProject.getSpace().getName(),
                                       workspaceProject.getRepository().getAlias(),
                                       Math.max(0, currentPage - 1),
                                       PAGE_SIZE,
                                       searchFilter);
        } else {
            changeRequestService.call(getChangeRequestsCallback(),
                                      new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView))
                    .getChangeRequests(workspaceProject.getSpace().getName(),
                                       workspaceProject.getRepository().getAlias(),
                                       Math.max(0, currentPage - 1),
                                       PAGE_SIZE,
                                       getStatusByFilterType(),
                                       searchFilter);
        }
    }

    private RemoteCallback<PaginatedChangeRequestList> getChangeRequestsCallback() {
        return (final PaginatedChangeRequestList paginatedList) -> {
            setupCounters(paginatedList.getTotal());

            this.view.clearList();

            if (paginatedList.getTotal() == 0) {
                showSearchHitNothing();
            } else {
                hideEmptyState();

                paginatedList.getChangeRequests().forEach(item -> {
                    ChangeRequestListItemView viewItem = changeRequestListItemViewInstances.get();

                    viewItem.init(resolveChangeRequestStatusIcon(item.getStatus()),
                                  item.toString(),
                                  item.getAuthorId(),
                                  formatCreatedTime(item.getCreatedDate()),
                                  formatChangedFiles(item.getChangedFilesCount()),
                                  String.valueOf(item.getCommentsCount()),
                                  selectCommand(item.getId()));

                    this.view.addChangeRequestItem(viewItem);
                });
            }

            busyIndicatorView.hideBusyIndicator();
        };
    }

    private IsWidget resolveChangeRequestStatusIcon(final ChangeRequestStatus status) {
        switch (status) {
            case OPEN:
                return new Image(LibraryImages.INSTANCE.changeRequestOpenStatus().getSafeUri());
            case ACCEPTED:
            case REVERTED:
            case REVERT_FAILED:
                return new Image(LibraryImages.INSTANCE.changeRequestMergedStatus().getSafeUri());
            case REJECTED:
            case CLOSED:
            default:
                return new Image(LibraryImages.INSTANCE.changeRequestClosedStatus().getSafeUri());
        }
    }

    private void setupCounters(final int totalChangeRequests) {
        final int offset = (this.currentPage - 1) * PAGE_SIZE;
        final int fromCount = totalChangeRequests > 0 ? offset + 1 : offset;
        final int toCount = this.resolveCounter(totalChangeRequests,
                                                offset + PAGE_SIZE);
        final int totalCount = this.resolveCounter(totalChangeRequests,
                                                   0);

        final String indicatorText = ts.format(LibraryConstants.ItemCountIndicatorText,
                                               fromCount,
                                               toCount,
                                               totalCount);

        this.view.setPageIndicator(indicatorText);

        this.totalPages = (int) Math.ceil(totalChangeRequests / (float) PAGE_SIZE);

        final String totalText = ts.format(LibraryConstants.OfN,
                                           Math.max(this.totalPages, 1));
        this.view.setTotalPages(totalText);

        this.view.setCurrentPage(this.currentPage);

        this.checkPaginationButtons();
    }

    private List<ChangeRequestStatus> getStatusByFilterType() {
        List<ChangeRequestStatus> statusList = new ArrayList<>();

        if (this.filterType.equals(FILTER_CLOSED)) {
            statusList.add(ChangeRequestStatus.ACCEPTED);
            statusList.add(ChangeRequestStatus.REJECTED);
            statusList.add(ChangeRequestStatus.REVERT_FAILED);
            statusList.add(ChangeRequestStatus.REVERTED);
            statusList.add(ChangeRequestStatus.CLOSED);
        } else if (this.filterType.equals(FILTER_OPEN)) {
            statusList.add(ChangeRequestStatus.OPEN);
        } else {
            statusList.addAll(Arrays.asList(ChangeRequestStatus.values()));
        }

        return statusList;
    }

    private void checkPaginationButtons() {
        boolean isPreviousButtonEnabled = this.currentPage > 1;
        boolean isNextButtonEnabled = this.currentPage < this.totalPages;

        this.view.enablePreviousButton(isPreviousButtonEnabled);
        this.view.enableNextButton(isNextButtonEnabled);
    }

    private List<SelectOption> createFilterTypes() {
        return Arrays.asList(new SelectOptionImpl(FILTER_OPEN, ts.getTranslation(LibraryConstants.Open)),
                             new SelectOptionImpl(FILTER_CLOSED, ts.getTranslation(LibraryConstants.Closed)),
                             new SelectOptionImpl(FILTER_ALL, ts.getTranslation(LibraryConstants.ALL)));
    }

    private int resolveCounter(final int numberOfChangeRequests,
                               final int otherCounter) {
        if (numberOfChangeRequests < otherCounter || otherCounter == 0) {
            return numberOfChangeRequests;
        } else {
            return otherCounter;
        }
    }

    public interface View extends UberElemental<PopulatedChangeRequestListPresenter> {

        void setCurrentPage(final int currentPage);

        void setPageIndicator(final String pageIndicatorText);

        void setTotalPages(final String totalText);

        void clearList();

        void enablePreviousButton(final boolean isEnabled);

        void enableNextButton(final boolean isEnabled);

        void setFilterTypes(final List<SelectOption> categories);

        void clearSearch();

        void enableSubmitChangeRequestButton(final boolean isEnabled);

        void showEmptyState(final EmptyState emptyState);

        void hideEmptyState(final EmptyState emptyState);

        void addChangeRequestItem(final ChangeRequestListItemView item);

        void setFilterTextPlaceHolder(final String placeHolder);
    }
}

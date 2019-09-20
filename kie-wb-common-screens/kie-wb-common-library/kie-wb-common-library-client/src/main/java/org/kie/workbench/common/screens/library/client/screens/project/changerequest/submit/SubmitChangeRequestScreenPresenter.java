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

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.changerequest.ChangeRequestService;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequest;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestAlreadyOpenException;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestDiff;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.api.RepositoryFileListUpdatedEvent;
import org.kie.workbench.common.screens.library.client.perspective.LibraryPerspective;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.ChangeRequestUtils;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.diff.DiffItemPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.services.datamodel.util.SortHelper;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

@WorkbenchScreen(identifier = LibraryPlaces.SUBMIT_CHANGE_REQUEST,
        owningPerspective = LibraryPerspective.class)
public class SubmitChangeRequestScreenPresenter {

    private final View view;
    private final TranslationService ts;
    private final LibraryPlaces libraryPlaces;
    private final ManagedInstance<DiffItemPresenter> diffItemPresenterInstances;
    private final Caller<ChangeRequestService> changeRequestService;
    private final ProjectController projectController;
    private final Promises promises;
    private final BusyIndicatorView busyIndicatorView;
    private final ChangeRequestUtils changeRequestUtils;
    private final Event<NotificationEvent> notificationEvent;
    private WorkspaceProject workspaceProject;
    private String currentBranchName;
    private String selectedBranch;

    @Inject
    public SubmitChangeRequestScreenPresenter(final View view,
                                              final TranslationService ts,
                                              final LibraryPlaces libraryPlaces,
                                              final ManagedInstance<DiffItemPresenter> diffItemPresenterInstances,
                                              final Caller<ChangeRequestService> changeRequestService,
                                              final ProjectController projectController,
                                              final Promises promises,
                                              final BusyIndicatorView busyIndicatorView,
                                              final ChangeRequestUtils changeRequestUtils,
                                              final Event<NotificationEvent> notificationEvent) {
        this.view = view;
        this.ts = ts;
        this.libraryPlaces = libraryPlaces;
        this.diffItemPresenterInstances = diffItemPresenterInstances;
        this.changeRequestService = changeRequestService;
        this.projectController = projectController;
        this.promises = promises;
        this.busyIndicatorView = busyIndicatorView;
        this.changeRequestUtils = changeRequestUtils;
        this.notificationEvent = notificationEvent;
    }

    @PostConstruct
    public void postConstruct() {
        this.workspaceProject = libraryPlaces.getActiveWorkspace();

        this.view.init(this);
        this.view.setTitle(this.getTitle());
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ts.getTranslation(LibraryConstants.SubmitChangeRequest);
    }

    @WorkbenchPartView
    public View getView() {
        return view;
    }

    @OnClose
    public void onClose() {
        destroyDiffItems();
    }

    public void refreshOnFocus(@Observes final SelectPlaceEvent selectPlaceEvent) {
        if (workspaceProject != null && workspaceProject.getMainModule() != null) {
            final PlaceRequest place = selectPlaceEvent.getPlace();
            if (place.getIdentifier().equals(LibraryPlaces.SUBMIT_CHANGE_REQUEST)) {
                this.init();
            }
        }
    }

    public void onRepositoryFileListUpdated(@Observes final RepositoryFileListUpdatedEvent event) {
        if (event.getRepositoryId().equals(this.workspaceProject.getRepository().getIdentifier())) {
            final String updatedBranchName = event.getBranchName();

            if ((currentBranchName.equals(updatedBranchName) || selectedBranch.equals(updatedBranchName))) {
                this.updateDiffContainer();
            }
        }
    }

    public void cancel() {
        destroyDiffItems();

        this.libraryPlaces.goToProject(workspaceProject);
    }

    public void submit() {
        if (!validateFields()) {
            return;
        }

        busyIndicatorView.showBusyIndicator(ts.getTranslation(LibraryConstants.Loading));

        projectController.canSubmitChangeRequest(workspaceProject,
                                                 selectedBranch).then(userCanSubmitChangeRequest -> {
            if (Boolean.TRUE.equals(userCanSubmitChangeRequest)) {
                changeRequestService.call((final ChangeRequest item) -> {
                    busyIndicatorView.hideBusyIndicator();

                    notificationEvent.fire(
                            new NotificationEvent(ts.format(LibraryConstants.ChangeRequestSubmitMessage,
                                                            item.getId()),
                                                  NotificationEvent.NotificationType.SUCCESS));

                    destroyDiffItems();

                    this.libraryPlaces.goToChangeRequestReviewScreen(item.getId());
                }, createChangeRequestErrorCallback())
                        .createChangeRequest(workspaceProject.getSpace().getName(),
                                             workspaceProject.getRepository().getAlias(),
                                             currentBranchName,
                                             selectedBranch,
                                             view.getSummary(),
                                             view.getDescription());
            }
            return promises.resolve();
        });
    }

    public void selectBranch(final String branchName) {
        this.selectedBranch = branchName;
        this.updateDiffContainer();
    }

    public void updateDiffContainer() {
        busyIndicatorView.showBusyIndicator(ts.getTranslation(LibraryConstants.Loading));

        changeRequestService.call((final List<ChangeRequestDiff> diffList) -> {
            boolean hideDiff = diffList.isEmpty();

            if (hideDiff) {
                setupEmptyDiffList();
            } else {
                setupPopulatedDiffList(diffList);
            }

            view.showDiff(!hideDiff);
            busyIndicatorView.hideBusyIndicator();
        }, new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView))
                .getDiff(workspaceProject.getSpace().getName(),
                         workspaceProject.getRepository().getAlias(),
                         currentBranchName,
                         selectedBranch);
    }

    private ErrorCallback<Object> createChangeRequestErrorCallback() {
        return (message, throwable) -> {
            busyIndicatorView.hideBusyIndicator();

            if (throwable instanceof ChangeRequestAlreadyOpenException) {
                final Long changeRequestId = ((ChangeRequestAlreadyOpenException) throwable).getChangeRequestId();
                notificationEvent.fire(
                        new NotificationEvent(ts.format(LibraryConstants.ChangeRequestAlreadyOpenMessage,
                                                        changeRequestId,
                                                        currentBranchName,
                                                        selectedBranch),
                                              NotificationEvent.NotificationType.WARNING));
                return false;
            }

            return true;
        };
    }

    private void destroyDiffItems() {
        diffItemPresenterInstances.destroyAll();
    }

    private void init() {
        this.currentBranchName = this.workspaceProject.getBranch().getName();

        final Branch defaultBranch = this.workspaceProject.getRepository().getDefaultBranch()
                .orElseThrow(() -> new IllegalStateException("The default branch does not exist"));

        this.selectedBranch = defaultBranch.getName();
        
        this.reset();
        this.setup();
    }

    private void reset() {
        view.resetAll();
    }

    private void setup() {
        this.updateDestinationBranchList();
        this.updateDiffContainer();
    }

    private void setupEmptyDiffList() {
        destroyDiffItems();

        view.showWarning(false);
        view.clearDiffList();
        view.enableSubmitButton(false);
        view.setFilesSummary(ts.getTranslation(LibraryConstants.BranchesAreEven));
    }

    private boolean validateFields() {
        boolean isValid = true;

        Predicate<String> isInvalidContent = content -> content == null || content.trim().isEmpty();

        view.clearErrors();

        if (isInvalidContent.test(view.getSummary())) {
            view.setSummaryError();
            isValid = false;
        }

        if (isInvalidContent.test(view.getDescription())) {
            view.setDescriptionError();
            isValid = false;
        }

        return isValid;
    }

    private void setupPopulatedDiffList(final List<ChangeRequestDiff> diffList) {
        final int changedFilesCount = diffList.size();
        final int addedLinesCount = diffList.stream().mapToInt(ChangeRequestDiff::getAddedLinesCount).sum();
        final int deletedLinesCount = diffList.stream().mapToInt(ChangeRequestDiff::getDeletedLinesCount).sum();

        view.showWarning(diffList.stream().anyMatch(ChangeRequestDiff::isConflict));
        view.enableSubmitButton(true);
        view.setFilesSummary(changeRequestUtils.formatFilesSummary(changedFilesCount,
                                                                   addedLinesCount,
                                                                   deletedLinesCount));

        destroyDiffItems();
        view.clearDiffList();

        diffList.forEach(diff -> {
            DiffItemPresenter item = diffItemPresenterInstances.get();
            item.setup(diff, true);
            this.view.addDiffItem(item.getView(), item::draw);
        });
    }

    private void updateDestinationBranchList() {
        projectController.getReadableBranches(libraryPlaces.getActiveWorkspace()).then(branches -> {
            final List<String> destinationBranchNames = branches.stream()
                    .map(Branch::getName)
                    .filter(branchName -> !branchName.equals(currentBranchName))
                    .sorted(SortHelper.ALPHABETICAL_ORDER_COMPARATOR)
                    .collect(Collectors.toList());

            final int selectedBranchIdx = IntStream.range(0, destinationBranchNames.size())
                    .filter(i -> selectedBranch.equals(destinationBranchNames.get(i)))
                    .findFirst()
                    .orElse(0);

            view.setDestinationBranches(destinationBranchNames, selectedBranchIdx);

            return promises.resolve();
        });
    }

    public interface View extends UberElemental<SubmitChangeRequestScreenPresenter> {

        void setTitle(final String title);

        void setDestinationBranches(final List<String> branches, final int selectedIdx);

        void showWarning(final boolean isVisible);

        void addDiffItem(final DiffItemPresenter.View item, final Runnable draw);

        String getSummary();

        String getDescription();

        void setDescription(final String description);

        void clearErrors();

        void clearDiffList();

        void setFilesSummary(final String text);

        void enableSubmitButton(final boolean isEnabled);

        void setSummaryError();

        void setDescriptionError();

        void showDiff(final boolean isVisible);

        void clearInputFields();

        void resetAll();
    }
}
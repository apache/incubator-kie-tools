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

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.review;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.RepositoryUpdatedEvent;
import org.guvnor.structure.repositories.changerequest.ChangeRequestService;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequest;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestAlreadyOpenException;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestCommit;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestStatus;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestStatusUpdatedEvent;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestUpdatedEvent;
import org.guvnor.structure.repositories.changerequest.portable.NothingToMergeException;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.api.RepositoryFileListUpdatedEvent;
import org.kie.workbench.common.screens.library.client.perspective.LibraryPerspective;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.ChangeRequestUtils;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.review.tab.changedfiles.ChangedFilesScreenPresenter;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.review.tab.overview.OverviewScreenPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.NotificationEvent;

@WorkbenchScreen(identifier = LibraryPlaces.CHANGE_REQUEST_REVIEW,
        owningPerspective = LibraryPerspective.class)
public class ChangeRequestReviewScreenPresenter {

    private final View view;
    private final TranslationService ts;
    private final LibraryPlaces libraryPlaces;
    private final Caller<ChangeRequestService> changeRequestService;
    private final Caller<RepositoryService> repositoryService;
    private final BusyIndicatorView busyIndicatorView;
    private final OverviewScreenPresenter overviewScreen;
    private final ChangedFilesScreenPresenter changedFilesScreen;
    private final Promises promises;
    private final ProjectController projectController;
    private final Event<NotificationEvent> notificationEvent;
    private final SessionInfo sessionInfo;
    private WorkspaceProject workspaceProject;
    private SquashChangeRequestPopUpPresenter squashChangeRequestPopUpPresenter;
    private long currentChangeRequestId;
    private Branch currentSourceBranch;
    private Branch currentTargetBranch;
    private boolean overviewTabLoaded;
    private boolean changedFilesTabLoaded;
    private Repository repository;
    private String authorId;

    @Inject
    public ChangeRequestReviewScreenPresenter(final View view,
                                              final TranslationService ts,
                                              final LibraryPlaces libraryPlaces,
                                              final Caller<ChangeRequestService> changeRequestService,
                                              final Caller<RepositoryService> repositoryService,
                                              final BusyIndicatorView busyIndicatorView,
                                              final OverviewScreenPresenter overviewScreen,
                                              final ChangedFilesScreenPresenter changedFilesScreen,
                                              final Promises promises,
                                              final ProjectController projectController,
                                              final Event<NotificationEvent> notificationEvent,
                                              final SessionInfo sessionInfo,
                                              final SquashChangeRequestPopUpPresenter squashChangeRequestPopUpPresenter) {
        this.view = view;
        this.ts = ts;
        this.libraryPlaces = libraryPlaces;
        this.changeRequestService = changeRequestService;
        this.repositoryService = repositoryService;
        this.busyIndicatorView = busyIndicatorView;
        this.overviewScreen = overviewScreen;
        this.changedFilesScreen = changedFilesScreen;
        this.promises = promises;
        this.projectController = projectController;
        this.notificationEvent = notificationEvent;
        this.sessionInfo = sessionInfo;
        this.squashChangeRequestPopUpPresenter = squashChangeRequestPopUpPresenter;
    }

    @PostConstruct
    public void postConstruct() {
        this.workspaceProject = libraryPlaces.getActiveWorkspace();
        this.repository = workspaceProject.getRepository();

        this.view.init(this);
        this.view.setTitle(this.getTitle());
    }

    public void refreshOnFocus(@Observes final SelectPlaceEvent selectPlaceEvent) {
        if (workspaceProject != null && workspaceProject.getMainModule() != null) {
            final PlaceRequest place = selectPlaceEvent.getPlace();

            if (place.getIdentifier().equals(LibraryPlaces.CHANGE_REQUEST_REVIEW)) {
                final String changeRequestIdValue = place.getParameter(ChangeRequestUtils.CHANGE_REQUEST_ID_KEY, null);

                if (changeRequestIdValue != null && !changeRequestIdValue.equals("")) {
                    this.currentChangeRequestId = Long.parseLong(changeRequestIdValue);
                    this.loadContent();
                }
            }
        }
    }

    public void onChangeRequestUpdated(@Observes final ChangeRequestUpdatedEvent event) {
        if (event.getRepositoryId().equals(repository.getIdentifier())
                && event.getChangeRequestId() == currentChangeRequestId) {
            this.refreshContent(true,
                                false);
            this.notifyOtherUsers(event.getUserId());
        }
    }

    public void onChangeRequestStatusUpdated(@Observes final ChangeRequestStatusUpdatedEvent event) {
        if (event.getRepositoryId().equals(repository.getIdentifier())
                && event.getChangeRequestId() == currentChangeRequestId) {
            final boolean refreshChangedFiles = event.getNewStatus() == ChangeRequestStatus.OPEN;

            this.refreshContent(true,
                                refreshChangedFiles);
            this.notifyOtherUsers(event.getUserId());
        }
    }

    public void onRepositoryFileListUpdated(@Observes final RepositoryFileListUpdatedEvent event) {
        if (event.getRepositoryId().equals(repository.getIdentifier())) {
            final String updatedBranch = event.getBranchName();

            if (currentSourceBranch.getName().equals(updatedBranch) ||
                    currentTargetBranch.getName().equals(updatedBranch)) {
                this.refreshContent(false,
                                    true);
            }
        }
    }

    public void onRepositoryUpdated(@Observes final RepositoryUpdatedEvent event) {
        if (event.getRepository().getIdentifier().equals(repository.getIdentifier())) {
            this.repository = event.getRepository();

            if (!this.repository.getBranches().contains(currentSourceBranch) ||
                    !this.repository.getBranches().contains(currentTargetBranch)) {
                this.goBackToProject();
            }
        }
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ts.getTranslation(LibraryConstants.ChangeRequest);
    }

    @WorkbenchPartView
    public View getView() {
        return view;
    }

    public void showOverviewContent() {
        this.view.setContent(this.overviewScreen.getView().getElement());
    }

    public void showChangedFilesContent() {
        this.view.setContent(this.changedFilesScreen.getView().getElement());
    }

    public void cancel() {
        this.goBackToProject();
    }

    public void reject() {
        this.doActionIfAllowed(this::rejectChangeRequestAction);
    }

    public void squash() {
        changeRequestService.call((RemoteCallback<List<ChangeRequestCommit>>) this::showSquashPopUp)
                .getCommits(workspaceProject.getSpace().getName(),
                            repository.getAlias(),
                            currentChangeRequestId);
    }

    public void merge() {
        this.doActionIfAllowed(this::mergeChangeRequestAction);
    }

    public void revert() {
        this.doActionIfAllowed(this::revertChangeRequestAction);
    }

    public void close() {
        if (isUserAuthor()) {
            closeChangeRequestAction();
        }
    }

    public void reopen() {
        if (isUserAuthor()) {
            reopenChangeRequestAction();
        }
    }

    private void showSquashPopUp(final List<ChangeRequestCommit> commits) {
        String messages = commits.stream()
                                 .map(ChangeRequestCommit::getMessage)
                                 .collect(Collectors.joining("\n"));
        ParameterizedCommand<String> command = message -> {
            doActionIfAllowed(() -> squashChangeRequestAction(message));
        };
        squashChangeRequestPopUpPresenter.show(messages, command);
    }

    private void notifyOtherUsers(final String userWhoMadeUpdates) {
        if (!sessionInfo.getIdentity().getIdentifier().equals(userWhoMadeUpdates)) {
            fireNotificationEvent(ts.format(LibraryConstants.ChangeRequestUpdatedMessage,
                                            currentChangeRequestId,
                                            userWhoMadeUpdates),
                                  NotificationEvent.NotificationType.INFO);
        }
    }

    private void goBackToProject() {
        this.reset();
        this.libraryPlaces.closeChangeRequestReviewScreen();
        this.libraryPlaces.goToProject(workspaceProject);
    }

    private void doActionIfAllowed(final Runnable action) {
        projectController.canUpdateBranch(workspaceProject,
                                          this.currentTargetBranch).then(userCanUpdateBranch -> {
            if (Boolean.TRUE.equals(userCanUpdateBranch)) {
                action.run();
            }

            return promises.resolve();
        });
    }

    private void loadContent() {
        this.reset();

        this.view.setTitle(ts.format(LibraryConstants.ChangeRequestAndId, currentChangeRequestId));

        this.busyIndicatorView.showBusyIndicator(ts.getTranslation(LibraryConstants.Loading));

        this.repositoryService.call((Repository updatedRepository) -> {
            this.repository = updatedRepository;

            this.setup(loadChangeRequestCallback());
        }).getRepositoryFromSpace(this.workspaceProject.getSpace(),
                                  this.repository.getAlias());
    }

    private void refreshContent(final boolean refreshOverview,
                                final boolean refreshChangedFiles) {
        this.busyIndicatorView.showBusyIndicator(ts.getTranslation(LibraryConstants.Loading));

        this.setup(reloadChangeRequestCallback(refreshOverview,
                                               refreshChangedFiles));
    }

    private void reset() {
        this.overviewTabLoaded = false;
        this.changedFilesTabLoaded = false;

        this.overviewScreen.reset();
        this.changedFilesScreen.reset();

        this.view.resetAll();

        this.view.activateOverviewTab();
    }

    private void setup(final RemoteCallback<ChangeRequest> getChangeRequestCallback) {
        changeRequestService.call(getChangeRequestCallback,
                                  new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView))
                .getChangeRequest(workspaceProject.getSpace().getName(),
                                  repository.getAlias(),
                                  currentChangeRequestId);
    }

    private RemoteCallback<ChangeRequest> loadChangeRequestCallback() {
        return (final ChangeRequest changeRequest) -> {
            this.authorId = changeRequest.getAuthorId();
            this.resolveInvolvedBranches(changeRequest);

            this.setupOverviewScreen(changeRequest);
            this.setupChangedFilesScreen(changeRequest);
        };
    }

    private void resolveInvolvedBranches(final ChangeRequest changeRequest) {
        this.currentSourceBranch = repository.getBranch(changeRequest.getSourceBranch())
                .orElseThrow(() -> new IllegalArgumentException(
                        "The branch " + changeRequest.getSourceBranch() + " does not exist."));

        this.currentTargetBranch = repository.getBranch(changeRequest.getTargetBranch())
                .orElseThrow(() -> new IllegalStateException(
                        "The branch " + changeRequest.getTargetBranch() + " does not exist."));
    }

    private void setupOverviewScreen(final ChangeRequest changeRequest) {
        this.overviewScreen.setup(changeRequest,
                                  (final Boolean success) -> {
                                      overviewTabLoaded = true;
                                      finishLoading(changeRequest);
                                  });
    }

    private void setupChangedFilesScreen(final ChangeRequest changeRequest) {
        this.changedFilesScreen.setup(changeRequest,
                                      (final Boolean success) -> {
                                          changedFilesTabLoaded = true;
                                          finishLoading(changeRequest);
                                      }, this.view::setChangedFilesCount);
    }

    private RemoteCallback<ChangeRequest> reloadChangeRequestCallback(final boolean refreshOverview,
                                                                      final boolean refreshChangedFiles) {
        return (final ChangeRequest changeRequest) -> {
            if (refreshOverview) {
                overviewTabLoaded = false;
                this.setupOverviewScreen(changeRequest);
            } else {
                overviewTabLoaded = true;
            }

            if (changeRequest.getStatus() == ChangeRequestStatus.OPEN && refreshChangedFiles) {
                changedFilesTabLoaded = false;
                this.setupChangedFilesScreen(changeRequest);
            } else {
                changedFilesTabLoaded = true;
            }
        };
    }

    private void setupActionButtons(final ChangeRequest changeRequest,
                                    final Branch targetBranch) {
        projectController.canUpdateBranch(workspaceProject, targetBranch).then(userCanUpdateBranch -> {
            this.view.resetButtonState();

            switch (changeRequest.getStatus()) {
                case ACCEPTED:
                    this.view.showRevertButton(userCanUpdateBranch);
                    break;
                case OPEN:
                    final boolean canBeAccepted = userCanUpdateBranch && !changeRequest.isConflict() &&
                            changeRequest.getChangedFilesCount() > 0;

                    this.view.showRejectButton(userCanUpdateBranch);
                    this.view.showAcceptButton(userCanUpdateBranch);
                    this.view.enableAcceptButton(canBeAccepted);
                    this.view.showCloseButton(isUserAuthor());
                    break;
                case REJECTED:
                case CLOSED:
                    this.view.showReopenButton(isUserAuthor());
                    break;
                case REVERT_FAILED:
                case REVERTED:
                default:
                    break;
            }

            return promises.resolve();
        });
    }

    private void rejectChangeRequestAction() {
        this.changeRequestService.call(v -> fireNotificationEvent(ts.format(LibraryConstants.ChangeRequestRejectMessage,
                                                                            currentChangeRequestId),
                                                                  NotificationEvent.NotificationType.SUCCESS))
                .rejectChangeRequest(workspaceProject.getSpace().getName(),
                                     repository.getAlias(),
                                     currentChangeRequestId);
    }

    private void squashChangeRequestAction(String message) {
        busyIndicatorView.showBusyIndicator(ts.getTranslation(LibraryConstants.Loading));

        this.changeRequestService.call((final Boolean succeeded) -> {
            if (Boolean.TRUE.equals(succeeded)) {
                fireNotificationEvent(ts.format(LibraryConstants.ChangeRequestAcceptMessage,
                                                currentChangeRequestId),
                                      NotificationEvent.NotificationType.SUCCESS);
            }

            busyIndicatorView.hideBusyIndicator();
        }, mergeChangeRequestErrorCallback())
                .squashChangeRequest(workspaceProject.getSpace().getName(),
                                     repository.getAlias(),
                                     currentChangeRequestId,
                                     message);
    }

    private void mergeChangeRequestAction() {
        busyIndicatorView.showBusyIndicator(ts.getTranslation(LibraryConstants.Loading));

        this.changeRequestService.call((final Boolean succeeded) -> {
            if (Boolean.TRUE.equals(succeeded)) {
                fireNotificationEvent(ts.format(LibraryConstants.ChangeRequestAcceptMessage,
                                                currentChangeRequestId),
                                      NotificationEvent.NotificationType.SUCCESS);
            }

            busyIndicatorView.hideBusyIndicator();
        }, mergeChangeRequestErrorCallback())
                .mergeChangeRequest(workspaceProject.getSpace().getName(),
                                     repository.getAlias(),
                                     currentChangeRequestId);
    }

    private ErrorCallback<Object> mergeChangeRequestErrorCallback() {
        return (message, throwable) -> {
            busyIndicatorView.hideBusyIndicator();

            if (throwable instanceof NothingToMergeException) {
                notificationEvent.fire(
                        new NotificationEvent(ts.getTranslation(LibraryConstants.NothingToMergeMessage),
                                              NotificationEvent.NotificationType.WARNING));
                return false;
            }

            return true;
        };
    }

    private void revertChangeRequestAction() {
        this.changeRequestService.call((final Boolean succeeded) -> {
            if (Boolean.TRUE.equals(succeeded)) {
                fireNotificationEvent(ts.format(LibraryConstants.ChangeRequestRevertMessage,
                                                currentChangeRequestId),
                                      NotificationEvent.NotificationType.SUCCESS);
            } else {
                fireNotificationEvent(ts.format(LibraryConstants.ChangeRequestRevertFailMessage,
                                                currentChangeRequestId),
                                      NotificationEvent.NotificationType.WARNING);
            }
        }).revertChangeRequest(workspaceProject.getSpace().getName(),
                               repository.getAlias(),
                               currentChangeRequestId);
    }

    private void closeChangeRequestAction() {
        this.changeRequestService.call(v -> fireNotificationEvent(ts.format(LibraryConstants.ChangeRequestCloseMessage,
                                                                            currentChangeRequestId),
                                                                  NotificationEvent.NotificationType.SUCCESS))
                .closeChangeRequest(workspaceProject.getSpace().getName(),
                                    repository.getAlias(),
                                    currentChangeRequestId);
    }

    private void reopenChangeRequestAction() {
        this.changeRequestService.call(v -> fireNotificationEvent(ts.format(LibraryConstants.ChangeRequestReopenMessage,
                                                                            currentChangeRequestId),
                                                                  NotificationEvent.NotificationType.SUCCESS),
                                       reopenChangeRequestErrorCallback())
                .reopenChangeRequest(workspaceProject.getSpace().getName(),
                                     repository.getAlias(),
                                     currentChangeRequestId);
    }

    private ErrorCallback<Object> reopenChangeRequestErrorCallback() {
        return (message, throwable) -> {
            busyIndicatorView.hideBusyIndicator();

            if (throwable instanceof ChangeRequestAlreadyOpenException) {
                final Long changeRequestId = ((ChangeRequestAlreadyOpenException) throwable).getChangeRequestId();
                notificationEvent.fire(
                        new NotificationEvent(ts.format(LibraryConstants.ChangeRequestAlreadyOpenMessage,
                                                        changeRequestId,
                                                        currentSourceBranch.getName(),
                                                        currentTargetBranch.getName()),
                                              NotificationEvent.NotificationType.WARNING));
                return false;
            }

            return true;
        };
    }

    private void fireNotificationEvent(final String message,
                                       final NotificationEvent.NotificationType type) {
        notificationEvent.fire(new NotificationEvent(message,
                                                     type));
    }

    private void finishLoading(final ChangeRequest changeRequest) {
        if (overviewTabLoaded && changedFilesTabLoaded) {
            this.setupActionButtons(changeRequest,
                                    this.currentTargetBranch);

            this.overviewScreen.checkWarnConflict(changeRequest);

            busyIndicatorView.hideBusyIndicator();
        }
    }

    private boolean isUserAuthor() {
        return this.authorId.equals(this.sessionInfo.getIdentity().getIdentifier());
    }

    public interface View extends UberElemental<ChangeRequestReviewScreenPresenter> {

        void setTitle(final String title);

        void setChangedFilesCount(final int count);

        void setContent(final HTMLElement content);

        void showRejectButton(final boolean isVisible);

        void showAcceptButton(final boolean isVisible);

        void enableAcceptButton(final boolean isEnabled);

        void showRevertButton(final boolean isVisible);

        void showCloseButton(final boolean isVisible);

        void showReopenButton(final boolean isVisible);

        void activateOverviewTab();

        void activateChangedFilesTab();

        void resetButtonState();

        void resetAll();
    }
}

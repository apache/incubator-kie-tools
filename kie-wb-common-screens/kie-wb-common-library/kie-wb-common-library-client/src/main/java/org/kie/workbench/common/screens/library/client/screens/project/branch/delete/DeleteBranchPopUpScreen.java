/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens.project.branch.delete;

import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.workbench.events.NotificationEvent;

public class DeleteBranchPopUpScreen {

    public interface View extends UberElemental<DeleteBranchPopUpScreen>,
                                  HasBusyIndicator {

        String getConfirmedName();

        void show(String name);

        void showError(final String errorMessage);

        void hide();

        String getWrongConfirmedNameValidationMessage();

        String getDeletingMessage();

        String getBranchDeletedSuccessfullyMessage();

    }

    private Branch branch;

    private DeleteBranchPopUpScreen.View view;

    private Caller<LibraryService> libraryService;

    private LibraryPlaces libraryPlaces;

    private Event<NotificationEvent> notificationEvent;

    @Inject
    public DeleteBranchPopUpScreen(final DeleteBranchPopUpScreen.View view,
                                   final Caller<LibraryService> libraryService,
                                   final LibraryPlaces libraryPlaces,
                                   final Event<NotificationEvent> notificationEvent) {
        this.view = view;
        this.libraryService = libraryService;
        this.libraryPlaces = libraryPlaces;
        this.notificationEvent = notificationEvent;
    }

    @PostConstruct
    public void setup() {
        view.init(this);
    }

    public void show(final Branch branch) {
        this.branch = branch;
        view.show(branch.getName());
    }

    public void delete() {
        final String confirmedName = view.getConfirmedName();
        if (!branch.getName().equals(confirmedName)) {
            view.showError(view.getWrongConfirmedNameValidationMessage());
            return;
        }

        view.showBusyIndicator(view.getDeletingMessage());
        libraryService.call(v -> {
                                view.hideBusyIndicator();
                                view.hide();
                                notificationEvent.fire(new NotificationEvent(view.getBranchDeletedSuccessfullyMessage(),
                                                                             NotificationEvent.NotificationType.SUCCESS));

                                final Optional<Branch> defaultBranch = libraryPlaces.getActiveWorkspace().getRepository().getDefaultBranch();
                                if (defaultBranch.isPresent()) {
                                    libraryPlaces.goToProject(libraryPlaces.getActiveWorkspace(), defaultBranch.get());
                                } else {
                                    libraryPlaces.goToLibrary();
                                }
                            },
                            new HasBusyIndicatorDefaultErrorCallback(view)).removeBranch(branch);
    }

    public void cancel() {
        view.hide();
    }
}

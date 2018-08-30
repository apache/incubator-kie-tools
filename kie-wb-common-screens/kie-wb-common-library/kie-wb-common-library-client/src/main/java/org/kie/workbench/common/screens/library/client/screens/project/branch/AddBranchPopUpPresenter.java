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

package org.kie.workbench.common.screens.library.client.screens.project.branch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.Branch;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.services.datamodel.util.SortHelper;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.kie.workbench.common.widgets.client.callbacks.CommandWithThrowableDrivenErrorCallback;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.workbench.events.NotificationEvent;

public class AddBranchPopUpPresenter {

    public interface View extends UberElemental<AddBranchPopUpPresenter>,
                                  HasBusyIndicator {
        void setBranches(final List<String> branches);

        String getName();

        String getBranchFrom();

        void setBranchFrom(final String branchFrom);

        void show();

        void hide();

        void showError(final String errorMessage);

        String getSavingMessage();

        String getAddBranchSuccessMessage();

        String getDuplicatedBranchMessage();

        String getEmptyNameMessage();

        String getInvalidNameMessage();

        void setAddButtonEnabled(final boolean enabled);
    }

    private Caller<LibraryService> libraryService;

    private BusyIndicatorView busyIndicatorView;

    private Event<NotificationEvent> notificationEvent;

    private LibraryPlaces libraryPlaces;

    private View view;

    private Caller<ValidationService> validationService;

    WorkspaceProject project;

    @Inject
    public AddBranchPopUpPresenter(final Caller<LibraryService> libraryService,
                                   final BusyIndicatorView busyIndicatorView,
                                   final Event<NotificationEvent> notificationEvent,
                                   final LibraryPlaces libraryPlaces,
                                   final View view,
                                   final Caller<ValidationService> validationService) {
        this.libraryService = libraryService;
        this.busyIndicatorView = busyIndicatorView;
        this.notificationEvent = notificationEvent;
        this.libraryPlaces = libraryPlaces;
        this.view = view;
        this.validationService = validationService;
    }

    @PostConstruct
    public void setup() {
        view.init(AddBranchPopUpPresenter.this);
        project = libraryPlaces.getActiveWorkspace();
        view.setBranches(project.getRepository().getBranches().stream().map(Branch::getName).sorted(SortHelper.ALPHABETICAL_ORDER_COMPARATOR).collect(Collectors.toList()));
        view.setBranchFrom(project.getBranch().getName());
    }

    public void show() {
        view.show();
    }

    public void add() {
        final String name = view.getName();
        final String branchFrom = view.getBranchFrom();

        beginBranchCreation();
        validateFields(name,
                       () -> libraryService.call(v -> {
                           endBranchCreation();
                           notifySuccess();
                           view.hide();
                       }, getErrorCallback()).addBranch(name,
                                                        branchFrom, project));
    }

    private void validateFields(final String name,
                                final Runnable successCallback) {
        if (name == null || name.trim().isEmpty()) {
            endBranchCreation();
            view.showError(view.getEmptyNameMessage());
            return;
        }

        validationService.call((Boolean isValid) -> {
            if (Boolean.TRUE.equals(isValid)) {
                if (successCallback != null) {
                    successCallback.run();
                }
            } else {
                endBranchCreation();
                view.showError(view.getInvalidNameMessage());
            }
        }).isBranchNameValid(name);
    }

    private void beginBranchCreation() {
        view.setAddButtonEnabled(false);
        view.showBusyIndicator(view.getSavingMessage());
    }

    private void endBranchCreation() {
        view.setAddButtonEnabled(true);
        view.hideBusyIndicator();
    }

    private void notifySuccess() {
        notificationEvent.fire(new NotificationEvent(view.getAddBranchSuccessMessage(),
                                                     NotificationEvent.NotificationType.SUCCESS));
    }

    private ErrorCallback<?> getErrorCallback() {
        Map<Class<? extends Throwable>, CommandWithThrowableDrivenErrorCallback.CommandWithThrowable> errors = new HashMap<Class<? extends Throwable>, CommandWithThrowableDrivenErrorCallback.CommandWithThrowable>() {{
            put(FileAlreadyExistsException.class,
                parameter -> {
                    endBranchCreation();
                    view.showError(view.getDuplicatedBranchMessage());
                });
        }};

        return createErrorCallback(errors);
    }

    ErrorCallback<?> createErrorCallback(Map<Class<? extends Throwable>, CommandWithThrowableDrivenErrorCallback.CommandWithThrowable> errors) {
        return new CommandWithThrowableDrivenErrorCallback(busyIndicatorView,
                                                           errors);
    }

    public void cancel() {
        view.hide();
    }
}

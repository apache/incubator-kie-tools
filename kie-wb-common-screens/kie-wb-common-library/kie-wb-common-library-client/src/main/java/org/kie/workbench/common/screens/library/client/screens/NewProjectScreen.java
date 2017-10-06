/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.library.api.LibraryInfo;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.ProjectInfo;
import org.kie.workbench.common.screens.library.api.preferences.LibraryPreferences;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.projecteditor.util.NewProjectUtils;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.kie.workbench.common.widgets.client.callbacks.CommandWithThrowableDrivenErrorCallback;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.NotificationEvent;

@WorkbenchScreen(identifier = LibraryPlaces.NEW_PROJECT_SCREEN)
public class NewProjectScreen {

    public interface View extends UberElement<NewProjectScreen> {

        void setProjectDescription(String defaultProjectDescription);

        String getCreatingProjectMessage();

        String getProjectCreatedSuccessfullyMessage();

        String getEmptyNameMessage();

        String getInvalidNameMessage();

        String getDuplicatedProjectMessage();
    }

    private Caller<LibraryService> libraryService;

    private PlaceManager placeManager;

    private BusyIndicatorView busyIndicatorView;

    private Event<NotificationEvent> notificationEvent;

    private LibraryPlaces libraryPlaces;

    private View view;

    private SessionInfo sessionInfo;

    private Event<NewProjectEvent> newProjectEvent;

    private LibraryPreferences libraryPreferences;

    private ConflictingRepositoriesPopup conflictingRepositoriesPopup;

    private Caller<ValidationService> validationService;

    LibraryInfo libraryInfo;

    @Inject
    public NewProjectScreen(final Caller<LibraryService> libraryService,
                            final PlaceManager placeManager,
                            final BusyIndicatorView busyIndicatorView,
                            final Event<NotificationEvent> notificationEvent,
                            final LibraryPlaces libraryPlaces,
                            final View view,
                            final SessionInfo sessionInfo,
                            final Event<NewProjectEvent> newProjectEvent,
                            final LibraryPreferences libraryPreferences,
                            final ConflictingRepositoriesPopup conflictingRepositoriesPopup,
                            final Caller<ValidationService> validationService) {
        this.libraryService = libraryService;
        this.placeManager = placeManager;
        this.busyIndicatorView = busyIndicatorView;
        this.notificationEvent = notificationEvent;
        this.libraryPlaces = libraryPlaces;
        this.view = view;
        this.sessionInfo = sessionInfo;
        this.newProjectEvent = newProjectEvent;
        this.libraryPreferences = libraryPreferences;
        this.conflictingRepositoriesPopup = conflictingRepositoriesPopup;
        this.validationService = validationService;
    }

    @OnStartup
    public void load() {
        libraryService.call(new RemoteCallback<LibraryInfo>() {
            @Override
            public void callback(LibraryInfo libraryInfo) {
                NewProjectScreen.this.libraryInfo = libraryInfo;
            }
        }).getLibraryInfo(libraryPlaces.getSelectedRepository(),
                          libraryPlaces.getSelectedBranch());

        libraryPreferences.load(loadedLibraryPreferences -> {
                                    view.init(NewProjectScreen.this);
                                    view.setProjectDescription(loadedLibraryPreferences.getProjectPreferences().getDescription());
                                },
                                error -> {
                                });
    }

    public void cancel() {
        libraryPlaces.goToLibrary();
    }

    public void createProject(final String projectName,
                              final String projectDescription) {
        createProject(projectName,
                      projectDescription,
                      DeploymentMode.VALIDATED);
    }

    private void createProject(final String projectName,
                               final String projectDescription,
                               final DeploymentMode mode) {
        busyIndicatorView.showBusyIndicator(view.getCreatingProjectMessage());

        validateFields(projectName,
                       projectDescription,
                       () -> {
                           libraryService.call(getSuccessCallback(),
                                               getErrorCallback(projectName,
                                                                projectDescription)).createProject(projectName,
                                                                                                   libraryPlaces.getSelectedOrganizationalUnit(),
                                                                                                   libraryPlaces.getSelectedRepository(),
                                                                                                   getBaseURL(),
                                                                                                   projectDescription,
                                                                                                   mode);
                       });
    }

    private void validateFields(final String projectName,
                                final String projectDescription,
                                final Command successCallback) {
        if (projectName == null || projectName.trim().isEmpty()) {
            hideLoadingBox();
            notificationEvent.fire(new NotificationEvent(view.getEmptyNameMessage(),
                                                         NotificationEvent.NotificationType.ERROR));
            return;
        }

        validationService.call((Boolean isValid) -> {
            final String sanitizeProjectName = NewProjectUtils.sanitizeProjectName(projectName);
            if (Boolean.TRUE.equals(isValid) && !sanitizeProjectName.isEmpty()) {
                if (successCallback != null) {
                    successCallback.execute();
                }
            } else {
                hideLoadingBox();
                notificationEvent.fire(new NotificationEvent(view.getInvalidNameMessage(),
                                                             NotificationEvent.NotificationType.ERROR));
            }
        }).isProjectNameValid(projectName);
    }

    private RemoteCallback<KieProject> getSuccessCallback() {
        return project -> {
            newProjectEvent.fire(new NewProjectEvent(project,
                                                     sessionInfo.getId(),
                                                     sessionInfo.getIdentity().getIdentifier()));
            hideLoadingBox();
            notifySuccess();
            goToProject(project);
        };
    }

    private ErrorCallback<?> getErrorCallback(final String projectName,
                                              final String projectDescription) {

        Map<Class<? extends Throwable>, CommandWithThrowableDrivenErrorCallback.CommandWithThrowable> errors = new HashMap<Class<? extends Throwable>, CommandWithThrowableDrivenErrorCallback.CommandWithThrowable>() {{
            put(GAVAlreadyExistsException.class,
                parameter -> {
                    libraryService.call((GAV gav) -> {
                        hideLoadingBox();
                        conflictingRepositoriesPopup.setContent(gav,
                                                                ((GAVAlreadyExistsException) parameter).getRepositories(),
                                                                () -> {
                                                                    conflictingRepositoriesPopup.hide();
                                                                    createProject(projectName,
                                                                                  projectDescription,
                                                                                  DeploymentMode.FORCED);
                                                                });
                        conflictingRepositoriesPopup.show();
                    }).createGAV(projectName,
                                 libraryPlaces.getSelectedOrganizationalUnit());
                });
            put(FileAlreadyExistsException.class,
                parameter -> {
                    hideLoadingBox();
                    notificationEvent.fire(new NotificationEvent(view.getDuplicatedProjectMessage(),
                                                                 NotificationEvent.NotificationType.ERROR));
                });
        }};

        return createErrorCallback(errors);
    }

    ErrorCallback<?> createErrorCallback(Map<Class<? extends Throwable>, CommandWithThrowableDrivenErrorCallback.CommandWithThrowable> errors) {
        return new CommandWithThrowableDrivenErrorCallback(busyIndicatorView,
                                                           errors);
    }

    boolean isDuplicatedProjectName(Throwable throwable) {
        return throwable instanceof FileAlreadyExistsException;
    }

    String getBaseURL() {
        final String url = GWT.getModuleBaseURL();
        final String baseUrl = url.replace(GWT.getModuleName() + "/",
                                           "");
        return baseUrl;
    }

    private void goToProject(KieProject project) {
        final ProjectInfo projectInfo = new ProjectInfo(libraryPlaces.getSelectedOrganizationalUnit(),
                                                        libraryPlaces.getSelectedRepository(),
                                                        libraryInfo.getSelectedBranch(),
                                                        project);
        libraryPlaces.goToProject(projectInfo);
    }

    private void notifySuccess() {
        notificationEvent.fire(new NotificationEvent(view.getProjectCreatedSuccessfullyMessage(),
                                                     NotificationEvent.NotificationType.SUCCESS));
    }

    private void hideLoadingBox() {
        busyIndicatorView.hideBusyIndicator();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "New Project Screen";
    }

    @WorkbenchPartView
    public UberElement<NewProjectScreen> getView() {
        return view;
    }
}

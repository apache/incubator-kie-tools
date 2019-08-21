/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.api.LibraryInfo;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.preferences.LibraryPreferences;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.projecteditor.client.util.KiePOMDefaultOptions;
import org.kie.workbench.common.screens.projecteditor.client.wizard.POMBuilder;
import org.kie.workbench.common.services.refactoring.model.index.events.IndexingFinishedEvent;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.kie.workbench.common.widgets.client.callbacks.CommandWithThrowableDrivenErrorCallback;
import org.slf4j.Logger;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.views.pfly.widgets.ErrorPopup;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.NotificationEvent;

public class AddProjectPopUpPresenter {

    public interface View extends UberElement<AddProjectPopUpPresenter>,
                                  HasBusyIndicator {

        String getName();

        String getDescription();

        String getGroupId();

        String getArtifactId();

        String getVersion();

        void setDescription(String description);
        
        void setGroupId(String groupId);
        
        void setArtifactId(String artifactId);
        
        void setVersion(String version);

        boolean isAdvancedOptionsSelected();

        void show();

        void hide();

        void showError(final String errorMessage);

        String getSavingMessage();

        String getAddProjectSuccessMessage();

        String getDuplicatedProjectMessage();

        String getEmptyNameMessage();

        String getInvalidNameMessage();

        String getEmptyGroupIdMessage();

        String getInvalidGroupIdMessage();

        String getEmptyArtifactIdMessage();

        String getInvalidArtifactIdMessage();

        String getEmptyVersionMessage();

        String getInvalidVersionMessage();

        void setAddButtonEnabled(boolean enabled);
    }
    
    private Caller<LibraryService> libraryService;

    private BusyIndicatorView busyIndicatorView;

    private Event<NotificationEvent> notificationEvent;

    private LibraryPlaces libraryPlaces;

    private WorkspaceProjectContext projectContext;
    private View view;

    private SessionInfo sessionInfo;

    private Event<NewProjectEvent> newProjectEvent;

    private LibraryPreferences libraryPreferences;

    private ConflictingRepositoriesPopup conflictingRepositoriesPopup;

    private Caller<ValidationService> validationService;

    private ErrorPopup errorPopup;

    private TranslationService translationService;

    private Caller<WorkspaceProjectService> projectService;

    private Logger logger;

    LibraryInfo libraryInfo;

    ParameterizedCommand<WorkspaceProject> successCallback;

    Path newProjectPath;

    List<Path> projectsIndexed;

    @Inject
    public AddProjectPopUpPresenter(final Caller<LibraryService> libraryService,
                                    final BusyIndicatorView busyIndicatorView,
                                    final Event<NotificationEvent> notificationEvent,
                                    final LibraryPlaces libraryPlaces,
                                    final WorkspaceProjectContext projectContext,
                                    final View view,
                                    final SessionInfo sessionInfo,
                                    final Event<NewProjectEvent> newProjectEvent,
                                    final LibraryPreferences libraryPreferences,
                                    final ConflictingRepositoriesPopup conflictingRepositoriesPopup,
                                    final Caller<ValidationService> validationService,
                                    final ErrorPopup errorPopup,
                                    final TranslationService translationService,
                                    final Caller<WorkspaceProjectService> projectService,
                                    final Logger logger) {
        this.libraryService = libraryService;
        this.busyIndicatorView = busyIndicatorView;
        this.notificationEvent = notificationEvent;
        this.libraryPlaces = libraryPlaces;
        this.projectContext = projectContext;
        this.view = view;
        this.sessionInfo = sessionInfo;
        this.newProjectEvent = newProjectEvent;
        this.libraryPreferences = libraryPreferences;
        this.conflictingRepositoriesPopup = conflictingRepositoriesPopup;
        this.validationService = validationService;
        this.errorPopup = errorPopup;
        this.translationService = translationService;
        this.projectService = projectService;
        this.logger = logger;
        this.newProjectPath = null;
        this.projectsIndexed = new ArrayList<>();
    }

    @PostConstruct
    public void setup() {
        view.init(AddProjectPopUpPresenter.this);

        libraryService.call(new RemoteCallback<LibraryInfo>() {
            @Override
            public void callback(LibraryInfo libraryInfo) {
                AddProjectPopUpPresenter.this.libraryInfo = libraryInfo;
            }
        }).getLibraryInfo(projectContext.getActiveOrganizationalUnit()
                                        .orElseThrow(() -> new IllegalStateException("Cannot get library info without an active organizational unit.")));
    }

    public void setSuccessCallback(ParameterizedCommand<WorkspaceProject> successCallback) {
        this.successCallback = successCallback;
    }

    public void show() {
        libraryPreferences.load(loadedLibraryPreferences -> {
                                    view.setDescription(loadedLibraryPreferences.getProjectPreferences().getDescription());
                                    view.setVersion(loadedLibraryPreferences.getProjectPreferences().getVersion());
                                    view.setGroupId(projectContext.getActiveOrganizationalUnit().isPresent() ? projectContext.getActiveOrganizationalUnit().get().getDefaultGroupId() 
                                                                                                             : loadedLibraryPreferences.getOrganizationalUnitPreferences().getGroupId());
                                    view.show();
                                },
                                error -> {
                                });
    }

    public void add() {
        createProject(DeploymentMode.VALIDATED);
    }

    private void createProject(final DeploymentMode mode) {
        beginProjectCreation();

        final String name = view.getName();
        final String description = view.getDescription();
        final String groupId = view.getGroupId();
        final String artifactId = view.getArtifactId();
        final String version = view.getVersion();
        
        validateFields(name,
                       groupId,
                       artifactId,
                       version,
                       () -> {
                           Map<Class<? extends Throwable>, CommandWithThrowableDrivenErrorCallback.CommandWithThrowable> errors = new HashMap<Class<? extends Throwable>, CommandWithThrowableDrivenErrorCallback.CommandWithThrowable>() {{
                               put(GAVAlreadyExistsException.class, exception -> handleGAVAlreadyExistsException((GAVAlreadyExistsException) exception));
                               put(FileAlreadyExistsException.class, exception -> handleFileExistsException((FileAlreadyExistsException) exception));
                           }};

                           final ErrorCallback<?> errorCallback = new CommandWithThrowableDrivenErrorCallback(busyIndicatorView, errors, throwable -> handleUnexpectedException(name, throwable));

                           final POM pom = setDefaultPOM(groupId,
                                                         artifactId,
                                                         version,
                                                         name, 
                                                         description);

                           libraryService.call((WorkspaceProject project) -> {
                                                   newProjectPath = project.getRootPath();
                                                   if (projectsIndexed.contains(newProjectPath)) {
                                                       endProjectCreation();
                                                       getSuccessCallback().execute(project);
                                                   }
                                               },
                                               errorCallback).createProject(projectContext.getActiveOrganizationalUnit()
                                                                                          .orElseThrow(() -> new IllegalStateException("Cannot create new project without an active organizational unit.")),
                                                                            pom,
                                                                            mode);
                       });
    }

    public void onProjectIndexingFinishedEvent(@Observes IndexingFinishedEvent event) {
        projectsIndexed.add(event.getPath());
        if (newProjectPath != null && newProjectPath.equals(event.getPath())) {
            projectService.call((WorkspaceProject project) -> {
                                    endProjectCreation();
                                    getSuccessCallback().execute(project);
                                },
                                (o, throwable) -> {
                                    logger.info("Project path " + event.getPath().toURI() + " not found.");
                                    return false;
                                }).resolveProject(event.getPath());
        }
    }

    private void beginProjectCreation() {
        view.setAddButtonEnabled(false);
        view.showBusyIndicator(view.getSavingMessage());
    }

    private void endProjectCreation() {
        view.setAddButtonEnabled(true);
        view.hideBusyIndicator();
    }

    private void validateFields(final String name,
                                final String groupId,
                                final String artifactId,
                                final String version,
                                final Command successCallback) {
        final Command validateVersion = () -> validateVersion(version,
                                                              successCallback);
        final Command validateArtifactId = () -> validateArtifactId(artifactId,
                                                                    validateVersion);
        final Command validateGroupId = () -> validateGroupId(groupId,
                                                              validateArtifactId);
        validateName(name,
                     view.isAdvancedOptionsSelected() ? validateGroupId : successCallback);
    }

    private void validateName(final String name,
                              final Command successCallback) {
        if (name == null || name.trim().isEmpty()) {
            endProjectCreation();
            view.showError(view.getEmptyNameMessage());
            return;
        }

        validationService.call((Boolean isValid) -> {
            if (Boolean.TRUE.equals(isValid)) {
                if (successCallback != null) {
                    successCallback.execute();
                }
            } else {
                endProjectCreation();
                view.showError(view.getInvalidNameMessage());
            }
        }).isProjectNameValid(name);
    }

    private void validateGroupId(final String groupId,
                                 final Command successCallback) {
        if (groupId == null || groupId.trim().isEmpty()) {
            endProjectCreation();
            view.showError(view.getEmptyGroupIdMessage());
            return;
        }

        validationService.call((Boolean isValid) -> {
            if (Boolean.TRUE.equals(isValid)) {
                if (successCallback != null) {
                    successCallback.execute();
                }
            } else {
                endProjectCreation();
                view.showError(view.getInvalidGroupIdMessage());
            }
        }).validateGroupId(groupId);
    }

    private void validateArtifactId(final String artifactId,
                                    final Command successCallback) {
        if (artifactId == null || artifactId.trim().isEmpty()) {
            endProjectCreation();
            view.showError(view.getEmptyArtifactIdMessage());
            return;
        }

        validationService.call((Boolean isValid) -> {
            if (Boolean.TRUE.equals(isValid)) {
                if (successCallback != null) {
                    successCallback.execute();
                }
            } else {
                endProjectCreation();
                view.showError(view.getInvalidArtifactIdMessage());
            }
        }).validateArtifactId(artifactId);
    }

    private void validateVersion(final String version,
                                 final Command successCallback) {
        if (version == null || version.trim().isEmpty()) {
            endProjectCreation();
            view.showError(view.getEmptyVersionMessage());
            return;
        }

        validationService.call((Boolean isValid) -> {
            if (Boolean.TRUE.equals(isValid)) {
                if (successCallback != null) {
                    successCallback.execute();
                }
            } else {
                endProjectCreation();
                view.showError(view.getInvalidVersionMessage());
            }
        }).validateGAVVersion(version);
    }

    public ParameterizedCommand<WorkspaceProject> getSuccessCallback() {
        if (successCallback != null) {
            return successCallback;
        } else {
            return getProjectCreationSuccessCallback();
        }
    }

    public ParameterizedCommand<WorkspaceProject> getProjectCreationSuccessCallback() {
        return project -> {
            view.hide();
            notifySuccess();
            libraryPlaces.goToProject(project);
        };
    }

    private void handleGAVAlreadyExistsException(final GAVAlreadyExistsException exception) {
        endProjectCreation();
        conflictingRepositoriesPopup.setContent(exception.getGAV(),
                                                exception.getRepositories(),
                                                () -> {
                                                    conflictingRepositoriesPopup.hide();
                                                    createProject(DeploymentMode.FORCED);
                                                });
        conflictingRepositoriesPopup.show();
    }

    private void handleUnexpectedException(final String project, final Throwable throwable) {
        view.hide();
        endProjectCreation();
        busyIndicatorView.hideBusyIndicator();
        errorPopup.showError(translationService.format(LibraryConstants.ErrorCreatingPoject, project), Arrays.toString(throwable.getStackTrace()));
    }

    private void handleFileExistsException(final FileAlreadyExistsException exception) {
        endProjectCreation();
        view.showError(view.getDuplicatedProjectMessage());
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

    private void notifySuccess() {
        notificationEvent.fire(new NotificationEvent(view.getAddProjectSuccessMessage(),
                                                     NotificationEvent.NotificationType.SUCCESS));
    }

    public void cancel() {
        view.hide();
    }

    public void setDefaultArtifactId() {
        view.setArtifactId(view.getName().replaceAll(" ", ""));
    }
    
    public void restoreAdvancedOptions() {
        libraryPreferences.load(loadedLibraryPreferences -> {
            view.setVersion(loadedLibraryPreferences.getProjectPreferences().getVersion());
            setDefaultArtifactId();
            view.setGroupId(projectContext.getActiveOrganizationalUnit().isPresent() ? projectContext.getActiveOrganizationalUnit().get().getDefaultGroupId() 
                                                                                     : loadedLibraryPreferences.getOrganizationalUnitPreferences().getGroupId());
        },
        error -> {
        });
    }
    
    private POM setDefaultPOM(String groupId, String artifactId, String version, String name, String description) {
        final POM pom = new POM(new GAV(groupId,
                                        artifactId,
                                        version));
        pom.setName(name);
        pom.setDescription(description);
        
        POMBuilder pomBuilder = new POMBuilder(pom);
        KiePOMDefaultOptions pomDefaultOptions = new KiePOMDefaultOptions();
        pomBuilder.setBuildPlugins(pomDefaultOptions.getBuildPlugins());
        
        return pomBuilder.build();
    }
}

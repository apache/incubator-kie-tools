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

package org.kie.workbench.common.screens.library.client.screens.importrepository;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.examples.exception.EmptyRemoteRepositoryException;
import org.kie.workbench.common.screens.examples.model.Credentials;
import org.kie.workbench.common.screens.examples.model.ExampleRepository;
import org.kie.workbench.common.screens.examples.model.ImportProject;
import org.kie.workbench.common.screens.examples.service.ProjectImportService;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.workbench.events.NotificationEvent;

public class ImportRepositoryPopUpPresenter {

    public interface View extends UberElement<ImportRepositoryPopUpPresenter>,
                                  HasBusyIndicator {

        String getRepositoryURL();

        String getUserName();

        String getPassword();

        void show();

        void hide();

        void showError(final String errorMessage);

        String getLoadingMessage();

        String getNoProjectsToImportMessage();

        String getEmptyRepositoryURLValidationMessage();
    }

    private View view;

    private Caller<ProjectImportService> importService;

    private LibraryPlaces libraryPlaces;

    private Caller<LibraryService> libraryService;

    private WorkspaceProjectContext projectContext;

    private Event<NotificationEvent> notificationEvent;

    private TranslationService ts;

    @Inject
    public ImportRepositoryPopUpPresenter(final View view,
                                          final LibraryPlaces libraryPlaces,
                                          final Caller<ProjectImportService> importService,
                                          final Caller<LibraryService> libraryService,
                                          final WorkspaceProjectContext projectContext,
                                          final Event<NotificationEvent> notificationEvent,
                                          final TranslationService ts) {
        this.view = view;
        this.libraryPlaces = libraryPlaces;
        this.importService = importService;
        this.libraryService = libraryService;
        this.projectContext = projectContext;
        this.notificationEvent = notificationEvent;
        this.ts = ts;
    }

    @PostConstruct
    public void setup() {
        view.init(this);
    }

    public void show() {
        view.show();
    }

    public void importRepository() {
        final String repositoryUrl = view.getRepositoryURL();
        if (isEmpty(repositoryUrl)) {
            view.showError(view.getEmptyRepositoryURLValidationMessage());
            return;
        }

        final String fixedRepositoryUrl = repositoryUrl.trim();

        view.showBusyIndicator(view.getLoadingMessage());
        importService.call((Set<ImportProject> projects) -> {
                               view.hideBusyIndicator();
                               if (projects.isEmpty()) {
                                   view.showError(view.getNoProjectsToImportMessage());
                               } else {
                                   view.hide();
                                   libraryPlaces.goToExternalImportPresenter(projects);
                               }
                           },
                           (Message message, Throwable throwable) -> {
                               if (throwable instanceof EmptyRemoteRepositoryException) {
                                   final String repositoryAlias =
                                           ((EmptyRemoteRepositoryException) throwable).getRepositoryAlias();
                                   createProjectFromEmptyRemoteRepository(fixedRepositoryUrl,
                                                                          repositoryAlias);
                               } else {
                                   view.hideBusyIndicator();
                                   view.showError(view.getNoProjectsToImportMessage());
                               }

                               return false;
                           }).getProjects(this.libraryPlaces.getActiveSpace(),
                                          new ExampleRepository(fixedRepositoryUrl,
                                                                new Credentials(
                                                                        view.getUserName(),
                                                                        view.getPassword())));
    }

    private void createProjectFromEmptyRemoteRepository(final String repositoryUrl,
                                                        final String repositoryAlias) {
        libraryService.call((final WorkspaceProject project) -> {
                                view.hideBusyIndicator();
                                view.hide();

                                notificationEvent.fire(
                                        new NotificationEvent(ts.getTranslation(LibraryConstants.AddProjectSuccess),
                                                              NotificationEvent.NotificationType.SUCCESS));

                                libraryPlaces.goToProject(project);
                            },
                            (message, throwable) -> {
                                view.hideBusyIndicator();
                                view.showError(view.getNoProjectsToImportMessage());
                                return false;
                            }).createProject(activeOrganizationalUnit(),
                                             repositoryUrl,
                                             repositoryAlias);
    }

    private OrganizationalUnit activeOrganizationalUnit() {
        return projectContext.getActiveOrganizationalUnit()
                .orElseThrow(() -> new IllegalStateException("Cannot create new project without an active organizational unit."));
    }

    public void cancel() {
        view.hide();
    }

    public View getView() {
        return view;
    }

    private boolean isEmpty(final String text) {
        return text == null || text.trim().isEmpty();
    }
}

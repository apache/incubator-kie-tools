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

package org.kie.workbench.common.screens.library.client.util;

import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.examples.model.ExampleProject;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.ProjectInfo;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.NotificationEvent;

@ApplicationScoped
public class ExamplesUtils {

    private SessionInfo sessionInfo;

    private TranslationService ts;

    private LibraryPlaces libraryPlaces;

    private Caller<LibraryService> libraryService;

    private BusyIndicatorView busyIndicatorView;

    private Event<NotificationEvent> notificationEvent;

    private Event<NewProjectEvent> newProjectEvent;

    private Set<ExampleProject> exampleProjects;

    @Inject
    public ExamplesUtils(final SessionInfo sessionInfo,
                         final TranslationService ts,
                         final LibraryPlaces libraryPlaces,
                         final Caller<LibraryService> libraryService,
                         final BusyIndicatorView busyIndicatorView,
                         final Event<NotificationEvent> notificationEvent,
                         final Event<NewProjectEvent> newProjectEvent) {
        this.sessionInfo = sessionInfo;
        this.ts = ts;
        this.libraryPlaces = libraryPlaces;
        this.libraryService = libraryService;
        this.busyIndicatorView = busyIndicatorView;
        this.notificationEvent = notificationEvent;
        this.newProjectEvent = newProjectEvent;
    }

    public void refresh() {
        this.exampleProjects = null;
    }

    public void getExampleProjects(final ParameterizedCommand<Set<ExampleProject>> callback) {
        if (exampleProjects == null) {
            libraryService.call(new RemoteCallback<Set<ExampleProject>>() {
                @Override
                public void callback(final Set<ExampleProject> exampleProjects) {
                    ExamplesUtils.this.exampleProjects = exampleProjects;
                    callback.execute(exampleProjects);
                }
            }).getExampleProjects();
        } else {
            callback.execute(exampleProjects);
        }
    }

    public void importProject(final ExampleProject exampleProject) {
        busyIndicatorView.showBusyIndicator(ts.getTranslation(LibraryConstants.Importing));
        libraryService.call((Project project) -> {
                                busyIndicatorView.hideBusyIndicator();
                                notificationEvent.fire(new NotificationEvent(ts.getTranslation(LibraryConstants.ProjectImportedSuccessfully),
                                                                             NotificationEvent.NotificationType.SUCCESS));

                                newProjectEvent.fire(new NewProjectEvent(project,
                                                                         sessionInfo.getId(),
                                                                         sessionInfo.getIdentity().getIdentifier()));
                                goToProject(project);
                            },
                            (o, throwable) -> {
                                busyIndicatorView.hideBusyIndicator();
                                notificationEvent.fire(new NotificationEvent(ts.getTranslation(LibraryConstants.ProjectImportError),
                                                                             NotificationEvent.NotificationType.ERROR));
                                return false;
                            }).importProject(libraryPlaces.getSelectedOrganizationalUnit(),
                                             libraryPlaces.getSelectedRepository(),
                                             libraryPlaces.getSelectedBranch(),
                                             exampleProject);
    }

    private void goToProject(Project project) {
        final ProjectInfo projectInfo = new ProjectInfo(libraryPlaces.getSelectedOrganizationalUnit(),
                                                        libraryPlaces.getSelectedRepository(),
                                                        libraryPlaces.getSelectedBranch(),
                                                        project);
        libraryPlaces.goToProject(projectInfo);
    }
}

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

package org.kie.workbench.common.screens.library.client.screens.samples;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.examples.model.ExampleOrganizationalUnit;
import org.kie.workbench.common.screens.examples.model.ExampleProject;
import org.kie.workbench.common.screens.examples.model.ExampleTargetRepository;
import org.kie.workbench.common.screens.examples.service.ExamplesService;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.perspective.LibraryPerspective;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.widgets.common.TileWidget;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

@WorkbenchScreen(identifier = LibraryPlaces.IMPORT_PROJECTS_SCREEN,
        owningPerspective = LibraryPerspective.class)
public class ImportProjectsScreen {

    public interface View extends UberElement<ImportProjectsScreen>,
                                  HasBusyIndicator {

        void setTitle(String title);

        void clearProjects();

        void addProject(HTMLElement project);

        String getNumberOfAssetsMessage(int numberOfAssets);

        String getTrySamplesLabel();

        String getNoProjectsToImportMessage();

        String getImportingMessage();

        String getLoadingMessage();

        String getNoProjectsSelectedMessage();

        String getImportProjectsSuccessMessage();
    }

    private View view;

    private LibraryPlaces libraryPlaces;

    private Caller<LibraryService> libraryService;

    private ManagedInstance<TileWidget> tileWidgets;

    private Caller<ExamplesService> examplesService;

    private Event<NotificationEvent> notificationEvent;

    private Event<ProjectContextChangeEvent> projectContextChangeEvent;

    private Map<ExampleProject, TileWidget> projectWidgetsByName;

    @Inject
    public ImportProjectsScreen(final View view,
                                final LibraryPlaces libraryPlaces,
                                final Caller<LibraryService> libraryService,
                                final ManagedInstance<TileWidget> tileWidgets,
                                final Caller<ExamplesService> examplesService,
                                final Event<NotificationEvent> notificationEvent,
                                final Event<ProjectContextChangeEvent> projectContextChangeEvent) {
        this.view = view;
        this.libraryPlaces = libraryPlaces;
        this.libraryService = libraryService;
        this.tileWidgets = tileWidgets;
        this.examplesService = examplesService;
        this.notificationEvent = notificationEvent;
        this.projectContextChangeEvent = projectContextChangeEvent;
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        view.init(this);

        final String title = placeRequest.getParameter("title",
                                                       view.getTrySamplesLabel());
        view.setTitle(title);

        final String repositoryUrl = placeRequest.getParameter("repositoryUrl",
                                                               null);

        view.showBusyIndicator(view.getLoadingMessage());
        libraryService.call((Set<ExampleProject> projects) -> {
                                view.hideBusyIndicator();
                                if (projects == null || projects.isEmpty()) {
                                    showNoProjects();
                                    return;
                                }

                                projectWidgetsByName = new HashMap<>();
                                projects.forEach(project -> {
                                    TileWidget projectWidget = createProjectWidget(project);
                                    projectWidgetsByName.put(project,
                                                             projectWidget);
                                });

                                updateView(projectWidgetsByName.values());
                            },
                            new DefaultErrorCallback() {
                                @Override
                                public boolean error(final Message message,
                                                     final Throwable throwable) {
                                    view.hideBusyIndicator();
                                    showNoProjects();
                                    return super.error(message,
                                                       throwable);
                                }
                            }).getProjects(repositoryUrl);
    }

    private TileWidget createProjectWidget(final ExampleProject project) {
        TileWidget tileWidget = tileWidgets.get();
        tileWidget.init(project.getName(),
                        project.getDescription(),
                        null,
                        null,
                        selectCommand(tileWidget));
        return tileWidget;
    }

    private void showNoProjects() {
        notificationEvent.fire(new NotificationEvent(view.getNoProjectsToImportMessage(),
                                                     NotificationEvent.NotificationType.ERROR));
        libraryPlaces.goToLibrary();
    }

    private void updateView(final Collection<TileWidget> projectWidgets) {
        view.clearProjects();
        final List<TileWidget> sortedProjectWidgets = sortProjectWidgets(projectWidgets);
        sortedProjectWidgets.stream().forEach(projectWidget -> {
            view.addProject(projectWidget.getView().getElement());
        });
    }

    private List<TileWidget> sortProjectWidgets(final Collection<TileWidget> projectWidgets) {
        final List<TileWidget> sortedProjectWidgets = new ArrayList<>(projectWidgets);
        Collections.sort(sortedProjectWidgets,
                         Comparator.comparing(o -> o.getLabel().toUpperCase()));
        return sortedProjectWidgets;
    }

    private Command selectCommand(final TileWidget tileWidget) {
        return () -> tileWidget.setSelected(!tileWidget.isSelected());
    }

    public List<TileWidget> filterProjects(final String filter) {
        List<TileWidget> filteredProjectWidgets = projectWidgetsByName.entrySet().stream()
                .filter(p -> p.getKey().getName().toUpperCase().contains(filter.toUpperCase()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        updateView(filteredProjectWidgets);

        return filteredProjectWidgets;
    }

    public void ok() {
        final List<ExampleProject> projects = projectWidgetsByName.entrySet().stream()
                .filter(p -> p.getValue().isSelected())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (projects.isEmpty()) {
            notificationEvent.fire(new NotificationEvent(view.getNoProjectsSelectedMessage(),
                                                         NotificationEvent.NotificationType.ERROR));
            return;
        }

        view.showBusyIndicator(view.getImportingMessage());
        examplesService.call((ProjectContextChangeEvent contextChangeEvent) -> {
                                 view.hideBusyIndicator();
                                 notificationEvent.fire(new NotificationEvent(view.getImportProjectsSuccessMessage(),
                                                                              NotificationEvent.NotificationType.SUCCESS));
                                 projectContextChangeEvent.fire(contextChangeEvent);
                             },
                             new HasBusyIndicatorDefaultErrorCallback(view)).setupExamples(new ExampleOrganizationalUnit(libraryPlaces.getSelectedOrganizationalUnit().getName()),
                                                                                           new ExampleTargetRepository(libraryPlaces.getSelectedRepository().getAlias()),
                                                                                           libraryPlaces.getSelectedBranch(),
                                                                                           projects);
    }

    public void cancel() {
        libraryPlaces.goToLibrary();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Try Samples Screen";
    }

    @WorkbenchPartView
    public View getView() {
        return view;
    }
}

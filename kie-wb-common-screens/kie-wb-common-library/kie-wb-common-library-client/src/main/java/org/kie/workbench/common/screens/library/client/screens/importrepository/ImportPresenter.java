/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.library.client.screens.importrepository;

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
import javax.enterprise.inject.Produces;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.examples.model.ExampleOrganizationalUnit;
import org.kie.workbench.common.screens.examples.model.ExampleProject;
import org.kie.workbench.common.screens.examples.service.ExamplesService;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.widgets.common.TileWidget;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.workbench.common.screens.library.client.screens.importrepository.Source.Kind.EXAMPLE;
import static org.kie.workbench.common.screens.library.client.screens.importrepository.Source.Kind.EXTERNAL;

public abstract class ImportPresenter {

    private static final class ExamplesImportPresenter extends ImportPresenter {

        private Caller<ExamplesService> examplesService;

        private ExamplesImportPresenter(View view,
                                        LibraryPlaces libraryPlaces,
                                        Caller<LibraryService> libraryService,
                                        ManagedInstance<TileWidget> tileWidgets,
                                        Caller<ExamplesService> examplesService,
                                        WorkspaceProjectContext projectContext,
                                        Event<NotificationEvent> notificationEvent,
                                        Event<WorkspaceProjectContextChangeEvent> projectContextChangeEvent) {
            super(view,
                  libraryPlaces,
                  libraryService,
                  tileWidgets,
                  projectContext,
                  notificationEvent,
                  projectContextChangeEvent);
            this.examplesService = examplesService;
        }

        @Override
        protected void loadProjects(PlaceRequest placeRequest, RemoteCallback<Set<ExampleProject>> callback) {
            view.showBusyIndicator(view.getLoadingMessage());
            libraryService.call(callback, loadingErrorCallback()).getExampleProjects();
        }

        @Override
        protected void importProjects(List<ExampleProject> projects,
                                      RemoteCallback<WorkspaceProjectContextChangeEvent> callback,
                                      ErrorCallback<Message> errorCallback) {
            examplesService.call(callback, errorCallback).setupExamples(new ExampleOrganizationalUnit(activeOrganizationalUnit().getName()),
                                                                        projects);
        }
    }

    private static final class ExternalImportPresenter extends ImportPresenter {

        private ExternalImportPresenter(View view,
                                       LibraryPlaces libraryPlaces,
                                       Caller<LibraryService> libraryService,
                                       ManagedInstance<TileWidget> tileWidgets,
                                       WorkspaceProjectContext projectContext,
                                       Event<NotificationEvent> notificationEvent,
                                       Event<WorkspaceProjectContextChangeEvent> projectContextChangeEvent) {
            super(view,
                  libraryPlaces,
                  libraryService,
                  tileWidgets,
                  projectContext,
                  notificationEvent,
                  projectContextChangeEvent);
        }

        @Override
        protected void loadProjects(PlaceRequest placeRequest, RemoteCallback<Set<ExampleProject>> callback) {
            // Projects are loaded by CDI event that calls setupEvents. Nothing to do here.
        }

        @Override
        protected void importProjects(List<ExampleProject> projects,
                                      RemoteCallback<WorkspaceProjectContextChangeEvent> callback,
                                      ErrorCallback<Message> errorCallback) {
            final OrganizationalUnit activeOU = activeOrganizationalUnit();
            libraryService.call((List<WorkspaceProject> importedProjects) -> {
                if (importedProjects.size() == 1) {
                    final WorkspaceProject importedProject = importedProjects.get(0);
                    callback.callback(new WorkspaceProjectContextChangeEvent(importedProject, importedProject.getMainModule()));
                } else {
                    callback.callback(new WorkspaceProjectContextChangeEvent(activeOU));
                }
            }, errorCallback).importProjects(activeOU, projects);
        }

    }

    public interface View extends UberElement<ImportPresenter>,
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

    @Produces @Source(EXAMPLE)
    public static ImportPresenter examplesImportPresenter(final View view,
                                                          final LibraryPlaces libraryPlaces,
                                                          final Caller<LibraryService> libraryService,
                                                          final ManagedInstance<TileWidget> tileWidgets,
                                                          final Caller<ExamplesService> examplesService,
                                                          final WorkspaceProjectContext projectContext,
                                                          final Event<NotificationEvent> notificationEvent,
                                                          final Event<WorkspaceProjectContextChangeEvent> projectContextChangeEvent) {
        return new ExamplesImportPresenter(view,
                                           libraryPlaces,
                                           libraryService,
                                           tileWidgets,
                                           examplesService,
                                           projectContext,
                                           notificationEvent,
                                           projectContextChangeEvent);
    }

    @Produces @Source(EXTERNAL)
    public static ImportPresenter externalImportPresenter(final View view,
                                                          final LibraryPlaces libraryPlaces,
                                                          final Caller<LibraryService> libraryService,
                                                          final ManagedInstance<TileWidget> tileWidgets,
                                                          final WorkspaceProjectContext projectContext,
                                                          final Event<NotificationEvent> notificationEvent,
                                                          final Event<WorkspaceProjectContextChangeEvent> projectContextChangeEvent) {
        return new ExternalImportPresenter(view,
                                           libraryPlaces,
                                           libraryService,
                                           tileWidgets,
                                           projectContext,
                                           notificationEvent,
                                           projectContextChangeEvent);
    }

    protected final View view;

    protected final LibraryPlaces libraryPlaces;

    protected final Caller<LibraryService> libraryService;

    protected final ManagedInstance<TileWidget> tileWidgets;

    protected final WorkspaceProjectContext projectContext;
    protected final Event<NotificationEvent> notificationEvent;

    protected final Event<WorkspaceProjectContextChangeEvent> projectContextChangeEvent;

    protected Map<ExampleProject, TileWidget> projectWidgetsByName;

    public ImportPresenter(final View view,
                           final LibraryPlaces libraryPlaces,
                           final Caller<LibraryService> libraryService,
                           final ManagedInstance<TileWidget> tileWidgets,
                           final WorkspaceProjectContext projectContext,
                           final Event<NotificationEvent> notificationEvent,
                           final Event<WorkspaceProjectContextChangeEvent> projectContextChangeEvent) {
        this.view = view;
        this.libraryPlaces = libraryPlaces;
        this.libraryService = libraryService;
        this.tileWidgets = tileWidgets;
        this.projectContext = projectContext;
        this.notificationEvent = notificationEvent;
        this.projectContextChangeEvent = projectContextChangeEvent;
    }

    public void onStartup(final PlaceRequest placeRequest) {
        view.init(this);

        final String title = placeRequest.getParameter("title",
                                                       view.getTrySamplesLabel());
        view.setTitle(title);

        loadProjects(placeRequest, projects -> {
                                 view.hideBusyIndicator();
                                 setupProjects(projects);
                             });
    }

    protected abstract void loadProjects(final PlaceRequest placeRequest, final RemoteCallback<Set<ExampleProject>> callback);

    protected abstract void importProjects(List<ExampleProject> projects,
                                           RemoteCallback<WorkspaceProjectContextChangeEvent> callback,
                                           ErrorCallback<Message> errorCallback);

    protected DefaultErrorCallback loadingErrorCallback() {
        return new DefaultErrorCallback() {
            @Override
            public boolean error(final Message message,
                                 final Throwable throwable) {
                view.hideBusyIndicator();
                showNoProjects();
                return super.error(message,
                                   throwable);
            }
        };
    }

    public void setupEvent(final ImportProjectsSetupEvent event) {
        setupProjects(event.getProjects());
    }

    private void setupProjects(final Set<ExampleProject> projects) {
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
        importProjects(projects, event -> {
            view.hideBusyIndicator();
            notificationEvent.fire(new NotificationEvent(view.getImportProjectsSuccessMessage(),
                                                         NotificationEvent.NotificationType.SUCCESS));
            projectContextChangeEvent.fire(event);
            // In this case we've imported multiple projects, so just go to the space screen.
            if (event.getWorkspaceProject() == null) {
                libraryPlaces.goToLibrary();
            }
        }, new HasBusyIndicatorDefaultErrorCallback(view));
    }

    public void cancel() {
        libraryPlaces.goToLibrary();
    }

    public View getView() {
        return view;
    }

    protected OrganizationalUnit activeOrganizationalUnit() {
        return projectContext.getActiveOrganizationalUnit()
                             .orElseThrow(() -> new IllegalStateException("Cannot setup examples without an active organizational unit."));
    }
}

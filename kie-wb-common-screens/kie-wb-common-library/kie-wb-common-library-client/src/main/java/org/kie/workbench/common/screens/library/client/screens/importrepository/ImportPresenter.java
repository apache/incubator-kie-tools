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

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.examples.model.ImportProject;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.preferences.ImportProjectsPreferences;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.widgets.example.ExampleProjectWidget;
import org.kie.workbench.common.screens.library.client.widgets.example.ExampleProjectWidgetContainer;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

public abstract class ImportPresenter implements ExampleProjectWidgetContainer {

    public interface View extends UberElement<ImportPresenter>,
                                  HasBusyIndicator {

        void setTitle(String title);

        void clearProjects();

        void addProject(HTMLElement project);

        String getNumberOfAssetsMessage(int numberOfAssets);

        String getNoProjectsToImportMessage();

        String getImportingMessage();

        String getLoadingMessage();

        String getNoProjectsSelectedMessage();

        String getImportProjectsSuccessMessage();

        String getTitle();
    }

    protected final View view;

    protected final LibraryPlaces libraryPlaces;

    protected final ManagedInstance<ExampleProjectWidget> tileWidgets;

    protected final WorkspaceProjectContext projectContext;
    protected final Event<NotificationEvent> notificationEvent;

    protected final Event<WorkspaceProjectContextChangeEvent> projectContextChangeEvent;
    protected final Elemental2DomUtil elemental2DomUtil;
    protected final ImportProjectsPreferences importProjectsPreferences;
    protected final Caller<LibraryService> libraryService;
    private String title;
    private boolean multipleProjectSelectionEnabled = false;

    protected Map<ImportProject, ExampleProjectWidget> projectWidgetsByProject;

    public ImportPresenter(final View view,
                           final LibraryPlaces libraryPlaces,
                           final ManagedInstance<ExampleProjectWidget> tileWidgets,
                           final WorkspaceProjectContext projectContext,
                           final Event<NotificationEvent> notificationEvent,
                           final Event<WorkspaceProjectContextChangeEvent> projectContextChangeEvent,
                           final Elemental2DomUtil elemental2DomUtil,
                           final ImportProjectsPreferences importProjectsPreferences,
                           final Caller<LibraryService> libraryService,
                           final String title) {
        this.view = view;
        this.libraryPlaces = libraryPlaces;
        this.tileWidgets = tileWidgets;
        this.projectContext = projectContext;
        this.notificationEvent = notificationEvent;
        this.projectContextChangeEvent = projectContextChangeEvent;
        this.elemental2DomUtil = elemental2DomUtil;
        this.importProjectsPreferences = importProjectsPreferences;
        this.libraryService = libraryService;
        this.title = title;
    }

    public void onStartup(final PlaceRequest placeRequest) {
        libraryService.call((Boolean isClustered) -> importProjectsPreferences.load(loadedImportProjectsPreferences -> {
            setMultipleProjectSelectionEnabled(!isClustered || loadedImportProjectsPreferences.isMultipleProjectsImportOnClusterEnabled());

            view.init(this);

            final String title = placeRequest.getParameter("title",
                                                           this.title);
            view.setTitle(title);

            loadProjects(placeRequest,
                         projects -> {
                             view.hideBusyIndicator();
                             setupProjects(projects);
                         });
        }, error -> {
            throw new RuntimeException(error);
        })).isClustered();
    }

    protected abstract void loadProjects(final PlaceRequest placeRequest,
                                         final RemoteCallback<Set<ImportProject>> callback);

    protected abstract void importProjects(List<ImportProject> projects,
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

    public void setupProjects(final Set<ImportProject> projects) {
        if (projects == null || projects.isEmpty()) {
            showNoProjects();
            return;
        }

        projectWidgetsByProject = new HashMap<>();
        projects.forEach(project -> {
            ExampleProjectWidget projectWidget = createProjectWidget(project);
            projectWidgetsByProject.put(project,
                                        projectWidget);
        });

        updateView(projectWidgetsByProject.values());
    }

    private ExampleProjectWidget createProjectWidget(final ImportProject project) {
        ExampleProjectWidget tileWidget = tileWidgets.get();
        tileWidget.init(project, this);
        return tileWidget;
    }

    private void showNoProjects() {
        notificationEvent.fire(new NotificationEvent(view.getNoProjectsToImportMessage(),
                                                     NotificationEvent.NotificationType.ERROR));
        libraryPlaces.goToLibrary();
    }

    private void updateView(final Collection<ExampleProjectWidget> projectWidgets) {
        view.clearProjects();
        final List<ExampleProjectWidget> sortedProjectWidgets = sortProjectWidgets(projectWidgets);
        sortedProjectWidgets.stream().forEach(projectWidget -> {
            view.addProject(elemental2DomUtil.asHTMLElement(projectWidget.getView().getElement()));
        });
    }

    private List<ExampleProjectWidget> sortProjectWidgets(final Collection<ExampleProjectWidget> projectWidgets) {
        final List<ExampleProjectWidget> sortedProjectWidgets = new ArrayList<>(projectWidgets);
        Collections.sort(sortedProjectWidgets,
                         Comparator.comparing(o -> o.getName().toUpperCase()));
        return sortedProjectWidgets;
    }

    private Command selectCommand(final ExampleProjectWidget tileWidget) {
        return () -> tileWidget.setSelected(!tileWidget.isSelected());
    }

    public List<ExampleProjectWidget> filterProjects(final String filter) {
        List<ExampleProjectWidget> filteredProjectWidgets = projectWidgetsByProject.entrySet().stream()
                .filter(p -> p.getKey().getName().toUpperCase().contains(filter.toUpperCase()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        updateView(filteredProjectWidgets);

        return filteredProjectWidgets;
    }

    public void ok() {
        final List<ImportProject> projects = projectWidgetsByProject.entrySet().stream()
                .filter(p -> p.getValue().isSelected())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (projects.isEmpty()) {
            notificationEvent.fire(new NotificationEvent(view.getNoProjectsSelectedMessage(),
                                                         NotificationEvent.NotificationType.ERROR));
            return;
        }

        view.showBusyIndicator(view.getImportingMessage());
        importProjects(projects,
                       event -> {
                           view.hideBusyIndicator();
                           notificationEvent.fire(new NotificationEvent(view.getImportProjectsSuccessMessage(),
                                                                        NotificationEvent.NotificationType.SUCCESS));
                           projectContextChangeEvent.fire(event);
                           // In this case we've imported multiple projects, so just go to the space screen.
                           if (event.getWorkspaceProject() == null) {
                               libraryPlaces.goToLibrary();
                           }
                       },
                       new HasBusyIndicatorDefaultErrorCallback(view));
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

    @Override
    public void selectProject(final ExampleProjectWidget selectedWidget) {
        projectWidgetsByProject.values().forEach(widget -> {
            if (widget.equals(selectedWidget)) {
                widget.select();
            } else if (!multipleProjectSelectionEnabled) {
                widget.unselect();
            }
        });
    }

    public void setMultipleProjectSelectionEnabled(final boolean multipleProjectSelectionEnabled) {
        this.multipleProjectSelectionEnabled = multipleProjectSelectionEnabled;
    }

    public Map<ImportProject, ExampleProjectWidget> getProjectWidgetsByProject() {
        return projectWidgetsByProject;
    }
}

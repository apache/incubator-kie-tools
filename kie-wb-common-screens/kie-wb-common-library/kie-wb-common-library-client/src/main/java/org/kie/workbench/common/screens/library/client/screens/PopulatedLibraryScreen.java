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

package org.kie.workbench.common.screens.library.client.screens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.library.api.LibraryInfo;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.ProjectAssetListUpdated;
import org.kie.workbench.common.screens.library.api.ProjectCountUpdate;
import org.kie.workbench.common.screens.library.api.Routed;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.widgets.common.TileWidget;
import org.kie.workbench.common.screens.library.client.widgets.library.AddProjectButtonPresenter;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.mvp.Command;
import org.uberfire.spaces.Space;

public class PopulatedLibraryScreen {

    public interface View extends UberElement<PopulatedLibraryScreen>,
                                  HasBusyIndicator {

        void clearProjects();

        void addProject(HTMLElement project);

        void addAction(HTMLElement action);

        void clearFilterText();

        String getNumberOfAssetsMessage(int numberOfAssets);
    }

    private View view;

    private LibraryPlaces libraryPlaces;

    private Caller<LibraryService> libraryService;

    private ProjectController projectController;

    private WorkspaceProjectContext projectContext;
    private Event<ProjectCountUpdate> projectCountUpdateEvent;

    private ManagedInstance<TileWidget> tileWidgets;

    private AddProjectButtonPresenter addProjectButtonPresenter;

    List<WorkspaceProject> projects;

    @Inject
    public PopulatedLibraryScreen(final View view,
                                  final LibraryPlaces libraryPlaces,
                                  final Caller<LibraryService> libraryService,
                                  final ProjectController projectController,
                                  final WorkspaceProjectContext projectContext,
                                  final ManagedInstance<TileWidget> tileWidgets,
                                  final AddProjectButtonPresenter addProjectButtonPresenter,
                                  final Event<ProjectCountUpdate> projectCountUpdateEvent) {
        this.view = view;
        this.libraryPlaces = libraryPlaces;
        this.libraryService = libraryService;
        this.projectController = projectController;
        this.projectContext = projectContext;
        this.projectCountUpdateEvent = projectCountUpdateEvent;
        this.projects = Collections.emptyList();
        this.tileWidgets = tileWidgets;
        this.addProjectButtonPresenter = addProjectButtonPresenter;
    }

    @PostConstruct
    public void setup() {
        view.init(this);
        if (userCanCreateProjects()) {
            view.addAction(addProjectButtonPresenter.getView().getElement());
        }

        refreshProjects();
    }

    void refreshProjects() {
        libraryService.call((RemoteCallback<LibraryInfo>) this::updateLibrary)
                .getLibraryInfo(getOrganizationalUnit());
    }

    private OrganizationalUnit getOrganizationalUnit() {
        return projectContext.getActiveOrganizationalUnit()
                .orElseThrow(() -> new IllegalStateException("Cannot get library info without an active organizational unit."));
    }

    private void updateLibrary(final LibraryInfo libraryInfo) {
        projects = new ArrayList<>(libraryInfo.getProjects());
        view.clearFilterText();
        setupProjects();
    }

    private void setupProjects() {
        projects.sort(Comparator.comparing(p -> p.getName().toUpperCase()));
        updateView(projects);
    }

    public List<WorkspaceProject> filterProjects(final String filter) {
        List<WorkspaceProject> filteredProjects = projects.stream()
                .filter(p -> p.getName().toUpperCase().contains(filter.toUpperCase()))
                .collect(Collectors.toList());

        updateView(filteredProjects);

        return filteredProjects;
    }

    private void updateView(final List<WorkspaceProject> projects) {
        view.clearProjects();
        this.projectCountUpdateEvent.fire(new ProjectCountUpdate(projects.size(),
                                                                 this.getOrganizationalUnit().getSpace()));
        projects.stream().forEach(project -> {

            final TileWidget tileWidget = createProjectWidget(project);
            view.addProject(tileWidget.getView().getElement());
        });

    }

    private TileWidget createProjectWidget(final WorkspaceProject project) {
        final TileWidget tileWidget = tileWidgets.get();

        if (project.getMainModule() != null) {
            final POM pom = project.getMainModule().getPom();
            tileWidget.init(project.getName(),
                            pom != null ? pom.getDescription() : "",
                            String.valueOf(project.getMainModule().getNumberOfAssets()),
                            view.getNumberOfAssetsMessage(project.getMainModule().getNumberOfAssets()),
                            selectCommand(project));
        } else {
            tileWidget.init(project.getName(),
                            "",
                            "0",
                            "0",
                            selectCommand(project));
        }
        return tileWidget;
    }

    public boolean userCanCreateProjects() {
        return projectController.canCreateProjects(libraryPlaces.getActiveSpace());
    }

    public int getProjectsCount() {
        return projects.size();
    }

    Command selectCommand(final WorkspaceProject project) {
        return () -> libraryPlaces.goToProject(project);
    }

    public View getView() {
        return view;
    }

    public void onNewProjectEvent(@Observes NewProjectEvent e) {

        projectContext.getActiveOrganizationalUnit().ifPresent(p -> {
            if (eventOnCurrentSpace(p,
                                    e.getWorkspaceProject().getSpace())) {
                refreshProjects();
            }
        });
    }

    public void onRepositoryRemovedEvent(@Observes RepositoryRemovedEvent e) {
        projectContext.getActiveOrganizationalUnit().ifPresent(p -> {
            if (eventOnCurrentSpace(p,
                                    e.getRepository().getSpace())) {
                refreshProjects();
            }
        });
    }

    boolean eventOnCurrentSpace(OrganizationalUnit p,
                                Space space) {
        return p.getSpace().getName().equalsIgnoreCase(space.getName());
    }

    public void onAssetListUpdated(@Observes @Routed ProjectAssetListUpdated event) {
        libraryService.call((LibraryInfo libraryInfo) -> {
            boolean anyMatch = libraryInfo.getProjects()
                    .stream()
                    .anyMatch(workspaceProject -> event.getProject().getRepository().getIdentifier().equals(workspaceProject.getRepository().getIdentifier()));
            if (anyMatch) {
                refreshProjects();
            }
        }).getLibraryInfo(this.getOrganizationalUnit());
    }
}

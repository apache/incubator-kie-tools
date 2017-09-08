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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.library.api.LibraryInfo;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.ProjectInfo;
import org.kie.workbench.common.screens.library.api.search.FilterUpdateEvent;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.kie.workbench.common.screens.library.client.perspective.LibraryPerspective;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

@WorkbenchScreen(identifier = LibraryPlaces.LIBRARY_SCREEN,
        owningPerspective = LibraryPerspective.class)
public class LibraryScreen {

    public interface View extends UberElement<LibraryScreen> {

        void clearProjects();

        void addProject(String project,
                        Command details,
                        Command select);

        void clearFilterText();

        void setFilterName(String name);
    }

    private View view;

    private PlaceManager placeManager;

    private LibraryPlaces libraryPlaces;

    private Event<ProjectDetailEvent> projectDetailEvent;

    private Caller<LibraryService> libraryService;

    private ProjectController projectController;

    List<Project> projects;

    @Inject
    public LibraryScreen(final View view,
                         final PlaceManager placeManager,
                         final LibraryPlaces libraryPlaces,
                         final Event<ProjectDetailEvent> projectDetailEvent,
                         final Caller<LibraryService> libraryService,
                         final ProjectController projectController) {
        this.view = view;
        this.placeManager = placeManager;
        this.libraryPlaces = libraryPlaces;
        this.projectDetailEvent = projectDetailEvent;
        this.libraryService = libraryService;
        this.projectController = projectController;
        this.projects = Collections.emptyList();
    }

    @PostConstruct
    public void setup() {
        Repository selectedRepository = libraryPlaces.getSelectedRepository();
        String selectedBranch = libraryPlaces.getSelectedBranch();

        libraryService.call((RemoteCallback<LibraryInfo>) this::updateLibrary)
                .getLibraryInfo(selectedRepository,
                                selectedBranch);

        placeManager.closePlace(LibraryPlaces.EMPTY_LIBRARY_SCREEN);
    }

    private void updateLibrary(final LibraryInfo libraryInfo) {
        projects = libraryInfo.getProjects();
        view.clearFilterText();
        setupProjects();
    }

    private void setupProjects() {
        if (projectController.canReadProjects()) {
            projects = projects.stream()
                    .filter(p -> projectController.canReadProject(p))
                    .collect(Collectors.toList());
            projects.sort((p1, p2) -> p1.getProjectName().compareTo(p2.getProjectName()));

            updateView(projects);
        }
    }

    public List<Project> filterProjects(final String filter) {
        List<Project> filteredProjects = projects.stream()
                .filter(p -> p.getProjectName().toUpperCase().contains(filter.toUpperCase()))
                .collect(Collectors.toList());

        updateView(filteredProjects);

        return filteredProjects;
    }

    private void updateView(final List<Project> projects) {
        view.clearProjects();
        projects.stream().forEach(p -> view.addProject(p.getProjectName(),
                                                       detailsCommand(p),
                                                       selectCommand(p)));
    }

    public void filterUpdate(@Observes final FilterUpdateEvent event) {
        view.setFilterName(event.getName());
        filterProjects(event.getName());
    }

    public boolean userCanCreateProjects() {
        return projectController.canCreateProjects();
    }

    Command selectCommand(final Project project) {
        return () -> {
            final ProjectInfo projectInfo = getProjectInfo(project);
            libraryPlaces.goToProject(projectInfo);
        };
    }

    Command detailsCommand(final Project project) {
        return () -> {
            final ProjectInfo projectInfo = getProjectInfo(project);
            projectDetailEvent.fire(new ProjectDetailEvent(projectInfo));
        };
    }

    private ProjectInfo getProjectInfo(final Project project) {
        return new ProjectInfo(libraryPlaces.getSelectedOrganizationalUnit(),
                               libraryPlaces.getSelectedRepository(),
                               libraryPlaces.getSelectedBranch(),
                               project);
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Library Screen";
    }

    @WorkbenchPartView
    public UberElement<LibraryScreen> getView() {
        return view;
    }
}

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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.library.api.LibraryInfo;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.ProjectInfo;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.widgets.common.TileWidget;
import org.kie.workbench.common.screens.library.client.widgets.library.AddProjectButtonPresenter;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

public class PopulatedLibraryScreen {

    public interface View extends UberElement<PopulatedLibraryScreen> {

        void clearProjects();

        void addProject(HTMLElement project);

        void addAction(HTMLElement action);

        void clearFilterText();

        String getNumberOfAssetsMessage(int numberOfAssets);
    }

    private View view;

    private LibraryPlaces libraryPlaces;

    private Event<ProjectDetailEvent> projectDetailEvent;

    private Caller<LibraryService> libraryService;

    private ProjectController projectController;

    private ManagedInstance<TileWidget> tileWidgets;

    private AddProjectButtonPresenter addProjectButtonPresenter;

    List<Project> projects;

    @Inject
    public PopulatedLibraryScreen(final View view,
                                  final LibraryPlaces libraryPlaces,
                                  final Event<ProjectDetailEvent> projectDetailEvent,
                                  final Caller<LibraryService> libraryService,
                                  final ProjectController projectController,
                                  final ManagedInstance<TileWidget> tileWidgets,
                                  final AddProjectButtonPresenter addProjectButtonPresenter) {
        this.view = view;
        this.libraryPlaces = libraryPlaces;
        this.projectDetailEvent = projectDetailEvent;
        this.libraryService = libraryService;
        this.projectController = projectController;
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

        final Repository selectedRepository = libraryPlaces.getSelectedRepository();
        final String selectedBranch = libraryPlaces.getSelectedBranch();

        libraryService.call((RemoteCallback<LibraryInfo>) this::updateLibrary)
                .getLibraryInfo(selectedRepository,
                                selectedBranch);
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
            projects.sort((p1, p2) -> p1.getProjectName().toUpperCase().compareTo(p2.getProjectName().toUpperCase()));

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
        projects.stream().forEach(project -> {
            final TileWidget tileWidget = createProjectWidget(project);
            view.addProject(tileWidget.getView().getElement());
        });
    }

    private TileWidget createProjectWidget(final Project project) {
        final TileWidget tileWidget = tileWidgets.get();
        final POM pom = project.getPom();
        tileWidget.init(project.getProjectName(),
                        pom != null ? pom.getDescription() : "",
                        String.valueOf(project.getNumberOfAssets()),
                        view.getNumberOfAssetsMessage(project.getNumberOfAssets()),
                        selectCommand(project));
        return tileWidget;
    }

    public boolean userCanCreateProjects() {
        return projectController.canCreateProjects();
    }

    public int getProjectsCount() {
        return projects.size();
    }

    Command selectCommand(final Project project) {
        return () -> {
            final ProjectInfo projectInfo = getProjectInfo(project);
            libraryPlaces.goToProject(projectInfo);
        };
    }

    private ProjectInfo getProjectInfo(final Project project) {
        return new ProjectInfo(libraryPlaces.getSelectedOrganizationalUnit(),
                               libraryPlaces.getSelectedRepository(),
                               libraryPlaces.getSelectedBranch(),
                               project);
    }

    public View getView() {
        return view;
    }
}

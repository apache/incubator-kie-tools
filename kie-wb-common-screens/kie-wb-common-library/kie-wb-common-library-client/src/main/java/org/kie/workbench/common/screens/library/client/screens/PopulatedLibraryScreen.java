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

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.Predicate;
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
import org.guvnor.structure.repositories.Repository;
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

    private static final Comparator<TileWidget<WorkspaceProject>> comparator = Comparator.comparing(tile -> tile.getContent().getName().toUpperCase());

    public interface View extends UberElement<PopulatedLibraryScreen>,
                                  HasBusyIndicator {

        void clearProjects();

        void addProject(TileWidget<WorkspaceProject> tile);

        void addProject(TileWidget<WorkspaceProject> tileToAdd, TileWidget<WorkspaceProject> tileAfter);

        void removeProject(TileWidget<WorkspaceProject> tile);

        void addAction(HTMLElement action);

        void clearFilterText();
    }

    private View view;

    private LibraryPlaces libraryPlaces;

    private Caller<LibraryService> libraryService;

    private ProjectController projectController;

    private WorkspaceProjectContext projectContext;
    private Event<ProjectCountUpdate> projectCountUpdateEvent;

    private ManagedInstance<TileWidget<WorkspaceProject>> tileWidgets;

    private AddProjectButtonPresenter addProjectButtonPresenter;

    TreeSet<TileWidget<WorkspaceProject>> libraryTiles = new TreeSet<>(comparator);

    @Inject
    public PopulatedLibraryScreen(final View view,
                                  final LibraryPlaces libraryPlaces,
                                  final Caller<LibraryService> libraryService,
                                  final ProjectController projectController,
                                  final WorkspaceProjectContext projectContext,
                                  final ManagedInstance<TileWidget<WorkspaceProject>> tileWidgets,
                                  final AddProjectButtonPresenter addProjectButtonPresenter,
                                  final Event<ProjectCountUpdate> projectCountUpdateEvent) {
        this.view = view;
        this.libraryPlaces = libraryPlaces;
        this.libraryService = libraryService;
        this.projectController = projectController;
        this.projectContext = projectContext;
        this.projectCountUpdateEvent = projectCountUpdateEvent;
        this.tileWidgets = tileWidgets;
        this.addProjectButtonPresenter = addProjectButtonPresenter;
    }

    @PostConstruct
    public void setup() {
        view.init(this);
        if (userCanCreateProjects()) {
            view.addAction(addProjectButtonPresenter.getView().getElement());
        }

        libraryService.call((RemoteCallback<LibraryInfo>) this::setupLibrary)
                .getLibraryInfo(getOrganizationalUnit());
    }

    private OrganizationalUnit getOrganizationalUnit() {
        return projectContext.getActiveOrganizationalUnit()
                .orElseThrow(() -> new IllegalStateException("Cannot get library info without an active organizational unit."));
    }

    private void setupLibrary(final LibraryInfo libraryInfo) {
        view.clearFilterText();

        libraryTiles.clear();

        libraryInfo.getProjects().stream()
                .map(this::createProjectWidget)
                .collect(Collectors.toCollection(() -> libraryTiles));

        updateView(libraryTiles);
    }

    public List<TileWidget<WorkspaceProject>> filterProjects(final String filter) {
        List<TileWidget<WorkspaceProject>> filteredProjects = libraryTiles.stream()
                .filter(p -> p.getContent().getName().toUpperCase().contains(filter.toUpperCase()))
                .collect(Collectors.toList());

        updateView(filteredProjects);

        return filteredProjects;
    }

    private void updateView(Collection<TileWidget<WorkspaceProject>> projects) {
        view.clearProjects();

        this.projectCountUpdateEvent.fire(new ProjectCountUpdate(projects.size(),
                                                                 this.getOrganizationalUnit().getSpace()));

        projects.forEach(project -> {
            view.addProject(project);
        });
    }

    private TileWidget<WorkspaceProject> createProjectWidget(final WorkspaceProject project) {
        final TileWidget<WorkspaceProject> tileWidget = tileWidgets.get();

        tileWidget.setContent(project);

        if (project.getMainModule() != null) {
            final POM pom = project.getMainModule().getPom();
            tileWidget.init(project.getName(), pom != null ? pom.getDescription() : "", selectCommand(project));
            updateAssetCount(tileWidget);
        } else {
            tileWidget.init(project.getName(), "", selectCommand(project));
        }

        libraryTiles.add(tileWidget);

        return tileWidget;
    }

    private void updateAssetCount(final TileWidget<WorkspaceProject> tileWidget) {
        libraryService.call((RemoteCallback<Integer>) tileWidget::setNumberOfAssets).getNumberOfAssets(tileWidget.getContent());
    }

    public boolean userCanCreateProjects() {
        return projectController.canCreateProjects(libraryPlaces.getActiveSpace());
    }

    public int getProjectsCount() {
        return libraryTiles.size();
    }

    Command selectCommand(final WorkspaceProject project) {
        return () -> libraryPlaces.goToProject(project);
    }

    public View getView() {
        return view;
    }

    public void onNewProjectEvent(@Observes NewProjectEvent e) {

        projectContext.getActiveOrganizationalUnit().ifPresent(organizationalUnit -> {
            if (eventOnCurrentSpace(organizationalUnit, e.getWorkspaceProject().getSpace())) {

                Optional<TileWidget<WorkspaceProject>> workspaceOptional = findTile(e.getWorkspaceProject());

                // Checking if the project is already there
                if (workspaceOptional.isPresent()) {
                    return;
                }

                TileWidget<WorkspaceProject> tile = createProjectWidget(e.getWorkspaceProject());

                Optional<TileWidget<WorkspaceProject>> optional = Optional.ofNullable(libraryTiles.higher(tile));

                if (optional.isPresent()) {
                    view.addProject(tile, optional.get());
                } else {
                    view.addProject(tile);
                }

                this.projectCountUpdateEvent.fire(new ProjectCountUpdate(libraryTiles.size(), this.getOrganizationalUnit().getSpace()));
            }
        });
    }

    public void onRepositoryRemovedEvent(@Observes RepositoryRemovedEvent e) {
        projectContext.getActiveOrganizationalUnit().ifPresent(p -> {
            if (eventOnCurrentSpace(p, e.getRepository().getSpace())) {
                findTile(e.getRepository()).ifPresent(tile -> {
                    view.removeProject(tile);
                    libraryTiles.remove(tile);
                    tileWidgets.destroy(tile);
                    this.projectCountUpdateEvent.fire(new ProjectCountUpdate(libraryTiles.size(), this.getOrganizationalUnit().getSpace()));
                });
            }
        });
    }

    private Optional<TileWidget<WorkspaceProject>> findTile(WorkspaceProject project) {
        return findTile(tile -> tile.getContent().equals(project));
    }

    private Optional<TileWidget<WorkspaceProject>> findTile(Repository repository) {
        return findTile(tile -> tile.getContent().getRepository().getIdentifier().equals(repository.getIdentifier()));
    }

    private Optional<TileWidget<WorkspaceProject>> findTile(Predicate<TileWidget<WorkspaceProject>> filter) {
        return libraryTiles.stream()
                .filter(filter)
                .findAny();
    }

    boolean eventOnCurrentSpace(OrganizationalUnit organizationalUnit,
                                Space space) {
        return organizationalUnit.getSpace().getName().equalsIgnoreCase(space.getName());
    }

    public void onAssetListUpdated(@Observes @Routed ProjectAssetListUpdated event) {
        if (event.getProject().getSpace().equals(getOrganizationalUnit().getSpace())) {
            findTile(event.getProject())
                    .ifPresent(this::updateAssetCount);
        }
    }
}

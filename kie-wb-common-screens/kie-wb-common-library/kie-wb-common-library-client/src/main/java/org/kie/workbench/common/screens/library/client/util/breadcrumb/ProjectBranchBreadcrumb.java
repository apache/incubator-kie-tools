/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.util.breadcrumb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.NewBranchEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryUpdatedEvent;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.services.datamodel.util.SortHelper;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.client.promise.Promises;
import org.uberfire.ext.widgets.common.client.breadcrumbs.widget.BreadcrumbPresenter;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class ProjectBranchBreadcrumb implements BreadcrumbPresenter {

    public interface View extends UberElemental<ProjectBranchBreadcrumb> {

        String getBranchDeletedMessage(final String branchName);
    }

    private final View view;

    private List<Branch> branches;

    private LibraryPlaces libraryPlaces;

    private Event<NotificationEvent> notificationEvent;

    private ProjectController projectController;

    private Promises promises;

    @Inject
    public ProjectBranchBreadcrumb(final View view,
                                   final LibraryPlaces libraryPlaces,
                                   final Event<NotificationEvent> notificationEvent,
                                   final ProjectController projectController,
                                   final Promises promises) {
        this.view = view;
        this.libraryPlaces = libraryPlaces;
        this.notificationEvent = notificationEvent;
        this.projectController = projectController;
        this.promises = promises;
    }

    public ProjectBranchBreadcrumb setup(final Collection<Branch> branches) {
        final Set<Branch> branchesSet = new HashSet<>();
        final List<Promise<Boolean>> canReadBranchPromises = branches.stream().map(b -> projectController.canReadBranch(libraryPlaces.getActiveWorkspace(),
                                                                                                                        b.getName()).then(canReadBranch -> {
            if (canReadBranch) {
                branchesSet.add(b);
            }
            return promises.resolve(true);
        })).collect(Collectors.toList());

        promises.all(canReadBranchPromises.toArray(new Promise[canReadBranchPromises.size()])).then(ignored -> {
            this.branches = branchesSet.stream().sorted(BRANCH_ALPHABETICAL_ORDER_COMPARATOR).collect(Collectors.toList());
            view.init(this);
            return promises.resolve();
        });

        return this;
    }

    @Override
    public void activate() {
    }

    @Override
    public void deactivate() {
    }

    public Branch getCurrentBranch() {
        return libraryPlaces.getActiveWorkspace().getBranch();
    }

    public List<Branch> getBranches() {
        return branches;
    }

    @Override
    public UberElemental<? extends BreadcrumbPresenter> getView() {
        return view;
    }

    public void onBranchChanged(final Branch branch) {
        libraryPlaces.goToProject(libraryPlaces.getActiveWorkspace(), branch);
    }

    public void newBranchEvent(@Observes final NewBranchEvent newBranchEvent) {
        final User user = newBranchEvent.getUser();
        final Repository repository = newBranchEvent.getRepository();

        if (libraryPlaces.isThisUserAccessingThisRepository(user, repository)) {
            libraryPlaces.goToProject(libraryPlaces.getActiveWorkspace(), repository.getBranch(newBranchEvent.getNewBranchName()).get());
        }
    }

    public void repositoryUpdatedEvent(@Observes final RepositoryUpdatedEvent event) {
        final Repository repository = event.getRepository();
        if (libraryPlaces.isThisRepositoryBeingAccessed(repository)) {
            updateBranches(repository.getBranches());
        }
    }

    private void updateBranches(final Collection<Branch> branches) {
        if (!branches.contains(getCurrentBranch())) {
            notificationEvent.fire(new NotificationEvent(view.getBranchDeletedMessage(getCurrentBranch().getName()),
                                                         NotificationEvent.NotificationType.DEFAULT));

            final Optional<Branch> defaultBranch = libraryPlaces.getActiveWorkspace().getRepository().getDefaultBranch();
            if (defaultBranch.isPresent()) {
                libraryPlaces.goToProject(libraryPlaces.getActiveWorkspace(), defaultBranch.get());
            } else {
                libraryPlaces.goToLibrary();
            }
        } else {
            setup(branches);
        }
    }

    public static final Comparator<Branch> BRANCH_ALPHABETICAL_ORDER_COMPARATOR = (branch1, branch2) ->
            SortHelper.ALPHABETICAL_ORDER_COMPARATOR.compare(branch1.getName(), branch2.getName());
}

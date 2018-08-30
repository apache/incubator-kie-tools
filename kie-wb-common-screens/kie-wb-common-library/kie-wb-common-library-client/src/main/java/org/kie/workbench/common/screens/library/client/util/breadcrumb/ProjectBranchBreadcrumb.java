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

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.NewBranchEvent;
import org.guvnor.structure.repositories.Repository;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.services.datamodel.util.SortHelper;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.ext.widgets.common.client.breadcrumbs.widget.BreadcrumbPresenter;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.spaces.Space;

@Dependent
public class ProjectBranchBreadcrumb implements BreadcrumbPresenter {

    public interface View extends UberElemental<ProjectBranchBreadcrumb> {

    }

    private final View view;

    private List<Branch> branches;

    private LibraryPlaces libraryPlaces;

    private SessionInfo sessionInfo;

    @Inject
    public ProjectBranchBreadcrumb(final View view,
                                   final LibraryPlaces libraryPlaces,
                                   final SessionInfo sessionInfo) {
        this.view = view;
        this.libraryPlaces = libraryPlaces;
        this.sessionInfo = sessionInfo;
    }

    public ProjectBranchBreadcrumb setup(final Collection<Branch> branches) {
        this.branches = branches.stream().sorted(BRANCH_ALPHABETICAL_ORDER_COMPARATOR).collect(Collectors.toList());
        view.init(this);

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
        final Repository repository = newBranchEvent.getRepository();
        final Space space = repository.getSpace();
        final String repositoryAlias = repository.getAlias();

        final Space activeSpace = libraryPlaces.getActiveSpace().getSpace();
        final Repository activeRepository = libraryPlaces.getActiveWorkspace().getRepository();
        final String activeRepositoryAlias = activeRepository.getAlias();

        if (space.equals(activeSpace) && repositoryAlias.equals(activeRepositoryAlias) && sessionInfo.getIdentity().equals(newBranchEvent.getUser())) {
            setup(repository.getBranches());
            libraryPlaces.goToProject(libraryPlaces.getActiveWorkspace(), newBranchEvent.getRepository().getBranch(newBranchEvent.getNewBranchName()).get());
        }
    }

    public static final Comparator<Branch> BRANCH_ALPHABETICAL_ORDER_COMPARATOR = (branch1, branch2) ->
            SortHelper.ALPHABETICAL_ORDER_COMPARATOR.compare(branch1.getName(), branch2.getName());
}

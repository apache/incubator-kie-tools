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

package org.kie.workbench.common.screens.library.client.widgets.library;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

@ApplicationScoped
public class LibraryToolbarPresenter {

    public interface View extends UberElement<LibraryToolbarPresenter> {

        void clearBranches();

        void addBranch(final String branchName);

        String getSelectedBranch();

        void setSelectedBranch(final String branchName);

        void setBranchSelectorVisibility(boolean visible);
    }

    private WorkspaceProjectContext projectContext;
    private Caller<WorkspaceProjectService> projectService;
    private LibraryPlaces libraryPlaces;
    private View view;
    private PlaceManager placeManager;

    public LibraryToolbarPresenter() {
    }

    @Inject
    public LibraryToolbarPresenter(final WorkspaceProjectContext projectContext,
                                   final Caller<WorkspaceProjectService> projectService,
                                   final LibraryPlaces libraryPlaces,
                                   final View view,
                                   final PlaceManager placeManager) {
        this.projectContext = projectContext;
        this.projectService = projectService;
        this.libraryPlaces = libraryPlaces;
        this.view = view;
        this.placeManager = placeManager;

        view.init(this);
    }

    public void init(final Command callback) {
        view.setBranchSelectorVisibility(false);
        callback.execute();
    }

    public void setUpBranches() {
        view.clearBranches();

        projectContext.getActiveWorkspaceProject().ifPresent(proj -> {
            for (final Branch branch : proj.getRepository().getBranches()) {
                view.addBranch(branch.getName());
            }
            view.setSelectedBranch(proj.getBranch().getName());
        });

        setBranchSelectorVisibility();
    }

    void onUpdateSelectedBranch() {
        if (placeManager.closeAllPlacesOrNothing()) {

            Repository repository = projectContext.getActiveWorkspaceProject()
                    .map(workspaceProject -> workspaceProject.getRepository())
                    .orElseThrow(() -> new IllegalStateException("Cannot get repository without an active workspace project"));
            projectService.call(new RemoteCallback<WorkspaceProject>() {
                @Override
                public void callback(WorkspaceProject project) {

                    libraryPlaces.goToProject(project);

                    setBranchSelectorVisibility();
                }
            }).resolveProject(repository.getSpace(), repository.getBranch(view.getSelectedBranch()).get());
        } else {
            setUpBranches();
        }
    }

    private void setBranchSelectorVisibility() {
        view.setBranchSelectorVisibility(isBranchVisible());
    }

    private boolean isBranchVisible() {
        return projectContext.getActiveWorkspaceProject().isPresent() && projectContext.getActiveWorkspaceProject().get().getRepository().getBranches().size() > 1;
    }

    public UberElement<LibraryToolbarPresenter> getView() {
        return view;
    }
}

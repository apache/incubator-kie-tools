/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.screens.explorer.client.widgets;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.events.DeleteProjectEvent;
import org.guvnor.common.services.project.events.NewPackageEvent;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.events.RenameProjectEvent;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.config.SystemRepositoryChangedEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.RepoAddedToOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.RepoRemovedFromOrganizationalUnitEvent;
import org.guvnor.structure.repositories.NewBranchEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentUpdatedEvent;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.explorer.client.utils.Utils;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerContentQuery;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;

public class ActiveContextManager {

    private ActiveContextItems activeContextItems;
    private ActiveContextOptions activeOptions;
    private Caller<ExplorerService> explorerService;
    private AuthorizationManager authorizationManager;
    private transient SessionInfo sessionInfo;
    private PlaceManager placeManager;

    private View view;

    private RemoteCallback<ProjectExplorerContent> contentCallback;

    public ActiveContextManager() {
    }

    @Inject
    public ActiveContextManager(final ActiveContextItems activeContextItems,
                                final ActiveContextOptions activeOptions,
                                final Caller<ExplorerService> explorerService,
                                final AuthorizationManager authorizationManager,
                                final SessionInfo sessionInfo,
                                final PlaceManager placeManager) {
        this.activeContextItems = activeContextItems;
        this.activeOptions = activeOptions;
        this.explorerService = explorerService;
        this.authorizationManager = authorizationManager;
        this.sessionInfo = sessionInfo;
        this.placeManager = placeManager;
    }

    public void init(final View view,
                     final RemoteCallback<ProjectExplorerContent> contentCallback) {
        this.view = view;
        this.contentCallback = contentCallback;
    }

    public void initActiveContext(final String path) {
        view.showBusyIndicator(CommonConstants.INSTANCE.Loading());

        explorerService.call(contentCallback,
                             new HasBusyIndicatorDefaultErrorCallback(view)).getContent(path,
                                                                                        activeOptions.getOptions());
    }

    public void initActiveContext(final OrganizationalUnit organizationalUnit) {

        view.showBusyIndicator(CommonConstants.INSTANCE.Loading());
        refresh(new ProjectExplorerContentQuery(organizationalUnit));
    }

    public void initActiveContext(final OrganizationalUnit organizationalUnit,
                                  final Repository repository,
                                  final String branch) {

        view.showBusyIndicator(CommonConstants.INSTANCE.Loading());
        refresh(new ProjectExplorerContentQuery(organizationalUnit,
                                                repository,
                                                branch));
    }

    public void initActiveContext(final OrganizationalUnit organizationalUnit,
                                  final Repository repository,
                                  final String branch,
                                  final Project project) {

        view.showBusyIndicator(CommonConstants.INSTANCE.Loading());
        refresh(new ProjectExplorerContentQuery(organizationalUnit,
                                                repository,
                                                branch,
                                                project));
    }

    public void initActiveContext(final OrganizationalUnit organizationalUnit,
                                  final Repository repository,
                                  final String branch,
                                  final Project project,
                                  final org.guvnor.common.services.project.model.Package pkg) {
        view.showBusyIndicator(CommonConstants.INSTANCE.Loading());
        refresh(new ProjectExplorerContentQuery(organizationalUnit,
                                                repository,
                                                branch,
                                                project,
                                                pkg));
    }

    private void refresh(final ProjectExplorerContentQuery query) {

        query.setOptions(activeOptions.getOptions());

        explorerService.call(contentCallback,
                             new HasBusyIndicatorDefaultErrorCallback(view)).getContent(query);
    }

    private void refresh(final Project project) {
        refresh(new ProjectExplorerContentQuery(activeContextItems.getActiveOrganizationalUnit(),
                                                activeContextItems.getActiveRepository(),
                                                activeContextItems.getActiveBranch(),
                                                project));
    }

    void refresh() {
        refresh(new ProjectExplorerContentQuery(activeContextItems.getActiveOrganizationalUnit(),
                                                activeContextItems.getActiveRepository(),
                                                activeContextItems.getActiveBranch(),
                                                activeContextItems.getActiveProject(),
                                                activeContextItems.getActivePackage(),
                                                activeContextItems.getActiveFolderItem()));
    }

    private boolean isInActiveBranch(final Project project) {
        return Utils.isInBranch(getCurrentBranchRoot(),
                                project);
    }

    private Path getCurrentBranchRoot() {
        if (activeContextItems.getActiveRepository() == null) {
            return null;
        } else {
            return activeContextItems.getActiveRepository().getBranchRoot(activeContextItems.getActiveBranch());
        }
    }

    public void initActiveContext(final ProjectContext context) {
        initActiveContext(context.getActiveOrganizationalUnit(),
                          context.getActiveRepository(),
                          context.getActiveBranch(),
                          context.getActiveProject(),
                          context.getActivePackage());
    }

    public void onBranchCreated(@Observes final NewBranchEvent event) {
        if (activeContextItems.isTheActiveRepository(event.getRepositoryAlias())) {
            if (activeContextItems.getActiveRepository() instanceof GitRepository) {
                addBranch(activeContextItems.getActiveRepository(),
                          event.getBranchName(),
                          event.getBranchPath());
            }
        }

        if (activeContextItems.getRepositories() != null) {
            for (Repository repository : activeContextItems.getRepositories()) {
                if (repository.getAlias().equals(event.getRepositoryAlias())) {
                    addBranch(repository,
                              event.getBranchName(),
                              event.getBranchPath());
                }
            }
        }
    }

    private void addBranch(final Repository repository,
                           final String branchName,
                           final Path branchPath) {
        ((GitRepository) repository).addBranch(branchName,
                                               branchPath);
        refresh();
    }
}

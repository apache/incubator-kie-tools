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

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.NewBranchEvent;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerContentQuery;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;

public class ActiveContextManager {

    private ActiveContextItems activeContextItems;
    private ActiveContextOptions activeOptions;
    private Caller<ExplorerService> explorerService;

    private View view;

    private RemoteCallback<ProjectExplorerContent> contentCallback;

    public ActiveContextManager() {
    }

    @Inject
    public ActiveContextManager(final ActiveContextItems activeContextItems,
                                final ActiveContextOptions activeOptions,
                                final Caller<ExplorerService> explorerService) {
        this.activeContextItems = activeContextItems;
        this.activeOptions = activeOptions;
        this.explorerService = explorerService;
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

    public void initActiveContext(final Repository repository,
                                  final Branch branch) {

        view.showBusyIndicator(CommonConstants.INSTANCE.Loading());
        refresh(new ProjectExplorerContentQuery(repository,
                                                branch));
    }

    public void initActiveContext(final Repository repository,
                                  final Branch branch,
                                  final Module module) {

        view.showBusyIndicator(CommonConstants.INSTANCE.Loading());
        refresh(new ProjectExplorerContentQuery(repository,
                                                branch,
                                                module));
    }

    public void initActiveContext(final Repository repository,
                                  final Branch branch,
                                  final Module module,
                                  final org.guvnor.common.services.project.model.Package pkg) {
        view.showBusyIndicator(CommonConstants.INSTANCE.Loading());
        refresh(new ProjectExplorerContentQuery(repository,
                                                branch,
                                                module,
                                                pkg));
    }

    private void refresh(final ProjectExplorerContentQuery query) {
        query.setOptions(activeOptions.getOptions());
        explorerService.call(contentCallback,
                             new HasBusyIndicatorDefaultErrorCallback(view)).getContent(query);
    }

    private void refresh(final Module module) {
        refresh(new ProjectExplorerContentQuery(activeContextItems.getActiveProject().getRepository(),
                                                activeContextItems.getActiveProject().getBranch(),
                                                module));
    }

    void refresh() {
        if (activeContextItems.getActiveProject() != null) {
            refresh(new ProjectExplorerContentQuery(activeContextItems.getActiveProject().getRepository(),
                                                    activeContextItems.getActiveProject().getBranch(),
                                                    activeContextItems.getActiveModule(),
                                                    activeContextItems.getActivePackage(),
                                                    activeContextItems.getActiveFolderItem()));
        }
    }

    public void initActiveContext(final WorkspaceProjectContext context) {
        WorkspaceProject activeProject = context
                                                .getActiveWorkspaceProject()
                                                .orElseThrow(() -> new IllegalStateException("Cannot initialize active context without an active project."));
        initActiveContext(activeProject.getRepository(),
                          activeProject.getBranch(),
                          /*
                           * XXX I think these are allowed to be null but this should be
                           * documented somewhere, like on the ProjectExplorerContentQuery
                           */
                          context.getActiveModule().orElse(null),
                          context.getActivePackage().orElse(null));
    }

    public void onBranchCreated(@Observes final NewBranchEvent event) {
        if (activeContextItems.getActiveProject().getRepository().getAlias().equals(event.getRepository().getAlias())) {
            initActiveContext(activeContextItems.getActiveProject().getRepository(),
                              activeContextItems.getActiveProject().getBranch());
        }
    }
}

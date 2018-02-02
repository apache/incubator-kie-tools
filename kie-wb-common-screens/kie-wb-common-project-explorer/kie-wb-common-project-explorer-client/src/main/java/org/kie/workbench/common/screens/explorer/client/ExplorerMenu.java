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
package org.kie.workbench.common.screens.explorer.client;

import java.util.Optional;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.structure.repositories.Branch;
import org.kie.workbench.common.screens.explorer.client.widgets.ActiveContextOptions;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.Menus;

public class ExplorerMenu {

    private ActiveContextOptions activeOptions;

    private WorkspaceProjectContext context;

    private Command refreshCommand;
    private Command updateCommand;

    private ExplorerMenuView view;

    public ExplorerMenu() {

    }

    @Inject
    public ExplorerMenu(final ExplorerMenuView view,
                        final ActiveContextOptions activeOptions,
                        final WorkspaceProjectContext projectContext) {
        this.view = view;
        this.activeOptions = activeOptions;
        this.context = projectContext;

        view.setPresenter(this);
    }

    public Menus asMenu() {
        return view.asMenu();
    }

    public void refresh() {
        if (activeOptions.isTreeNavigatorVisible()) {
            view.showTreeNav();
        } else {
            view.showBreadcrumbNav();
        }

        if (activeOptions.isTechnicalViewActive()) {
            view.showTechViewIcon();
            view.hideBusinessViewIcon();
        } else {
            view.showBusinessViewIcon();
            view.hideTechViewIcon();
        }

        if (activeOptions.canShowTag()) {
            view.showTagFilterIcon();
        } else {
            view.hideTagFilterIcon();
        }
    }

    public void addRefreshCommand(Command refreshCommand) {
        this.refreshCommand = refreshCommand;
    }

    public void addUpdateCommand(Command updateCommand) {
        this.updateCommand = updateCommand;
    }

    public void onBusinessViewSelected() {
        if (!activeOptions.isBusinessViewActive()) {
            activeOptions.activateBusinessView();
            refresh();
            updateCommand.execute();
        }
    }

    public void onTechViewSelected() {
        if (!activeOptions.isTechnicalViewActive()) {
            activeOptions.activateTechView();
            refresh();
            updateCommand.execute();
        }
    }

    public void onTreeExplorerSelected() {
        if (!activeOptions.isTreeNavigatorVisible()) {
            activeOptions.activateTreeViewNavigation();
            refresh();
            updateCommand.execute();
        }
    }

    public void onBreadCrumbExplorerSelected() {
        if (!activeOptions.isBreadCrumbNavigationVisible()) {
            activeOptions.activateBreadCrumbNavigation();
            refresh();
            updateCommand.execute();
        }
    }

    public void onShowTagFilterSelected() {
        if (activeOptions.canShowTag()) {
            activeOptions.disableTagFiltering();
        } else {
            activeOptions.activateTagFiltering();
        }
        refresh();
        updateCommand.execute();
    }

    public void onArchiveActiveProject() {
        view.archive(context.getActiveModule()
                            .map(module -> module.getRootPath())
                            .orElseThrow(() -> new IllegalStateException("Cannot get root path without active module.")));
    }

    public void onArchiveActiveRepository() {
        final Optional<Branch> defaultBranch = context.getActiveWorkspaceProject()
                                                      .map(proj -> proj.getRepository())
                                                      .orElseThrow(() -> new IllegalStateException("Cannot check for default branch without an active project."))
                                                      .getDefaultBranch();
        if (defaultBranch.isPresent()) {
            view.archive(defaultBranch.get().getPath());
        }
    }

    public void onRefresh() {
        refreshCommand.execute();
    }
}

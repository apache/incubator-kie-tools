/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.projecteditor.client.menu;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.widgets.client.resources.i18n.ToolsMenuConstants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;

@ApplicationScoped
public class ProjectMenu {

    private PlaceManager placeManager;
    private WorkspaceProjectContext context;
    private MenuItem projectScreen = MenuFactory.newSimpleItem(ToolsMenuConstants.INSTANCE.ProjectEditor()).respondsWith(
            new Command() {
                @Override
                public void execute() {
                    placeManager.goTo( "ProjectSettings" );
                }
            }).endMenu().build().getItems().get(0);
    private MenuItem projectStructureScreen = MenuFactory.newSimpleItem(ToolsMenuConstants.INSTANCE.RepositoryStructure()).respondsWith(
            new Command() {
                @Override
                public void execute() {
                    placeManager.goTo("repositoryStructureScreen");
                }
            }).endMenu().build().getItems().get(0);

    public ProjectMenu() {
        //Zero argument constructor for CDI proxies
    }

    @Inject
    public ProjectMenu(final PlaceManager placeManager,
                       final WorkspaceProjectContext context) {
        this.placeManager = placeManager;
        this.context = context;
    }

    public List<MenuItem> getMenuItems() {
        enableToolsMenuItems((KieModule) context.getActiveModule().orElse(null));

        ArrayList<MenuItem> menuItems = new ArrayList<>();

        menuItems.add(projectScreen);
        menuItems.add(projectStructureScreen);

        return menuItems;
    }

    public void onWorkspaceProjectContextChanged(@Observes final WorkspaceProjectContextChangeEvent event) {
        enableToolsMenuItems((KieModule) event.getModule());
    }

    private void enableToolsMenuItems(final KieModule project) {
        final boolean enabled = (project != null);
        projectScreen.setEnabled(enabled);
    }
}

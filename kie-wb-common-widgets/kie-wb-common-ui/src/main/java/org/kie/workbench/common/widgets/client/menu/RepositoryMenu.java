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

package org.kie.workbench.common.widgets.client.menu;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.widgets.client.resources.i18n.ToolsMenuConstants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;

@ApplicationScoped
public class RepositoryMenu {

    @Inject
    protected Caller<KieModuleService> moduleService;

    @Inject
    protected WorkspaceProjectContext context;

    @Inject
    private PlaceManager placeManager;

    private MenuItem repositoryStructureScreen = MenuFactory.newSimpleItem(ToolsMenuConstants.INSTANCE.RepositoryStructure()).respondsWith(
            new Command() {
                @Override
                public void execute() {
                    placeManager.goTo("repositoryStructureScreen");
                }
            }).endMenu().build().getItems().get(0);

    private MenuItem categoriesEditor = MenuFactory.newSimpleItem(ToolsMenuConstants.INSTANCE.CategoriesEditor()).respondsWith(
            new Command() {
                @Override
                public void execute() {
                    placeManager.goTo("CategoryManager");
                }
            }).endMenu().build().getItems().get(0);

    public List<MenuItem> getMenuItems() {
        ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();

        //@TODO: the idea is to remove this one when we add the option to the project explorer

        menuItems.add(repositoryStructureScreen);

        //menuItems.add( categoriesEditor );

        return menuItems;
    }

    //@TODO: we need to remove these two when we remove the projectScreen from here
    public void onWorkspaceProjectContextChanged(@Observes final WorkspaceProjectContextChangeEvent event) {
        enableToolsMenuItems((KieModule) event.getModule());
    }

    private void enableToolsMenuItems(final KieModule module) {
        final boolean enabled = (module != null);
    }
}

/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.client.perspectives;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.client.AuthoringWorkbenchDocks;
import org.kie.workbench.common.widgets.client.handlers.NewResourcesMenu;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@ApplicationScoped
@WorkbenchPerspective( identifier = "ProjectPerspective" )
public class ProjectPerspective {

    @Inject
    private NewResourcesMenu newResourcesMenu;

    @Inject
    private AuthoringWorkbenchDocks docks;

    @Perspective
    public PerspectiveDefinition getPerspective() {
        return new PerspectiveDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
    }

    @PostConstruct
    public void setup() {
        docks.setup( "ProjectPerspective", new DefaultPlaceRequest( "org.kie.guvnor.explorer" ) );
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return MenuFactory
                .newTopLevelMenu( "New Item" )
                .withItems( newResourcesMenu.getMenuItems() )
                .endMenu()
                .build();
    }

}

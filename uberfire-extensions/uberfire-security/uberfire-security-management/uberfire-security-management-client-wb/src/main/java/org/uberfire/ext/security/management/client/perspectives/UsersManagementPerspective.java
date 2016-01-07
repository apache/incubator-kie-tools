/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.security.management.client.perspectives;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.ext.security.management.api.UserSystemManager;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWorkbenchConstants;
import org.uberfire.ext.security.management.client.screens.explorer.UsersExplorerScreen;
import org.uberfire.ext.security.management.client.screens.home.UsersManagementHomeScreen;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.annotations.Roles;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

import javax.enterprise.context.ApplicationScoped;

@Roles({UserSystemManager.ADMIN})
@ApplicationScoped
@WorkbenchPerspective(identifier = "UsersManagementPerspective", isTransient = false)
public class UsersManagementPerspective {

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        PerspectiveDefinition perspective = new PerspectiveDefinitionImpl(MultiListWorkbenchPanelPresenter.class.getName());
        perspective.setName(UsersManagementWorkbenchConstants.INSTANCE.usersManagement());
        perspective.getRoot().addPart(new PartDefinitionImpl(new DefaultPlaceRequest(UsersManagementHomeScreen.SCREEN_ID)));
        final PanelDefinition west = new PanelDefinitionImpl(MultiListWorkbenchPanelPresenter.class.getName());
        west.setWidth(600);
        west.setMinWidth(400);
        west.addPart(new PartDefinitionImpl(new DefaultPlaceRequest(UsersExplorerScreen.SCREEN_ID)));
        perspective.getRoot().insertChild( CompassPosition.WEST, west );
        return perspective;
    }

}

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

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWorkbenchConstants;
import org.uberfire.ext.security.management.client.screens.explorer.SecurityExplorerScreen;
import org.uberfire.ext.security.management.client.screens.home.SecurityManagementHomeScreen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

@ApplicationScoped
@WorkbenchPerspective(identifier = "SecurityManagementPerspective", isTransient = true)
public class SecurityManagementPerspective {

    private PerspectiveDefinition perspective;

    @Perspective
    public PerspectiveDefinition getPerspective() {
        if (perspective == null) {
            return createPerspectiveDefinition();
        }

        return perspective;
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        perspective = createPerspectiveDefinition();
        configurePerspective(placeRequest);
    }

    PerspectiveDefinition createPerspectiveDefinition() {
        PerspectiveDefinition perspective = new PerspectiveDefinitionImpl(MultiListWorkbenchPanelPresenter.class.getName());
        perspective.setName(UsersManagementWorkbenchConstants.INSTANCE.securityManagement());

        return perspective;
    }

    void configurePerspective(final PlaceRequest placeRequest) {
        perspective.getRoot().addPart(SecurityManagementHomeScreen.SCREEN_ID);

        final PanelDefinition west = new PanelDefinitionImpl(StaticWorkbenchPanelPresenter.class.getName());
        west.setWidth(400);
        west.setMinWidth(400);
        west.addPart(new PartDefinitionImpl(new DefaultPlaceRequest(SecurityExplorerScreen.SCREEN_ID,
                                                                    placeRequest.getParameters())));
        perspective.getRoot().insertChild(CompassPosition.WEST,
                                          west);
    }
}

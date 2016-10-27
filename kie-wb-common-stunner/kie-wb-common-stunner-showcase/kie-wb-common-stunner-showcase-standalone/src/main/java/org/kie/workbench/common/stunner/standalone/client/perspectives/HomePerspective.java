/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.standalone.client.perspectives;

import org.kie.workbench.common.stunner.client.widgets.loading.LoadingBox;
import org.kie.workbench.common.stunner.client.widgets.palette.bs3.BS3PaletteWidgetImpl;
import org.kie.workbench.common.stunner.standalone.client.screens.*;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@WorkbenchPerspective( identifier = HomePerspective.PERSPECTIVE_ID, isTransient = false, isDefault = true )
public class HomePerspective {

    public static final String PERSPECTIVE_ID = "HomePerspective";
    public static final int WEST_PANEL_WIDTH = BS3PaletteWidgetImpl.getDefaultWidth();
    public static final int EAST_PANEL_WIDTH = 450;

    @Inject
    LoadingBox loadingBox;

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        PerspectiveDefinition perspective = new PerspectiveDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        perspective.setName( "Stunner Home" );
        perspective.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( HomeNavigatorScreen.SCREEN_ID ) ) );
        return perspective;
    }

}

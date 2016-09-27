/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.wires.client.preferences.central;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter;
import org.uberfire.ext.wires.client.preferences.central.actions.PreferencesCentralActionsScreen;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

@ApplicationScoped
@WorkbenchPerspective(identifier = PreferencesCentralPerspective.IDENTIFIER)
public class PreferencesCentralPerspective {

    public static final String IDENTIFIER = "PreferencesCentralPerspective";

    private PerspectiveDefinition perspective;

    @Perspective
    public PerspectiveDefinition getPerspective() {
        perspective = new PerspectiveDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        perspective.setName( "Preferences Central" );

        final PanelDefinition navBar = new PanelDefinitionImpl( SimpleWorkbenchPanelPresenter.class.getName() );
        navBar.setWidth( 400 );
        navBar.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( PreferencesCentralNavBarScreen.IDENTIFIER ) ) );

        final PanelDefinition actionsBar = new PanelDefinitionImpl( StaticWorkbenchPanelPresenter.class.getName() );
        actionsBar.setHeight( 80 );
        actionsBar.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( PreferencesCentralActionsScreen.IDENTIFIER ) ) );

        perspective.getRoot().insertChild( CompassPosition.WEST,
                                           navBar );
        perspective.getRoot().insertChild( CompassPosition.SOUTH,
                                           actionsBar );

        return perspective;
    }
}
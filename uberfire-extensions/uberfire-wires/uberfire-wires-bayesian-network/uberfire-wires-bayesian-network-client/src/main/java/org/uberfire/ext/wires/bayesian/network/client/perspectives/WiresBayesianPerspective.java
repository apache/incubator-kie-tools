/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.wires.bayesian.network.client.perspectives;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

/**
 * A Perspective to show Bayesian related panels
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = "WiresBayesianPerspective")
public class WiresBayesianPerspective {

    private static final String WIRES = "Bayesian Networks";

    private static final String BAYESIAN_SCREEN = "BayesianScreen";
    private static final String WIRES_LAYERS_SCREEN = "WiresLayersScreen";
    private static final String WIRES_TEMPLATE_SCREEN = "BayesianTemplatesScreen";
    private static final String BAYESIAN_VARIABLES_SCREEN = "BayesianVariablesScreen";

    private static final int MIN_WIDTH_PANEL = 200;
    private static final int WIDTH_PANEL = 300;

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        PerspectiveDefinition perspective = new PerspectiveDefinitionImpl( SimpleWorkbenchPanelPresenter.class.getName() );
        perspective.setName( WIRES );

        perspective.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( BAYESIAN_SCREEN ) ) );

        final PanelDefinition layersPanel = new PanelDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        layersPanel.setMinWidth( MIN_WIDTH_PANEL );
        layersPanel.setWidth( WIDTH_PANEL );
        layersPanel.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( WIRES_LAYERS_SCREEN ) ) );

        final PanelDefinition templatesPanel = new PanelDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        templatesPanel.setMinWidth( MIN_WIDTH_PANEL );
        templatesPanel.setWidth( WIDTH_PANEL );
        templatesPanel.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( WIRES_TEMPLATE_SCREEN ) ) );

        layersPanel.appendChild( CompassPosition.SOUTH,
                                 templatesPanel );

        perspective.getRoot().insertChild( CompassPosition.EAST,
                                           layersPanel );

        final PanelDefinition variablesPanel = new PanelDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        variablesPanel.setMinWidth( MIN_WIDTH_PANEL );
        variablesPanel.setWidth( WIDTH_PANEL );
        variablesPanel.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( BAYESIAN_VARIABLES_SCREEN ) ) );

        perspective.getRoot().insertChild( CompassPosition.SOUTH,
                                           variablesPanel );

        return perspective;
    }

}

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

package org.uberfire.ext.wires.core.scratchpad.client.perspectives;

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
 * A Perspective for Wires Scratch Pad
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = "WiresScratchPadPerspective")
public class WiresScratchPadPerspective {

    private static final String WIRES = "Wires";

    private static final String WIRES_LAYERS_SCREEN = "WiresLayersScreen";
    private static final String WIRES_PALETTE_SCREEN = "WiresPaletteScreen";
    private static final String WIRES_CANVAS_SCREEN = "WiresScratchPadScreen";
    private static final String WIRES_ACTIONS_SCREEN = "WiresActionsScreen";
    private static final String WIRES_PROPERTIES_SCREEN = "WiresPropertiesScreen";

    private static final int MIN_WIDTH_PANEL = 200;
    private static final int WIDTH_PANEL = 300;

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        PerspectiveDefinition perspective = new PerspectiveDefinitionImpl( SimpleWorkbenchPanelPresenter.class.getName() );
        perspective.setName( WIRES );

        perspective.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( WIRES_CANVAS_SCREEN ) ) );

        final PanelDefinition layersPanel = new PanelDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        layersPanel.setMinWidth( MIN_WIDTH_PANEL );
        layersPanel.setWidth( WIDTH_PANEL );
        layersPanel.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( WIRES_LAYERS_SCREEN ) ) );

        final PanelDefinition actionsPanel = new PanelDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        actionsPanel.setMinWidth( MIN_WIDTH_PANEL );
        actionsPanel.setWidth( WIDTH_PANEL );
        actionsPanel.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( WIRES_ACTIONS_SCREEN ) ) );

        layersPanel.appendChild( CompassPosition.SOUTH,
                                 actionsPanel );

        perspective.getRoot().insertChild( CompassPosition.EAST,
                                           layersPanel );

        final PanelDefinition palettePanel = new PanelDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        palettePanel.setMinWidth( MIN_WIDTH_PANEL );
        palettePanel.setWidth( WIDTH_PANEL );
        palettePanel.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( WIRES_PALETTE_SCREEN ) ) );

        final PanelDefinition propertiesPanel = new PanelDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        propertiesPanel.setMinWidth( MIN_WIDTH_PANEL );
        propertiesPanel.setWidth( WIDTH_PANEL );
        propertiesPanel.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( WIRES_PROPERTIES_SCREEN ) ) );

        palettePanel.appendChild( CompassPosition.SOUTH,
                                  propertiesPanel );

        perspective.getRoot().insertChild( CompassPosition.WEST,
                                           palettePanel );

        return perspective;
    }

}

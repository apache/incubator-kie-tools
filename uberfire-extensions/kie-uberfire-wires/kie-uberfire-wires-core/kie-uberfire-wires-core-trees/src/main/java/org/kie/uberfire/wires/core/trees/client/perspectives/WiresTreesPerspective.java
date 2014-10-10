package org.kie.uberfire.wires.core.trees.client.perspectives;

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
@WorkbenchPerspective(identifier = "WiresTreesPerspective")
public class WiresTreesPerspective {

    private static final String WIRES = "Wires Trees";

    private static final String WIRES_LAYERS_SCREEN = "WiresLayersScreen";
    private static final String WIRES_PALETTE_SCREEN = "WiresTreesPaletteScreen";
    private static final String WIRES_CANVAS_SCREEN = "WiresTreesScreen";
    private static final String WIRES_PROPERTIES_SCREEN = "WiresPropertiesScreen";

    private static final int MIN_WIDTH_PANEL = 200;
    private static final int WIDTH_PANEL = 300;

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        PerspectiveDefinition perspective = new PerspectiveDefinitionImpl( SimpleWorkbenchPanelPresenter.class.getName() );
        perspective.setName( WIRES );

        perspective.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( WIRES_CANVAS_SCREEN ) ) );

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

        final PanelDefinition layersPanel = new PanelDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        layersPanel.setMinWidth( MIN_WIDTH_PANEL );
        layersPanel.setWidth( WIDTH_PANEL );
        layersPanel.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( WIRES_LAYERS_SCREEN ) ) );

        perspective.getRoot().insertChild( CompassPosition.EAST,
                                           layersPanel );

        return perspective;
    }

}

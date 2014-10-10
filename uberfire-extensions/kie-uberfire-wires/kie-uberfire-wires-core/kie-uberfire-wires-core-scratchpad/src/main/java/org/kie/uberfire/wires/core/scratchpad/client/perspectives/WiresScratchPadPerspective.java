package org.kie.uberfire.wires.core.scratchpad.client.perspectives;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

/**
 * A Perspective for Wires Scratch Pad
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = "WiresScratchPadPerspective", isDefault = true)
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

        createPanelWithChild( perspective,
                              CompassPosition.EAST );

        final PanelDefinition panel = new PanelDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        panel.setMinWidth( MIN_WIDTH_PANEL );
        panel.setWidth( WIDTH_PANEL );
        panel.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( WIRES_PALETTE_SCREEN ) ) );
        PanelDefinition propertiesPanel = new PanelDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        propertiesPanel.setMinWidth( MIN_WIDTH_PANEL );
        propertiesPanel.setWidth( WIDTH_PANEL );
        propertiesPanel.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( WIRES_PROPERTIES_SCREEN ) ) );
        panel.appendChild( CompassPosition.SOUTH,
                           propertiesPanel );

        perspective.getRoot().insertChild( CompassPosition.WEST,
                                           panel );

        return perspective;
    }

    private void createPanelWithChild( final PerspectiveDefinition p,
                                       final Position position ) {
        final PanelDefinition actionsPanel = newPanel( p,
                                                       WIRES_ACTIONS_SCREEN );
        actionsPanel.setHeight( 150 );
        actionsPanel.setMinHeight( 80 );

        final PanelDefinition parentPanel = newPanel( p,
                                                      WIRES_LAYERS_SCREEN );
        parentPanel.setHeight( 180 );
        parentPanel.setMinHeight( 150 );
        parentPanel.appendChild( CompassPosition.SOUTH,
                                 actionsPanel );
        p.getRoot().insertChild( position,
                                 parentPanel );
    }

    private PanelDefinition newPanel( final PerspectiveDefinition p,
                                      final String identifierPanel ) {
        final PanelDefinition panel = new PanelDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        panel.setWidth( WIDTH_PANEL );
        panel.setMinWidth( MIN_WIDTH_PANEL );
        panel.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( identifierPanel ) ) );
        return panel;
    }
}

package org.uberfire.client.perspectives;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PartDefinition;
import org.uberfire.client.workbench.model.PerspectiveDefinition;
import org.uberfire.shared.mvp.PlaceRequest;

public class HomePerspective {

    @Perspective(identifier = "homePerspective", isDefault = true)
    public PerspectiveDefinition getPerspective() {
        final PerspectiveDefinition definition = new PerspectiveDefinition();
        definition.setName( "home" );

        final PanelDefinition west = new PanelDefinition();
        west.addPart( new PartDefinition( new PlaceRequest( "perspectives" ) ) );
        definition.getRoot().getChildren( Position.WEST ).add( west );

        final PanelDefinition east1 = new PanelDefinition();
        east1.addPart( new PartDefinition( new PlaceRequest( "notifications" ) ) );
        definition.getRoot().getChildren( Position.EAST ).add( east1 );

        final PanelDefinition east2 = new PanelDefinition();
        east2.addPart( new PartDefinition( new PlaceRequest( "rssFeed" ) ) );
        east1.getChildren( Position.EAST ).add( east2 );

        definition.getRoot().addPart( new PartDefinition( new PlaceRequest( "welcome" ) ) );

        return definition;
    }
}

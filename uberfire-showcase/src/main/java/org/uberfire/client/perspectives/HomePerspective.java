package org.uberfire.client.perspectives;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PerspectiveDefinition;
import org.uberfire.client.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.client.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.client.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

public class HomePerspective {

    @Perspective(identifier = "homePerspective", isDefault = true)
    public PerspectiveDefinition getPerspective() {
        final PerspectiveDefinition definition = new PerspectiveDefinitionImpl();
        definition.setName( "home" );

        final PanelDefinition west = new PanelDefinitionImpl();
        west.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "perspectives" ) ) );
        definition.getRoot().setChild( Position.WEST,
                                       west );

        final PanelDefinition east1 = new PanelDefinitionImpl();
        east1.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "notifications" ) ) );
        definition.getRoot().setChild( Position.EAST,
                                       east1 );

        final PanelDefinition east2 = new PanelDefinitionImpl();
        east2.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "rssFeed" ) ) );
        east1.setChild( Position.EAST,
                        east2 );

        definition.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "welcome" ) ) );

        return definition;
    }
}

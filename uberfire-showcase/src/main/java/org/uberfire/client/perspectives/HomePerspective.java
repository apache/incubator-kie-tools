package org.uberfire.client.perspectives;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.perspectives.PerspectiveDefinition;
import org.uberfire.client.workbench.perspectives.PerspectivePartDefinition;
import org.uberfire.shared.mvp.PlaceRequest;

public class HomePerspective {

    @Perspective(identifier = "homePerspective", isDefault = true)
    public PerspectiveDefinition getPerspective() {
        final PerspectiveDefinition definition = new PerspectiveDefinition();
        definition.setName("home");

        definition.addPart(new PerspectivePartDefinition(Position.WEST,
                new PlaceRequest("perspectives")));

        definition.addPart(new PerspectivePartDefinition(Position.ROOT,
                new PlaceRequest("welcome")));

        definition.addPart(new PerspectivePartDefinition(Position.EAST,
                new PlaceRequest("notifications")));
        definition.addPart(new PerspectivePartDefinition(Position.EAST,
                new PlaceRequest("rssFeed")));

        return definition;
    }
}

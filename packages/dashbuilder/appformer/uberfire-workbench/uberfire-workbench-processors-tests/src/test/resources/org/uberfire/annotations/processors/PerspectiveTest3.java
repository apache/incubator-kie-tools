package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.workbench.model.PerspectiveDefinition;

@WorkbenchPerspective(identifier = "PerspectiveTest3")
public class PerspectiveTest3 {

    @Perspective
    public PerspectiveDefinition getPerspective( final String p0 ) {
        return null;
    }

}

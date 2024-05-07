package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.workbench.model.PerspectiveDefinition;

@WorkbenchPerspective(identifier = "PerspectiveTest10")
public class PerspectiveTest10 {

    @Perspective
    public PerspectiveDefinition getPerspective() {
        return null;
    }

}

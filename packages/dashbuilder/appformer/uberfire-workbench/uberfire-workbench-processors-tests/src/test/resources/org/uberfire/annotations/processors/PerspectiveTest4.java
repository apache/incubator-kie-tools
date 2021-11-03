package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.workbench.model.PerspectiveDefinition;

@WorkbenchPerspective(identifier = "PerspectiveTest4")
public class PerspectiveTest4 {

    @Perspective
    public PerspectiveDefinition getPerspective() {
        return null;
    }

}

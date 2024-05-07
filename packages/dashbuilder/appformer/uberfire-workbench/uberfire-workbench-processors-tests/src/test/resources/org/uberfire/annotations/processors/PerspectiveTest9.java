package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.workbench.model.PerspectiveDefinition;

@WorkbenchPerspective(identifier = "PerspectiveTest9")
public class PerspectiveTest9 {

    @Perspective
    public PerspectiveDefinition getPerspective() {
        return null;
    }

}

package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.workbench.model.PerspectiveDefinition;

@WorkbenchPerspective(identifier = "PerspectiveTest24", isDynamic=true)
public class PerspectiveTest24 {

    @Perspective
    public PerspectiveDefinition getPerspective() {
        return null;
    }

}

package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.workbench.perspectives.PerspectiveDefinition;

public class PerspectiveTest5 {

    @Perspective(identifier = "PerspectiveTest5", isDefault = true)
    public PerspectiveDefinition getPerspective() {
        return null;
    }

}

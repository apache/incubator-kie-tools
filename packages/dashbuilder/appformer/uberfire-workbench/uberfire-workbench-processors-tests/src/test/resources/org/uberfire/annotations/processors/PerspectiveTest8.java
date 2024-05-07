package org.uberfire.annotations.processors;

import java.util.function.Consumer;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.workbench.model.PerspectiveDefinition;

@WorkbenchPerspective(identifier = "PerspectiveTest8")
public class PerspectiveTest8 {

    @Perspective
    public PerspectiveDefinition getPerspective() {
        return null;
    }

}

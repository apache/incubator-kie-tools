package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.workbench.perspectives.PerspectiveDefinition;
import org.uberfire.security.annotations.Roles;

public class PerspectiveTest4 {

    @Perspective(identifier = "PerspectiveTest4")
    @Roles({"ADMIN", "SUDO"})
    public PerspectiveDefinition getPerspective() {
        return null;
    }

}

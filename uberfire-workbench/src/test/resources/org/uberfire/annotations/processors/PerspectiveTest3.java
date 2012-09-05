package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.workbench.model.PerspectiveDefinition;
import org.uberfire.security.annotations.Roles;

public class PerspectiveTest3 {

    @Perspective(identifier = "PerspectiveTest3")
    @Roles({"ADMIN", "SUDO"})
    public PerspectiveDefinition getPerspective(final String p0) {
        return null;
    }

}

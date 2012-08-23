package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.workbench.perspectives.PerspectiveDefinition;
import org.uberfire.security.annotations.AnyRole;

public class PerspectiveTest3 {

    @Perspective(identifier = "PerspectiveTest3")
    @AnyRole({"ADMIN", "SUDO"})
    public PerspectiveDefinition getPerspective(final String p0) {
        return null;
    }

}

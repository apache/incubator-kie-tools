package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.workbench.model.PerspectiveDefinition;
import org.uberfire.security.annotations.All;
import org.uberfire.security.annotations.Roles;

public class PerspectiveTest5 {

    @Perspective(identifier = "PerspectiveTest5", isDefault = true)
    @All @Roles({"ADMIN", "SUDO"})
    public PerspectiveDefinition getPerspective() {
        return null;
    }

}

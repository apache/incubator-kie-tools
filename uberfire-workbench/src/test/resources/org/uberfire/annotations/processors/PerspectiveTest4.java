package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.security.annotations.Roles;

@WorkbenchPerspective(identifier = "PerspectiveTest4")
public class PerspectiveTest4 {

    @Perspective
    @Roles({"ADMIN", "SUDO"})
    public PerspectiveDefinition getPerspective() {
        return null;
    }

}

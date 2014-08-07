package org.uberfire.annotations.processors;

import org.jboss.errai.ioc.client.api.ActivatedBy;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.security.annotations.Roles;
import org.uberfire.workbench.model.PerspectiveDefinition;

@WorkbenchPerspective(identifier = "PerspectiveTest20")
@ActivatedBy(TestBeanActivator.class)
public class PerspectiveTest20 {

    @Perspective
    @Roles({"ADMIN", "SUDO"})
    public PerspectiveDefinition getPerspective() {
        return null;
    }

}
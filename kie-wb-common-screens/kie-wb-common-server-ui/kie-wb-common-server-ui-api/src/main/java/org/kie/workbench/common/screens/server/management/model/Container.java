package org.kie.workbench.common.screens.server.management.model;

import org.guvnor.common.services.project.model.GAV;

public interface Container extends ContainerRef {

    GAV getResolvedReleasedId();

}

package org.kie.workbench.common.screens.server.management.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.screens.server.management.model.Container;
import org.kie.workbench.common.screens.server.management.model.ContainerRef;

@Portable
public class ContainerStopped {

    private ContainerRef container;

    public ContainerStopped() {

    }

    public ContainerStopped( final ContainerRef container ) {
        this.container = container;
    }

    public ContainerRef getContainer() {
        return container;
    }
}

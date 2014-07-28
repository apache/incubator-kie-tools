package org.kie.workbench.common.screens.server.management.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.screens.server.management.model.ContainerRef;

@Portable
public class ContainerCreated {

    private ContainerRef container;

    public ContainerCreated() {

    }

    public ContainerCreated( final ContainerRef container ) {
        this.container = container;
    }

    public ContainerRef getContainer() {
        return container;
    }
}

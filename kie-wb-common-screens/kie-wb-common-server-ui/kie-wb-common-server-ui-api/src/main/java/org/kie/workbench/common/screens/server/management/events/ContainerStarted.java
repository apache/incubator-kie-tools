package org.kie.workbench.common.screens.server.management.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.screens.server.management.model.Container;

@Portable
public class ContainerStarted {

    private Container container;

    public ContainerStarted() {

    }

    public ContainerStarted( final Container container ) {
        this.container = container;
    }

    public Container getContainer() {
        return container;
    }
}

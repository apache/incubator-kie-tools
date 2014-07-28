package org.kie.workbench.common.screens.server.management.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.screens.server.management.model.Container;

@Portable
public class ContainerUpdated {

    private Container container;

    public ContainerUpdated() {

    }

    public ContainerUpdated( final Container container ) {
        this.container = container;
    }

    public Container getContainer() {
        return container;
    }
}

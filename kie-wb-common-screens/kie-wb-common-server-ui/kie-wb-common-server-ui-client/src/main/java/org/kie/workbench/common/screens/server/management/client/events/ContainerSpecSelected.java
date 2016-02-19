package org.kie.workbench.common.screens.server.management.client.events;

import org.kie.server.controller.api.model.spec.ContainerSpecKey;

/**
 * TODO: update me
 */
public class ContainerSpecSelected {

    final ContainerSpecKey containerSpecKey;

    public ContainerSpecSelected( final ContainerSpecKey containerSpecKey ) {
        this.containerSpecKey = containerSpecKey;
    }

    public ContainerSpecKey getContainerSpecKey() {
        return containerSpecKey;
    }
}

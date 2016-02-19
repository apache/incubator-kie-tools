package org.kie.workbench.common.screens.server.management.client.events;

import org.kie.server.controller.api.model.spec.ContainerSpecKey;

public class RefreshRemoteServers {

    private final ContainerSpecKey containerSpecKey;

    public RefreshRemoteServers( final ContainerSpecKey containerSpecKey ) {
        this.containerSpecKey = containerSpecKey;
    }

    public ContainerSpecKey getContainerSpecKey() {
        return containerSpecKey;
    }
}

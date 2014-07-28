package org.kie.workbench.common.screens.server.management.events;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ContainerDeleted {

    private String serverId;

    private String containerId;

    public ContainerDeleted() {

    }

    public ContainerDeleted( final String serverId,
                             final String containerId ) {
        this.serverId = serverId;
        this.containerId = containerId;
    }

    public String getServerId() {
        return serverId;
    }

    public String getContainerId() {
        return containerId;
    }
}

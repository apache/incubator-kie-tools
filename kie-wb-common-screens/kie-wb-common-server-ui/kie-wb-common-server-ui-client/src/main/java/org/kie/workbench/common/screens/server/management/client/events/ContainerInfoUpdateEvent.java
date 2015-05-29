package org.kie.workbench.common.screens.server.management.client.events;

public class ContainerInfoUpdateEvent {

    private String serverId;
    private String containerId;

    public ContainerInfoUpdateEvent( final String serverId,
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

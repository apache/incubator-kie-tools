package org.kie.workbench.common.screens.server.management.client.events;

public class ContainerInfo {

    private String serverId;
    private String containerId;

    public ContainerInfo( String serverId,
                          String containerId ) {
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

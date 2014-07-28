package org.kie.workbench.common.screens.server.management.events;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ServerDeleted {

    private String serverId;

    public ServerDeleted() {

    }

    public ServerDeleted( final String serverId ) {
        this.serverId = serverId;
    }

    public String getServerId() {
        return serverId;
    }

}

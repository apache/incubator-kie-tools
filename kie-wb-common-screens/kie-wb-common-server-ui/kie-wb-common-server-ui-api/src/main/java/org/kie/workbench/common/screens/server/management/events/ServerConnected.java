package org.kie.workbench.common.screens.server.management.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.screens.server.management.model.Server;

@Portable
public class ServerConnected {

    private Server server;

    public ServerConnected() {
    }

    public ServerConnected( final Server server ) {
        this.server = server;
    }

    public Server getServer() {
        return server;
    }

}

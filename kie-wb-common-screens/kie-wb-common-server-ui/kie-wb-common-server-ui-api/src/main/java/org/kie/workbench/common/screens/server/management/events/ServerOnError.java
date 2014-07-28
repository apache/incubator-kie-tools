package org.kie.workbench.common.screens.server.management.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.screens.server.management.model.ServerRef;

@Portable
public class ServerOnError {

    private String message;
    private ServerRef server;

    public ServerOnError() {
    }

    public ServerOnError( final ServerRef server,
                          final String message ) {
        this.server = server;
        this.message = message;
    }

    public ServerRef getServer() {
        return server;
    }

    public String getMessage() {
        return message;
    }
}

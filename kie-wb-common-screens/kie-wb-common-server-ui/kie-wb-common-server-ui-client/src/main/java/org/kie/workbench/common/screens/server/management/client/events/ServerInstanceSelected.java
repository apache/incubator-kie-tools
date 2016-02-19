package org.kie.workbench.common.screens.server.management.client.events;

import org.kie.server.controller.api.model.runtime.ServerInstanceKey;

/**
 * TODO: update me
 */
public class ServerInstanceSelected {

    private final ServerInstanceKey serverInstanceKey;

    public ServerInstanceSelected( final ServerInstanceKey serverInstanceKey ) {
        this.serverInstanceKey = serverInstanceKey;
    }

    public ServerInstanceKey getServerInstanceKey() {
        return serverInstanceKey;
    }
}

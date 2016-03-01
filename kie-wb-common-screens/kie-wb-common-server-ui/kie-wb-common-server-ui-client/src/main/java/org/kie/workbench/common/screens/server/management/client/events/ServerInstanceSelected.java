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

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof ServerInstanceSelected ) ) {
            return false;
        }

        final ServerInstanceSelected that = (ServerInstanceSelected) o;

        return serverInstanceKey.equals( that.serverInstanceKey );

    }

    @Override
    public int hashCode() {
        return serverInstanceKey.hashCode();
    }
}

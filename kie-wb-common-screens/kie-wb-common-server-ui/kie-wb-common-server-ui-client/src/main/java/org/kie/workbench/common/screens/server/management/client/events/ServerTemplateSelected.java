package org.kie.workbench.common.screens.server.management.client.events;

import org.kie.server.controller.api.model.spec.ServerTemplateKey;

/**
 * TODO: update me
 */
public class ServerTemplateSelected {

    private final ServerTemplateKey serverTemplateKey;
    private final String containerId;

    public ServerTemplateSelected( final ServerTemplateKey serverTemplateKey ) {
        this( serverTemplateKey, null );
    }

    public ServerTemplateSelected( final ServerTemplateKey serverTemplateKey,
                                   final String containerId ) {
        this.serverTemplateKey = serverTemplateKey;
        this.containerId = containerId;
    }

    public ServerTemplateKey getServerTemplateKey() {
        return serverTemplateKey;
    }

    public String getContainerId() {
        return containerId;
    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof ServerTemplateSelected ) ) {
            return false;
        }

        final ServerTemplateSelected that = (ServerTemplateSelected) o;

        if ( serverTemplateKey != null ? !serverTemplateKey.equals( that.serverTemplateKey ) : that.serverTemplateKey != null ) {
            return false;
        }
        return containerId != null ? containerId.equals( that.containerId ) : that.containerId == null;

    }

    @Override
    public int hashCode() {
        int result = serverTemplateKey != null ? serverTemplateKey.hashCode() : 0;
        result = 31 * result + ( containerId != null ? containerId.hashCode() : 0 );
        return result;
    }
}

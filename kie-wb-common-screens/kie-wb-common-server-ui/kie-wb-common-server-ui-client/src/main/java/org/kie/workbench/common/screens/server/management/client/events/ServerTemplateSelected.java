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
}

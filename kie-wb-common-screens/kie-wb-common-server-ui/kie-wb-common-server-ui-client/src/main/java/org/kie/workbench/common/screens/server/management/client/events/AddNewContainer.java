package org.kie.workbench.common.screens.server.management.client.events;

import org.kie.server.controller.api.model.spec.ServerTemplate;

public class AddNewContainer {

    private final ServerTemplate serverTemplate;

    public AddNewContainer( final ServerTemplate serverTemplate ) {
        this.serverTemplate = serverTemplate;
    }

    public ServerTemplate getServerTemplate() {
        return serverTemplate;
    }
}

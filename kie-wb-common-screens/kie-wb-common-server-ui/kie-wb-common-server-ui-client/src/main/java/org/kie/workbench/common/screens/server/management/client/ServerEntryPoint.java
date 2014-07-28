package org.kie.workbench.common.screens.server.management.client;

import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.kie.workbench.common.screens.server.management.client.resources.ContainerResources;

@EntryPoint
public class ServerEntryPoint {

    @AfterInitialization
    public void startApp() {
        ContainerResources.INSTANCE.CSS().ensureInjected();
    }

}

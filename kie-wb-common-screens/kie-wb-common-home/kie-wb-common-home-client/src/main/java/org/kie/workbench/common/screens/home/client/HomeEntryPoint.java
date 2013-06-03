package org.kie.workbench.common.screens.home.client;

import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.kie.workbench.common.screens.home.client.resources.HomeResources;

/**
 * Entry Point for Home Page
 */
@EntryPoint
public class HomeEntryPoint {

    @AfterInitialization
    public void startApp() {
        HomeResources.INSTANCE.CSS().ensureInjected();
    }

}

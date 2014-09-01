package org.kie.workbench.common.screens.social.hp.client;

import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.kie.workbench.common.screens.social.hp.client.resources.ContainerResources;

@EntryPoint
public class SocialHomePageEntryPoint {

    @AfterInitialization
    public void startApp() {
        ContainerResources.INSTANCE.CSS().ensureInjected();
    }

}

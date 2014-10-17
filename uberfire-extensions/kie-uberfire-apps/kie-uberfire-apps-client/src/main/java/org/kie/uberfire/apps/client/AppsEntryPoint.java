package org.kie.uberfire.apps.client;

import javax.annotation.PostConstruct;

import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.kie.uberfire.apps.client.resources.WebAppResource;

@EntryPoint
public class AppsEntryPoint {


    @PostConstruct
    public void init() {
        WebAppResource.INSTANCE.CSS().ensureInjected();
    }

    @AfterInitialization
    public void setup() {
    }

}

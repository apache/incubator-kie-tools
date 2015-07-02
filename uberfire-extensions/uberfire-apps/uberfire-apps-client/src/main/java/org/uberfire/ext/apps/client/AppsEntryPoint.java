package org.uberfire.ext.apps.client;

import javax.annotation.PostConstruct;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.uberfire.ext.apps.client.resources.WebAppResource;

@EntryPoint
public class AppsEntryPoint {

    @PostConstruct
    public void init() {
        WebAppResource.INSTANCE.CSS().ensureInjected();
    }

}

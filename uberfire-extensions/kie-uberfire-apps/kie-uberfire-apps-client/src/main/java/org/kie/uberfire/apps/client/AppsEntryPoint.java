package org.kie.uberfire.apps.client;

import javax.annotation.PostConstruct;

import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;

@EntryPoint
public class AppsEntryPoint {


    @PostConstruct
    public void init() {
    }

    @AfterInitialization
    public void setup() {
    }

}

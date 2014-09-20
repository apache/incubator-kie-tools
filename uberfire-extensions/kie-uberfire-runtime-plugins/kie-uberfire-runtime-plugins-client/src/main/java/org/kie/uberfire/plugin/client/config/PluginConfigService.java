package org.kie.uberfire.plugin.client.config;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.kie.uberfire.plugin.service.PluginServices;

@ApplicationScoped
public class PluginConfigService {

    @Inject
    private Caller<PluginServices> pluginServices;

    private String mediaServletURI;

    @AfterInitialization
    public void init() {
        pluginServices.call( new RemoteCallback<String>() {
            @Override
            public void callback( final String response ) {
                mediaServletURI = response;
            }
        } ).getMediaServletURI();
    }

    public String getMediaServletURI() {
        return mediaServletURI;
    }
}

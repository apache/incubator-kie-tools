package org.kie.workbench.common.screens.server.management.service;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ServerAlreadyRegisteredException extends RuntimeException {

    public ServerAlreadyRegisteredException() {
        super();
    }

    public ServerAlreadyRegisteredException( String s ) {
        super( s );
    }
}

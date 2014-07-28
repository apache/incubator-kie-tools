package org.kie.workbench.common.screens.server.management.service;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ContainerAlreadyRegisteredException extends RuntimeException {

    public ContainerAlreadyRegisteredException() {
        super();
    }

    public ContainerAlreadyRegisteredException( String s ) {
        super( s );
    }
}

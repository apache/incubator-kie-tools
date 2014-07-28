package org.kie.workbench.common.screens.server.management.service;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ContainerNotFoundException extends RuntimeException {

    public ContainerNotFoundException() {
        super();
    }

    public ContainerNotFoundException( String s ) {
        super( s );
    }
}

package org.kie.workbench.common.screens.server.management.service;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RemoteOperationFailedException extends RuntimeException {

    public RemoteOperationFailedException() {
        super();
    }

    public RemoteOperationFailedException( String s ) {
        super( s );
    }
}

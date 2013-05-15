package org.kie.workbench.services.shared.exceptions;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Root of all portable Exceptions resulting from server-side errors that need to be sent to the client
 */
@Portable
public class GenericPortableException extends RuntimeException {

    public GenericPortableException() {
    }

    public GenericPortableException( final String message ) {
        super( message );
    }

}

package org.uberfire.backend.repositories;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RepositoryAlreadyExistsException extends RuntimeException {

    public RepositoryAlreadyExistsException() {
        super();
    }

    public RepositoryAlreadyExistsException( final String alias ) {
        super( alias );
    }

}

package org.uberfire.shared.repository;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RepositoryAlreadyExists extends RuntimeException {

    public RepositoryAlreadyExists() {
    }

    public RepositoryAlreadyExists( final String name ) {
        super( name );
    }
}

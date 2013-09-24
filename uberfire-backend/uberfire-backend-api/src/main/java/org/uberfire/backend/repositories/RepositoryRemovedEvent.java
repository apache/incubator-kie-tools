package org.uberfire.backend.repositories;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RepositoryRemovedEvent {

    private Repository repository;

    public RepositoryRemovedEvent() {
    }

    public RepositoryRemovedEvent( final Repository repository ) {
        this.repository = repository;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository( final Repository repository ) {
        this.repository = repository;
    }
}

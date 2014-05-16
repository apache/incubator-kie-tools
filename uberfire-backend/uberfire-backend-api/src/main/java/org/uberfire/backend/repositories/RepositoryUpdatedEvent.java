package org.uberfire.backend.repositories;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RepositoryUpdatedEvent {

    private Repository repository;

    private Repository updatedRepository;

    public RepositoryUpdatedEvent() {
    }

    public RepositoryUpdatedEvent(final Repository repository, final Repository updatedRepository) {
        this.repository = repository;
        this.updatedRepository = updatedRepository;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository( final Repository repository ) {
        this.repository = repository;
    }

    public Repository getUpdatedRepository() {
        return updatedRepository;
    }

    public void setUpdatedRepository( final Repository updatedRepository ) {
        this.updatedRepository = updatedRepository;
    }
}

package org.uberfire.backend.repositories;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class NewRepositoryEvent {

    private Repository newRepository;

    public NewRepositoryEvent() {
    }

    public NewRepositoryEvent( final Repository newRepository ) {
        this.newRepository = newRepository;
    }

    public Repository getNewRepository() {
        return newRepository;
    }

    public void setNewRepository( final Repository newRepository ) {
        this.newRepository = newRepository;
    }
}

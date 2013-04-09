package org.uberfire.backend.repositories;

import java.util.Collection;

import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface RepositoryService {

    Repository getRepository( String alias );

    Collection<Repository> getRepositories();

    void createRepository( final String scheme,
                           final String alias,
                           final String userName,
                           final String password );

    void cloneRepository( final String scheme,
                          final String alias,
                          final String origin,
                          final String userName,
                          final String password );

}

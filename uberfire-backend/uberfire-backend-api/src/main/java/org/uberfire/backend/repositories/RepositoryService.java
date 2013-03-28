package org.uberfire.backend.repositories;

import java.util.Collection;

import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface RepositoryService {

    Repository getRepository( String alias );

    Collection<Repository> getRepositories();

    void createRepository( String scheme,
                           String alias,
                           String userName,
                           String password );

    void cloneRepository( String scheme,
                          String alias,
                          String origin,
                          String userName,
                          String password );

}

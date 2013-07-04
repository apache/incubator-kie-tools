package org.uberfire.backend.repositories;

import java.util.Collection;
import java.util.Map;

import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface RepositoryService {

    Repository getRepository( final String alias );

    Collection<Repository> getRepositories();

    Repository createRepository( final String scheme,
                                 final String alias,
                                 final Map<String, Object> env );

    void addRole( final Repository repository,
                  final String role );

    void removeRole( final Repository repository,
                     final String role );

}

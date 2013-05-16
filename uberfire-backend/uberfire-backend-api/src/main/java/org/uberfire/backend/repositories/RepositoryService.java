package org.uberfire.backend.repositories;

import java.util.Collection;
import java.util.Map;

import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface RepositoryService {

    Repository getRepository( String alias );

    Collection<Repository> getRepositories();

    void createRepository( final String scheme,
                           final String alias,
                           final Map<String, Object> env );
}

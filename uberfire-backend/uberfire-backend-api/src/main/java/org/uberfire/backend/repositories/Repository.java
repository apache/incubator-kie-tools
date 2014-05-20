package org.uberfire.backend.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.uberfire.backend.vfs.Path;
import org.uberfire.security.authz.RuntimeResource;
import org.uberfire.commons.data.Cacheable;

public interface Repository extends RuntimeResource, Cacheable {

    String getAlias();

    String getScheme();

    Map<String, Object> getEnvironment();

    void addEnvironmentParameter( final String key,
                                  final Object value );

    boolean isValid();

    String getUri();

    List<PublicURI> getPublicURIs();

    Path getRoot();

    void setRoot( final Path root );

    /**
     * Returns "read-only" view of all branches available in this repository.
     * @return
     */
    Collection<String> getBranches();

    /**
     * Returns current branch that is configured for this repository.
     * It will always provide branch name even if there was no explicit
     * branch selected/created - which in that case is always 'master'
     * @return
     */
    String getCurrentBranch();
}

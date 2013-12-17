package org.uberfire.backend.repositories;

import java.util.List;
import java.util.Map;

import org.uberfire.backend.vfs.Path;
import org.uberfire.security.authz.RuntimeResource;

public interface Repository extends RuntimeResource {

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

}

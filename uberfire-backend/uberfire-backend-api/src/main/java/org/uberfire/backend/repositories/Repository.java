package org.uberfire.backend.repositories;

import java.util.Map;

import org.uberfire.backend.vfs.Path;

public interface Repository {

    String getAlias();

    String getScheme();

    Map<String, Object> getEnvironment();

    void addEnvironmentParameter( final String key,
                                  final Object value );

    boolean isValid();

    String getUri();

    Path getRoot();

}

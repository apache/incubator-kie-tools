package org.uberfire.backend.repositories;

import java.net.URI;
import java.util.Map;

public interface Repository {

    String getAlias();

    String getScheme();

    Map<String, Object> getEnvironment();

    void addEnvironmentParameter( final String key,
                                  final Object value );

    boolean isValid();

    URI getUri();

}

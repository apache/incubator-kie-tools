package org.uberfire.backend.server.repositories;

import java.util.HashMap;
import java.util.Map;

import org.uberfire.backend.repositories.impl.git.GitRepository;

/**
 * Definition of the bootstrap repository
 */
public class SystemRepository extends GitRepository {

    private static final String ALIAS = "system";

    public static final SystemRepository SYSTEM_REPO = new SystemRepository( ALIAS );

    private final Map<String, Object> environment = new HashMap<String, Object>();

    private SystemRepository( final String alias ) {
        super( alias );
        environment.put( "init", Boolean.TRUE );
    }

    @Override
    public Map<String, Object> getEnvironment() {
        return environment;
    }

    @Override
    public void addEnvironmentParameter( final String key,
                                         final Object value ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getUri() {
        return getScheme() + "://" + getAlias();
    }

    @Override
    public boolean isValid() {
        return true;
    }

}

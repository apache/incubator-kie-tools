package org.uberfire.backend.server.repositories;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.uberfire.backend.repositories.Repository;

/**
 * Definition of the bootstrap repository
 */
public class DefaultSystemRepository implements Repository {

    private static final String ALIAS = "system";

    public static final String SCHEME = "git";

    private Map<String, Object> environment = new HashMap<String, Object>() {{
        put( "init",
             Boolean.TRUE );
    }};

    @Override
    public String getAlias() {
        return ALIAS;
    }

    @Override
    public String getScheme() {
        return SCHEME;
    }

    @Override
    public Map<String, Object> getEnvironment() {
        return Collections.unmodifiableMap( environment );
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

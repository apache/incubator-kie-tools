package org.uberfire.backend.server.repositories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.uberfire.backend.repositories.impl.git.GitRepository;

import static java.util.Collections.*;

/**
 * Definition of the bootstrap repository
 */
public class SystemRepository extends GitRepository {

    private static final String ALIAS = "system";

    private static final Collection<String> roles = new ArrayList<String>( 1 ) {{
        add( "admin" );
    }};

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

    @Override
    public Collection<String> getRoles() {
        return unmodifiableCollection( roles );
    }
}

package org.uberfire.backend.server.repositories;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.repositories.Repository;

import static org.kie.commons.validation.Preconditions.*;

@Portable
public class GitRepository implements Repository {

    private String alias;
    private Map<String, Object> environment = new HashMap<String, Object>();

    public static final String SCHEME = "git";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String ORIGIN = "origin";

    public GitRepository() {
    }

    public GitRepository( final String alias ) {
        checkNotNull( "alias", alias );
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return this.alias;
    }

    @Override
    public String getScheme() {
        return SCHEME;
    }

    @Override
    public Map<String, Object> getEnvironment() {
        return this.environment;
    }

    @Override
    public void addEnvironmentParameter( final String key,
                                         final Object value ) {
        checkNotNull( "key", key );
        checkNotNull( "value", value );
        this.environment.put( key, value );
    }

    @Override
    public String getUri() {
        return getScheme() + "://" + getAlias();
    }

    @Override
    public boolean isValid() {
        final Object username = environment.get( USERNAME );
        final Object password = environment.get( PASSWORD );
        final Object origin = environment.get( ORIGIN );
        return alias != null &&
                username != null &&
                password != null &&
                origin != null;
    }

}

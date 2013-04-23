package org.uberfire.backend.server.repositories.git;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.server.repositories.EnvironmentParameters;

import static org.kie.commons.validation.Preconditions.*;

@Portable
public class LocalGitRepository implements Repository {

    private String alias;
    private Map<String, Object> environment = new HashMap<String, Object>();

    public static final String SCHEME = "git";

    public LocalGitRepository() {
    }

    public LocalGitRepository( final String alias ) {
        checkNotNull( "alias",
                      alias );
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
        final Object username = environment.get( EnvironmentParameters.USER_NAME );
        final Object password = environment.get( EnvironmentParameters.USER_PASSWORD );
        return alias != null &&
                username != null &&
                password != null;
    }

}

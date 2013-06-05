package org.uberfire.backend.repositories.impl.git;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.vfs.Path;

@Portable
public class GitRepository implements Repository {

    public static final String SCHEME = "git";
    private static final String USER_NAME = "username";
    private static final String USER_PASSWORD = "password";

    private String alias = null;
    private final Map<String, Object> environment = new HashMap<String, Object>();
    private Path root;

    private Collection<String> roles = new ArrayList<String>();

    public GitRepository() {
    }

    public GitRepository( final String alias ) {
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public String getScheme() {
        return SCHEME;
    }

    @Override
    public Map<String, Object> getEnvironment() {
        return environment;
    }

    @Override
    public void addEnvironmentParameter( String key,
                                         Object value ) {
        environment.put( key, value );
    }

    public void setRoot( final Path root ) {
        this.root = root;
    }

    @Override
    public Path getRoot() {
        return root;
    }

    @Override
    public boolean isValid() {
        final Object username = environment.get( USER_NAME );
        final Object password = environment.get( USER_PASSWORD );
        return alias != null &&
                username != null &&
                password != null;
    }

    @Override
    public String getUri() {
        return getScheme() + "://" + getAlias();
    }

    @Override
    public String getSignatureId() {
        return getClass().getName() + "#" + getUri();
    }

    @Override
    public Collection<String> getRoles() {
        return roles;
    }

    public void setRoles( Collection<String> roles ) {
        this.roles = roles;
    }

    @Override
    public Collection<String> getTraits() {
        return Collections.emptySet();
    }

}

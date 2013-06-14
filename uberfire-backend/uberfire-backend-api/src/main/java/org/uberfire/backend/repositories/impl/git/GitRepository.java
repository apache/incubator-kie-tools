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

    private String alias = null;
    private final Map<String, Object> environment = new HashMap<String, Object>();
    private Path root;

    private Collection<String> roles = new ArrayList<String>();
    private String publicURI;

    public GitRepository() {
    }

    public GitRepository( final String alias ) {
        this.alias = alias;
    }

    public GitRepository( final String alias,
                          final String publicURI ) {
        this.alias = alias;
        this.publicURI = publicURI;
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
        return alias != null;
    }

    @Override
    public String getUri() {
        return getScheme() + "://" + getAlias();
    }

    @Override
    public String getPublicUri() {
        return publicURI;
    }

    @Override
    public String getSignatureId() {
        return getClass().getName() + "#" + getUri();
    }

    public void setPublicUri( String publicURI ) {
        this.publicURI = publicURI;
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

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof GitRepository ) ) {
            return false;
        }

        GitRepository that = (GitRepository) o;

        if ( alias != null ? !alias.equals( that.alias ) : that.alias != null ) {
            return false;
        }
        if ( !environment.equals( that.environment ) ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = alias != null ? alias.hashCode() : 0;
        result = 31 * result + ( environment.hashCode() );
        return result;
    }

}
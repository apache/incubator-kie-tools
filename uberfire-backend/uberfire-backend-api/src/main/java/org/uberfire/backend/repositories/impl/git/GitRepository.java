package org.uberfire.backend.repositories.impl.git;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.repositories.PublicURI;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.vfs.Path;

@Portable
public class GitRepository implements Repository {

    public static final String SCHEME = "git";

    private final Map<String, Object> environment = new HashMap<String, Object>();
    private final List<PublicURI> publicURIs = new ArrayList<PublicURI>();

    private String alias = null;
    private Path root;

    private Collection<String> roles = new ArrayList<String>();

    public GitRepository() {
    }

    public GitRepository( final String alias ) {
        this.alias = alias;
    }

    public GitRepository( final String alias,
                          final List<PublicURI> publicURIs ) {
        this.alias = alias;
        if ( publicURIs != null && !publicURIs.isEmpty() ) {
            this.publicURIs.addAll( publicURIs );
        }
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
    public List<PublicURI> getPublicURIs() {
        return publicURIs;
    }

    public void setPublicURIs( final List<PublicURI> publicURIs ) {
        if ( publicURIs != null && !publicURIs.isEmpty() ) {
            this.publicURIs.clear();
            this.publicURIs.addAll( publicURIs );
        }
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

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof GitRepository ) ) {
            return false;
        }

        final GitRepository that = (GitRepository) o;

        if ( alias != null ? !alias.equals( that.alias ) : that.alias != null ) {
            return false;
        }
        if ( !environment.equals( that.environment ) ) {
            return false;
        }
        if ( !publicURIs.equals( that.publicURIs ) ) {
            return false;
        }
        if ( roles != null ? !roles.equals( that.roles ) : that.roles != null ) {
            return false;
        }
        if ( root != null ? !root.equals( that.root ) : that.root != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = environment.hashCode();
        result = 31 * result + ( publicURIs.hashCode() );
        result = 31 * result + ( alias != null ? alias.hashCode() : 0 );
        result = 31 * result + ( root != null ? root.hashCode() : 0 );
        result = 31 * result + ( roles != null ? roles.hashCode() : 0 );
        return result;
    }

    @Override
    public String toString() {
        return "GitRepository [alias=" + alias + ", environment=" + environment + ", root=" + root + ", roles=" + roles
                + ", publicURI=" + publicURIs + "]";
    }

}
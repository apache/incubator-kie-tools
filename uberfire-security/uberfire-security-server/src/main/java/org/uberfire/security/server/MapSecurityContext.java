package org.uberfire.security.server;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.security.Resource;
import org.uberfire.security.SecurityContext;

public abstract class MapSecurityContext implements SecurityContext {

    protected final Resource resource;
    protected final Map<String, Object> content = new HashMap<String, Object>();
    protected User user;

    public MapSecurityContext( final Resource resource ) {
        this.resource = resource;
    }

    public void put( final String key,
                     final Object value ) {
        content.put( key, value );
    }

    public Object get( String key ) {
        return content.get( key );
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    public User getCurrentSubject() {
        return user;
    }

    public void setCurrentSubject( final User user ) {
        this.user = user;
    }

}

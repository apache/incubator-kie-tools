package org.uberfire.backend.server.security;

import java.util.HashMap;
import java.util.Map;

import org.uberfire.backend.server.UserRegistry;
import org.uberfire.security.auth.Principal;
import org.uberfire.security.auth.SubjectPropertiesProvider;

public class SubjectRelatedPropertiesProvider implements SubjectPropertiesProvider {

    @Override
    public void initialize( final Map<String, ?> options ) {
    }

    @Override
    public Map<String, String> loadProperties( final Principal principal ) {
        final Map<String, String> properties = new HashMap<String, String>();

        if ( UserRegistry.hasUser( principal.getName() ) ) {
            properties.put( "email", UserRegistry.getEmail( principal.getName() ) );
            properties.put( "full_name", UserRegistry.getFullName( principal.getName() ) );
        }

        return properties;
    }
}

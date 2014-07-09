package org.uberfire.security.server.auth.impl;

import java.util.HashMap;
import javax.enterprise.inject.Alternative;

import org.uberfire.security.auth.RolesMode;
import org.uberfire.security.auth.SubjectPropertiesProvider;
import org.uberfire.security.server.auth.BasicUserPassAuthenticationScheme;
import org.uberfire.security.server.auth.source.JAASAuthenticationSource;

import static org.uberfire.security.server.SecurityConstants.*;

@Alternative
public class JAASAuthenticationManager extends SimpleUserPassAuthenticationManager {

    public JAASAuthenticationManager() {
        this( null );
    }

    public JAASAuthenticationManager( final String realm ) {
        this( null, realm, null );
    }

    public JAASAuthenticationManager( final String realm,
                                      final RolesMode mode ) {
        this( null, realm, mode );
    }

    public JAASAuthenticationManager( final SubjectPropertiesProvider propertiesProvider,
                                      final String realm,
                                      final RolesMode mode ) {
        super( new JAASAuthenticationSource(),
               new BasicUserPassAuthenticationScheme(),
               null,
               propertiesProvider,
               new HashMap<String, String>() {{
                   if ( realm != null ) {
                       put( AUTH_DOMAIN_KEY, realm );
                   }
                   if ( mode != null ) {
                       put( ROLE_MODE_KEY, mode.toString() );
                   }
               }} );
    }

}
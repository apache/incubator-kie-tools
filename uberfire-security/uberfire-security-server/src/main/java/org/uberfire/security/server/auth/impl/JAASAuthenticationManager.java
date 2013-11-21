package org.uberfire.security.server.auth.impl;

import java.util.HashMap;

import org.uberfire.security.auth.SubjectPropertiesProvider;
import org.uberfire.security.server.auth.BasicUserPassAuthenticationScheme;
import org.uberfire.security.server.auth.source.JAASAuthenticationSource;

import static org.uberfire.security.server.SecurityConstants.*;

public class JAASAuthenticationManager extends SimpleUserPassAuthenticationManager {

    public JAASAuthenticationManager() {
        this( null );
    }

    public JAASAuthenticationManager( final String realm ) {
        this( null, realm );
    }

    public JAASAuthenticationManager( final SubjectPropertiesProvider propertiesProvider,
                                      final String realm ) {
        super( new JAASAuthenticationSource(),
               new BasicUserPassAuthenticationScheme(),
               null,
               propertiesProvider,
               new HashMap<String, String>() {{
                   if ( realm != null ) {
                       put( AUTH_DOMAIN_KEY, realm );
                   }
               }} );
    }

}

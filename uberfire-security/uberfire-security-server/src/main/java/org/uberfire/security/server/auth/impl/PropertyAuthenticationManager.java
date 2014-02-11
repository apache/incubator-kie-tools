package org.uberfire.security.server.auth.impl;

import java.util.Collections;

import javax.enterprise.inject.Alternative;

import org.uberfire.security.auth.SubjectPropertiesProvider;
import org.uberfire.security.server.auth.BasicUserPassAuthenticationScheme;
import org.uberfire.security.server.auth.source.PropertyUserSource;

@Alternative
public class PropertyAuthenticationManager extends SimpleUserPassAuthenticationManager {

    public PropertyAuthenticationManager( final SubjectPropertiesProvider propertiesProvider ) {
        super( new PropertyUserSource(),
               new BasicUserPassAuthenticationScheme(),
               null,
               propertiesProvider,
               Collections.<String, String>emptyMap() );
    }

}

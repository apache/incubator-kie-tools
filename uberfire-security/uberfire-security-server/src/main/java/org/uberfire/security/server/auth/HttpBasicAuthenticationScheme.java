/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.security.server.auth;

import com.google.common.base.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.uberfire.security.SecurityContext;
import org.uberfire.security.auth.AuthenticationScheme;
import org.uberfire.security.auth.Credential;
import org.uberfire.security.impl.auth.UsernamePasswordCredential;
import org.uberfire.security.server.HttpSecurityContext;

import static org.uberfire.commons.validation.Preconditions.*;

public class HttpBasicAuthenticationScheme implements AuthenticationScheme {

    public boolean isAuthenticationRequest( final SecurityContext context ) {
        final HttpSecurityContext httpSecurityContext = checkInstanceOf( "context", context, HttpSecurityContext.class );

        return httpSecurityContext.getRequest().getHeader( "Authorization" ) != null;
    }

    @Override
    public void challengeClient( SecurityContext context ) {
    }

    public Credential buildCredential( final SecurityContext context ) {
        final HttpSecurityContext httpSecurityContext = checkInstanceOf( "context", context, HttpSecurityContext.class );

        final String auth = httpSecurityContext.getRequest().getHeader( "Authorization" );
        if ( auth != null ) {
            final int index = auth.indexOf( ' ' );
            if ( index > 0 ) {
                final String[] credentials = new String( Base64.decodeBase64( auth.substring( index ) ), Charsets.UTF_8 ).split( ":" );

                if ( credentials.length == 2 ) {
                    return new UsernamePasswordCredential( credentials[ 0 ], credentials[ 1 ] );
                }
            }
        }

        return null;
    }

}

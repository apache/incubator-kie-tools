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

import javax.servlet.http.Cookie;

import org.uberfire.security.SecurityContext;
import org.uberfire.security.Subject;
import org.uberfire.security.auth.AuthenticatedStorageProvider;
import org.uberfire.security.auth.Principal;
import org.uberfire.security.crypt.CryptProvider;
import org.uberfire.security.server.HttpSecurityContext;
import org.uberfire.security.server.crypt.DefaultCryptProvider;

import static org.kie.commons.validation.Preconditions.*;

public class CookieStorage implements AuthenticatedStorageProvider {

    private static final int           DEFAULT_EXPIRE_48_HOURS = 60 * 60 * 48;
    private static final String        EMPTY                   = "__empty__";
    private static final CryptProvider CRYPT_PROVIDER          = new DefaultCryptProvider();

    private final String cookieName;

    public CookieStorage( final String cookieName ) {
        this.cookieName = checkNotEmpty( "cookieName", cookieName );
    }

    @Override
    public void store( final SecurityContext context,
                       final Subject subject ) {
        final HttpSecurityContext httpContext = checkInstanceOf( "context", context, HttpSecurityContext.class );

        final String content = CRYPT_PROVIDER.encrypt( subject.getName(), null );
        final Cookie securityCookie = new Cookie( cookieName, content );
        securityCookie.setPath( "/" );
        securityCookie.setMaxAge( DEFAULT_EXPIRE_48_HOURS );

        httpContext.getResponse().addCookie( securityCookie );
    }

    @Override
    public void cleanup( final SecurityContext context ) {
        final HttpSecurityContext httpContext = checkInstanceOf( "context", context, HttpSecurityContext.class );

        final Cookie securityCookie = new Cookie( cookieName, EMPTY );
        securityCookie.setPath( "/" );
        securityCookie.setMaxAge( 0 );

        httpContext.getResponse().addCookie( securityCookie );

    }

    public Principal load( final SecurityContext context ) {
        final HttpSecurityContext httpContext = checkInstanceOf( "context", context, HttpSecurityContext.class );
        final String originalCookieValue = getCookieValue( cookieName, null, httpContext.getRequest().getCookies() );

        if ( originalCookieValue == null ) {
            return null;
        }

        final String userId = CRYPT_PROVIDER.decrypt( originalCookieValue, null );
        if ( userId == null ) {
            return null;
        }

        return new Principal() {
            @Override
            public String getName() {
                return userId;
            }
        };
    }

    private String getCookieValue( final String cookieName,
                                   final String defaultValue,
                                   final Cookie... cookies ) {
        if ( cookies == null || cookies.length == 0 ) {
            return defaultValue;
        }

        for ( final Cookie cookie : cookies ) {
            if ( cookieName.equals( cookie.getName() ) ) {
                return cookie.getValue();
            }
        }
        return defaultValue;
    }
}

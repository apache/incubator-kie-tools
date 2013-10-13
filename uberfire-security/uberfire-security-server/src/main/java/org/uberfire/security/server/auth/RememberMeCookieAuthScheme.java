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

import org.uberfire.security.SecurityContext;
import org.uberfire.security.auth.AuthenticationScheme;
import org.uberfire.security.auth.Credential;
import org.uberfire.security.auth.Principal;
import org.uberfire.security.server.HttpSecurityContext;

import static java.lang.Boolean.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;
import static org.uberfire.commons.validation.Preconditions.*;

public class RememberMeCookieAuthScheme implements AuthenticationScheme {

    final CookieStorage cookieStorage;

    public RememberMeCookieAuthScheme( final CookieStorage cookieStorage ) {
        this.cookieStorage = checkNotNull( "cookieStorage", cookieStorage );
    }

    public boolean isAuthenticationRequest( final SecurityContext context ) {
        final HttpSecurityContext httpSecurityContext = checkInstanceOf( "context", context, HttpSecurityContext.class );
        return cookieStorage.load( httpSecurityContext ) != null;
    }

    @Override
    public void challengeClient( final SecurityContext context ) {
    }

    public Credential buildCredential( final SecurityContext context ) {
        final HttpSecurityContext httpSecurityContext = checkInstanceOf( "context", context, HttpSecurityContext.class );
        final Principal principal = cookieStorage.load( httpSecurityContext );

        if ( principal == null ) {
            return null;
        }

        return new RememberMeCredential( TRUE.toString(), principal.getName() );
    }

    static class RememberMeCredential implements Credential {

        private final String userId;
        private final boolean rememberForLater;

        public RememberMeCredential( final String rememberForLater,
                                     final String userId ) {
            if ( rememberForLater == null ) {
                this.rememberForLater = false;
            } else {
                this.rememberForLater = Boolean.valueOf( rememberForLater );
            }

            this.userId = userId;
        }

        public String getUserId() {
            return userId;
        }

        public boolean rememberForLater() {
            return rememberForLater;
        }
    }

}

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

package org.uberfire.security.server.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.uberfire.security.authz.AuthorizationManager;

public class SecurityFactory {

    private static final ThreadLocal<User> users = new ThreadLocal<User>();

    static private AuthorizationManager authzManager = null;

    public static void setSubject( final User user ) {
        users.set( user );
    }

    public static void setAuthzManager( final AuthorizationManager authzManager ) {
        SecurityFactory.authzManager = authzManager;
    }

    @Produces
    @RequestScoped
    public static User getIdentity() {
        if ( users.get() == null ) {
            return User.ANONYMOUS;
        }
        return new UserImpl( users.get().getIdentifier(), users.get().getRoles(), users.get().getProperties() );
    }

    @Produces
    @ApplicationScoped
    @AppResourcesAuthz
    public static AuthorizationManager getAuthzManager() {
        return authzManager;
    }

}

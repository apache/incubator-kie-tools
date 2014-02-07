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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

import org.uberfire.security.Identity;
import org.uberfire.security.Role;
import org.uberfire.security.Subject;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.impl.IdentityImpl;
import org.uberfire.security.impl.RoleImpl;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;

public class SecurityFactory {

    private static final List<Role> ANONYMOUS_ROLE = new ArrayList<Role>() {{
        add( new RoleImpl( Identity.ANONYMOUS ) );
    }};
    private static final ThreadLocal<Subject> subjects = new ThreadLocal<Subject>();

    static private RuntimeAuthorizationManager authzManager = null;

    public static void setSubject( final Subject subject ) {
        subjects.set( subject );
    }

    public static void setAuthzManager( final RuntimeAuthorizationManager authzManager ) {
        SecurityFactory.authzManager = authzManager;
    }

    @Produces
    @RequestScoped
    public static Identity getIdentity() {
        if ( subjects.get() == null ) {
            return new IdentityImpl( Identity.ANONYMOUS, ANONYMOUS_ROLE, Collections.<String, String>emptyMap() );
        }
        return new IdentityImpl( subjects.get().getName(), subjects.get().getRoles(), subjects.get().getProperties() );
    }

    @Produces
    @ApplicationScoped
    public static RuntimeAuthorizationManager getAuthzManager() {
        return authzManager;
    }

}

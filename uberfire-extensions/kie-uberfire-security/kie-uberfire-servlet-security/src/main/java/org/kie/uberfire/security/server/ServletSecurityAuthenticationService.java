/**
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.uberfire.security.server;

import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.kie.uberfire.security.server.adapter.GroupsAdapter;

@Service
@ApplicationScoped
public class ServletSecurityAuthenticationService implements AuthenticationService {

    private ServiceLoader<GroupsAdapter> groupsAdapterServiceLoader = ServiceLoader.load( GroupsAdapter.class );

    private static final String USER_SESSION_ATTR_NAME = "kie.uf.security.user";

    @Override
    public User login( String username,
                       String password ) {
        throw new UnsupportedOperationException( "Logins must be handled by the servlet container (use login-config in web.xml)." );
    }

    @Override
    public boolean isLoggedIn() {
        HttpServletRequest request = getRequestForThread();
        return request.getUserPrincipal() != null;
    }

    @Override
    public void logout() {
        HttpServletRequest request = getRequestForThread();
        request.getSession().invalidate();
    }

    @Override
    public User getUser() {
        HttpServletRequest request = getRequestForThread();

        if ( request.getUserPrincipal() == null ) {
            return null;
        }

        final HttpSession session = request.getSession( false );
        User user = (User) session.getAttribute( USER_SESSION_ATTR_NAME );
        if ( user == null ) {
            final Set<Role> userRoles = new HashSet<Role>();
            for ( final Role checkRole : RolesRegistry.get().getRegisteredRoles() ) {
                if ( request.isUserInRole( checkRole.getName() ) ) {
                    userRoles.add( checkRole );
                }
            }

            final Set<Group> userGroups = new HashSet<Group>();
            for ( final GroupsAdapter adapter : groupsAdapterServiceLoader ) {
                final List<Group> groupRoles = adapter.getGroups( request.getUserPrincipal().getName() );
                if ( groupRoles != null ) {
                    userGroups.addAll( groupRoles );
                }
            }

            user = new UserImpl( request.getUserPrincipal().getName(), userRoles, userGroups );
            session.setAttribute( USER_SESSION_ATTR_NAME, user );
        }

        return user;
    }

    private HttpServletRequest getRequestForThread() {
        HttpServletRequest request = SecurityIntegrationFilter.getRequest();
        if ( request == null ) {
            throw new IllegalStateException( "This service only works from threads that are handling HTTP servlet requests" );
        }
        return request;
    }

}
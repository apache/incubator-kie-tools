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

import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.GroupImpl;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.jboss.errai.security.shared.exception.FailedAuthenticationException;
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
        final HttpServletRequest request = getRequestForThread();

        try {
            request.login( username, password );
            return getUser();
        } catch ( final ServletException e ) {
            throw new FailedAuthenticationException();
        }
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

            final String name = request.getUserPrincipal().getName();

            final Set<Group> userGroups = new HashSet<Group>( loadGroups( name ) );
            for ( final GroupsAdapter adapter : groupsAdapterServiceLoader ) {
                final List<Group> groupRoles = adapter.getGroups( name );
                if ( groupRoles != null ) {
                    userGroups.addAll( groupRoles );
                }
            }

            user = new UserImpl( name, userRoles, userGroups );
            session.setAttribute( USER_SESSION_ATTR_NAME, user );
        }

        return user;
    }

    private Set<Group> loadGroups( final String rolePrincipleName ) {

        Subject subject;
        try {
            subject = (Subject) PolicyContext.getContext( "javax.security.auth.Subject.container" );
        } catch ( final Exception e ) {
            subject = null;
        }
        if ( subject == null ) {
            return Collections.emptySet();
        }

        final Set<Group> result = new HashSet<Group>();

        final Set<java.security.Principal> principals = subject.getPrincipals();

        if ( principals != null && !principals.isEmpty() ) {
            for ( java.security.Principal p : principals ) {
                if ( p instanceof java.security.acl.Group && rolePrincipleName.equalsIgnoreCase( p.getName() ) ) {
                    final Enumeration<? extends Principal> groups = ( (java.security.acl.Group) p ).members();

                    while ( groups.hasMoreElements() ) {
                        final java.security.Principal groupPrincipal = groups.nextElement();
                        result.add( new GroupImpl( groupPrincipal.getName() ) );
                    }
                    break;
                }
            }
        }

        return result;
    }

    private HttpServletRequest getRequestForThread() {
        HttpServletRequest request = SecurityIntegrationFilter.getRequest();
        if ( request == null ) {
            throw new IllegalStateException( "This service only works from threads that are handling HTTP servlet requests" );
        }
        return request;
    }

}
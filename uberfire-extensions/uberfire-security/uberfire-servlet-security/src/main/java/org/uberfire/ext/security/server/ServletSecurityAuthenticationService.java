/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.security.server;

import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
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
import org.uberfire.backend.server.security.adapter.GroupAdapterAuthorizationSource;

@Service
@ApplicationScoped
public class ServletSecurityAuthenticationService extends GroupAdapterAuthorizationSource implements AuthenticationService {

    private static final String USER_SESSION_ATTR_NAME = "uf.security.user";
    private static final String DEFAULT_ROLE_PRINCIPLE_NAME = "Roles";



    private String[] rolePrincipleNames = new String[]{ DEFAULT_ROLE_PRINCIPLE_NAME };

    public ServletSecurityAuthenticationService() {
        final String value = System.getProperty( "org.uberfire.security.principal.names", "" );
        if ( value != null && !value.trim().isEmpty() ) {
            rolePrincipleNames = value.split( "," );
        }
    }

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
        try {
            request.logout();
        } catch (Exception e) {

        }
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    @Override
    public User getUser() {
        HttpServletRequest request = getRequestForThread();

        if ( request.getUserPrincipal() == null ) {
            return null;
        }
        User user = null;
        final HttpSession session = request.getSession();
        if ( session != null ) {
            user = (User) session.getAttribute( USER_SESSION_ATTR_NAME );
            if ( user == null ) {
                final Set<Role> userRoles = new HashSet<Role>();
                for ( final Role checkRole : RolesRegistry.get().getRegisteredRoles() ) {
                    if ( request.isUserInRole( checkRole.getName() ) ) {
                        userRoles.add( checkRole );
                    }
                }

                final String name = request.getUserPrincipal().getName();

                final Set<Group> userGroups = new HashSet<Group>( loadGroups() );
                Set<Group> rolesFromAdapters = collectGroups(name);
                if (rolesFromAdapters != null && !rolesFromAdapters.isEmpty()) {
                    userGroups.addAll(rolesFromAdapters);
                }

                user = new UserImpl( name, userRoles, userGroups );
                session.setAttribute( USER_SESSION_ATTR_NAME, user );
            }
        }

        return user;
    }

    private Set<Group> loadGroups() {

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
                if ( p instanceof java.security.acl.Group ) {
                    for ( final String rolePrincipleName : rolePrincipleNames ) {
                        if ( rolePrincipleName.equalsIgnoreCase( p.getName() ) ) {
                            final Enumeration<? extends Principal> groups = ( (java.security.acl.Group) p ).members();

                            while ( groups.hasMoreElements() ) {
                                final java.security.Principal groupPrincipal = groups.nextElement();
                                result.add( new GroupImpl( groupPrincipal.getName() ) );
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    protected static HttpServletRequest getRequestForThread() {
        HttpServletRequest request = SecurityIntegrationFilter.getRequest();
        if ( request == null ) {
            throw new IllegalStateException( "This service only works from threads that are handling HTTP servlet requests" );
        }
        return request;
    }

}
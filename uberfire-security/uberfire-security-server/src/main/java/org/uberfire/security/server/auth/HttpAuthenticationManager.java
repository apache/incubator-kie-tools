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

import static org.uberfire.commons.validation.PortablePreconditions.*;
import static org.uberfire.commons.validation.Preconditions.*;
import static org.uberfire.security.auth.AuthenticationStatus.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Alternative;
import javax.servlet.ServletException;

import org.uberfire.security.ResourceManager;
import org.uberfire.security.Role;
import org.uberfire.security.SecurityContext;
import org.uberfire.security.Subject;
import org.uberfire.security.auth.AuthenticatedStorageProvider;
import org.uberfire.security.auth.AuthenticationException;
import org.uberfire.security.auth.AuthenticationManager;
import org.uberfire.security.auth.AuthenticationProvider;
import org.uberfire.security.auth.AuthenticationResult;
import org.uberfire.security.auth.AuthenticationScheme;
import org.uberfire.security.auth.Credential;
import org.uberfire.security.auth.Principal;
import org.uberfire.security.auth.RoleProvider;
import org.uberfire.security.auth.SubjectPropertiesProvider;
import org.uberfire.security.impl.SubjectImpl;
import org.uberfire.security.server.HttpSecurityContext;

@Alternative
public class HttpAuthenticationManager implements AuthenticationManager {

    private final List<AuthenticationScheme> authSchemes;
    private final List<AuthenticationProvider> authProviders;
    private final List<RoleProvider> roleProviders;
    private final List<SubjectPropertiesProvider> subjectPropertiesProviders;
    private final List<AuthenticatedStorageProvider> authStorageProviders;

    private final ResourceManager resourceManager;
    private final Map<String, String> requestCache = new HashMap<String, String>();
    private final String forceURL;

    //if System.getProperty("java.security.auth.login.config") != null => create a JAASProvider

    public HttpAuthenticationManager( final List<AuthenticationScheme> authScheme,
                                      final String forceURL,
                                      final List<AuthenticationProvider> authProviders,
                                      final List<RoleProvider> roleProviders,
                                      final List<SubjectPropertiesProvider> subjectPropertiesProviders,
                                      final List<AuthenticatedStorageProvider> authStorageProviders,
                                      final ResourceManager resourceManager ) {
        this.forceURL = forceURL;
        this.authSchemes = checkNotEmpty( "authScheme", authScheme );
        this.authProviders = checkNotEmpty( "authProviders", authProviders );
        this.roleProviders = checkNotEmpty( "roleProviders", roleProviders );
        this.subjectPropertiesProviders = checkNotNull( "subjectPropertiesProviders", subjectPropertiesProviders );
        this.authStorageProviders = checkNotEmpty( "authStorageProviders", authStorageProviders );
        this.resourceManager = checkNotNull( "resourceManager", resourceManager );
    }

    @Override
    public Subject authenticate( final SecurityContext context ) throws AuthenticationException {
        final HttpSecurityContext httpContext = checkInstanceOf( "context", context, HttpSecurityContext.class );

        if ( !resourceManager.requiresAuthentication( httpContext.getResource() ) ) {
            return null;
        }

        saveTargetUrlForAfterAuthentication( httpContext );

        Principal principal = null;

        all_auth:
            for ( final AuthenticationScheme authScheme : authSchemes ) {
                if ( !authScheme.isAuthenticationRequest( httpContext ) ) {
                    continue;
                }

                final Credential credential = authScheme.buildCredential( httpContext );

                if ( credential == null ) {
                    continue;
                }

                for ( final AuthenticationProvider authProvider : authProviders ) {
                    final AuthenticationResult result = authProvider.authenticate( credential, context );
                    if ( result.getStatus().equals( FAILED ) ) {
                        authScheme.challengeClient( httpContext );
                        throw new AuthenticationException( "Invalid credentials." );
                    } else if ( result.getStatus().equals( SUCCESS ) ) {
                        principal = result.getPrincipal();
                        break all_auth;
                    }
                }
            }

        // since this wasn't a login attempt but the resource requires authentication, look for cached auth info
        if ( principal == null ) {
            for ( final AuthenticatedStorageProvider storeProvider : authStorageProviders ) {
                Subject subjectInSession = storeProvider.load( httpContext );
                if ( subjectInSession != null ) {

                    // return immediately; we should not attempt to build up a new Subject
                    return subjectInSession;
                }
            }
        }

        if ( principal == null ) {
            throw new AuthenticationException( "Invalid credentials." );
        }

        final List<Role> roles = new ArrayList<Role>();

        for ( final RoleProvider roleProvider : roleProviders ) {
            roles.addAll( roleProvider.loadRoles( principal ) );
        }

        final Map<String, String> properties = new HashMap<String, String>();
        for ( final SubjectPropertiesProvider propertiesProvider : subjectPropertiesProviders ) {
            properties.putAll( propertiesProvider.loadProperties( principal ) );
        }

        final String name = principal.getName();
        final Subject result = new SubjectImpl( name, roles, properties );

        for ( final AuthenticatedStorageProvider storeProvider : authStorageProviders ) {
            storeProvider.store( httpContext, result );
        }

        final String originalRequest = requestCache.remove( httpContext.getRequest().getSession().getId() );
        if ( originalRequest != null && !originalRequest.isEmpty()
                && !isSameLocation( originalRequest, httpContext.getRequestURI() )
                && !httpContext.getResponse().isCommitted() ) {
            try {
                httpContext.getResponse().sendRedirect( originalRequest );
            } catch ( Exception e ) {
                throw new RuntimeException( "Unable to redirect.", e );
            }
        }

        return result;
    }

    /**
     * Compares the two URL or URI fragments ignoring their query strings.
     * 
     * @return true if both requests are for the same location without considering their query parameters.
     */
    private boolean isSameLocation( String originalRequest, String currentRequest ) {
        int queryIdx = originalRequest.indexOf( '?' );
        if (queryIdx >= 0) {
            originalRequest = originalRequest.substring( 0, queryIdx );
        }
        queryIdx = currentRequest.indexOf( '?' );
        if ( queryIdx >= 0) {
            currentRequest = currentRequest.substring( 0, queryIdx );
        }
        return originalRequest.equals( currentRequest );
    }

    private void saveTargetUrlForAfterAuthentication( final HttpSecurityContext httpContext ) {
        if ( !requestCache.containsKey( httpContext.getRequest().getSession().getId() ) ) {

            String preservedQueryStr = httpContext.getRequest().getQueryString();

            if ( preservedQueryStr == null ) {
                preservedQueryStr = "";
            } else {
                preservedQueryStr = "?" + preservedQueryStr;
            }

            // this is for the benefit of dev mode logins: the uf_security_check form
            // won't have the gwt.codeserver parameter on it, but the referer will
            String referer = httpContext.getRequest().getHeader( "Referer" );
            if ( preservedQueryStr.equals( "" ) && referer != null && referer.indexOf( '?' ) >= 0 ) {
                preservedQueryStr = referer.substring( referer.indexOf( '?' ) );
            }

            if ( forceURL != null ) {

                // prepend context path for context-relative forceURLs
                String contextPrefix = "";
                if ( forceURL.startsWith( "/" ) ) {
                    contextPrefix = httpContext.getRequest().getContextPath();
                }

                requestCache.put( httpContext.getRequest().getSession().getId(), contextPrefix + forceURL + preservedQueryStr );
            } else {
                requestCache.put( httpContext.getRequest().getSession().getId(), httpContext.getRequest().getRequestURI() + preservedQueryStr );
            }
        }
    }

    @Override
    public void logout( final SecurityContext context ) throws AuthenticationException {
        for ( final AuthenticatedStorageProvider storeProvider : authStorageProviders ) {
            storeProvider.cleanup( context );
        }
        final HttpSecurityContext httpContext = checkInstanceOf( "context", context, HttpSecurityContext.class );
        httpContext.getRequest().getSession().invalidate();
        try {
            httpContext.getRequest().logout();
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "HttpAuthenticationManager [authSchemes=" + authSchemes + ", authProviders=" + authProviders
                + ", roleProviders=" + roleProviders + ", subjectPropertiesProviders=" + subjectPropertiesProviders
                + ", authStorageProviders=" + authStorageProviders + ", resourceManager=" + resourceManager
                + ", requestCache=" + requestCache + ", forceURL=" + forceURL + "]";
    }
}

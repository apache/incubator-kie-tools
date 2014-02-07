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

import static org.uberfire.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.commons.validation.Preconditions.checkInstanceOf;
import static org.uberfire.security.Role.ROLE_REMEMBER_ME;
import static org.uberfire.security.auth.AuthenticationStatus.FAILED;
import static org.uberfire.security.auth.AuthenticationStatus.SUCCESS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;

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
import org.uberfire.security.impl.IdentityImpl;
import org.uberfire.security.impl.RoleImpl;
import org.uberfire.security.server.HttpSecurityContext;
import org.uberfire.security.server.SecurityConstants;
import org.uberfire.security.server.cdi.SecurityFactory;

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

        Principal principal = null;
        for ( final AuthenticatedStorageProvider storeProvider : authStorageProviders ) {
            principal = storeProvider.load( httpContext );
            if ( principal != null ) {
                break;
            }
        }

        if ( principal != null && principal instanceof Subject ) {
            return (Subject) principal;
        }

        boolean isRememberOp = principal != null;

        final boolean requiresAuthentication = resourceManager.requiresAuthentication( httpContext.getResource() );

        if ( principal == null ) {
            for ( final AuthenticationScheme authScheme : authSchemes ) {
                if ( authScheme.isAuthenticationRequest( httpContext ) ) {
                    break;
                } else if ( requiresAuthentication ) {
                    if ( !requestCache.containsKey( httpContext.getRequest().getSession().getId() ) ) {

                        String preservedQueryStr = httpContext.getRequest().getQueryString();

                        if (preservedQueryStr == null) {
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
                            if (forceURL.startsWith( "/" )) {
                                contextPrefix = httpContext.getRequest().getContextPath();
                            }

                            requestCache.put( httpContext.getRequest().getSession().getId(), contextPrefix + forceURL + preservedQueryStr );
                        } else {
                            requestCache.put( httpContext.getRequest().getSession().getId(), httpContext.getRequest().getRequestURI() + preservedQueryStr );
                        }
                    }
                    authScheme.challengeClient( httpContext );
                }
            }

            if ( !requiresAuthentication ) {
                return null;
            }

            all_auth:
            for ( final AuthenticationScheme authScheme : authSchemes ) {
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
        }

        if ( principal == null ) {
            throw new AuthenticationException( "Invalid credentials." );
        }

        final List<Role> roles = new ArrayList<Role>();
        if ( isRememberOp ) {
            roles.add( new RoleImpl( ROLE_REMEMBER_ME ) );
        }

        for ( final RoleProvider roleProvider : roleProviders ) {
            roles.addAll( roleProvider.loadRoles( principal ) );
        }

        final Map<String, String> properties = new HashMap<String, String>();
        for ( final SubjectPropertiesProvider propertiesProvider : subjectPropertiesProviders ) {
            properties.putAll( propertiesProvider.loadProperties( principal ) );
        }

        final String name = principal.getName();
        final Subject result = new IdentityImpl( name, roles, properties );

        for ( final AuthenticatedStorageProvider storeProvider : authStorageProviders ) {
            storeProvider.store( httpContext, result );
        }

        final String originalRequest = requestCache.remove( httpContext.getRequest().getSession().getId() );
        if ( originalRequest != null && !originalRequest.isEmpty() && !httpContext.getResponse().isCommitted() ) {
            try {
                if ( useRedirect( originalRequest ) ) {
                    httpContext.getResponse().sendRedirect( originalRequest );
                } else {
                    // subject must be already set here since we forwarding
                    SecurityFactory.setSubject( result );
                    RequestDispatcher rd = httpContext.getRequest().getRequestDispatcher( originalRequest.replaceFirst( httpContext.getRequest().getContextPath(), "" ) );
                    // forward instead of sendRedirect as sendRedirect will always use GET method which
                    // means it can change http method if non GET was used for instance POST
                    rd.forward( httpContext.getRequest(), httpContext.getResponse() );
                }
            } catch ( Exception e ) {
                throw new RuntimeException( "Unable to redirect.", e );
            }
        }

        return result;
    }

    @Override
    public void logout( final SecurityContext context ) throws AuthenticationException {
        for ( final AuthenticatedStorageProvider storeProvider : authStorageProviders ) {
            storeProvider.cleanup( context );
        }
        final HttpSecurityContext httpContext = checkInstanceOf( "context", context, HttpSecurityContext.class );
        httpContext.getRequest().getSession().invalidate();
    }

    private boolean useRedirect( String originalRequest ) {
        // hack for gwt hosted mode
        // and form-based auth
        // TODO perhaps the "hack" should cover the case that forwarding was meant to address?
        return originalRequest.contains( "gwt.codesvr" ) || originalRequest.contains( SecurityConstants.HTTP_FORM_SECURITY_CHECK_URI );
    }

    @Override
    public String toString() {
      return "HttpAuthenticationManager [authSchemes=" + authSchemes + ", authProviders=" + authProviders
              + ", roleProviders=" + roleProviders + ", subjectPropertiesProviders=" + subjectPropertiesProviders
              + ", authStorageProviders=" + authStorageProviders + ", resourceManager=" + resourceManager
              + ", requestCache=" + requestCache + ", forceURL=" + forceURL + "]";
    }


}

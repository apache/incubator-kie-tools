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

package org.uberfire.security.server;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.security.ResourceManager;
import org.uberfire.security.SecurityContext;
import org.uberfire.security.SecurityManager;
import org.uberfire.security.Subject;
import org.uberfire.security.auth.AuthenticationException;
import org.uberfire.security.auth.AuthenticationManager;
import org.uberfire.security.auth.AuthenticationProvider;
import org.uberfire.security.auth.AuthenticationScheme;
import org.uberfire.security.auth.RoleProvider;
import org.uberfire.security.auth.SubjectPropertiesProvider;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.authz.ResourceDecisionManager;
import org.uberfire.security.authz.RoleDecisionManager;
import org.uberfire.security.authz.VotingStrategy;
import org.uberfire.security.impl.authz.ConsensusBasedVoter;
import org.uberfire.security.server.auth.CookieStorage;
import org.uberfire.security.server.auth.FormAuthenticationScheme;
import org.uberfire.security.server.auth.HttpBasicAuthenticationScheme;
import org.uberfire.security.server.auth.HttpSessionStorage;
import org.uberfire.security.server.auth.RememberMeCookieAuthScheme;
import org.uberfire.security.server.cdi.SecurityFactory;

import static javax.servlet.http.HttpServletResponse.*;
import static org.uberfire.security.server.SecurityConstants.*;

public class UberFireSecurityFilter implements Filter {

    private final Logger LOG = LoggerFactory.getLogger( UberFireSecurityFilter.class );
    private SecurityManager securityManager = null;

    @Override
    public void init( final FilterConfig filterConfig )
            throws ServletException {

        final Map<String, String> options = buildOptions( filterConfig );

        final CookieStorage cookieStorage = getCookieStorage( options );
        final AuthenticationScheme basicAuthScheme = new HttpBasicAuthenticationScheme();
        final AuthenticationScheme rememberMeAuthScheme = getRememberMeAuthScheme( options, cookieStorage );
        final AuthenticationScheme authScheme = getAuthenticationScheme( options );
        final AuthenticationManager authManager = getAuthenticationManager( options );
        final AuthenticationProvider authProvider = getAuthenticationProvider( options );
        final ResourceManager resourceManager = getResourceManager( options );
        final AuthorizationManager authzManager = getAuthorizationManager( options );
        final VotingStrategy urlVotingStrategy = getURLVotingStrategy( options );
        final ResourceDecisionManager accessDecisionManager = getURLAccessDecisionManager( options );
        final RoleDecisionManager roleDecisionManager = getRoleDecisionManager( options );
        final RoleProvider roleProvider = getRoleProvider( options );
        final SubjectPropertiesProvider propertiesProvider = getPropertiesProvider( options );

        this.securityManager = HttpSecurityManagerImpl.newBuilder()
                .addAuthManager( authManager )
                .addAuthScheme( basicAuthScheme )
                .addAuthScheme( rememberMeAuthScheme )
                .addAuthScheme( authScheme )
                .addAuthProvider( authProvider )
                .addAuthenticatedStorageProvider( new HttpSessionStorage() )
                .addAuthenticatedStorageProvider( cookieStorage )
                .addRoleProvider( roleProvider )
                .addSubjectPropertiesProvider( propertiesProvider )
                .addAuthzManager( authzManager )
                .addVotingStrategy( urlVotingStrategy )
                .addAccessDecisionManager( accessDecisionManager )
                .addResourceManager( resourceManager )
                .addRoleDecisionManager( roleDecisionManager )
                .loadAvailableAuthenticationSources()
                .build( options );

        securityManager.start();
    }

    private AuthenticationScheme getRememberMeAuthScheme( final Map<String, String> options,
                                                          final CookieStorage cookieStorage ) {
        if ( hasRememerMe( options ) ) {
            return new RememberMeCookieAuthScheme( cookieStorage );
        }

        return null;
    }

    private boolean hasRememerMe( final Map<String, String> options ) {
        final String rememberMe = options.get( AUTH_REMEMBER_ME_SCHEME_KEY );

        return rememberMe == null || rememberMe.trim().equalsIgnoreCase( "enabled" );
    }

    private Map<String, String> buildOptions( FilterConfig filterConfig ) {
        final Map<String, String> result = new HashMap<String, String>();

        final Enumeration initParams = filterConfig.getInitParameterNames();
        while ( initParams.hasMoreElements() ) {
            final String name = (String) initParams.nextElement();
            final String value = filterConfig.getInitParameter( name );
            if ( !value.trim().isEmpty() ) {
                result.put( name, value );
            }
        }

        final Enumeration servletInitParams = filterConfig.getServletContext().getInitParameterNames();
        while ( servletInitParams.hasMoreElements() ) {
            final String name = (String) servletInitParams.nextElement();
            final String value = filterConfig.getServletContext().getInitParameter( name );
            if ( !value.trim().isEmpty() ) {
                result.put( name, value );
            }
        }

        return result;
    }

    private CookieStorage getCookieStorage( final Map<String, String> options ) {
        if ( hasRememerMe( options ) ) {
            final String cookieName = options.get( COOKIE_NAME_KEY );
            if ( cookieName == null || cookieName.trim().isEmpty() ) {
                throw new RuntimeException( "Can't find cookie id." );
            }
            return new CookieStorage( cookieName );
        }
        return null;
    }

    private RoleProvider getRoleProvider( final Map<String, String> options ) {
        final String roleProvider = options.get( ROLE_PROVIDER_KEY );

        return loadConfigClazz( roleProvider, RoleProvider.class );
    }

    private SubjectPropertiesProvider getPropertiesProvider( final Map<String, String> options ) {
        final String propertiesProvider = options.get( SUBJECT_PROPERTIES_PROVIDER_KEY );

        return loadConfigClazz( propertiesProvider, SubjectPropertiesProvider.class );
    }

    private RoleDecisionManager getRoleDecisionManager( final Map<String, String> options ) {
        final String roleManager = options.get( ROLE_DECISION_MANAGER_KEY );

        return loadConfigClazz( roleManager, RoleDecisionManager.class );
    }

    private ResourceDecisionManager getURLAccessDecisionManager( final Map<String, String> options ) {
        final String accessManager = options.get( URL_ACCESS_DECISION_MANAGER_KEY );

        return loadConfigClazz( accessManager, ResourceDecisionManager.class );
    }

    private VotingStrategy getURLVotingStrategy( final Map<String, String> options ) {
        final String votingStrategy = options.get( URL_VOTING_MANAGER_KEY );

        if ( votingStrategy == null || votingStrategy.isEmpty() ) {
            return new ConsensusBasedVoter();
        }

        return loadConfigClazz( votingStrategy, VotingStrategy.class );
    }

    private AuthorizationManager getAuthorizationManager( final Map<String, String> options ) {
        final String autzhManager = options.get( AUTHZ_MANAGER_KEY );

        return loadConfigClazz( autzhManager, AuthorizationManager.class );
    }

    private ResourceManager getResourceManager( final Map<String, String> options ) {
        final String resManager = options.get( RESOURCE_MANAGER_KEY );

        if ( resManager == null || resManager.isEmpty() ) {
            final String configFile = options.get( RESOURCE_MANAGER_CONFIG_KEY );

            return new URLResourceManager( configFile );
        }

        return loadConfigClazz( resManager, ResourceManager.class );
    }

    private AuthenticationProvider getAuthenticationProvider( final Map<String, String> options ) {
        final String authProvider = options.get( AUTH_PROVIDER_KEY );

        return loadConfigClazz( authProvider, AuthenticationProvider.class );
    }

    private AuthenticationManager getAuthenticationManager( final Map<String, String> options ) {
        final String authManager = options.get( AUTH_MANAGER_KEY );

        return loadConfigClazz( authManager, AuthenticationManager.class );
    }

    private AuthenticationScheme getAuthenticationScheme( final Map<String, String> options ) {
        final String authScheme = options.get( AUTH_SCHEME_KEY );
        AuthenticationScheme scheme = null;
        if ( authScheme == null || authScheme.isEmpty() ) {
            return new FormAuthenticationScheme();
        } else {
            scheme = loadConfigClazz( authScheme, AuthenticationScheme.class );
        }

        if ( scheme == null && authScheme.equalsIgnoreCase( FORM ) ) {
            return new FormAuthenticationScheme();
        }

        return scheme;
    }

    @Override
    public void doFilter( final ServletRequest request,
                          final ServletResponse response,
                          final FilterChain chain )
            throws IOException, ServletException {

        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;

        final SecurityContext context = securityManager.newSecurityContext( httpRequest, httpResponse );

        try {
            logout( context, httpRequest, httpResponse );

            authenticate( context, httpResponse );

            authorize( context, httpResponse );

            if ( !response.isCommitted() ) {
                chain.doFilter( request, response );
            }
        } catch ( AuthenticationException e ) {
            if ( !response.isCommitted() ) {
                ( (HttpServletResponse) response ).sendError( 401, e.getMessage() );
            }
        }
    }

    private void logout( final SecurityContext context,
                         final HttpServletRequest httpRequest,
                         final HttpServletResponse httpResponse ) {
        if ( httpResponse.isCommitted() ) {
            return;
        }

        if ( isLogoutRequest( httpRequest ) ) {
            securityManager.logout( context );
            try {
                httpResponse.sendRedirect( getBaseUrl( httpRequest ) );
            } catch ( IOException e ) {
                LOG.error( "Can't redirect. Message: " + e.toString() );
            }
        }
    }

    private String getBaseUrl( HttpServletRequest request ) {
        if ( ( request.getServerPort() == 80 ) ||
                ( request.getServerPort() == 443 ) ) {
            return request.getScheme() + "://" +
                    request.getServerName() +
                    request.getContextPath();
        } else {
            return request.getScheme() + "://" +
                    request.getServerName() + ":" + request.getServerPort() +
                    request.getContextPath();
        }
    }

    private boolean isLogoutRequest( final HttpServletRequest request ) {
        return request.getRequestURI().contains( LOGOUT_URI );
    }

    private void authenticate( final SecurityContext context,
                               final HttpServletResponse httpResponse )
            throws AuthenticationException {
        if ( httpResponse.isCommitted() ) {
            return;
        }

        final Subject subject = securityManager.authenticate( context );
        SecurityFactory.setSubject( subject );
    }

    private void authorize( final SecurityContext context,
                            final HttpServletResponse httpResponse )
            throws IOException {
        if ( httpResponse.isCommitted() ) {
            return;
        }

        boolean authorize = securityManager.authorize( context );

        if ( !authorize && !httpResponse.isCommitted() ) {
            httpResponse.sendError( SC_FORBIDDEN );
        }
    }

    private <T> T loadConfigClazz( final String clazzName,
                                   final Class<T> typeOf ) {

        if ( clazzName == null || clazzName.isEmpty() ) {
            return null;
        }

        try {
            final Class<?> clazz = Class.forName( clazzName );

            if ( !typeOf.isAssignableFrom( clazz ) ) {
                LOG.error( "Invalid class type '" + typeOf.getName() + "'" );
                return null;
            }

            return typeOf.cast( clazz.newInstance() );
        } catch ( ClassNotFoundException e ) {
            LOG.error( "Class not found '" + clazzName + "'" );
        } catch ( InstantiationException e ) {
            LOG.error( "Can't instantiate class '" + clazzName + "'" );
        } catch ( IllegalAccessException e ) {
            LOG.error( "The following error ocurred. " + e.getMessage() );
        }

        return null;
    }

    @Override
    public void destroy() {
        securityManager.dispose();
    }
}

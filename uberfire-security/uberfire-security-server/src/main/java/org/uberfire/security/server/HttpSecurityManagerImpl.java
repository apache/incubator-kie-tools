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

import static java.util.Collections.emptyList;
import static org.uberfire.commons.validation.PortablePreconditions.checkCondition;
import static org.uberfire.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.commons.validation.Preconditions.checkInstanceOf;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.uberfire.security.ResourceManager;
import org.uberfire.security.SecurityContext;
import org.uberfire.security.SecurityManager;
import org.uberfire.security.Subject;
import org.uberfire.security.auth.AuthenticatedStorageProvider;
import org.uberfire.security.auth.AuthenticationException;
import org.uberfire.security.auth.AuthenticationManager;
import org.uberfire.security.auth.AuthenticationProvider;
import org.uberfire.security.auth.AuthenticationScheme;
import org.uberfire.security.auth.AuthenticationSource;
import org.uberfire.security.auth.RoleProvider;
import org.uberfire.security.auth.SubjectPropertiesProvider;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.authz.ResourceDecisionManager;
import org.uberfire.security.authz.RoleDecisionManager;
import org.uberfire.security.authz.VotingStrategy;
import org.uberfire.security.impl.authz.DefaultAuthorizationManager;
import org.uberfire.security.impl.authz.DefaultRoleDecisionManager;
import org.uberfire.security.server.auth.DefaultAuthenticationProvider;
import org.uberfire.security.server.auth.HttpAuthenticationManager;
import org.uberfire.security.server.auth.RememberMeCookieAuthProvider;
import org.uberfire.security.server.authz.URLAccessDecisionManager;

public class HttpSecurityManagerImpl implements SecurityManager {

    private AuthenticationManager authManager;
    private List<AuthorizationManager> authzManagers = new ArrayList<AuthorizationManager>();

    private HttpSecurityManagerImpl( final AuthenticationManager authManager,
                                     final List<AuthenticationScheme> authSchemes,
                                     final String forceURL,
                                     final List<AuthenticationProvider> authProviders,
                                     final List<RoleProvider> roleProviders,
                                     final List<SubjectPropertiesProvider> subjectPropertiesProviders,
                                     final List<AuthenticatedStorageProvider> authStorageProviders,
                                     final List<AuthorizationManager> authzManagers,
                                     final ResourceManager resourceManager,
                                     final Collection<ResourceDecisionManager> resourceDecisionManagers,
                                     final VotingStrategy votingStrategy,
                                     final RoleDecisionManager roleDecisionManager ) {
        if ( authManager == null ) {
            this.authManager = new HttpAuthenticationManager( authSchemes, forceURL, authProviders, roleProviders, subjectPropertiesProviders, authStorageProviders, resourceManager );
        } else {
            this.authManager = authManager;

            try {
                final Method method = this.authManager.getClass().getMethod( "addSchemes", Collection.class );
                method.invoke( this.authManager, authSchemes );
            } catch ( Exception e ) {
            }

            try {
                final Method method = this.authManager.getClass().getMethod( "addRoleProviders", Collection.class );
                method.invoke( this.authManager, roleProviders );
            } catch ( Exception e ) {
            }

            try {
                final Method method = this.authManager.getClass().getMethod( "addSubjectPropertiesProviders", Collection.class );
                method.invoke( this.authManager, subjectPropertiesProviders );
            } catch ( Exception e ) {
            }

            try {
                final Method method = this.authManager.getClass().getMethod( "addStorageProviders", Collection.class );
                method.invoke( this.authManager, authStorageProviders );
            } catch ( Exception e ) {
            }

            try {
                final Method method = this.authManager.getClass().getMethod( "setResourceManager", ResourceManager.class );
                method.invoke( this.authManager, resourceManager );
            } catch ( Exception e ) {
            }

            if ( authProviders != null && authProviders.size() > 0 ) {
                try {
                    final Method method = this.authManager.getClass().getMethod( "addProviders", Collection.class );
                    method.invoke( this.authManager, authProviders );
                } catch ( Exception e ) {
                }
            }

            try {
                final Method method = this.authManager.getClass().getMethod( "init" );
                method.invoke( this.authManager );
            } catch ( Exception e ) {
            }
        }

        final RoleDecisionManager roleDecision;
        if ( roleDecisionManager != null ) {
            roleDecision = roleDecisionManager;
        } else {
            roleDecision = new DefaultRoleDecisionManager();
        }

        if ( resourceDecisionManagers == null || resourceDecisionManagers.isEmpty() ) {
            URLResourceManager urlResourceManager = null;
            if ( resourceManager instanceof URLResourceManager ) {
                urlResourceManager = (URLResourceManager) resourceManager;
            }
            if ( urlResourceManager == null ) {
                throw new IllegalStateException( "Can't find URLResourceManager." );
            }
            resourceDecisionManagers.add( new URLAccessDecisionManager( urlResourceManager ) );
        }

        if ( authzManagers == null || authzManagers.isEmpty() ) {
            this.authzManagers.add( new DefaultAuthorizationManager( resourceDecisionManagers, resourceManager, votingStrategy, roleDecision ) );
        } else {
            for ( final AuthorizationManager authzManager : authzManagers ) {
                try {
                    final Method method = authzManager.getClass().getMethod( "addDecisionManagers", Collection.class );
                    method.invoke( authzManager, resourceDecisionManagers );
                } catch ( Exception e ) {
                }

                try {
                    final Method method = authzManager.getClass().getMethod( "setResourceManager", ResourceManager.class );
                    method.invoke( authzManager, resourceManager );
                } catch ( Exception e ) {
                }

                try {
                    final Method method = authzManager.getClass().getMethod( "setVotingStrategy", VotingStrategy.class );
                    method.invoke( authzManager, votingStrategy );
                } catch ( Exception e ) {
                }

                try {
                    final Method method = authzManager.getClass().getMethod( "init" );
                    method.invoke( authzManager );
                } catch ( Exception e ) {
                }
                this.authzManagers.add( authzManager );
            }
        }
    }

    @Override
    public SecurityContext newSecurityContext( Object... params ) {
        checkNotEmpty( "params", params );
        checkCondition( "at least two params should be provided", params.length >= 2 );

        HttpServletRequest httpRequest = null;
        HttpServletResponse httpResponse = null;

        final List<Object> others;
        if ( params.length == 2 ) {
            others = emptyList();
        } else {
            others = new ArrayList<Object>( params.length - 2 );
        }

        for ( final Object param : params ) {
            if ( param instanceof HttpServletRequest ) {
                httpRequest = (HttpServletRequest) param;
            } else if ( param instanceof HttpServletResponse ) {
                httpResponse = (HttpServletResponse) param;
            } else {
                others.add( param );
            }
        }

        checkNotNull( "param.http.request", httpRequest );
        checkNotNull( "param.http.response", httpResponse );

        return new HttpSecurityContext( httpRequest, httpResponse, others.toArray( new Object[ others.size() ] ) );
    }

    @Override
    public void logout( final SecurityContext context ) {
        authManager.logout( context );
    }

    @Override
    public Subject authenticate( final SecurityContext context ) throws AuthenticationException {
        final HttpSecurityContext httpContext = checkInstanceOf( "context", context, HttpSecurityContext.class );

        try {
            final Subject subject = authManager.authenticate( httpContext );
            httpContext.setCurrentSubject( subject );
            return subject;
        } catch ( Exception ex ) {
            throw new AuthenticationException( "Validation fails.", ex );
        }
    }

    @Override
    public boolean authorize( final SecurityContext context ) {
        final HttpSecurityContext httpContext = checkInstanceOf( "context", context, HttpSecurityContext.class );
        for ( final AuthorizationManager authzManager : authzManagers ) {
            if ( authzManager.supports( httpContext.getResource() ) ) {
                return authzManager.authorize( httpContext.getResource(), httpContext.getCurrentSubject() );
            }
        }
        return false;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void start() {

    }

    public static HttpSecurityManagerBuilder newBuilder() {
        return new HttpSecurityManagerBuilder();
    }

    public static class HttpSecurityManagerBuilder {

        private AuthenticationManager authManager = null;
        private RoleDecisionManager roleDecisionManager = null;
        private VotingStrategy votingStrategy = null;
        private ResourceManager resourceManager = null;
        private String forceURL = null;
        private List<AuthorizationManager> authzManagers = new ArrayList<AuthorizationManager>();
        private List<AuthenticationScheme> authSchemes = new ArrayList<AuthenticationScheme>();
        private List<AuthenticationProvider> authProviders = new ArrayList<AuthenticationProvider>();
        private List<RoleProvider> roleProviders = new ArrayList<RoleProvider>();
        private List<SubjectPropertiesProvider> subjectPropertiesProviders = new ArrayList<SubjectPropertiesProvider>();
        private List<AuthenticatedStorageProvider> authStorageProviders = new ArrayList<AuthenticatedStorageProvider>();
        private List<ResourceDecisionManager> accessDecisionManagers = new ArrayList<ResourceDecisionManager>();

        HttpSecurityManagerImpl build( final Map<String, String> options ) {

            if ( authProviders != null && !authProviders.isEmpty() ) {
                for ( final AuthenticationProvider provider : authProviders ) {
                    provider.initialize( options );
                }
            }

            return new HttpSecurityManagerImpl( authManager, authSchemes, forceURL, authProviders, roleProviders, subjectPropertiesProviders, authStorageProviders,
                                                authzManagers, resourceManager, accessDecisionManagers, votingStrategy, roleDecisionManager );
        }

        HttpSecurityManagerBuilder addAuthScheme( final AuthenticationScheme authScheme ) {
            if ( authScheme != null ) {
                authSchemes.add( authScheme );
            }
            return this;
        }

        HttpSecurityManagerBuilder addAuthManager( final AuthenticationManager authManager ) {
            this.authManager = authManager;
            return this;
        }

        HttpSecurityManagerBuilder addAuthProvider( final AuthenticationProvider authProvider ) {
            if ( authProvider != null ) {
                authProviders.add( authProvider );
            }
            return this;
        }

        HttpSecurityManagerBuilder addResourceManager( final ResourceManager resourceManager ) {
            this.resourceManager = resourceManager;
            return this;
        }

        HttpSecurityManagerBuilder addAccessDecisionManager( final ResourceDecisionManager accessDecisionManager ) {
            if ( accessDecisionManager != null ) {
                this.accessDecisionManagers.add( accessDecisionManager );
            }
            return this;
        }

        public HttpSecurityManagerBuilder addRoleProvider( final RoleProvider roleProvider ) {
            if ( roleProvider != null ) {
                this.roleProviders.add( roleProvider );
            }
            return this;
        }

        public HttpSecurityManagerBuilder addSubjectPropertiesProvider( final SubjectPropertiesProvider subjectPropertiesProvider ) {
            if ( subjectPropertiesProvider != null ) {
                this.subjectPropertiesProviders.add( subjectPropertiesProvider );
            }
            return this;
        }

        public HttpSecurityManagerBuilder addAuthenticatedStorageProvider( final AuthenticatedStorageProvider authStorageProvider ) {
            if ( authStorageProvider != null ) {
                this.authStorageProviders.add( authStorageProvider );
            }
            return this;
        }

        HttpSecurityManagerBuilder addAuthzManager( final AuthorizationManager authzManager ) {
            if ( authzManager != null ) {
                this.authzManagers.add( authzManager );
            }
            return this;
        }

        HttpSecurityManagerBuilder addVotingStrategy( final VotingStrategy votingStrategy ) {
            this.votingStrategy = votingStrategy;
            return this;
        }

        HttpSecurityManagerBuilder loadAvailableAuthenticationSources() {
            final ServiceLoader<AuthenticationSource> sources = ServiceLoader.load( AuthenticationSource.class );
            if ( sources != null ) {
                for ( final AuthenticationSource source : sources ) {
                    authProviders.add( new DefaultAuthenticationProvider( source ) );
                    if ( source instanceof RoleProvider ) {
                        roleProviders.add( (RoleProvider) source );
                    }
                }
            }
            authProviders.add( new RememberMeCookieAuthProvider() );
            return this;
        }

        HttpSecurityManagerBuilder addRoleDecisionManager( final RoleDecisionManager roleDecisionManager ) {
            this.roleDecisionManager = roleDecisionManager;
            return this;
        }

        HttpSecurityManagerBuilder addForceURL( String forceRedirectURL ) {
            this.forceURL = forceRedirectURL;
            return this;
        }
    }

    @Override
    public String toString() {
      return "HttpSecurityManagerImpl [\n"
              + "  Authentication Manager: " + authManager + ",\n"
              + "  Authorization Managers: " + authzManagers + "]";
    }

}

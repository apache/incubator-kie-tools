package org.uberfire.backend.server.io;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.server.repositories.RepositoryServiceImpl;
import org.uberfire.backend.server.security.RepositoryAuthorizationManager;
import org.uberfire.commons.cluster.ClusterServiceFactory;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceNio2WrapperImpl;
import org.uberfire.io.impl.cluster.IOServiceClusterImpl;
import org.uberfire.security.auth.AuthenticationManager;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.server.SecurityConstants;
import org.uberfire.security.server.auth.impl.JAASAuthenticationManager;
import org.uberfire.security.server.auth.impl.PropertyAuthenticationManager;

import static org.uberfire.backend.server.repositories.SystemRepository.*;

@ApplicationScoped
public class ConfigIOServiceProducer {

    @Inject
    private RepositoryServiceImpl repositoryService;

    @Inject
    @Named("clusterServiceFactory")
    private ClusterServiceFactory clusterServiceFactory;

    private IOService configIOService;
    private AuthorizationManager authorizationManager;
    private AuthenticationManager authenticationManager;

    ConfigIOServiceProducer() {
        final String authType = System.getProperty( "org.uberfire.io.auth", null );
        final String domain = System.getProperty( SecurityConstants.AUTH_DOMAIN_KEY, null );

        if ( authType == null || authType.toLowerCase().equals( "jaas" ) || authType.toLowerCase().equals( "container" ) ) {
            authenticationManager = new JAASAuthenticationManager( domain );
        } else if ( authType.toLowerCase().equals( "property" ) ) {
            authenticationManager = new PropertyAuthenticationManager( null );
        } else {
            authenticationManager = null;
        }
    }

    @PostConstruct
    public void setup() {
        if ( clusterServiceFactory == null ) {
            configIOService = new IOServiceNio2WrapperImpl( "config" );
        } else {
            configIOService = new IOServiceClusterImpl(
                    new IOServiceNio2WrapperImpl( "config" ), clusterServiceFactory, clusterServiceFactory.isAutoStart() );
        }
        authorizationManager = new RepositoryAuthorizationManager( repositoryService );
        configIOService.setAuthenticationManager( authenticationManager );
        configIOService.setAuthorizationManager( authorizationManager );
    }

    @PreDestroy
    public void onShutdown() {
        configIOService.dispose();
    }

    @Produces
    @Named("configIO")
    public IOService configIOService() {
        return configIOService;
    }

    @Produces
    @Named("system")
    public Repository systemRepository() {
        return SYSTEM_REPO;
    }

    @Produces
    @IOSecurityAuthz
    public AuthorizationManager authorizationManager() {
        return authorizationManager;
    }

    @Produces
    @IOSecurityAuth
    public AuthenticationManager authenticationManager() {
        return authenticationManager;
    }

}

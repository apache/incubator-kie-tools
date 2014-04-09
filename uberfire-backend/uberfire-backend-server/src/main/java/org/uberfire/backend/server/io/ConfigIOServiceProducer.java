package org.uberfire.backend.server.io;

import static org.uberfire.backend.server.repositories.SystemRepository.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.security.shared.service.AuthenticationService;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.server.repositories.RepositoryServiceImpl;
import org.uberfire.backend.server.security.JAASAuthenticationService;
import org.uberfire.backend.server.security.RepositoryAuthorizationManager;
import org.uberfire.commons.cluster.ClusterServiceFactory;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceNio2WrapperImpl;
import org.uberfire.io.impl.cluster.IOServiceClusterImpl;

@ApplicationScoped
public class ConfigIOServiceProducer {

    @Inject
    private RepositoryServiceImpl repositoryService;

    @Inject
    @Named("clusterServiceFactory")
    private ClusterServiceFactory clusterServiceFactory;

    @Inject @IOSecurityAuth
    private Instance<AuthenticationService> applicationProvidedConfigIOAuthService;

    private IOService configIOService;

    @PostConstruct
    public void setup() {
        if ( clusterServiceFactory == null ) {
            configIOService = new IOServiceNio2WrapperImpl( "config" );
        } else {
            configIOService = new IOServiceClusterImpl(
                    new IOServiceNio2WrapperImpl( "config" ), clusterServiceFactory, clusterServiceFactory.isAutoStart() );
        }

        AuthenticationService authenticationService;
        if ( applicationProvidedConfigIOAuthService.isUnsatisfied() ) {
            authenticationService = new JAASAuthenticationService( JAASAuthenticationService.DEFAULT_DOMAIN );
        } else {
            authenticationService = applicationProvidedConfigIOAuthService.get();
        }

        configIOService.setAuthenticationManager( authenticationService );
        configIOService.setAuthorizationManager( new RepositoryAuthorizationManager( repositoryService ) );
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
}

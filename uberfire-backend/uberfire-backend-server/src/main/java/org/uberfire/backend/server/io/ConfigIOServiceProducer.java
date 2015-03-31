package org.uberfire.backend.server.io;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.security.shared.service.AuthenticationService;
import org.uberfire.backend.server.security.IOSecurityAuth;
import org.uberfire.commons.cluster.ClusterServiceFactory;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceNio2WrapperImpl;
import org.uberfire.io.impl.cluster.IOServiceClusterImpl;

@ApplicationScoped
@Startup(StartupType.BOOTSTRAP)
public class ConfigIOServiceProducer {

    @Inject
    @Named("clusterServiceFactory")
    private ClusterServiceFactory clusterServiceFactory;

    @Inject
    @IOSecurityAuth
    private Instance<AuthenticationService> applicationProvidedConfigIOAuthService;

    private IOService configIOService;

    @PostConstruct
    public void setup() {
        if ( clusterServiceFactory == null ) {
            configIOService = new IOServiceNio2WrapperImpl( "config" );
        } else {
            configIOService = new IOServiceClusterImpl( new IOServiceNio2WrapperImpl( "config" ), clusterServiceFactory, clusterServiceFactory.isAutoStart() );
        }
    }

    @Produces
    @Named("configIO")
    public IOService configIOService() {
        return configIOService;
    }

}

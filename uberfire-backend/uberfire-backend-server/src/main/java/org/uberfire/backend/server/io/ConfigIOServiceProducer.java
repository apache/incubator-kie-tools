package org.uberfire.backend.server.io;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.commons.async.SimpleAsyncExecutorService;
import org.uberfire.commons.cluster.ClusterServiceFactory;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceNio2WrapperImpl;
import org.uberfire.io.impl.cluster.IOServiceClusterImpl;

@ApplicationScoped
@Startup
public class ConfigIOServiceProducer {

    @Inject
    @Named("clusterServiceFactory")
    private ClusterServiceFactory clusterServiceFactory;

    private IOService configIOService;

    @PostConstruct
    public void setup() {
        if ( clusterServiceFactory == null ) {
            configIOService = new IOServiceNio2WrapperImpl( "config" );
        } else {
            configIOService = new IOServiceClusterImpl(
                    new IOServiceNio2WrapperImpl( "config" ), clusterServiceFactory, clusterServiceFactory.isAutoStart() );
        }
    }

    @PreDestroy
    public void onShutdown() {
        configIOService.dispose();
        SimpleAsyncExecutorService.shutdownInstances();
    }

    @Produces
    @Named("configIO")
    public IOService configIOService() {
        return configIOService;
    }

}
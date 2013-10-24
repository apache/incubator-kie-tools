package org.uberfire.backend.server.io;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.backend.repositories.Repository;
import org.uberfire.commons.cluster.ClusterServiceFactory;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceNio2WrapperImpl;
import org.uberfire.io.impl.cluster.IOServiceClusterImpl;

import static org.uberfire.backend.server.repositories.SystemRepository.*;

@ApplicationScoped
public class ConfigIOServiceProducer {

    @Inject
    @Named("clusterServiceFactory")
    private ClusterServiceFactory clusterServiceFactory;

    private IOService configIOService;

    @PostConstruct
    public void setup() {
        if ( clusterServiceFactory == null ) {
            configIOService = new IOServiceNio2WrapperImpl();
        } else {
            configIOService = new IOServiceClusterImpl( new IOServiceNio2WrapperImpl(), clusterServiceFactory );
        }
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

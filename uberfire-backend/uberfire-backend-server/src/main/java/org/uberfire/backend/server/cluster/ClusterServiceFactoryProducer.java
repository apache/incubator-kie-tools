package org.uberfire.backend.server.cluster;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.cluster.ClusterService;
import org.uberfire.commons.cluster.ClusterServiceFactory;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;

@ApplicationScoped
@Startup(StartupType.BOOTSTRAP)
public class ClusterServiceFactoryProducer {

    private static final Logger logger = LoggerFactory.getLogger( ClusterServiceFactoryProducer.class );

    private final ClusterServiceFactory factory;
    private ClusterService clusterService = null;

    ClusterServiceFactoryProducer() {
        this.factory = ClusterServiceFactorySetup.buildFactory();
    }

    @Produces
    @Named("clusterServiceFactory")
    public ClusterServiceFactory clusterServiceFactory() {
        return factory;
    }
}

package org.uberfire.backend.server.cluster;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.cluster.ClusterService;
import org.uberfire.commons.cluster.ClusterServiceFactory;
import org.uberfire.commons.services.cdi.ApplicationStarted;

@ApplicationScoped
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

    public void startOnEvent( @Observes ApplicationStarted event ) {
        logger.debug( "Received event for application started {}", clusterService );
        if ( factory != null && factory instanceof ClusterServiceFactorySimpleImpl ) {
            logger.debug( "About to create cluster service..." );
            ( (ClusterServiceFactorySimpleImpl) factory ).startClusterService();
        }
    }
}

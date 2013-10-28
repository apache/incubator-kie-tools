package org.uberfire.backend.server.cluster;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.cluster.ClusterService;
import org.uberfire.commons.cluster.ClusterServiceFactory;
import org.uberfire.commons.message.MessageHandlerResolver;
import org.uberfire.commons.services.cdi.ApplicationStarted;
import org.uberfire.io.impl.cluster.helix.ClusterServiceHelix;

@ApplicationScoped
public class ClusterServiceFactoryProducer {

    private static final Logger logger = LoggerFactory.getLogger(ClusterServiceFactoryProducer.class);

    private final ClusterServiceFactory factory;
    private ClusterService clusterService = null;

    ClusterServiceFactoryProducer() {
        final String clusterName = System.getProperty( "org.uberfire.cluster.id", null );
        final String zkAddress = System.getProperty( "org.uberfire.cluster.zk", null );
        final String localId = System.getProperty( "org.uberfire.cluster.local.id", null );
        final String resourceName = System.getProperty( "org.uberfire.cluster.vfs.lock", null );
        final boolean autostart = Boolean.parseBoolean(System.getProperty("org.uberfire.cluster.autostart", "true"));

        if ( clusterName == null || zkAddress == null || localId == null || resourceName == null ) {
            this.factory = null;
        } else {
            this.factory = new ClusterServiceFactory() {
                @Override
                public ClusterService build( final MessageHandlerResolver resolver ) {
                    if ( clusterService == null ) {
                        clusterService = new ClusterServiceHelix( clusterName, zkAddress, localId, resourceName, resolver );
                    } else {
                        clusterService.addMessageHandlerResolver( resolver );
                    }
                    return clusterService;
                }

                @Override
                public boolean isAutoStart() {
                    return autostart;
                }
            };
        }
    }

    @Produces
    @Named("clusterServiceFactory")
    public ClusterServiceFactory clusterServiceFactory() {
        return factory;
    }


    public void startOnEvent(@Observes ApplicationStarted event) {
        logger.debug("Received event for application started {}", clusterService);
        if (clusterService != null) {
            logger.debug("About to create cluster service...");
            clusterService.start();
        }
    }
}

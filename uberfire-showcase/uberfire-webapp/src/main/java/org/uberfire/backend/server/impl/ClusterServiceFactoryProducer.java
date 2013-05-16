package org.uberfire.backend.server.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.kie.commons.cluster.ClusterService;
import org.kie.commons.cluster.ClusterServiceFactory;
import org.kie.commons.io.impl.cluster.helix.ClusterServiceHelix;
import org.kie.commons.message.MessageHandlerResolver;

@ApplicationScoped
public class ClusterServiceFactoryProducer {

    private final ClusterServiceFactory factory;

    ClusterServiceFactoryProducer() {
        final String clusterName = System.getProperty( "org.uberfire.cluster.id", null );
        final String zkAddress = System.getProperty( "org.uberfire.cluster.zk", null );
        final String localId = System.getProperty( "org.uberfire.cluster.local.id", null );
        final String resourceName = System.getProperty( "org.uberfire.cluster.vfs.lock", null );

        if ( clusterName == null || zkAddress == null || localId == null || resourceName == null ) {
            this.factory = null;
        } else {
            this.factory = new ClusterServiceFactory() {
                private ClusterService clusterService;

                @Override
                public ClusterService build( final MessageHandlerResolver resolver ) {
                    if ( clusterService == null ) {
                        clusterService = new ClusterServiceHelix( clusterName, zkAddress, localId, resourceName, resolver );
                    }
                    return clusterService;
                }
            };
        }

    }

    @Produces
    @Named("clusterServiceFactory")
    public ClusterServiceFactory clusterServiceFactory() {
        return factory;
    }

}

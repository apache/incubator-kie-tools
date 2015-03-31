package org.uberfire.backend.server.cluster;

import org.uberfire.commons.cluster.ClusterService;
import org.uberfire.commons.cluster.ClusterServiceFactory;
import org.uberfire.commons.message.MessageHandlerResolver;
import org.uberfire.io.impl.cluster.helix.ClusterServiceHelix;

/**
 * TODO: update me
 */
public class ClusterServiceFactorySimpleImpl implements ClusterServiceFactory {

    private ClusterService clusterService;
    private final String clusterName;
    private final String zkAddress;
    private final String localId;
    private final String resourceName;
    private final boolean autostart;

    public ClusterServiceFactorySimpleImpl( final String clusterName,
                                            final String zkAddress,
                                            final String localId,
                                            final String resourceName,
                                            final boolean autostart ) {
        this.clusterName = clusterName;
        this.zkAddress = zkAddress;
        this.localId = localId;
        this.resourceName = resourceName;
        this.autostart = autostart;
    }

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

}

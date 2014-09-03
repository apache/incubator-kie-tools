package org.uberfire.backend.server.cluster;

import org.uberfire.commons.cluster.ClusterServiceFactory;

public final class ClusterServiceFactorySetup {

    public static ClusterServiceFactory buildFactory() {
        final String clusterName = System.getProperty( "org.uberfire.cluster.id", null );
        final String zkAddress = System.getProperty( "org.uberfire.cluster.zk", null );
        final String localId = System.getProperty( "org.uberfire.cluster.local.id", null );
        final String resourceName = System.getProperty( "org.uberfire.cluster.vfs.lock", null );
        final boolean autostart = Boolean.parseBoolean( System.getProperty( "org.uberfire.cluster.autostart", "true" ) );

        if ( clusterName == null || zkAddress == null || localId == null || resourceName == null ) {
            return null;
        }

        return new ClusterServiceFactorySimpleImpl( clusterName, zkAddress, localId, resourceName, autostart );
    }
}

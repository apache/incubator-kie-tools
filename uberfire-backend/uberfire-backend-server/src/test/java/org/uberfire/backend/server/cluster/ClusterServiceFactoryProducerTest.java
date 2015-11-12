package org.uberfire.backend.server.cluster;

import org.junit.Test;
import org.uberfire.commons.cluster.ClusterServiceFactory;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ClusterServiceFactoryProducerTest {

    @Test
    public void testClusterNotAvailable() {
        final ClusterServiceFactoryProducer factoryProducer = new ClusterServiceFactoryProducer() {
            ClusterServiceFactory buildFactory() {
                return null;
            }
        };

        assertNull( factoryProducer.clusterServiceFactory() );
    }

    @Test
    public void testClusterInitializedBeforeAnyUse() {
        final ClusterServiceFactory clusterServiceFactory = mock( ClusterServiceFactory.class );

        final ClusterServiceFactoryProducer factoryProducer = new ClusterServiceFactoryProducer() {
            ClusterServiceFactory buildFactory() {
                return clusterServiceFactory;
            }
        };

        final ClusterServiceFactory factory = factoryProducer.clusterServiceFactory();
        assertNotNull( factory );
        assertEquals( clusterServiceFactory, factory );

        verify( factory, times( 1 ) ).build( null );
    }

}

package org.uberfire.io.impl.cluster.helix;

import java.util.HashMap;
import java.util.Map;

import org.apache.helix.HelixManager;
import org.apache.helix.model.ExternalView;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.commons.lifecycle.PriorityDisposableRegistry;
import org.uberfire.commons.message.MessageHandlerResolver;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ClusterServiceHelixTest {

    ClusterServiceHelix clusterServiceHelix;
    ExternalView externalView;

    @Test
    public void getNodeStatusEmptyOrNullShouldReturnOfflineTest() {

        when( externalView.getStateMap( "resourceName_0" ) ).thenReturn( null );
        assertEquals( "OFFLINE", clusterServiceHelix.getNodeStatus() );

        Map<String, String> emptyMap = new HashMap<String, String>();
        when( externalView.getStateMap( "resourceName_0" ) ).thenReturn( emptyMap );
        assertEquals( "OFFLINE", clusterServiceHelix.getNodeStatus() );

    }

    @Test
    public void getNodeStatusTest() {

        Map<String, String> valueMap = new HashMap<String, String>();
        valueMap.put( "instanceName", "LEADER" );
        when( externalView.getStateMap( "resourceName_0" ) ).thenReturn( valueMap );
        assertEquals( "LEADER", clusterServiceHelix.getNodeStatus() );

    }

    @Test
    public void getNodeStatusNullViewTest() {
        externalView = null;
        assertEquals( "OFFLINE", clusterServiceHelix.getNodeStatus() );
    }

    @Before
    public void setup() {
        externalView = mock( ExternalView.class );

        clusterServiceHelix = new ClusterServiceHelix( "clusterName",
                                                       "zkAddress",
                                                       "instanceName",
                                                       "resourceName",
                                                       mock( MessageHandlerResolver.class ) ) {
            @Override
            HelixManager getZkHelixManager( String clusterName,
                                            String zkAddress,
                                            String instanceName ) {
                return mock( HelixManager.class );
            }

            @Override
            void start() {
            }

            @Override
            public void addMessageHandlerResolver( MessageHandlerResolver resolver ) {
            }

            @Override
            ExternalView getResourceExternalView() {
                return externalView;
            }
        };

        assertTrue( PriorityDisposableRegistry.getDisposables().contains( clusterServiceHelix ) );
    }
}
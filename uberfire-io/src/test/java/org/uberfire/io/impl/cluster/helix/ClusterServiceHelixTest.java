/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
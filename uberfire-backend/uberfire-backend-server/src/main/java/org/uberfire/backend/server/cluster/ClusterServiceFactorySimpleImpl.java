/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
    public synchronized ClusterService build( final MessageHandlerResolver resolver ) {
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

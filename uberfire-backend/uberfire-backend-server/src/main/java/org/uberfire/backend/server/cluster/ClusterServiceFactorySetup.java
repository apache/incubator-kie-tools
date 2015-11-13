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

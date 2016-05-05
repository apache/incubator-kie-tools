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

package org.uberfire.ext.metadata.backend.lucene.model;

import org.uberfire.ext.metadata.model.KCluster;

public class KClusterImpl implements KCluster {

    private final String clusterId;

    public KClusterImpl( final String clusterId ) {
        this.clusterId = clusterId;
    }

    @Override
    public String getClusterId() {
        return clusterId;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof KClusterImpl ) ) {
            return false;
        }

        KClusterImpl kCluster = (KClusterImpl) o;

        if ( !clusterId.equals( kCluster.clusterId ) ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return clusterId.hashCode();
    }
}

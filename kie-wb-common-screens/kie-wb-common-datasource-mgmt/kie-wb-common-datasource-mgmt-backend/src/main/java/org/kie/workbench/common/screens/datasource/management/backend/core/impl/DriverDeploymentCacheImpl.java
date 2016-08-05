/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datasource.management.backend.core.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.workbench.common.screens.datasource.management.backend.core.DriverDeploymentCache;
import org.kie.workbench.common.screens.datasource.management.backend.core.DriverDeploymentCacheEntry;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;
import org.kie.workbench.common.screens.datasource.management.model.DriverDeploymentInfo;

public class DriverDeploymentCacheImpl
        implements DriverDeploymentCache {

    private Map<DriverDeploymentInfo, DriverDeploymentCacheEntry> entries = new HashMap<>(  );

    @Override
    public DriverDeploymentCacheEntry put( DriverDeploymentInfo deploymentInfo, DriverDef driverDef ) {
        DriverDeploymentCacheEntry entry = new DriverDeploymentCacheEntry( driverDef );
        entries.put( deploymentInfo, entry );
        return entry;
    }

    @Override
    public void remove( DriverDeploymentInfo deploymentInfo ) {
        entries.remove( deploymentInfo );
    }

    @Override
    public DriverDeploymentCacheEntry get( DriverDeploymentInfo deploymentInfo ) {
        return entries.get( deploymentInfo );
    }

    @Override
    public List<DriverDeploymentCacheEntry> findReferencedEntries( DataSourceDeploymentInfo deploymentInfo ) {
        List<DriverDeploymentCacheEntry> result = new ArrayList<>( );
        for ( DriverDeploymentCacheEntry entry : entries.values() ) {
            if ( entry.hasDependant( deploymentInfo ) ) {
                result.add( entry );
            }
        }
        return result;
    }
}

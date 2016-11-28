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

package org.kie.workbench.common.screens.datasource.management.backend.service;

import java.util.Collection;

import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceRuntimeManager;
import org.kie.workbench.common.screens.datasource.management.backend.core.DefDeployer;
import org.kie.workbench.common.screens.datasource.management.service.DataSourceDefQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

public abstract class AbstractDefDeployer<T> implements DefDeployer {

    private static final Logger logger = LoggerFactory.getLogger( DataSourceDefDeployerImpl.class );

    protected IOService ioService;

    protected DataSourceDefQueryService queryService;

    protected DataSourceRuntimeManager runtimeManager;

    protected DefRegistry defRegistry;

    public AbstractDefDeployer() {
    }

    public AbstractDefDeployer( IOService ioService,
            DataSourceDefQueryService queryService,
            DataSourceRuntimeManager runtimeManager,
            DefRegistry defRegistry ) {
        this.ioService = ioService;
        this.queryService = queryService;
        this.runtimeManager = runtimeManager;
        this.defRegistry = defRegistry;
    }

    @Override
    public void deployGlobalDefs() {
        try {
            logger.debug( "Starting global defs deployment" );
            deployDefs( findGlobalDefs() );
            logger.debug( "End of global defs deployment" );
        } catch ( Exception e ) {
            logger.error( "Global defs deployment failed.", e );
        }
    }

    @Override
    public void deployProjectDefs( Path path ) {
        try {
            logger.debug( "Starting project defs deployment for path: " + path );
            deployDefs( findProjectDefs( path ) );
            logger.debug( "End of project defs deployment for path: " + path );
        } catch ( Exception e ) {
            logger.error( "Project defs deployment failed for paht: " + path, e );
        }
    }

    protected abstract Collection<T> findGlobalDefs();

    protected abstract Collection<T> findProjectDefs( Path path );

    protected abstract void deployDef( T defInfo );

    private void deployDefs( Collection<T> defs ) {
        for ( T def : defs ) {
            deployDef( def );
        }
    }
}

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
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceRuntimeManager;
import org.kie.workbench.common.screens.datasource.management.backend.core.DeploymentOptions;
import org.kie.workbench.common.screens.datasource.management.backend.core.DriverDefDeployer;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;
import org.kie.workbench.common.screens.datasource.management.model.DriverDefInfo;
import org.kie.workbench.common.screens.datasource.management.service.DataSourceDefQueryService;
import org.kie.workbench.common.screens.datasource.management.util.DriverDefSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

@ApplicationScoped
public class DriverDefDeployerImpl
        implements DriverDefDeployer {

    private static final Logger logger = LoggerFactory.getLogger( DriverDefDeployerImpl.class );

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private DataSourceDefQueryService queryService;

    @Inject
    private DataSourceRuntimeManager runtimeManager;

    public DriverDefDeployerImpl() {
    }

    @Override
    public void deployGlobalDefs() {
        try {
            logger.debug( "Starting global drivers deployment." );
            deployDrivers( queryService.findGlobalDrivers() );
            logger.debug( "End of global drivers deployment." );
        } catch ( Exception e ) {
            logger.error( "Global drivers deployment failed.", e );
        }
    }

    private void deployDrivers( Collection<DriverDefInfo> defs ) {
        for ( DriverDefInfo driverDefInfo : defs ) {
            deployDriver( driverDefInfo );
        }
    }

    private void deployDriver( DriverDefInfo driverDefInfo ) {
        try {
            String source = ioService.readAllString( Paths.convert( driverDefInfo.getPath() ) );
            DriverDef driverDef = DriverDefSerializer.deserialize( source );
            runtimeManager.deployDriver( driverDef, DeploymentOptions.createOrResync() );
        } catch ( Exception e ) {
            logger.error( "Driver deployment failed, driverDefInfo: " + driverDefInfo, e );
        }
    }

    @Override
    public void deployProjectDefs( Path path ) {
        try {
            logger.debug( "Starting project drivers deployment for path: " + path );
            deployDrivers( queryService.findProjectDrivers( path ) );
            logger.debug( "End of project drivers deployment for path: " + path );
        } catch ( Exception e ) {
            logger.error( "Project drivers deployment failed for paht: " + path, e );
        }
    }
}
